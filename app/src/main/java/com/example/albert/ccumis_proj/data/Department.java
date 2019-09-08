package com.example.albert.ccumis_proj.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "department")
public class Department {
  @PrimaryKey(autoGenerate = true)
  public int seri_no;
  @ColumnInfo
  public String value;
  @ColumnInfo
  public String name;
  @ColumnInfo
  public int type;
}
