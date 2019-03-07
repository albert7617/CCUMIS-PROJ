package com.example.albert.ccumis;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SelectEmploymentRemoteTask extends AsyncTask<Void, Void, Integer> {
  private Application application;
  private int operation;
  private EmploymentDao dao;
  private PostEmployment postEmployment;
  private Callback callback;

  public SelectEmploymentRemoteTask(Application application, int operation, PostEmployment postEmployment) {
    this.application = application;
    this.operation = operation;
    this.postEmployment = postEmployment;
    AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
    this.dao = db.employmentDao();
  }

  @Override
  protected Integer doInBackground(Void... voids) {
    String phpsessid = login();
    if(phpsessid == null) {
      return null;
    } else {
      switch (operation) {
        case 1:
          return getSelect(phpsessid);
        case 2:
          return getDelete(phpsessid);
        case 3:
          return getPrint(phpsessid);
      }
      return null;
    }
  }

  @Override
  protected void onPostExecute(Integer integer) {
    if(callback != null) {
      callback.result(integer);
    }
  }

  private int getSelect(String phpsessid) {

    for (int tried = 0; tried < 5; tried++) {
      try {
        dao.nukeTable(operation);
        Connection.Response response = Jsoup.connect(application.getString(R.string.url_sp_sel))
                .cookie("PHPSESSID", phpsessid)
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();

        response = Jsoup.connect(application.getString(R.string.url_sel_row))
                .cookie("PHPSESSID", phpsessid)
                .followRedirects(true)
                .data("unit_cd2", postEmployment.department,
                        "sy", String.valueOf(postEmployment.start_year),
                        "sm", String.valueOf(postEmployment.start_month),
                        "sd", String.valueOf(postEmployment.start_day),
                        "ey", String.valueOf(postEmployment.end_year),
                        "em", String.valueOf(postEmployment.end_month),
                        "ed", String.valueOf(postEmployment.end_day),
                        "con_state", String.valueOf(postEmployment.status),
                        "hd", String.valueOf(postEmployment.weekend),
                        "all_go", "送出查詢")
                .method(Connection.Method.POST)
                .maxBodySize(0)
                .execute();
        Document document = response.parse();
        Elements table_rows = document.select("tr");
        for (Element table_row : table_rows) {
          if (table_row.attr("bgcolor").compareToIgnoreCase("#2F2FFF") == 0) {
            continue;
          }
          Elements table_data = table_row.children();
          Employment employment = new Employment();
          employment.operation = operation;
          employment.batch_num = table_data.get(0).text();
          employment.date = table_data.get(1).text() + " " + table_data.get(3).text();
          employment.department = table_data.get(2).text();
          employment.weekend = table_data.get(4).text();
          employment.hour_count = table_data.get(5).text();
          employment.process = table_data.get(6).text();
          employment.content = table_data.get(7).text();
          dao.insert(employment);
        }
        return 200;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


    return 400;
  }
  private int getDelete(String phpsessid) {
    for (int tried = 0; tried < 5; tried++) {
      try {
        dao.nukeTable(operation);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        sharedPreferences.edit().putString("PHPSESSID", phpsessid).apply();

        Connection.Response response = Jsoup.connect(application.getString(R.string.url_del_sel))
                .cookie("PHPSESSID", phpsessid)
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();

        response = Jsoup.connect(application.getString(R.string.url_del_row))
                .cookie("PHPSESSID", phpsessid)
                .followRedirects(true)
                .data("unit_cd1", postEmployment.department,
                        "sy", String.valueOf(postEmployment.start_year),
                        "sm", String.valueOf(postEmployment.start_month),
                        "sd", String.valueOf(postEmployment.start_day),
                        "ey", String.valueOf(postEmployment.end_year),
                        "em", String.valueOf(postEmployment.end_month),
                        "ed", String.valueOf(postEmployment.end_day),
                        "hd", String.valueOf(postEmployment.weekend),
                        "go", "依條件選出")
                .method(Connection.Method.POST)
                .maxBodySize(0)
                .execute();
        Document document = response.parse();
        Elements table_rows = document.select("tr");
        for (Element table_row : table_rows) {
          if (table_row.attr("bgcolor").compareToIgnoreCase("maroon") == 0) {
            continue;
          }
          Elements table_data = table_row.children();
          Employment employment = new Employment();
          employment.operation = operation;
          employment.batch_num = table_data.get(0).selectFirst("input").attr("name");
          employment.date = table_data.get(1).text() + " " + table_data.get(3).text();
          employment.department = table_data.get(2).text();
          employment.weekend = table_data.get(4).text();
          employment.hour_count = table_data.get(5).text();
          employment.content = table_data.get(6).text();
          dao.insert(employment);
        }
        return 200;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return 400;
  }
  private int getPrint(String phpsessid) {
    return 200;
  }

  private String login() {
    try {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
      String username = sharedPreferences.getString(application.getString(R.string.pref_username), "username");
      String password = sharedPreferences.getString(application.getString(R.string.pref_password), "password");

      Connection.Response response = Jsoup.connect(application.getString(R.string.url_login))
              .followRedirects(true)
              .data("staff_cd", username, "passwd", password)
              .method(Connection.Method.POST)
              .execute();
      String phpsessid = response.cookie("PHPSESSID");
      Document document = response.parse();

      if(document.title().equals("學習暨勞僱時數登錄系統")) {
        return phpsessid;
      } else {
        // Login fail
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      // Connection error
      return null;
    }
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void result(int result);
  }

// --Commented out by Inspection START (2019/3/7 17:53):
//  public static void longLog(String str) {
//    if (str.length() > 4000) {
//      Log.d("LongLog", str.substring(0, 4000));
//      longLog(str.substring(4000));
//    } else
//      Log.d("LongLog", str);
//  }
// --Commented out by Inspection STOP (2019/3/7 17:53)
}
