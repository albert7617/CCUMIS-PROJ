package com.example.albert.ccumis_proj.tasks;

import android.app.Application;

import com.example.albert.ccumis_proj.AppRoomDatabase;
import com.example.albert.ccumis_proj.EmploymentDao;
import com.example.albert.ccumis_proj.PostEmployment;
import com.example.albert.ccumis_proj.R;
import com.example.albert.ccumis_proj.data.Employment;
import com.example.albert.ccumis_proj.tasks.RemoteTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class SelectDocTask extends RemoteTask {
  private final int OPERATION = 1;
  private Application application;
  private PostEmployment postEmployment;
  private EmploymentDao dao;
  private Callback callback;

  public SelectDocTask(Application application, PostEmployment postEmployment) {
    super(application);
    this.application = application;
    this.postEmployment = postEmployment;
    this.dao = AppRoomDatabase.getDatabase(application).employmentDao();
  }

  @Override
  protected Map<String, String> doInBackground(Void... voids) {
    Map<String, String> result = new HashMap<>();
    try {
      dao.nukeTable(OPERATION);
      String phpsessid = super.login();
      if(phpsessid == null) {
        result.put("result", "400");
        result.put("msg", application.getString(R.string.error_login_fail));
        return result;
      }
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
                      "all_go", "送出查詢")
              .method(Connection.Method.POST)
              .maxBodySize(0)
              .execute();
      Document document = response.parse();
      Elements table_rows = document.select("tr");
      String project = "";
      for (Element table_row : table_rows) {
        if (table_row.attr("bgcolor").compareToIgnoreCase("#2F2FFF") != 0) {
          Elements table_data = table_row.children();
          if(table_data.size() == 1) {
            project = table_data.get(0).text();
          } else {
            Employment employment = new Employment();
            employment.operation = OPERATION;
            employment.batch_num = table_data.get(0).text();
            employment.date = table_data.get(1).text() + " " + table_data.get(2).text();
            employment.department = project;
            employment.hour_count = table_data.get(3).text();
            employment.process = table_data.get(4).text();
            employment.content = table_data.get(5).text();
            dao.insert(employment);
          }
        }


      }
      result.put("result", "200");
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      result.put("result", "400");
      result.put("msg", application.getString(R.string.error_unknown));
      return result;
    }
  }

  @Override
  protected void onPostExecute(Map<String, String> stringStringMap) {
    if(callback != null) {
      callback.result(stringStringMap);
    }
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }
}
