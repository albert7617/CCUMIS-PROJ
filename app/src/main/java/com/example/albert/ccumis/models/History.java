package com.example.albert.ccumis.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "history")
public class History {
  @PrimaryKey(autoGenerate = true)
  public int seri_no;
  @ColumnInfo
  public int type;
  @ColumnInfo
  public String value;
}
