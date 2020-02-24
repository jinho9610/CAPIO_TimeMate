package com.example.timemate.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OneDayDao {
    @Query("SELECT*FROM OneDay") // 모든 내용을 꺼내온다 - 라이브데이터관찰용
    LiveData<List<OneDay>> getAll();

    @Query("SELECT * FROM OneDay") // 모든 내용을 꺼내온다
    List<OneDay> getAllItems();

    @Insert
    void insert(OneDay oneDay);

    @Update
    void update(OneDay oneDay);

    // 매일 최초 실행 시 전체 삭제
    @Query("DELETE FROM OneDay")
    void clear();

}
