package com.example.timemate.Database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class TotalData {
    @PrimaryKey(autoGenerate = true)
    private int id;

    // 앱 사용 기간 중 총 category별 누적 시간
    private long work_Total;
    private long exercise_Total;
    private long study_Total;
    private long totalTime;

    public TotalData(long work_Total, long exercise_Total, long study_Total) {
        this.work_Total = work_Total;
        this.exercise_Total = exercise_Total;
        this.study_Total = study_Total;
        this.totalTime = work_Total + exercise_Total + study_Total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getWork_Total() {
        return work_Total;
    }

    public void setWork_Total(long work_Total) {
        this.work_Total = work_Total;
    }

    public long getExercise_Total() {
        return exercise_Total;
    }

    // setExercise_Total(getExercise_Total + t) 로 구현 시도해볼 것
    public void setExercise_Total(long exercise_Total) {
        this.exercise_Total = exercise_Total;
    }

    public long getStudy_Total() {
        return study_Total;
    }

    public void setStudy_Total(long study_Total) {
        this.study_Total = study_Total;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "TotalDatas{" +
                "work_Total=" + work_Total +
                ", exercise_Total=" + exercise_Total +
                ", study_Total=" + study_Total +
                ", totalTime=" + totalTime +
                '}' + "\n";
    }
}

