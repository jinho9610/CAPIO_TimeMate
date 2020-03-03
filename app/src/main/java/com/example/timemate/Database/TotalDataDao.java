package com.example.timemate.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TotalDataDao  {
    @Query("SELECT * FROM TotalData")
    LiveData<List<TotalData>> getAll();

    @Query("SELECT * FROM TotalData")
    List<TotalData> getAllItems();

    @Insert
    void insert(TotalData totalData);

    @Query("DELETE FROM TotalData")
    void clear();
}
