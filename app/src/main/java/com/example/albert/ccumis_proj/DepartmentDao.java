package com.example.albert.ccumis_proj;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.albert.ccumis_proj.data.Department;

import java.util.List;

@Dao
public interface DepartmentDao {
  @Insert
  void insert(com.example.albert.ccumis_proj.data.Department department);

  @Query("SELECT * FROM department WHERE seri_no > 0 AND type =:type")
  LiveData<List<com.example.albert.ccumis_proj.data.Department>> getAll(int type);


  @Query("DELETE FROM department")
  void dropAll();



  @Query("SELECT value FROM department WHERE seri_no = -1")
  String getLastUpdateTime();


  @Query("SELECT value FROM department WHERE name = :name")
  String getDepartmentCd(String name);

  @Update
  void update(Department department);

  @Query("DELETE FROM department WHERE type = :type")
  void nukeTable(int type);
}
