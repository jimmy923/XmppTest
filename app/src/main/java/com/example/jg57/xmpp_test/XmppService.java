package com.example.jg57.xmpp_test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import org.jivesoftware.smack.packet.Message;

public class XmppService extends Service {

    private ClientConServer ccs;
    private SharedPreferences setting;
    private String userName,Pwd;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        ccs = new ClientConServer();
        //先查找SharedPreferences是否有登入資訊
        setting = getSharedPreferences("LoginData" ,0);
        userName = setting.getString("userName", "");
        Pwd = setting.getString("Pwd","");
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!ccs.hasConnection()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean ok = ccs.login(userName, Pwd);
                    if (ok) {
                        setting.edit()
                               .putString("userName", userName)
                               .putString("Pwd", Pwd)
                               .apply();
                        ccs.setManagerListener(handler);
                    }
                }
            }).start();
        }else{
            ccs.setManagerListener(handler);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Message message = (Message) msg.obj;
            String[] msgList = new String[]{
                    message.getFrom(),
                    message.getBody()
            };
            //設定Notification
            NotificationManager barManger = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder barMsg = new Notification.Builder(context)
                    .setTicker("你有一封新Message")
                    .setContentTitle("你有一封新Message")
                    .setContentText(msgList[1])
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent);
            barManger.notify(0,barMsg.build());
        }
    };
}
