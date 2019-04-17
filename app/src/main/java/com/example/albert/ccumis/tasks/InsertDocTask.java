package com.example.albert.ccumis.tasks;

import android.app.Application;

import com.example.albert.ccumis.AppRoomDatabase;
import com.example.albert.ccumis.EmploymentDao;
import com.example.albert.ccumis.R;
import com.example.albert.ccumis.data.Employment;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertDocTask extends RemoteTask {
  private EmploymentDao dao;
  private Application application;
  private final int OPERATION = 0;
  private Callback callback;

  public InsertDocTask(Application application) {
    super(application);
    this.dao = AppRoomDatabase.getDatabase(application.getApplicationContext()).employmentDao();
    this.application = application;
  }

  @Override
  protected Map<String, String> doInBackground(Void... voids) {
    Map<String, String> result = new HashMap<>();
    Document document;
    try {
      List<Employment> employments = dao.getListWithOperation(OPERATION);
      if (employments.size() == 0) {
        // No data
        result.put("result", "400");
        result.put("msg", application.getString(R.string.error_no_data));
        return result;
      }
      String phpsessid = login();
      if(phpsessid == null) {
        result.put("result", "400");
        result.put("msg", application.getString(R.string.error_login_fail));
        return result;
      }

      boolean flag = true;

      Connection.Response response = Jsoup.connect(application.getString(R.string.url_control2))
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
                .header("Referer", application.getString(R.string.url_main2))
                .cookie("PHPSESSID", phpsessid)
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();
        document = response.parse();
        if (document.selectFirst("body > b > center > font").text().compareToIgnoreCase("資料已寫入資料庫!!") == 0) {
          dao.nukeTable(OPERATION);
          result.put("result", "200");
          result.put("msg", application.getString(R.string.success_to_db));
          return result;
        } else {
          // Unknown error
          result.put("result", "400");
          result.put("msg", application.getString(R.string.error_unknown));
          return result;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      // Connection error
      result.put("result", "400");
      result.put("msg", application.getString(R.string.error_no_connection));
      return result;
    }

    // Unknown error
    result.put("result", "400");
    result.put("msg", application.getString(R.string.error_unknown));
    return result;
  }

  @Override
  protected void onPostExecute(Map<String, String> stringStringMap) {
    if (callback != null) {
      callback.result(stringStringMap);
    }
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }
}
