package com.myapp.donotsmoke;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTimeSinceLastCigarette;
    TextView tvNonSmokedCigarettesNum;
    TextView tvSavedMoney;
    Button btnReset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        tvTimeSinceLastCigarette = findViewById(R.id.tvTimeSinceLastCigarette);
        tvNonSmokedCigarettesNum = findViewById(R.id.tvNonSmokedCigarettesNum);
        tvSavedMoney = findViewById(R.id.tvSavedMoney);
        btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //finish(); //завершаем работу InfoActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent); //это создаст новое активити


    }
}