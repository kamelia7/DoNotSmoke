package com.myapp.donotsmoke;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String BTN_RESET_CLICKED = "btnResetClicked";

    TextView tvTimeSinceLastCigarette;
    TextView tvNonSmokedCigarettesAmount;
    TextView tvSavedMoney;
    Button btnReset;

    DBHelper dbh2;

    public static final float CIGARETTES_IN_PACK_AMOUNT = 20f;

    long msSinceLastCigarette;
    int nonSmokedCigarettesAmount;
    float savedMoney;

    int daysSinceLastCigarette;
    int hoursSinceLastCigarette;
    int minutesSinceLastCigarette;
    int secondsSinceLastCigarette;

    InputTableRow itr;

    Timer timer;
    TimerTask timerTask;

    //Необходим Handler для доступа к перерисовке UI-элементов
    final Handler timeHandler = new Handler();

    private SharedPreferences sPrefIsTimerTicking; // по умолчанию false
    private static final String IS_TIMER_TICKING = "is_timer_ticking";

    //Запись boolean флага в sPrefIsTimerTicking
    public void setSPrefIsTimerTicking(final boolean b) {
        SharedPreferences.Editor ed = sPrefIsTimerTicking.edit();
        ed.putBoolean(IS_TIMER_TICKING, b);
        ed.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //используем getSharedPreferences, так как сохраняем данные, общие для обеих Activity, и задаем имя файла для сохранения - "myPref"
        sPrefIsTimerTicking = getSharedPreferences("myPref", MODE_PRIVATE);

        tvTimeSinceLastCigarette = findViewById(R.id.tvTimeSinceLastCigarette);
        tvNonSmokedCigarettesAmount = findViewById(R.id.tvNonSmokedCigarettesAmount);
        tvSavedMoney = findViewById(R.id.tvSavedMoney);
        btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(this);

        //В этом классе надо создать новый DBHelper, потому что при его создании задается контекст (this), а он разный у двух активити
        dbh2 = new DBHelper(this);

        itr = dbh2.readLastRowOfInputDataTable();
        dbh2.close();
    }

    void calculateAndDisplayOutputData(InputTableRow itr) {

        //1) Время с момента последней сигареты (ДД.ЧЧ.ММ) = текущее время - время последнего курения из БД (ms)
        msSinceLastCigarette = System.currentTimeMillis() - itr.getDateTimeOfLastCigarette();

        daysSinceLastCigarette = (int)(msSinceLastCigarette / 1000 / 60 / 60 / 24); //86400000
        hoursSinceLastCigarette = (int)((msSinceLastCigarette - daysSinceLastCigarette * 86400000) / 1000 / 60 / 60); //msSinceLastCigarette минус дни в ms, переводим это в часы и берем целую часть
        minutesSinceLastCigarette = (int)((msSinceLastCigarette - (daysSinceLastCigarette * 86400000) - (hoursSinceLastCigarette * 60 * 60 * 1000)) / 1000 / 60);
        secondsSinceLastCigarette = (int)((msSinceLastCigarette - (daysSinceLastCigarette * 86400000) - (hoursSinceLastCigarette * 60 * 60 * 1000) - (minutesSinceLastCigarette * 60 * 1000)) / 1000);

        //2) Количество невыкуренных сигарет = кол-во сигарет в день из БД * п.1 время с момента последней сигареты (берем только дни)
        nonSmokedCigarettesAmount = itr.getCigarettesPerDay() * daysSinceLastCigarette;

        //3) Сэкономлено денег = Количество невыкуренных сигарет п.2 / кол-во сигарет в пачке (сами придумываем) * средняя стоимость пачки из БД
        savedMoney = nonSmokedCigarettesAmount / CIGARETTES_IN_PACK_AMOUNT * itr.getPackAverageCost();

        tvTimeSinceLastCigarette.setText(daysSinceLastCigarette + " дней " + hoursSinceLastCigarette + " часов " + minutesSinceLastCigarette + " минут " + secondsSinceLastCigarette + " секунд");
        tvNonSmokedCigarettesAmount.setText(String.valueOf(nonSmokedCigarettesAmount));
        tvSavedMoney.setText(String.valueOf(savedMoney));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Запускаем таймер перед тем, как активити будет доступно для активности пользователя (взаимодействие)
        startTimer();
    }

    void startTimer() {
        timer = new Timer();

        // Инициализируем работу TimerTask
        initializeTimerTask();

        //Отображение текущего времени раз в 1 секунду при старте InfoActivity
        //schedule the timer: after the first 0ms the TimerTask will run every 1000ms
         timer.schedule(timerTask, 0, 1000);
    }

    void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // Необходим Handler для доступа к перерисовке UI-элементов
                timeHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        calculateAndDisplayOutputData(itr);
                    }
                });
            }
        };
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnReset) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Вы уверены, что хотите сбросить счетчик?");
            adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                    intent.putExtra(BTN_RESET_CLICKED, true); //флаг, что была нажата btnReset
                    setSPrefIsTimerTicking(false); // Запись false флага в sPrefIsTimerTicking (так как таймер остановлен)
                    startActivity(intent); // открываем MainActivity
                    finish(); // закрываем текущее InfoActivity
                }
            });
            adb.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss(); // закрываем диалог
                }
            });
            adb.create().show();
        }
    }
}