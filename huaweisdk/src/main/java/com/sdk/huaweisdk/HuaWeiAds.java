package com.sdk.huaweisdk;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.VideoConfiguration;
import com.huawei.hms.ads.VideoOperator;
import com.huawei.hms.ads.nativead.DislikeAdListener;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.reward.Reward;
import com.huawei.hms.ads.reward.RewardAd;
import com.huawei.hms.ads.reward.RewardAdLoadListener;
import com.huawei.hms.ads.reward.RewardAdStatusListener;

import android.view.ViewGroup.LayoutParams;

import androidx.annotation.RequiresApi;


public class HuaWeiAds {


    /**
     * unity项目启动时的的上下文
     */
    private static Activity _unityActivity;

    private static String GameObjName = "AdCallBack";

    private static Activity adsActivity;

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

    /**
     * 调用Unity的方法
     *
     * @param gameObjectName 调用的GameObject的名称
     * @param functionName   方法名
     * @param args           参数
     * @return 调用是否成功
     */
    private static boolean callUnity(String gameObjectName, String functionName, String args) {
        try {
            Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
            Method method = classtype.getMethod("UnitySendMessage", String.class, String.class, String.class);
            method.invoke(classtype, gameObjectName, functionName, args);
            return true;
        } catch (ClassNotFoundException e) {

        } catch (NoSuchMethodException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
        return false;
    }

    private static RewardAd rewardAd;

    public static void InitApp() {
        HwAds.init(getActivity());

    }

    /**
     * 加载激励广告
     */
    public static void loadRewardAd(String AD_ID) {
        if (rewardAd == null) {
            rewardAd = new RewardAd(getActivity(), AD_ID);
        }
        RewardAdLoadListener listener = new RewardAdLoadListener() {
            @Override
            public void onRewardedLoaded() {
                // 激励广告加载成功
                callUnity(GameObjName, "RewardSuccessLoad", "");
            }

            @Override
            public void onRewardAdFailedToLoad(int errorCode) {
                // 激励广告加载失败
                callUnity(GameObjName, "RewardFailedLoad", String.valueOf(errorCode));
            }
        };
        rewardAd.loadAd(new AdParam.Builder().build(), listener);
    }

    /**
     * 展示激励广告
     */
    public static void rewardAdShow() {
        if (rewardAd.isLoaded()) {
            rewardAd.show(getActivity(), new RewardAdStatusListener() {
                @Override
                public void onRewardAdOpened() {
                    // 激励广告被打开
                    callUnity(GameObjName, "RewardAdOpen", "");
                }

                @Override
                public void onRewardAdFailedToShow(int errorCode) {
                    // 激励广告展示失败
                    callUnity(GameObjName, "RewardAdFailedShow", String.valueOf(errorCode));
                }

                @Override
                public void onRewardAdClosed() {
                    // 激励广告被关闭
                    callUnity(GameObjName, "RewardAdClose", "");
                }

                @Override
                public void onRewarded(Reward reward) {
                    // 激励广告奖励达成
                    // TODO 发放奖励
                    callUnity(GameObjName, "RewardUser", String.valueOf(reward.getAmount()));
                }
            });
        }
    }

    private static NativeAd globalNativeAd;
    private static NativeAd nativeAd;
    private static int layoutId;


    private static final int COMPLETED = 1001;


    private static VideoOperator.VideoLifecycleListener videoLifecycleListener = new VideoOperator.VideoLifecycleListener() {
        @Override
        public void onVideoStart() {
            Log.d("【huaweiAds】", "onVideoStart: ");
        }

        @Override
        public void onVideoPlay() {
            Log.d("【huaweiAds】", "onVideoPlay: ");
        }

        @Override
        public void onVideoEnd() {
            // If there is a video, load a new native ad only after video playback is complete.
            Log.d("【huaweiAds】", "onVideoEnd: ");
        }
    };

    /**
     * 加载原生视频
     *
     * @param adId ad slot ID.
     */
    public static void loadAd(String adId) {
        layoutId = R.layout.native_video_template;
        NativeAdLoader.Builder builder = new NativeAdLoader.Builder(getActivity(), adId);

        builder.setNativeAdLoadedListener(new NativeAd.NativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd _nativeAd) {
                // Call this method when an ad is successfully loaded.
                callUnity(GameObjName, "OnInterSuccessLoad", "");
                nativeAd = _nativeAd;
                nativeAd.setDislikeAdListener(new DislikeAdListener() {
                    @Override
                    public void onAdDisliked() {
                        // Call this method when an ad is closed.
                        callUnity(GameObjName, "OnInterClose", "");
                    }
                });
            }
        }).setAdListener(new AdListener() {
            @Override
            public void onAdFailed(int errorCode) {
                // Call this method when an ad fails to be loaded.
                callUnity(GameObjName, "OnInterFailedLoad", String.valueOf(errorCode));
            }
        });

        VideoConfiguration videoConfiguration = new VideoConfiguration.Builder()
                // 设置是否在静音状态下开始播放视频素材。其默认值为true。
                .setStartMuted(false)
                .build();
        NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                .setVideoConfiguration(videoConfiguration)// 设置视频选项
                .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT) // Set custom attributes.
                .build();

        NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();


        nativeAdLoader.loadAd(new AdParam.Builder().build());
    }


    /**
     * 展示原生视频
     */
    private static void showNativeAd() {
        // Destroy the original native ad.
        if (nativeAd == null) {
            Log.e("HuaWeiAds", "nativeAd is null!");
            return;
        }
        getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (null != globalNativeAd) {
                            globalNativeAd.destroy();
                        }
                        globalNativeAd = nativeAd;

                        // Obtain NativeView.
                        NativeView nativeView = (NativeView) getActivity().getLayoutInflater().inflate(layoutId, null);

                        // Register and populate a native ad material view.
                        initNativeAdView(globalNativeAd, nativeView);
                        LinearLayout layout = new LinearLayout(getActivity().getApplicationContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setGravity(Gravity.CENTER);
                        getActivity().addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                        layout.removeAllViews();
                        layout.addView(nativeView);
                    }
                });
    }

    private static void initNativeAdView(NativeAd nativeAd, NativeView nativeView) {
        // Register a native ad material view.
        nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
        nativeView.setMediaView((MediaView) nativeView.findViewById(R.id.ad_media));
        nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
        nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));


        // Populate a native ad material view.
        ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (null != nativeAd.getAdSource()) {
            ((TextView) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }
        nativeView.getAdSourceView()
                .setVisibility(null != nativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);

        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeView.getChoicesView() .setVisibility(null != nativeAd.getChoicesInfo() ? View.VISIBLE : View.INVISIBLE);

        // Obtain a video controller.
        VideoOperator videoOperator = nativeAd.getVideoOperator();

        // Check whether a native ad contains video materials.
        if (videoOperator.hasVideo()) {
            // Add a video lifecycle event listener.
            Log.d("【huaweiAds】", "hasVideo: ");
            videoOperator.setVideoLifecycleListener(videoLifecycleListener);
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);
    }

}
