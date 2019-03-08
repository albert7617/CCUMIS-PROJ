package com.example.albert.ccumis;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.albert.ccumis.data.Employment;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class SaveToServerTask extends AsyncTask<Void, Void, Integer> {
  private Callback callback;
  private EmploymentDao dao;
  private Application application;
  private final int OPERATION = 0;
  public SaveToServerTask(Application application) {
    this.application = application;
    this.dao = AppRoomDatabase.getDatabase(application.getApplicationContext()).employmentDao();
  }

  @Override
  protected Integer doInBackground(Void... voids) {
    try {
      List<Employment> employments = dao.getListWithOperation(OPERATION);
      if (employments == null) {
        // No data
        return -3;
      }

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
      boolean flag = true;
      if(document.title().equals("學習暨勞僱時數登錄系統")) {
        response = Jsoup.connect(application.getString(R.string.url_control2))
                .cookie("PHPSESSID", phpsessid)
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();
        for (Employment employment : employments) {
          response = Jsoup.connect(application.getString(R.string.url_post_data))
                  .cookie("PHPSESSID", phpsessid)
                  .followRedirects(true)
                  .data("yy", String.valueOf(employment.year),
                          "mm", String.valueOf(employment.month),
                          "dd", String.valueOf(employment.day),
                          "type", employment.department_cd,
                          "shour", String.valueOf(employment.start_hour),
                          "smin", String.valueOf(employment.start_minute),
                          "ehour", String.valueOf(employment.end_hour),
                          "emin", String.valueOf(employment.end_minute),
                          "workin", employment.content)
                  .method(Connection.Method.POST)
                  .execute();

          document = response.parse();

          if (document.title().compareToIgnoreCase("系統警告訊息") == 0) {
            flag = false;
            employment.status = 409;
            employment.error_msg = document.selectFirst("body > center > table > tbody > tr:nth-child(2) > td").text();
            dao.update(employment);
          } else {
            employment.status = 200;
            dao.update(employment);
          }
        }
        if(flag) {
          response = Jsoup.connect(application.getString(R.string.url_save_to_db))
                  .header("Referer", "http://mis.cc.ccu.edu.tw/parttime/main2.php")
                  .cookie("PHPSESSID", phpsessid)
                  .followRedirects(true)
                  .method(Connection.Method.GET)
                  .execute();
          document = response.parse();
          if (document.selectFirst("body > b > center > font").text().compareToIgnoreCase("資料已寫入資料庫!!") == 0) {
            dao.nukeTable(OPERATION);
            return 1;
          } else {
            // Unknown error
            return -4;
          }
        }
      } else {
        // Login fail
        return -1;
      }
    } catch (Exception e) {
      e.printStackTrace();
      // Connection error
      return -2;
    }

    // Unknown error
    return -4;
  }

  @Override
  protected void onPostExecute(Integer msg) {
    if (callback != null) {
      callback.taskDone(msg);
    }
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void taskDone(int msg);
  }
}
