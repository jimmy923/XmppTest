package com.example.jg57.xmpp_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jivesoftware.smack.XMPPException;


public class MainActivity extends ActionBarActivity {

    private EditText textMsg,sendTo;
    private Context context;
    private TextView showMsg;
    private ClientConServer ccs;
    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textMsg = (EditText)findViewById(R.id.textMsg);
        sendTo = (EditText)findViewById(R.id.sendTo);
        showMsg = (TextView)findViewById(R.id.showMsg);
        Button btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
        context = this;
        setting = getSharedPreferences("LoginData" ,0);
        ccs = new ClientConServer();

        //設定寄送按鈕操作
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Msg = textMsg.getText().toString();
                String to = sendTo.getText().toString();
                try {
                    ccs.sendMsg(to ,Msg);
                    textMsg.setText("");
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });

        //設定Enter鍵操作
        textMsg.setOnKeyListener(new EditText.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    String Msg = textMsg.getText().toString();
                    String to = sendTo.getText().toString();
                    try {
                        ccs.sendMsg(to ,Msg);
                        textMsg.setText("");
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        //查詢是否登入若未登入轉至LoginActivity
        if(!ccs.hasConnection()){
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, XmppService.class);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //登出
        if (id == R.id.action_settings) {
            ccs.Logout();
            setting.edit().clear().apply();
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //寫入聊天訊息
    public void writeMsg(String[] Message){
        String msg = showMsg.getText().toString();
        msg = Message[0] + "：" + Message[1] + "\n" + msg;
        showMsg.setText(msg);
    }
}
