package com.myapp.donotsmoke;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.myapp.donotsmoke.InfoActivity.BTN_RESET_CLICKED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvDate;
    TextView tvTime;
    EditText etCigarettesPerDay;
    EditText etPackAverageCost;
    Button btnSubmit;

    public static final String LOG_TAG = "myLogs";

    //Выполняются эти строки, затем создастся TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(),
    //затем DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
    //затем onCreate, затем всякие системные ф-и, loop() и наконец появляется активити на экране
    //затем onClick, заходим в dpd.show(); затем в break текущей ветки свича, выходим из onClick
    // Затем всякие системные ф-и, loop и появляется диалоговое окно.
    //Выбираем дату в диалоге, вызывается onDateSet

    // Добавляем параметр Locale.getDefault(), чтобы избавиться от warning
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    DBHelper dbh1;

    Calendar chosenDateTimeCalendar = Calendar.getInstance();

    private SharedPreferences sPrefIsTimerTicking; // по умолчанию false

    private static final String IS_TIMER_TICKING = "is_timer_ticking";

    public void setSPrefIsTimerTicking(final boolean b) {
        SharedPreferences.Editor ed = sPrefIsTimerTicking.edit();
        ed.putBoolean(IS_TIMER_TICKING, b);
        ed.apply();
    }

    void prepareMainActivity() {
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        etCigarettesPerDay = findViewById(R.id.etCigarettesPerDay);
        etPackAverageCost = findViewById(R.id.etPackAverageCost);
        btnSubmit = findViewById(R.id.btnSubmit);

        //Allocates a Date object and initializes it so that it represents the time at which it was allocated, measured to the nearest millisecond.
        // sdfDate.format(new Date(System.currentTimeMillis())). new Date() уже сам с System.currentTimeMillis() по умолчанию
        String currentDate = sdfDate.format(new Date());
        tvDate.setText(currentDate);

        String currentTime = sdfTime.format(new Date());
        tvTime.setText(currentTime);

        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        dbh1 = new DBHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //используем getSharedPreferences, так как сохраняем данные, общие для обеих Activity, и задаем имя файла для сохранения - "myPref"
        sPrefIsTimerTicking = getSharedPreferences("myPref", MODE_PRIVATE);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();
        if (intent != null) {
            if (!intent.hasExtra(BTN_RESET_CLICKED)) {  //если нет в интенте экстраданных, то запуск этого активити был через список приложений (не нажимали btnReset)
                //проверяю преференсы - тикает ли таймер
                // (таймер тикает - открываем InfoActivity, таймер не тикает - готовим MainActivity к отрытию)
                if (sPrefIsTimerTicking.getBoolean(IS_TIMER_TICKING, false)) {
                    Intent newIntent = new Intent(this, InfoActivity.class);
                    //Флаги нужны, чтобы MainActivity не открылось, если нажмем back, находясь в InfoActivity. И чтобы мы попали на рабочий стол
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(newIntent); // открываем InfoActivit
                    finish(); // закрываем MainActivity
                }
                else prepareMainActivity();
            }
            //если есть экстраданные в интенте, то мы нажимали btnReset, интент пришел из класса InfoActivity и из этого класса был вызван MainActivity. Готовим MainActivity к отрытию
            else prepareMainActivity();
        }
    }

    @Override
    public void onClick(View view) {
        long dateTimeOfLastCigarette;
        int cigarettesPerDay;
        float packAverageCost;

        // The Calendar returned is based on the current time in the default time zone with the default Locale.Category#FORMAT locale.
        Calendar c = Calendar.getInstance();
        // в календаре MONTH=9, хотя сейчас октябрь. Потому что в календаре месяцы считаются с 0 (янв.), в DatePickerDialog тоже

        switch (view.getId()) {
            case R.id.tvDate: //в xml указываем android:clickable="true"
                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, onDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                //нужен break. вызывается dpd.show(); затем попадаем в break текущей ветки свича, выходим из onClick и потом открывается диалоговое окно
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
                dateTimeOfLastCigarette = chosenDateTimeCalendar.getTimeInMillis();

                dbh1.insertInputData(cigarettesPerDay, packAverageCost, dateTimeOfLastCigarette);
                setSPrefIsTimerTicking(true);

                dbh1.close();

                startActivity(new Intent(this, InfoActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            chosenDateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            chosenDateTimeCalendar.set(Calendar.MINUTE, minute);
            //1$ -1й параметр, 0 в начале пишем, если одна цифра (одного нуля достаточно в любом случае). 2- две цифры всего. d-ожидаем целое число получить.
            tvTime.setText(String.format(Locale.getDefault(), "%1$02d:%2$02d", hourOfDay, minute));
            //String.format("%d", 1) - выведет просто "1"
            //String.format("%1$d | %2$d | %3$d", 1, 2, 3) НОМЕР АРГУМЕНТА ЭТО %[1$]d, %[3$]s итд
        }
    };

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) { //2020 9 24
            chosenDateTimeCalendar.set(year, month, dayOfMonth);
            //пишем month + 1, чтобы вывести на экран октябрь как 10й месяц, а не как 9й (т.к. у диалога счет месяцев идет с 0)
            tvDate.setText(String.format(Locale.getDefault(), "%1$02d.%2$02d.%3$4d", dayOfMonth, month + 1, year)); //"dd.MM.yyyy"
        }
    };
}

