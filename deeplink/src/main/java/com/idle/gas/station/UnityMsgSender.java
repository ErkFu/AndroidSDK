package com.idle.gas.station;

import com.unity3d.player.UnityPlayer;

public class UnityMsgSender {

    private static String sReceiveGameObjectName = "SdkMsgRecver";

    public static void SendMsg(String msg) {
        UnityPlayer.UnitySendMessage(sReceiveGameObjectName, "OnJavaMsg", msg);
    }
}
