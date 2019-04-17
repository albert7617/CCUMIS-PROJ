package com.example.albert.ccumis;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.albert.ccumis.data.Department;
import com.example.albert.ccumis.data.Employment;

@Database(entities = {Employment.class, Department.class}, version = 1, exportSchema = false)
public abstract class AppRoomDatabase extends RoomDatabase {
  public abstract EmploymentDao employmentDao();
  public abstract DepartmentDao departmentDao();

  private static AppRoomDatabase INSTANCE;

  public static AppRoomDatabase getDatabase(final Context context) {
    if(INSTANCE == null) {
      synchronized (AppRoomDatabase.class) {
        if(INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppRoomDatabase.class, "employment")
                  .fallbackToDestructiveMigration()
                  .build();
        }
      }
    }
    return INSTANCE;
  }


}
