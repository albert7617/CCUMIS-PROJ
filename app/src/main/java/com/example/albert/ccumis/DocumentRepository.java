package com.example.albert.ccumis;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.albert.ccumis.data.Department;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocumentRepository {
  private DepartmentDao departmentDao;
  private LiveData<List<Department>> allDepartment;
  private Application application;
  DocumentRepository(Application application){
    AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
    departmentDao = db.departmentDao();
    allDepartment = departmentDao.getAll(0);
    this.application = application;
  }


  LiveData<List<Department>> getAll() {
    new RemoteDataTask(application, departmentDao).execute();
    return allDepartment;
  }

  private static class RemoteDataTask extends AsyncTask<Void, Void, Void> {
    private final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.TAIWAN);
    Application application;
    DepartmentDao dao;
    RemoteDataTask(Application application, DepartmentDao dao) {
      this.application = application;
      this.dao = dao;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      String lastUpdate = dao.getLastUpdateTime();
      if(lastUpdate == null) {
        dao.nukeTable(0);
        Department d = new Department();
        d.value = df.format(new Date());
        d.seri_no = -1;
        dao.insert(d);
      } else {
        Date date = null;
        try {
          date = df.parse(lastUpdate);
        } catch (ParseException e) {
          e.printStackTrace();
        }
        Calendar lastUpdateTime = Calendar.getInstance();
        lastUpdateTime.setTime(date);
        Calendar now = Calendar.getInstance();
        if(now.compareTo(lastUpdateTime) > 21600000) {
          dao.nukeTable(0);
          Department d = new Department();
          d.value = df.format(new Date());
          d.seri_no = -1;
          dao.update(d);
        } else {
          return null;
        }
      }

      try {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
        String username = pref.getString(application.getString(R.string.pref_username), "username");
        String password = pref.getString(application.getString(R.string.pref_password), "password");
        Connection.Response response = Jsoup.connect(application.getString(R.string.url_login))
                .followRedirects(true)
                .data("staff_cd", username, "passwd", password)
                .method(Connection.Method.POST)
                .execute();
        String phpsessid = response.cookie("PHPSESSID");
        Document document = response.parse();

        if(document.title().equals("學習暨勞僱時數登錄系統")) {
          response = Jsoup.connect(application.getString(R.string.url_main2))
                  .cookie("PHPSESSID", phpsessid)
                  .execute();
          document = response.parse();
          Elements elements = document.select("option");
          for (Element element : elements) {
            Department department = new Department();
            department.name = element.text();
            department.value = element.attr("value");
            department.type = 0;
            dao.insert(department);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }
  }

  void insert(Department department) {
    new InsertAsyncTask(departmentDao).execute(department);
  }

  private static class InsertAsyncTask extends AsyncTask<Department, Void, Void> {
    DepartmentDao dao;
    InsertAsyncTask(DepartmentDao departmentDao) {
      dao = departmentDao;
    }

    @Override
    protected Void doInBackground(Department... departments) {
      dao.insert(departments[0]);
      return null;
    }
  }
}
