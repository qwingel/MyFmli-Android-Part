package com.example.popov;

import android.app.Application;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Array;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class Login extends AppCompatActivity {

    EditText loginText, passText;
    Button toLogin, toCancel;
    String session, Access;


    public static final String SQurl = "http://10.222.149.95";

    public static class ResponseMessage {
        public String status, message;

        @Override
        public String toString() {
            return "ResponseMessage{" +
                    "status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    public static class LoginRequest{
        public LoginRequest(String login, String password){
            this.login = login;
            this.password = password;
        }
        String login, password;
    }

    public static class AccessRequest{
        public AccessRequest(String login){
            this.login = login;
        }
        String login;
    }

    public static class SessionRequest {
        public SessionRequest(String login){
            this.login = login;
        }
        String login;
    }
    public interface UserService {
        @POST("/login")
        Call<ResponseMessage> login(@Body LoginRequest loginRequest);
        @POST("/access")
        Call<ResponseMessage> access(@Body AccessRequest accessRequest);
        @POST("/")
         Call<ResponseMessage> session(@Body SessionRequest sessionRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().hide();

        toLogin = (Button) findViewById(R.id.tologin);
        toCancel = (Button) findViewById(R.id.cancellogbutton);

        loginText = (EditText) findViewById(R.id.logintext);
        passText = (EditText) findViewById(R.id.passtext);

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = loginText.getText().toString();
                String password = passText.getText().toString();

                Retrofit retrofit = new Retrofit.Builder().baseUrl(SQurl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                UserService userService = retrofit.create(UserService.class);
                Call<ResponseMessage> access = userService.access(new AccessRequest(login));
                Call<ResponseMessage> inSystem = userService.login(new LoginRequest(login, password));
                Call<ResponseMessage> inSession = userService.session(new SessionRequest(login));
                inSystem.enqueue(new Callback<ResponseMessage>() {
                    @Override
                    public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                        System.out.println(response.headers());
                        if(response.body() != null){
                            if(response.body().status == "failed"){
                                Toast.makeText(
                                        getApplicationContext(),
                                        response.body().message,
                                        Toast.LENGTH_LONG
                                ).show();
                            } else {
                                String login = response.body().message;
                                Toast.makeText(
                                        getApplicationContext(),
                                        response.body().status,
                                        Toast.LENGTH_LONG
                                ).show();

                                access.enqueue(new Callback<ResponseMessage>() {
                                    @Override
                                    public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                        if(response.body() != null) {
                                            String Access = response.body().message;

                                            inSession.enqueue(new Callback<ResponseMessage>() {
                                                @Override
                                                public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                                    String session = response.body().message;

                                                    Intent toMainWithLogin = new Intent(getApplicationContext(), MainActivity.class);
                                                    toMainWithLogin.putExtra("login", login);
                                                    toMainWithLogin.putExtra("access", Access);
                                                    toMainWithLogin.putExtra("session", session);
                                                    toMainWithLogin.putExtra("password", password);
                                                    startActivity(toMainWithLogin);
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseMessage> call, Throwable t) {
                                                    t.printStackTrace();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseMessage> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Empty",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessage> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        toCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toBack = new Intent(getApplicationContext(), MainActivity.class);
                toBack.putExtra("access", "ACCESS_LEVEL_GUEST");
                startActivity(toBack);
            }
        });
    }
}
