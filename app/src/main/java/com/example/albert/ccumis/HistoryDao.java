package com.example.albert.ccumis;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
// --Commented out by Inspection START (2019/3/7 17:53):
//  @Insert
//  void insert(History history);
// --Commented out by Inspection STOP (2019/3/7 17:53)
  @Query("SELECT value FROM history WHERE type = :type")
  LiveData<List<String>> getAll(int type);
}
