package com.example.jg57.xmpp_test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by Jimmy.S.Guo on 2015/6/5.
 */
public class ClientConServer {
    private final static int XmppPORT = 5222;
    private static Connection connection;
    private static String userName;
    private static Handler handler;

    public boolean login(String userName,String Pwd){
        //連接的網址和PORT
        ConnectionConfiguration config = new ConnectionConfiguration("10.16.211.80", XmppPORT);
        //關閉SSL驗證、並設定自動連接和獲取離線訊息
        config.setSASLAuthenticationEnabled(false);
        config.setReconnectionAllowed(true);
        config.setSendPresence(true);
        connection = new XMPPConnection(config);
        try{
            connection.connect();
            connection.login(userName,Pwd);
            ClientConServer.userName = userName;
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    //寄送訊息
    public void sendMsg(String to ,String msg) throws XMPPException {
        //設定要寄送的對象
        Chat myChat = connection.getChatManager().createChat(to + "@" + connection.getServiceName() , new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                android.os.Message m = handler.obtainMessage();
                m.obj = message;
                m.sendToTarget();
            }
        });
        Message message = new Message();
        message.setBody(msg);
//        String[] msgList = new String[]{
//            userName,
//            msg
//        };
        //((MainActivity)context).writeMsg(msgList);
        myChat.sendMessage(message);
    }

    //查詢是否登入
    public boolean hasConnection(){
        return connection != null && connection.isConnected();
    }

    //登出
    public void Logout(){
        connection.disconnect();
    }


    public void setManagerListener(Handler handler){
        ClientConServer.handler = handler;
        ChatManager chatManager = connection.getChatManager();
        //設定新訊息監聽
        chatManager.addChatListener(new MyChatManagerListener());
    }

    class MyChatManagerListener implements ChatManagerListener {
        public void chatCreated(Chat chat, boolean arg){
            chat.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    //接受資料後使用handler處理資料
                    android.os.Message m = handler.obtainMessage();
                    m.obj = message;
                    m.sendToTarget();
                }
            });
        }
    }
}
