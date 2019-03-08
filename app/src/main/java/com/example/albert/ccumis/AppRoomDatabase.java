package com.example.albert.ccumis;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.albert.ccumis.models.Department;
import com.example.albert.ccumis.models.Employment;
import com.example.albert.ccumis.models.History;

@Database(entities = {Employment.class, Department.class, History.class}, version = 12, exportSchema = false)
public abstract class AppRoomDatabase extends RoomDatabase {
  public abstract EmploymentDao employmentDao();
  public abstract DepartmentDao departmentDao();
  public abstract HistoryDao historyDao();

  private static AppRoomDatabase INSTANCE;

  static AppRoomDatabase getDatabase(final Context context) {
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
