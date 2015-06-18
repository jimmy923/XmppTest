package com.example.jg57.xmpp_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity {
    private EditText textUserName,textPwd;
    private Context context;
    private ClientConServer ccs;
    private SharedPreferences setting;
    private String userName,Pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textUserName = (EditText)findViewById(R.id.textUserName);
        textPwd = (EditText)findViewById(R.id.textPwd);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        context = this;
        ccs = new ClientConServer();
        //先查找SharedPreferences是否有登入資訊
        setting = getSharedPreferences("LoginData" ,0);
        userName = setting.getString("userName", "");
        Pwd = setting.getString("Pwd","");
        LoginOpenFire(false);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = textUserName.getText().toString();
                Pwd = textPwd.getText().toString();
                LoginOpenFire(true);
            }
        });
    }

    private void LoginOpenFire(final boolean showError){
        if(userName.equals("") || Pwd.equals("")){
            if(showError){
                Toast.makeText(context, "請輸入帳密", Toast.LENGTH_SHORT).show();
            }
        }else{
            //因主線程不可直接Connection所以使用執行緒登入
            new Thread(new Runnable(){
                @Override
                public void run() {
                    boolean ok = ccs.login(userName,Pwd);
                    if(ok){
                        setting.edit()
                                .putString("userName",userName)
                                .putString("Pwd",Pwd)
                                .apply();

                        Intent intent = new Intent(context, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("userName",userName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        if(showError) {
                            Looper.prepare();
                            Toast.makeText(context, "登入失敗", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
