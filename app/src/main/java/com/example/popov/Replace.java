package com.example.popov;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ResourceCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class Replace extends AppCompatActivity{

    public String get_dayOfWeek(int N){
        N = (6 + ((N - 1) % 6)) % 6;
        String [] arr_dayOfWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота" };
        return arr_dayOfWeek[N];
    }

    Calendar calendar = Calendar.getInstance();
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    String strClass, strNumOfLesson;
    String[] classes = {"7а", "8а", "8б", "9а", "9б", "10а", "10б", "10в", "11а", "11б", "11в"};
    String[] lesson_num = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    TextView week_day;// textViewNumOfLesson, textViewClass;
    EditText lesson, auditory;
    Button back, replace;
    Spinner spinner_classes, spinner_lesson_num;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replace);
        getSupportActionBar().hide();

        week_day = (TextView) findViewById(R.id.week_day_text);
        week_day.setText(get_dayOfWeek(dayOfWeek));

        lesson = (EditText) findViewById(R.id.replace_lesson);
        auditory = (EditText) findViewById(R.id.replace_auditory);

        back = (Button) findViewById(R.id.button_back);
        replace = (Button) findViewById(R.id.button_replace);

        spinner_classes = (Spinner) findViewById(R.id.spinner_classes);
        spinner_lesson_num = (Spinner) findViewById(R.id.spinner_num_of_lesson);

        ArrayAdapter<String> adapter_classes = new ArrayAdapter(this, R.layout.spinner_item, classes);
        ArrayAdapter<String> adapter_lesson_num = new ArrayAdapter(this, R.layout.spinner_item, lesson_num);

        adapter_classes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_lesson_num.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_classes.setAdapter(adapter_classes);
        spinner_lesson_num.setAdapter(adapter_lesson_num);


        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (view.getId()){
                    case R.id.spinner_classes:
                        String classesItem = (String)parent.getItemAtPosition(position);
                        break;

                    case R.id.spinner_num_of_lesson:
                        String num_of_lessonItem = (String)parent.getItemAtPosition(position);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spinner_classes.setOnItemSelectedListener(itemSelectedListener);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String les = lesson.getText().toString();
                String aud = auditory.getText().toString();


                /*if((

                Проверка на зполнение полей

                les != " " || les!= null) && (aud != null || aud != " ")){
                    Toast.makeText(
                            getApplicationContext(),
                            les + " : " + aud,
                            Toast.LENGTH_LONG
                    ).show();
                } else*/
                Toast.makeText(
                        getApplicationContext(),
                        les + " : " + aud,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
