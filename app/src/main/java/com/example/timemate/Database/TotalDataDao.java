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

    @Insert
    void insert(TotalData totalData);
}
