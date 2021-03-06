package cn.luern0313.wristbilibili.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.luern0313.wristbilibili.R;
import cn.luern0313.wristbilibili.models.BangumiRecommendModel;
import cn.luern0313.wristbilibili.util.ImageTaskUtil;

/**
 * 被 luern0313 创建于 2020/1/31.
 */

public class BangumiRecommendAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;

    private LruCache<String, BitmapDrawable> mImageCache;

    private ArrayList<BangumiRecommendModel> bangumiRecommendModelArrayList;
    private ListView listView;

    public BangumiRecommendAdapter(LayoutInflater inflater,  ListView listView, ArrayList<BangumiRecommendModel> bangumiRecommendModelArrayList)
    {
        mInflater = inflater;
        this.bangumiRecommendModelArrayList = bangumiRecommendModelArrayList;
        this.listView = listView;

        int maxCache = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxCache / 8;
        mImageCache = new LruCache<String, BitmapDrawable>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, BitmapDrawable value)
            {
                try
                {
                    return value.getBitmap().getByteCount();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return 0;
            }
        };
    }

    @Override
    public int getCount()
    {
        return bangumiRecommendModelArrayList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        BangumiRecommendModel bangumiRecommendModel = bangumiRecommendModelArrayList.get(position);
        ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.item_bangumi_recommend_bangumi, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.cover = convertView.findViewById(R.id.item_bgm_recommend_bangumi_img);
            viewHolder.title = convertView.findViewById(R.id.item_bgm_recommend_bangumi_title);
            viewHolder.play = convertView.findViewById(R.id.item_bgm_recommend_bangumi_play);
            viewHolder.follow = convertView.findViewById(R.id.item_bgm_recommend_bangumi_follow);
            viewHolder.newep = convertView.findViewById(R.id.item_bgm_recommend_bangumi_new);
            viewHolder.score = convertView.findViewById(R.id.item_bgm_recommend_bangumi_score);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.cover.setImageResource(R.drawable.img_default_vid);
        viewHolder.title.setText(bangumiRecommendModel.bangumi_title);
        viewHolder.play.setText(bangumiRecommendModel.bangumi_play);
        viewHolder.follow.setText(bangumiRecommendModel.bangumi_follow);
        viewHolder.newep.setText(bangumiRecommendModel.bangumi_new);
        viewHolder.score.setText(bangumiRecommendModel.bangumi_score);

        Drawable playNumDrawable = convertView.getResources().getDrawable(R.drawable.icon_video_play_num);
        Drawable likeNumDrawable = convertView.getResources().getDrawable(R.drawable.icon_video_like_num);
        playNumDrawable.setBounds(0,0,24,24);
        likeNumDrawable.setBounds(0,0,24,24);
        viewHolder.play.setCompoundDrawables(playNumDrawable,null, null,null);
        viewHolder.follow.setCompoundDrawables(likeNumDrawable,null, null,null);

        viewHolder.cover.setTag(bangumiRecommendModel.bangumi_cover);
        BitmapDrawable c = setImageFormWeb(bangumiRecommendModel.bangumi_cover);
        if(c != null) viewHolder.cover.setImageDrawable(c);
        return convertView;
    }

    class ViewHolder
    {
        ImageView cover;
        TextView title;
        TextView play;
        TextView follow;
        TextView newep;
        TextView score;
    }

    private BitmapDrawable setImageFormWeb(String url)
    {
        if(mImageCache.get(url) != null)
        {
            return mImageCache.get(url);
        }
        else
        {
            ImageTaskUtil it = new ImageTaskUtil(listView, mImageCache);
            it.execute(url);
            return null;
        }
    }
}
