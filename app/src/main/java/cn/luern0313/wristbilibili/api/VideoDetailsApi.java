package cn.luern0313.wristbilibili.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import cn.luern0313.wristbilibili.models.VideoModel;
import cn.luern0313.wristbilibili.util.NetWorkUtil;

/**
 * Created by liupe on 2018/11/13.
 * 不给title的api！！！
 * ...
 * ...
 * ...我错了，有这个api，我自罚重写
 */

public class VideoDetailsApi
{
    private String cookie;
    private String csrf;
    private String mid;
    private String access_key;
    public String aid;
    private ArrayList<String> appHeaders = new ArrayList<>();
    private ArrayList<String> webHeaders = new ArrayList<String>();

    private VideoModel videoModel;

    public VideoDetailsApi(final String cookie, String csrf, String mid, String access_key, String aid)
    {
        this.cookie = cookie;
        this.csrf = csrf;
        this.mid = mid;
        this.access_key = access_key;
        this.aid = aid;
        appHeaders = new ArrayList<String>(){{
            add("Cookie"); add(cookie);
            add("User-Agent"); add(ConfInfoApi.USER_AGENT_OWN);
        }};
        webHeaders = new ArrayList<String>(){{
            add("Cookie"); add(cookie);
            add("Referer"); add("https://www.bilibili.com/anime");
            add("User-Agent"); add(ConfInfoApi.USER_AGENT_WEB);
        }};
    }

    public VideoModel getVideoDetails() throws IOException
    {
        try
        {
            String url = "https://app.bilibili.com/x/v2/view";
            String temp_per = "access_key=" + access_key + "&aid=" + aid + "&appkey=" + ConfInfoApi.getConf("appkey") +
                    "&build=" + ConfInfoApi.getConf("build") + "&mobi_app=" + ConfInfoApi.getConf("mobi_app") +
                    "&plat=0&platform=" + ConfInfoApi.getConf("platform") + "&ts=" + (int) (System.currentTimeMillis() / 1000);
            String sign = ConfInfoApi.calc_sign(temp_per);
            JSONObject result = new JSONObject(NetWorkUtil.get(url + "?" + temp_per + "&sign=" + sign, appHeaders).body().string());
            if(result.optInt("code") == 0)
            {
                videoModel = new VideoModel(result.optJSONObject("data"));
                return videoModel;
            }
        }
        catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String likeVideo(int mode) throws IOException  //1好评，2取消差评，3差评，4取消差评，后一个会覆盖前一个
    {
        try
        {
            String url = "https://api.bilibili.com/x/web-interface/archive/like";
            String per = "aid=" + aid + "&like=" + mode + "&csrf=" + csrf;
            JSONObject result = new JSONObject(NetWorkUtil.post(url, per, webHeaders).body().string());
            if(result.optInt("code") == 0)
                return "";
        }
        catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return "未知错误";
    }

    public String coinVideo(int how) throws IOException
    {
        try
        {
            String url = "https://api.bilibili.com/x/web-interface/coin/add";
            String per = "aid=" + aid + "&multiply=" + how + "&cross_domain=true&csrf=" + csrf;
            JSONObject result = new JSONObject(NetWorkUtil.post(url, per, webHeaders).body().string());
            if(result.optInt("code") == 0)
                return "";
        }
        catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return "未知错误";
    }

    public String favVideo(String favId) throws IOException
    {
        try
        {
            String url = "https://api.bilibili.com/medialist/gateway/coll/resource/deal";
            String per = "rid=" + aid + "&type=2&add_media_ids=" + favId + "&del_media_ids=&csrf=" + csrf;
            JSONObject result = new JSONObject(NetWorkUtil.post(url, per, webHeaders).body().string());
            if(result.optInt("code") == 0)
                return "";
            else
                return result.optString("message");
        }
        catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return "未知错误";
    }

    public String playLater() throws IOException
    {
        try
        {
            String url = "https://api.bilibili.com/x/v2/history/toview/add";
            String per = "aid=" + aid + "&csrf=" + csrf;
            JSONObject result = new JSONObject(NetWorkUtil.post(url, per, webHeaders).body().string());
            if(result.optInt("code") == 0)
                return "";
        }
        catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return "未知错误";
    }

    public String playHistory() throws IOException
    {
        try
        {
            String url = "https://api.bilibili.com/x/report/web/heartbeat";
            String per = "aid=" + aid + "&cid=" + videoModel.video_cid + "&mid=" + mid + "&csrf=" + csrf + "&played_time=0&realtime=0&start_ts=" + (System.currentTimeMillis() / 1000) + "&type=3&dt=2&play_type=1";
            JSONObject result = new JSONObject(NetWorkUtil.post(url, per, webHeaders).body().string());
            if(result.optInt("code") == 0)
                return "";
        }
        catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return "未知错误";
    }

    public String shareVideo(String text) throws IOException
    {
        try
        {
            String url = "https://api.vc.bilibili.com/dynamic_repost/v1/dynamic_repost/share";
            String per = "csrf_token=" + csrf + "&platform=pc&uid=" + videoModel.video_up_mid + "&type=8&share_uid=" + mid + "&content=" + URLEncoder.encode(text, "UTF-8") + "&repost_code=20000&rid=" + videoModel.video_aid;
            JSONObject result = new JSONObject(NetWorkUtil.post(url, per, webHeaders).body().string());
            if(result.optInt("code") == 0)
                return "";
        }
        catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return "未知错误";
    }

    public String scoreVideo(int score) throws IOException
    {
        try
        {
            String url = "https://api.bilibili.com/x/stein/mark";
            String per = "aid=" + aid + "&mark=" + score + "&csrf=" + csrf;
            JSONObject result = new JSONObject(NetWorkUtil.post(url, per, webHeaders).body().string());
            if(result.optInt("code") == 0)
                return "";
        }
        catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return "未知错误";
    }

    public String sendReply(String text) throws IOException
    {
        try
        {
            String url = "https://api.bilibili.com/x/v2/reply/add";
            String per = "oid=" + aid + "&type=1&message=" + text + "&plat=1&jsonp=jsonp&csrf=" + csrf;
            JSONObject result = new JSONObject(NetWorkUtil.post(url, per, webHeaders).body().string());
            if(result.optInt("code") == 0)
                return "";
        }
        catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return "未知错误";
    }
}
