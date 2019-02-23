package com.example.albert.ccumis;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
  @Insert
  void insert(History history);
  @Query("SELECT value FROM history WHERE type = :type")
  LiveData<List<String>> getAll(int type);
}
