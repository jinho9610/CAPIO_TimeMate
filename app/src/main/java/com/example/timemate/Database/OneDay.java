package com.example.timemate.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class OneDay {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int year;
    private int month;
    private int day;
    private int date;

    // 1 - 업무 / 2- 운동 /  3 - 공부
    private int category;
    // 해당 활동을 한 시간
    private long time;

    // category가 1 이면 work_dayTotal에, 2이면 exercise_dayTotal에 ...
    // 저장시 마다 이전 값에 새로운 값을 더하여 갱신
    private long work_dayTotal;
    private long exercise_dayTotal;
    private long study_dayTotal;

    // 연산은 메인액티비티에서 진행할 것
    public OneDay(int year, int month, int day, int date, int category, long time, long work_dayTotal, long exercise_dayTotal, long study_dayTotal) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.date = date;
        this.category = category;
        this.time = time;
        this.work_dayTotal = work_dayTotal;
        this.exercise_dayTotal = exercise_dayTotal;
        this.study_dayTotal = study_dayTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getDate() {
        return date;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getWork_dayTotal() {
        return work_dayTotal;
    }

    public void setWork_dayTotal(long work_dayTotal) {
        this.work_dayTotal = work_dayTotal;
    }

    public long getExercise_dayTotal() {
        return exercise_dayTotal;
    }

    public void setExercise_dayTotal(long exercise_dayTotal) {
        this.exercise_dayTotal = exercise_dayTotal;
    }

    public long getStudy_dayTotal() {
        return study_dayTotal;
    }

    public void setStudy_dayTotal(long study_dayTotal) {
        this.study_dayTotal = study_dayTotal;
    }

    @Override
    public String toString() {
        return "OneDay{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", category=" + category +
                ", time=" + time +
                ", work_dayTotal=" + work_dayTotal +
                ", exercise_dayTotal=" + exercise_dayTotal +
                ", study_dayTotal=" + study_dayTotal +
                '}' + "\n";
    }
}
