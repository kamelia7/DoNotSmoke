package com.myapp.donotsmoke;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvDate;
    TextView tvTime;
    EditText etCigarettesPerDay;
    EditText etPackAverageCost;
    Button btnSubmit;

    private static final String LOG_TAG = "myLogs";


    //Выполняются эти строки, затем создастся TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(),
    //затем DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
    //затем onCreate, затем всякие системные ф-и, loop() и наконец появляется активити на экране
    //затем onClick, заходим в dpd.show(); затем в break текущей ветки свича, выходим из onClick
    // Затем всякие системные ф-и, loop и появляется диалоговое окно.
    //Выбираем дату в диалоге, вызывается onDateSet


    // Добавляем параметр Locale.getDefault(), чтобы избавиться от warning
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        etCigarettesPerDay = findViewById(R.id.etCigarettesPerDay);
        etPackAverageCost = findViewById(R.id.etPackAverageCost);
        btnSubmit = findViewById(R.id.btnSubmit);

        //Allocates a Date object and initializes it so that it represents the time at which it was allocated, measured to the nearest millisecond.
        // было sdfDate.format(new Date(System.currentTimeMillis())). new Date() уже сам с System.currentTimeMillis() по умолчанию
        String currentDate = sdfDate.format(new Date());
        tvDate.setText(currentDate);

        String currentTime = sdfTime.format(new Date());
        tvTime.setText(currentTime);

        //btnSubmit.setEnabled(false);
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int dateTimeOfLastCigarette;
        int cigarettesPerDay;
        float packAverageCost;

        // The Calendar returned is based on the current time in the default time zone with the default Locale.Category#FORMAT locale.
        Calendar c = Calendar.getInstance();
        // в календаре MONTH=9, хотя сейчас октябрь. Потому что в календаре месяцы считаются с 0 (янв.), в DatePickerDialog тоже

        switch (view.getId()) {

            case R.id.tvDate: //в xml указываем android:clickable="true"
                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, onDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                //нужен break. dpd.show(); затем в break текущей ветки свича, выходим из onClick и потом открывается диалоговое окно
                break;
            case R.id.tvTime: //в xml указываем android:clickable="true"
                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, onTimeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                tpd.show();
                break;
            case R.id.btnSubmit:
                if (TextUtils.isEmpty(etCigarettesPerDay.getText().toString())
                        || TextUtils.isEmpty(etPackAverageCost.getText().toString())) {
                    Toast.makeText(this, "Введите необходимые данные", Toast.LENGTH_SHORT).show();
                    return;
                }
                cigarettesPerDay = Integer.parseInt(etCigarettesPerDay.getText().toString());
                packAverageCost = Float.parseFloat(etPackAverageCost.getText().toString());

                Intent intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            //1$ -1й параметр, 0 в начале пишем, если одна цифра (одного нуля достаточно в любом случае). 2- две цифры всего. d-ожидаем целое число получить.
            tvTime.setText(String.format(Locale.getDefault(), "%1$02d:%2$02d", hourOfDay, minute));
            //String.format("%d", 1) - выведет просто "1"
            //String.format("%1$d | %2$d | %3$d", 1, 2, 3) НОМЕР АРГУМЕНТА ЭТО %[1$]d, %[3$]s итд
        }
    };

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) { //2020 9 24
            //пишем month + 1, чтобы вывести на экран октябрь как 10й месяц, а не как 9й (т.к. у диалога счет месяцев идет с 0)
            tvDate.setText(String.format(Locale.getDefault(), "%1$02d.%2$02d.%3$4d", dayOfMonth, month + 1, year)); //"dd.MM.yyyy"
        }
    };
}


























