package cn.luern0313.wristbilibili.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.luern0313.wristbilibili.R;
import cn.luern0313.wristbilibili.models.AnimationTimelineModel;
import cn.luern0313.wristbilibili.util.ImageTaskUtil;

/**
 * 被 luern0313 创建于 2020/1/9.
 */

public class AnimationTimelineAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;

    private LruCache<String, BitmapDrawable> mImageCache;
    private AnimationTimelineListener animationTimelineListener;

    private ArrayList<AnimationTimelineModel> arList;
    private ListView listView;

    public AnimationTimelineAdapter(LayoutInflater inflater, ArrayList<AnimationTimelineModel> arList, ListView listView, AnimationTimelineListener animationTimelineListener)
    {
        mInflater = inflater;
        this.arList = arList;
        this.listView = listView;
        this.animationTimelineListener = animationTimelineListener;

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
        return arList.size();
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
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        AnimationTimelineModel anim = arList.get(position);
        ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.item_aniremind, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.lay = convertView.findViewById(R.id.anre_lay);
            viewHolder.img = convertView.findViewById(R.id.anre_img);
            viewHolder.name = convertView.findViewById(R.id.anre_name);
            viewHolder.isfollow = convertView.findViewById(R.id.anre_isfollow);
            viewHolder.last = convertView.findViewById(R.id.anre_last);
            viewHolder.time = convertView.findViewById(R.id.anre_time);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.lay.setOnClickListener(onViewClick(position));
        viewHolder.name.setText(anim.name);
        viewHolder.last.setText("更新至" + anim.lastEpisode);
        viewHolder.img.setImageResource(R.drawable.img_default_animation);
        viewHolder.time.setText(anim.time);

        if(anim.isfollow == 1) viewHolder.isfollow.setVisibility(View.VISIBLE);
        else viewHolder.isfollow.setVisibility(View.GONE);

        viewHolder.img.setTag(anim.coverUrl);
        BitmapDrawable c = setImageFormWeb(anim.coverUrl);
        if(c != null) viewHolder.img.setImageDrawable(c);
        return convertView;
    }

    class ViewHolder
    {
        RelativeLayout lay;
        ImageView img;
        TextView name;
        TextView isfollow;
        TextView last;
        TextView time;
    }

    private View.OnClickListener onViewClick(final int position)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animationTimelineListener.onClick(v.getId(), position);
            }
        };
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

    private View.OnClickListener onViewClick(final int position, final int mode)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animationTimelineListener.onClick(v.getId(), position);
            }
        };
    }

    public interface AnimationTimelineListener
    {
        void onClick(int viewId, int position);
    }
}
