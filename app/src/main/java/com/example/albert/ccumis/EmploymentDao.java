package com.example.albert.ccumis;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.albert.ccumis.data.Employment;

import java.util.List;

@Dao
public interface EmploymentDao {
  @Insert
  void insert(Employment employment);

  @Query("SELECT * FROM employment WHERE operation = :operation ORDER BY month ASC, day ASC, start_hour ASC, start_minute ASC")
  LiveData<List<Employment>> getWithOperation(int operation);

  @Query("SELECT * FROM employment WHERE operation = :operation ORDER BY month ASC, day ASC, start_hour ASC, start_minute ASC")
  List<Employment> getListWithOperation(int operation);

  @Query("DELETE FROM employment WHERE seri_no = :seri_no")
  void delete(int seri_no);

  @Query("SELECT SUM(duration) FROM employment WHERE operation = :operation")
  LiveData<Integer> getSum(int operation);

  @Query("DELETE FROM employment WHERE operation = :operation")
  void nukeTable(int operation);

  @Query("SELECT * FROM employment WHERE seri_no =:seri_no")
  LiveData<Employment> getBySeri_no(int seri_no);

  @Update
  void update(Employment employment);

}
