package edu.stts.fatburner.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.stts.fatburner.ui.main.MainActivity;
import edu.stts.fatburner.R;
import edu.stts.fatburner.ui.register.RegisterActivity;
import edu.stts.fatburner.data.network.API;
import edu.stts.fatburner.data.network.ApiClient;
import edu.stts.fatburner.data.network.body.LoginBody;
import edu.stts.fatburner.data.network.response.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvEmail;
    private TextView tvPassword;
    private API mApiInterface;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        tvEmail = findViewById(R.id.etUsername);
        tvPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvRegister);
        mApiInterface = ApiClient.getClient().create(API.class);
        //Untuk simpan id user yg login(kyk simpan ke session)
        pref = getApplicationContext().getSharedPreferences("FatBurnerPrefs",Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login : {
                doLogin(tvEmail.getText().toString(),tvPassword.getText().toString());
                break;
            }
        }
    }

    private void doLogin(String email,String password){
        Call<LoginResponse> loginCall = mApiInterface.login(new LoginBody(email,password));
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
                LoginResponse response = res.body();
                if(!response.isError()){
                    saveUserLoginID(Integer.parseInt(response.getMessage()));
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
                else Toast.makeText(LoginActivity.this,response.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this,t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserLoginID(int id){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("userID",id);
        editor.apply();
    }

    public void onClickRegister(View v){
        Intent intent = null;
        switch(v.getId()){
            case R.id.tvRegister:
                intent = new Intent(this,RegisterActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);
    }
}