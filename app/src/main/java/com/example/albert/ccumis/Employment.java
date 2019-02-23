package com.example.albert.ccumis;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "employment")
public class Employment {
  @PrimaryKey(autoGenerate = true)
  public int seri_no;
  @ColumnInfo
  public int operation;
  @ColumnInfo
  public String department;
  @ColumnInfo
  public String department_cd;
  @ColumnInfo
  public String date;
  @ColumnInfo
  public int year;
  @ColumnInfo
  public int month;
  @ColumnInfo
  public int day;
  @ColumnInfo
  public int end_hour;
  @ColumnInfo
  public int end_minute;
  @ColumnInfo
  public int start_hour;
  @ColumnInfo
  public int start_minute;
  @ColumnInfo
  public int duration;
  @ColumnInfo
  public String content;
  @ColumnInfo
  public int status;
  @ColumnInfo
  public String error_msg;
  @ColumnInfo
  public String weekend;
  @ColumnInfo
  public String batch_num;
  @ColumnInfo
  public String hour_count;
  @ColumnInfo
  public String process;
}
