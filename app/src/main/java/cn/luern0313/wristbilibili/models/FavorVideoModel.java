package cn.luern0313.wristbilibili.models;

import org.json.JSONObject;

/**
 * 被 luern0313 创建于 2020/2/2.
 */

public class FavorVideoModel
{
    public String video_aid;
    public String video_title;
    public String video_cover;
    public String video_play;
    public String video_danmaku;
    public String video_owner_name;
    public FavorVideoModel(JSONObject video)
    {
        video_aid = String.valueOf(video.optInt("aid"));
        video_title = video.optString("title");
        video_cover = video.optString("pic");

        JSONObject video_recommend_video_stat = video.has("stat") ? video.optJSONObject("stat") : new JSONObject();
        video_play = getView(video_recommend_video_stat.optInt("view"));
        video_danmaku = getView(video_recommend_video_stat.optInt("danmaku"));

        JSONObject video_recommend_video_owner = video.has("owner") ? video.optJSONObject("owner") : new JSONObject();
        video_owner_name = video_recommend_video_owner.optString("name");
    }

    private static String getView(int view)
    {
        if(view > 10000) return view / 1000 / 10.0 + "万";
        else return String.valueOf(view);
    }
}
