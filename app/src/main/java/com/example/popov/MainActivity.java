package com.example.popov;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {
    int s = 0;
    int[] ids = new int[] {R.id.a1, R.id.a2, R.id.a3, R.id.a4, R.id.a5, R.id.a6, R.id.a7, R.id.a8, R.id.a9, R.id.a10};
//            R.id.a11, R.id.a12, R.id.a13, R.id.a14, R.id.a15, R.id.a16, R.id.a17, R.id.a18, R.id.a19, R.id.a20 };

    public String get_rus_dayOfWeek(int N){
        N = (6 + ((N - 1) % 6)) % 6;
        String [] arr_dayOfWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота" };
        return arr_dayOfWeek[N];
    }

    public String get_eng_dayOfWeek(int N) {
        N = (6 + ((N - 1) % 6)) % 6;
        String[] arr_dayOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return arr_dayOfWeek[N];
    }

    public void setTimeTable(String[] timeTable, String classes){
        if(timeTable == null){
            Toast.makeText(
                    getApplicationContext(),
                    "Войдите в систему",
                    Toast.LENGTH_LONG
            );
        } else {
            TextView classesText = (TextView) findViewById(R.id.classes);
            classesText.setText(classes);
            for(int i = 0; i < timeTable.length; i++){
                TextView textview = (TextView) findViewById(ids[i]);
                textview.setText(timeTable[i]);
            }
        }
    }
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

    public static class LessonRequest{
        public LessonRequest(String logins){
            login = logins;
        }
        String login;
    }

    public static class LoginRequest{
        public LoginRequest(String login, String password){
            this.login = login;
            this.password = password;
        }
        String login, password;
    }


    public static class LessonMessage extends ResponseMessage {
        ArrayList<String> data;
    }

    public interface UserService {
        @POST("/login")
        Call<ResponseMessage> login(@Body LoginRequest loginRequest);
        @POST("/lessons")
        Call<LessonMessage> lessons(@Header("Cookie") String cookie, @Body String dayOfWeek);

        @GET("/logout")
        Call<ResponseMessage> logout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Calendar calendar = Calendar.getInstance();

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // Текущий день недели
        s = dayOfWeek;

        Button h_beforeDay = findViewById(R.id.beforeDayBut); // Кнопка предыдущий день в хоме
        Button h_nextDay = findViewById(R.id.nextDayBut);// Кнопка следующего дня в хоме
        Button login = findViewById(R.id.loginBut); // Кнопка на экран логина / регистрации
        // Button replace = findViewById(R.id.replaceLesson);
        Button logout = findViewById(R.id.logoutbut);

        Intent intent = getIntent();
        String access = intent.getStringExtra("access");
        String logins = intent.getStringExtra("login");
        String password = intent.getStringExtra("password");
        String session = intent.getStringExtra("session");
//        String classes = intent.getStringExtra("classes");

//        String[] timeTable = intent.getStringArrayExtra("timetable");

        h_beforeDay.setText(get_rus_dayOfWeek(dayOfWeek - 1));
        h_nextDay.setText(get_rus_dayOfWeek(dayOfWeek + 1));

        if(session != null /*&& !session.equals("Войдите в систему")*/) {
            login.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);

//            logout.setText(logins);

            System.out.println(access);
            System.out.println(session);
           /* if (access.equals("ACCESS_LEVEL_ADMIN") || access.equals("ACCESS_LEVEL_TEACHER")){
                replace.setVisibility(View.VISIBLE);
            }*/
        }


        h_nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s++;
                String nextDayText = get_rus_dayOfWeek(s + 1);
                String beforeDayText = get_rus_dayOfWeek(s - 1);


                h_nextDay.setText(nextDayText);
                h_beforeDay.setText(beforeDayText);
                Retrofit retrofit = new Retrofit.Builder().baseUrl(SQurl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                UserService userService = retrofit.create(UserService.class);
                Call<ResponseMessage> call = userService.login(new LoginRequest(logins, password));
                call.enqueue(new Callback<ResponseMessage>() {
                    @Override
                    public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                        if(response.body() == null){
                            System.out.println(response.body());
                            System.out.println("123");
                        }
                        String cookie = response.headers().get("Set-Cookie");
                        String day = get_eng_dayOfWeek(s);
                        System.out.println(day);
                        Call<LessonMessage> lesson = userService.lessons(cookie, day);
                        lesson.enqueue(new Callback<LessonMessage>() {
                            @Override
                            public void onResponse(Call<LessonMessage> call, Response<LessonMessage> response) {
                                if(response.body() == null){
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                                String[] timeTable = new String[10];
                                System.out.println(response.body().data.get(0));
                                String classes = response.body().data.get(0);
                                for(int i = 1; i < response.body().data.size(); i++){
                                    timeTable[i - 1] = response.body().data.get(i);
                                }
                                setTimeTable(timeTable, classes);
//                                Intent toMainWithLessons = new Intent(getApplicationContext(), MainActivity.class);
//                                toMainWithLessons.putExtra("timetable", timeTable);
//                                toMainWithLessons.putExtra("classes", classes);
//                                toMainWithLessons.putExtra("session", "xdxd");
//                                toMainWithLessons.putExtra("logins", logins);
//                                startActivity(toMainWithLessons);
                            }

                            @Override
                            public void onFailure(Call<LessonMessage> call, Throwable t) {

                            }
                        });



//                    присвоить всем textview из ids текст из timetable

                    }

                    @Override
                    public void onFailure(Call<ResponseMessage> call, Throwable t) {

                    }
                });
                }
            });


        h_beforeDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s--;
                String beforeDayText = get_rus_dayOfWeek(s - 1);
                String nextDayText = get_rus_dayOfWeek(s + 1);
                h_beforeDay.setText(beforeDayText);
                h_nextDay.setText(nextDayText);
                Retrofit retrofit = new Retrofit.Builder().baseUrl(SQurl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                UserService userService = retrofit.create(UserService.class);
                Call<ResponseMessage> call = userService.login(new LoginRequest(logins, password));
                call.enqueue(new Callback<ResponseMessage>() {
                    @Override
                    public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                        assert response.body() !=null;
                        String cookie = response.headers().get("Set-Cookie");
                        String day = get_eng_dayOfWeek(s);
                        Call<LessonMessage> lesson = userService.lessons(cookie, day);
                        lesson.enqueue(new Callback<LessonMessage>() {
                            @Override
                            public void onResponse(Call<LessonMessage> call, Response<LessonMessage> response) {
                                assert response.body() != null;
                                System.out.println(response.body().data);
                            }

                            @Override
                            public void onFailure(Call<LessonMessage> call, Throwable t) {

                            }
                        });



//                    присвоить всем textview из ids текст из timetable

                    }

                    @Override
                    public void onFailure(Call<ResponseMessage> call, Throwable t) {

                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent toLoginWindow = new Intent(getApplicationContext(), Login.class);
                startActivity(toLoginWindow);
            }
        });

        /*replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toReplaceWindow = new Intent(getApplicationContext(), Replace.class);
                startActivity(toReplaceWindow);
            }
        });*/

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);
                String[] timeTable = {"-" , "-", "-", "-", "-", "-", "-", "-", "-", "-"};
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("timetable", timeTable);
                startActivity(intent);
            }
        });
    }
}