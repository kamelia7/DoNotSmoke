package com.myapp.donotsmoke;

import androidx.annotation.NonNull;

// Класс, объект которого будем возвращать при чтении последней строки таблицы входных данных
public class InputTableRow {
    private int cigarettesPerDay;
    private float packAverageCost;
    private long dateTimeOfLastCigarette;

    @NonNull
    @Override
    // Ctrl + O - добавляем метод toString() и перегружаем его
    // также удаляем вызов суперкласса
    public String toString() {
        return new String("cigarettesPerDay = " + this.cigarettesPerDay + "; packAverageCost = " + packAverageCost + "; dateTimeOfLastCigarette = " + dateTimeOfLastCigarette);
    }

    public InputTableRow(long dateTimeOfLastCigarette, int cigarettesPerDay, float packAverageCost) {
        this.dateTimeOfLastCigarette = dateTimeOfLastCigarette;
        this.cigarettesPerDay = cigarettesPerDay;
        this.packAverageCost = packAverageCost;
    }

    public int getCigarettesPerDay() {
        return cigarettesPerDay;
    }

    public float getPackAverageCost() {
        return packAverageCost;
    }

    public long getDateTimeOfLastCigarette() {
        return dateTimeOfLastCigarette;
    }
}
