package cn.luern0313.wristbilibili.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.luern0313.wristbilibili.R;
import cn.luern0313.wristbilibili.models.BangumiModel;

/**
 * 被 luern0313 创建于 2020/1/30.
 */

public class VideoPartAdapter extends RecyclerView.Adapter<VideoPartAdapter.ViewHolder>
{
    private OnItemClickListener itemClickLitener;
    private ArrayList<BangumiModel.BangumiEpisodeModel> bangumiEpisodeModelArrayList;
    private BangumiModel bangumiModel;
    private int mode;

    public VideoPartAdapter(ArrayList<BangumiModel.BangumiEpisodeModel> bangumiEpisodeModelArrayList, BangumiModel bangumiModel, int mode)
    {
        this.bangumiEpisodeModelArrayList = bangumiEpisodeModelArrayList;
        this.bangumiModel = bangumiModel;
        this.mode = mode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vd_videopart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        BangumiModel.BangumiEpisodeModel bangumiEpisodeModel = bangumiEpisodeModelArrayList.get(position);
        holder.text.setText(mode == 1 ? "第" + bangumiEpisodeModel.bangumi_episode_title + "话\n" +
                bangumiEpisodeModel.bangumi_episode_title_long : (bangumiEpisodeModel.bangumi_episode_title_long.equals("") ?
                bangumiEpisodeModel.bangumi_episode_title : bangumiEpisodeModel.bangumi_episode_title_long));
        holder.vip.setVisibility(bangumiEpisodeModel.bangumi_episode_vip.equals("") ? View.GONE : View.VISIBLE);
        holder.lay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                itemClickLitener.onItemClick(v, position);
            }
        });

        if(bangumiModel.bangumi_user_progress_mode == mode && bangumiModel.bangumi_user_progress_position == position)
            holder.lay.setBackgroundResource(R.drawable.selector_bg_bangumi_episode_now);

        else
            holder.lay.setBackgroundResource(R.drawable.selector_bg_bangumi_episode);
    }

    @Override
    public int getItemCount()
    {
        return bangumiEpisodeModelArrayList.size();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.itemClickLitener = mOnItemClickListener;
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout lay;
        TextView text;
        TextView vip;
        public ViewHolder(View itemView)
        {
            super(itemView);
            lay = itemView.findViewById(R.id.item_video_part);
            text = itemView.findViewById(R.id.item_video_part_text);
            vip = itemView.findViewById(R.id.item_video_part_vip);
        }
    }
}
