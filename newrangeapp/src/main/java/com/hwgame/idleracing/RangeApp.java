package com.hwgame.idleracing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;


import androidx.annotation.RequiresApi;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.util.UriConfig;
import com.unity3d.player.UnityPlayerActivity;

import java.util.UUID;


public class RangeApp extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("【RangerApp】", "TryInit");
        /* 初始化开始 */
        // appid和渠道，appid须保证与⼴告后台申请记录⼀致，渠道可⾃定义，如有多个⻢甲包建议 设置渠道号唯⼀标识⼀个⻢甲包。
        final InitConfig config = new InitConfig("182737", "bytedance");

       /*  域名默认国内: DEFAULT, 新加坡:SINGAPORE, 美东:AMERICA
         注意：国内外不同vendor服务注册的did不⼀样。由DEFAULT切换到SINGAPORE或者AMERICA，
        会发⽣变化，
        切回来也会发⽣变化。因此vendor的切换⼀定要慎重，随意切换导致⽤⼾新增和统计的问题，需
        要⾃⾏评估。*/

        // 是否在控制台输出⽇志，可⽤于观察⽤⼾⾏为⽇志上报情况，建议仅在调试时使⽤，release版
        //本请设置为false ！
        String uuid = android.os.Build.SERIAL;
        config.setUriConfig(UriConfig.DEFAULT);
        AppLog.setEnableLog(true);
        config.setEnablePlay(true);
        AppLog.setUserUniqueID(uuid);
        AppLog.init(this, config);
        Log.d("【RangerApp】", "Init");
    }

    //传事件
    public void LogEvent6Hour() {
        AppLog.onEventV3("day1_retention");
        Log.e("【RangerAppLog】", "day1_retention");
    }

    public void LogEvent24Hour() {
        AppLog.onEventV3("day1_retention_24h");
        Log.e("【RangerAppLog】", "day1_retention_24h");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("HardwareIds")
    private String getMyUUID() {

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return "";
            }
        }
        tmDevice = "" + tm.getDeviceId();
    	tmSerial = "" + tm.getSimSerialNumber();

    	 androidId = "" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

    	UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());

    	String uniqueId = deviceUuid.toString();
    	Log.d("debug","uuid="+uniqueId);

    	 return uniqueId;

    	}

}

