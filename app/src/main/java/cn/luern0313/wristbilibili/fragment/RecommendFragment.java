package cn.luern0313.wristbilibili.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import cn.luern0313.wristbilibili.R;
import cn.luern0313.wristbilibili.adapter.RecommendAdapter;
import cn.luern0313.wristbilibili.api.RecommendApi;
import cn.luern0313.wristbilibili.models.RecommendModel;
import cn.luern0313.wristbilibili.ui.MainActivity;
import cn.luern0313.wristbilibili.ui.VideodetailsActivity;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * 被 luern0313 创建于 2020/1/15.
 */

public class RecommendFragment extends Fragment
{
    Context ctx;
    View rootLayout;
    RecommendAdapter recommendAdapter;
    RecommendAdapter.RecommendAdapterListener adapterListener;
    ListView uiListView;
    WaveSwipeRefreshLayout uiWaveSwipeRefreshLayout;
    View uiLoadingView;

    RecommendApi recommendApi;
    ArrayList<RecommendModel> recommendVideoArrayList = new ArrayList<>();
    boolean isLoading = false;

    Handler handler = new Handler();
    Runnable runnableUi;
    Runnable runnableNoWeb;
    Runnable runnableNoMore;
    Runnable RunnableNoWebH;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        ctx = getActivity();
        rootLayout = inflater.inflate(R.layout.fragment_recommend, container, false);
        recommendApi = new RecommendApi(MainActivity.sharedPreferences.getString("mid", ""),
                                        MainActivity.sharedPreferences.getString("cookies", ""),
                                        MainActivity.sharedPreferences.getString("csrf", ""),
                                        MainActivity.sharedPreferences.getString("access_key", ""));

        uiLoadingView = inflater.inflate(R.layout.widget_loading, null, false);
        uiListView = rootLayout.findViewById(R.id.rc_listview);
        uiListView.addFooterView(uiLoadingView);
        uiWaveSwipeRefreshLayout = rootLayout.findViewById(R.id.rc_swipe);
        uiWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        uiWaveSwipeRefreshLayout.setWaveColor(Color.argb(255, 250, 114, 152));
        uiWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        isLoading = true;
                        getRecommend(1);
                    }
                });
            }
        });

        adapterListener = new RecommendAdapter.RecommendAdapterListener()
        {
            @Override
            public void onClick(int viewId, int position)
            {
                onViewClick(viewId, position);
            }
        };

        recommendAdapter = new RecommendAdapter(inflater, recommendVideoArrayList, uiListView, adapterListener);
        uiListView.setAdapter(recommendAdapter);

        runnableUi = new Runnable()
        {
            @Override
            public void run()
            {
                rootLayout.findViewById(R.id.rc_noweb).setVisibility(View.GONE);
                uiListView.setVisibility(View.VISIBLE);
                uiWaveSwipeRefreshLayout.setRefreshing(false);
                isLoading = false;
                recommendAdapter.notifyDataSetChanged();
            }
        };

        runnableNoWeb = new Runnable()
        {
            @Override
            public void run()
            {
                uiWaveSwipeRefreshLayout.setRefreshing(false);
                rootLayout.findViewById(R.id.rc_noweb).setVisibility(View.VISIBLE);
                isLoading = false;
            }
        };

        RunnableNoWebH = new Runnable()
        {
            @Override
            public void run()
            {
                ((TextView) uiLoadingView.findViewById(R.id.wid_load_button)).setText("好像没有网络...\n检查下网络？");
                uiLoadingView.findViewById(R.id.wid_load_button).setVisibility(View.VISIBLE);
            }
        };

        runnableNoMore = new Runnable()
        {
            @Override
            public void run()
            {
                ((TextView) uiLoadingView.findViewById(R.id.wid_load_text)).setText("  没有更多了...");
            }
        };

        uiListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if(visibleItemCount + firstVisibleItem == totalItemCount && !isLoading)
                {
                    isLoading = true;
                    getRecommend(2);
                }
            }
        });

        uiWaveSwipeRefreshLayout.setRefreshing(true);
        getRecommend(2);

        return rootLayout;
    }

    void getRecommend(final int mode) //1上 2下
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    ArrayList<RecommendModel> rankingModelArrayList = recommendApi.getRecommendVideo(mode == 1);
                    if(rankingModelArrayList != null && rankingModelArrayList.size() != 0)
                    {
                        if(mode == 1)
                        {
                            recommendVideoArrayList.add(0, new RecommendModel(1));
                            recommendVideoArrayList.addAll(0, rankingModelArrayList);
                        }
                        else
                            recommendVideoArrayList.addAll(rankingModelArrayList);
                        handler.post(runnableUi);
                    }
                    else
                    {
                        handler.post(runnableNoMore);
                    }
                }
                catch (IOException e)
                {
                    handler.post(runnableNoWeb);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void onViewClick(int viewId, int position)
    {
        if(viewId == R.id.rc_video)
        {
            if(!recommendVideoArrayList.get(position).video_aid.equals(""))
            {
                Intent intent = new Intent(ctx, VideodetailsActivity.class);
                intent.putExtra("aid", recommendVideoArrayList.get(position).video_aid);
                startActivity(intent);
            }
        }
        else if(viewId == R.id.widget_recommend_update_lay)
        {
            uiListView.smoothScrollToPositionFromTop(0, 0);
            uiWaveSwipeRefreshLayout.setRefreshing(true);
            isLoading = true;
            recommendVideoArrayList.remove(position);
            getRecommend(1);
        }
    }
}
