package cn.luern0313.wristbilibili.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by liupe on 2018/11/13.
 * 不给title的api！！！
 * ...
 * ...
 * ...我错了，有这个api，我自罚重写
 */

public class VideoDetails
{
    private String cookie;
    private String csrf;
    private String mid;
    private String aid;

    private JSONObject videoJSON;
    private JSONObject videoUserJson;
    private JSONObject videoViewJson;
    private JSONArray videoReplyJson;
    private int isLiked;//0,1喜欢,2不喜欢
    private int isCoined;//已投个数
    private boolean isFaved;

    private int beLiked;//喜欢总数
    private int beCoined;//硬币总数
    private int beFaved;//收藏总数

    public VideoDetails(String cookie, String csrf, String mid, String aid)
    {
        this.cookie = cookie;
        this.csrf = csrf;
        this.mid = mid;
        this.aid = aid;
    }

    public boolean getVideoDetails() throws IOException
    {
        try
        {
            videoJSON = new JSONObject((String) get("https://api.bilibili.com/x/web-interface/view/detail?aid=" + aid, 1));
            if(videoJSON.getInt("code") == -404) return false;
            videoUserJson = videoJSON.getJSONObject("data").getJSONObject("Card");
            videoViewJson = videoJSON.getJSONObject("data").getJSONObject("View");
            isLiked = new JSONObject((String) get("https://api.bilibili.com/x/web-interface/archive/has/like?aid=" + aid, 1)).getInt("data");
            isCoined = new JSONObject((String) get("https://api.bilibili.com/x/web-interface/archive/coins?aid=" + aid, 1)).getJSONObject("data").getInt("multiply");
            isFaved = new JSONObject((String) get("https://api.bilibili.com/x/v2/fav/video/favoured?aid=" + aid, 1)).getJSONObject("data").getBoolean("favoured");

            beLiked = (int) getInfoFromJson(getJsonFromJson(videoViewJson, "stat"), "like");
            beCoined = (int) getInfoFromJson(getJsonFromJson(videoViewJson, "stat"), "coin");
            beFaved = (int) getInfoFromJson(getJsonFromJson(videoViewJson, "stat"), "favorite");
            return true;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public String getVideoTitle()
    {
        return (String) getInfoFromJson(videoViewJson, "title");
    }

    public int getVideoCopyright()
    {
        return (int) getInfoFromJson(videoViewJson, "copyright");
    }

    private String getVideoCid()
    {
        return String.valueOf(videoViewJson.optJSONArray("pages").optJSONObject(0).optInt("cid"));
    }

    private int getVideoDuration()
    {
        return videoViewJson.optInt("duration");
    }

    public String getVideoUpAid()
    {
        return (String) getInfoFromJson(getJsonFromJson(videoUserJson, "card"), "mid");
    }

    public String getVideoUpName()
    {
        return (String) getInfoFromJson(getJsonFromJson(videoUserJson, "card"), "name");
    }

    public String getVideoUpSign()
    {
        return (String) getInfoFromJson(getJsonFromJson(videoUserJson, "card"), "sign");
    }

    public Bitmap getVideoUpFace() throws IOException
    {
        return (Bitmap) get((String) getInfoFromJson(getJsonFromJson(videoUserJson, "card"), "face"), 2);
    }

    public String getVideoFace()
    {
        return (String) getInfoFromJson(videoViewJson, "pic");
    }

    public boolean isFollowing()
    {
        return (boolean) getInfoFromJson(videoUserJson, "following");
    }

    public String getVideoPlay()
    {
        int view = (int) getInfoFromJson(getJsonFromJson(videoViewJson, "stat"), "view");
        if(view > 10000) return view / 1000 / 10.0 + "万";
        else return String.valueOf(view);
    }

    public String getVideoDanmaku()
    {
        return String.valueOf(getInfoFromJson(getJsonFromJson(videoViewJson, "stat"), "danmaku"));
    }

    public String getVideoupTime()
    {
        Date date = new Date((int) getInfoFromJson(videoViewJson, "pubdate") * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    public String getVideoReply() throws IOException
    {
        try
        {
            videoReplyJson = videoJSON.optJSONObject("data").optJSONObject("Reply").optJSONArray("replies");
            StringBuilder reply = new StringBuilder("<b>热门回复：</b>");
            if(videoReplyJson.length() == 0) return "<b>暂无热门回复</b>";
            for (int i = 0; i < videoReplyJson.length(); i++)
            {
                JSONObject replyJson = videoReplyJson.optJSONObject(i);
                OthersUser othersUser = new OthersUser(cookie, csrf, String.valueOf(replyJson.optInt("mid")));
                reply.append("<br/><br/><b>").append(new JSONObject(othersUser.getOtheruserInfo()).optJSONObject("data").optJSONObject("card").optString("name")).append("：</b>");
                reply.append(replyJson.optJSONObject("content").optString("message"));
            }
            return reply.toString();
        }
        catch (JSONException | NullPointerException e)
        {
            return "<b>暂无热门回复<b/>";
        }
    }

    public String getVideoAid()
    {
        return aid;
    }

    public String getVideoDetail()
    {
        return (String) getInfoFromJson(videoViewJson, "desc");
    }

    public String getVideoLike()
    {
        return String.valueOf(beLiked);
    }

    public String getVideoCoin()
    {
        return String.valueOf(beCoined);
    }

    public String getVideoFav()
    {
        return String.valueOf(beFaved);
    }

    public int getSelfLiked()
    {
        return isLiked;
    }

    public int getSelfCoined()
    {
        return isCoined;
    }

    public boolean getSelfFaved()
    {
        return isFaved;
    }

    public void setSelfLiked(int w)
    {
        this.beLiked += w;
    }

    public void setSelfCoined(int w)
    {
        this.beCoined += w;
    }

    public void setSelfFaved(int w)
    {
        this.beFaved += w;
    }

    public boolean likeVideo(int mode) throws IOException  //1好评，2取消差评，3差评，4取消差评，后一个会覆盖前一个
    {
        try
        {
            if(post("https://api.bilibili.com/x/web-interface/archive/like", "aid=" + aid + "&like=" + mode + "&csrf=" + csrf).body().string().equals("{\"code\":0,\"message\":\"0\",\"ttl\":1}"))
                return true;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean coinVideo(int how) throws IOException
    {
        try
        {
            if(post("https://api.bilibili.com/x/web-interface/coin/add", "aid=" + aid + "&multiply=" + how + "&cross_domain=true&csrf=" + csrf).body().string().equals("{\"code\":0,\"message\":\"0\",\"ttl\":1}"))
                return true;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean favVideo() throws IOException
    {
        try
        {
            if(post("https://api.bilibili.com/x/v2/fav/video/add", "aid=" + aid + "&csrf=" + csrf).body().string().equals("{\"code\":0,\"message\":\"0\",\"ttl\":1}"))
                return true;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean favCancalVideo() throws IOException
    {
        try
        {
            if(post("https://api.bilibili.com/x/v2/fav/video/del", "aid=" + aid + "&csrf=" + csrf).body().string().equals("{\"code\":0,\"message\":\"0\",\"ttl\":1}"))
                return true;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean playLater() throws IOException
    {
        try
        {
            if(post("https://api.bilibili.com/x/v2/history/toview/add", "aid=" + aid + "&csrf=" + csrf).body().string().equals("{\"code\":0,\"message\":\"0\",\"ttl\":1}"))
                return true;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean playHistory() throws IOException
    {
        try
        {
            if(post("https://api.bilibili.com/x/report/web/heartbeat", "aid=" + aid + "&cid=" + getVideoCid() + "&mid=" + mid + "&csrf=" + csrf + "&played_time=0&realtime=0&start_ts=" + (System.currentTimeMillis() / 1000) + "&type=3&dt=2&play_type=1").body().string().equals("{\"code\":0,\"message\":\"0\",\"ttl\":1}"))
                return true;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private Object getInfoFromJson(JSONObject json, String get)
    {
        try
        {
            return json.get(get);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getJsonFromJson(JSONObject json, String get)
    {
        try
        {
            return json.getJSONObject(get);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private Object get(String url, int mode) throws IOException
    {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).build();
        Request.Builder requestb = new Request.Builder().url(url).header("Referer", "https://www.bilibili.com/").addHeader("Accept", "*/*").addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        if(!cookie.equals("")) requestb.addHeader("Cookie", cookie);
        Request request = requestb.build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful())
        {
            if(mode == 1) return response.body().string();
            else if(mode == 2)
            {
                byte[] buffer = readStream(response.body().byteStream());
                return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            }
        }
        return null;
    }

    private Response post(String url, String data) throws IOException
    {
        Response response;
        OkHttpClient client;
        RequestBody body;
        Request request;
        client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).build();
        body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), data);
        request = new Request.Builder().url(url).post(body).header("Referer", "https://www.bilibili.com/").addHeader("Accept", "*/*").addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)").addHeader("Referer", "https://passport.bilibili.com/login").addHeader("Cookie", cookie).build();
        response = client.newCall(request).execute();
        if(response.isSuccessful())
        {
            return response;
        }
        return null;
    }

    private byte[] readStream(InputStream inStream) throws IOException
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1)
        {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }
}
