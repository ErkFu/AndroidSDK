package com.sdk.rangersapp;

import android.app.Activity;
import android.util.Log;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.util.UriConfig;


public class RangersApp {


    /**
     * unity项目启动时的的上下文
     */
    private static Activity _unityActivity;

    /**
     * 获取unity项目的上下文
     *
     * @return
     */
    private static Activity getActivity() {
        if (null == _unityActivity) {
            try {
                Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
                Activity activity = (Activity) classtype.getDeclaredField("currentActivity").get(classtype);
                _unityActivity = activity;
            } catch (ClassNotFoundException e) {

            } catch (IllegalAccessException e) {

            } catch (NoSuchFieldException e) {

            }
        }
        return _unityActivity;
    }

    //初始化
    public static void Init() {
        /* 初始化开始 */
        Log.e("【RangerApp】", "TryInit");
        // appid和渠道，appid须保证与⼴告后台申请记录⼀致，渠道可⾃定义，如有多个⻢甲包建议 设置渠道号唯⼀标识⼀个⻢甲包。
        final InitConfig config = new InitConfig("182737", "bytedance");

       /*  域名默认国内: DEFAULT, 新加坡:SINGAPORE, 美东:AMERICA
         注意：国内外不同vendor服务注册的did不⼀样。由DEFAULT切换到SINGAPORE或者AMERICA，
        会发⽣变化，
        切回来也会发⽣变化。因此vendor的切换⼀定要慎重，随意切换导致⽤⼾新增和统计的问题，需
        要⾃⾏评估。*/

        // 是否在控制台输出⽇志，可⽤于观察⽤⼾⾏为⽇志上报情况，建议仅在调试时使⽤，release版
        //本请设置为false ！

        config.setUriConfig(UriConfig.DEFAULT);
        config.setEnablePlay(true);
        AppLog.setEnableLog(true);
        AppLog.init(getActivity(), config);
        Log.e("【RangerApp】", "Init");
    }

    //传事件
    public static void LogEvent6Hour() {
        AppLog.onEventV3("day1_retention");
        Log.d("【RangerAppLog】","day1_retention" );
    }

    public static void LogEvent24Hour() {
        AppLog.onEventV3("day1_retention_24h");
        Log.d("【RangerAppLog】","day1_retention_24h" );
    }
}
