package com.myapp.donotsmoke;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvDate;
    TextView tvTime;
    EditText etCigarettesPerDay;
    EditText etPackAverageCost;
    Button btnSubmit;

    //надо final, иначе в строке case DIALOG_DATE_ID: ошибка error: constant expression required
    final int DIALOG_TIME_ID = 1;
    final int DIALOG_DATE_ID = 2;

    private static final String TAG = "myLogs";

    //Выполняются эти строки, затем создастся TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(),
    //затем DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
    //затем onCreate, затем всякие системные ф-и, loop() и наконец появляется активити на экране
    //затем onClick, заходим в showDialog(DIALOG_DATE_ID); затем системные ф-и, системная ф-я onCreateDialog
    // и наконец наша protected Dialog onCreateDialog(int id) {
    //проходим ее, затем выходим из системной ф-и onCreateDialog, выходим из нашей showDialog(DIALOG_DATE_ID);
    //и попадаем в break текущей ветки свича. Выходим из onClick.
    // Затем всякие системные ф-и, loop и появляется диалоговое окно

    // инициализировать тут (тогда выполнится до onCreate) или же в onCreate?
    SimpleDateFormat sdfTime; // = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfDate; // = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        etCigarettesPerDay = findViewById(R.id.etCigarettesPerDay);
        etPackAverageCost = findViewById(R.id.etPackAverageCost);
        btnSubmit = findViewById(R.id.btnSubmit);

        sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdfDate.format(new Date(System.currentTimeMillis()));
        tvDate.setText(currentDate);

        sdfTime = new SimpleDateFormat("HH:mm");
        String currentTime = sdfTime.format(new Date(System.currentTimeMillis()));
        tvTime.setText(currentTime);

        //btnSubmit.setEnabled(false);
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int CigarettesPerDay;
        float PackAverageCost;

        switch (view.getId()) {
            case R.id.tvDate: //в xml указываем android:clickable="true"
                showDialog(DIALOG_DATE_ID);
                // нужен break. В showDialog(DIALOG_DATE_ID); вызывается protected Dialog onCreateDialog(int id),
                // выходим из showDialog, затем break, выход из onClick и потом открывается диалоговое окно
                break;
            case R.id.tvTime: //в xml указываем android:clickable="true"
                showDialog(DIALOG_TIME_ID);
                break;
            case R.id.btnSubmit:
                if (TextUtils.isEmpty(etCigarettesPerDay.getText().toString())
                        || TextUtils.isEmpty(etPackAverageCost.getText().toString())) {
                    Toast.makeText(this, "Введите необходимые данные", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar c = Calendar.getInstance(); // The Calendar returned is based on the current time in the default time zone with the default Locale.Category#FORMAT locale.
        // в календаре MONTH=9, хотя сейчас октябрь. Потому что в календаре месяцы считаются с 0 (янв.)
        switch (id) {
            case DIALOG_TIME_ID:
                int currentHourOfDay = c.get(Calendar.HOUR_OF_DAY); //Field number for get and set indicating the hour of the day.
                int currentMinute = c.get(Calendar.MINUTE);
                return new TimePickerDialog(this, onTimeSetListener, currentHourOfDay, currentMinute, true);
            case DIALOG_DATE_ID:
                int currentYear = c.get(Calendar.YEAR); //2020
                int currentMonth = c.get(Calendar.MONTH); // currentMonth=9, хотя сейчас октябрь. НЕ делаем тут +1 к месяцу, иначе в диалоге откроется не октябрь (текущий), а ноябрь
                int currentDayOfMonth = c.get(Calendar.DAY_OF_MONTH); //24
                return new DatePickerDialog(this, onDateSetListener, currentYear, currentMonth, currentDayOfMonth);
            default:
                return super.onCreateDialog(id); //? все ветки вроде как должны возвращать зн-е
                //throw new IllegalStateException("Unexpected value: " + id); //? или просто вместо return поставить break
        }
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            String chosenTime = hourOfDay + ":" + minute; //chosenTime = "1:5", а не "01:05"
            try {
                Date time = sdfTime.parse(chosenTime); //time = "Thu Jan 01 01:05:00 GMT+03:00 1970"
                tvTime.setText(sdfTime.format(time)); // или вынести это за try?
            } catch (ParseException e) {
                e.printStackTrace(); //сюда не попадаем
            }
        }
    };

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) { //2020 9 24
            //chosenDate = 24.9.2020, если не написать month + 1, чтобы вывести на экран окт. как 10й месяц,
            // но тогда chosenDate = 24.91.2020. Надо (month + 1) и будет "24.10.2020"
            String chosenDate = dayOfMonth + "." + (month + 1) + "." + year;  //"dd.MM.yyyy"
            try { // без try/catch была ошибка surround with try/catch
                Date date = sdfDate.parse(chosenDate); // date = "Thu Oct 22 00:00:00 GMT+03:00 2020"
                tvDate.setText(sdfDate.format(date));
            } catch (ParseException e) {
                e.printStackTrace();  //сюда не попадаем
            }
        }
    };
}
//TODO сделать восстановление уже заданных в диалоге данных после закрытия приложения и повторного открытия