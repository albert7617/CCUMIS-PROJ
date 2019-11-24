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

public class SelectPrintTask extends RemoteTask {
  private Application application;
  private int OPERATION = 3;
  private EmploymentDao dao;
  private PostEmployment postEmployment;
  private Callback callback;

  public SelectPrintTask(Application application, PostEmployment postEmployment) {
    super(application);
    this.application = application;
    this.postEmployment = postEmployment;
    this.dao = AppRoomDatabase.getDatabase(application).employmentDao();
  }

  @Override
  protected Map<String, String> doInBackground(Void... voids) {
    Map<String, String> result = new HashMap<>();
    try {
      boolean flag = false;
      String phpsessid = login();
      if(phpsessid == null) {
        result.put("result", "400");
        result.put("msg", application.getString(R.string.error_login_fail));
        return result;
      }
      dao.nukeTable(OPERATION);
      Connection.Response response = Jsoup.connect(application.getString(R.string.url_prt_sel))
              .cookie("PHPSESSID", phpsessid)
              .followRedirects(true)
              .method(Connection.Method.GET)
              .execute();
      response = Jsoup.connect(application.getString(R.string.url_prt_row))
              .cookie("PHPSESSID", phpsessid)
              .followRedirects(true)
              .data("unit_cd1", postEmployment.department,
                      "sy", String.valueOf(postEmployment.start_year),
                      "sm", String.valueOf(postEmployment.start_month),
                      "sd", String.valueOf(postEmployment.start_day),
                      "ey", String.valueOf(postEmployment.end_year),
                      "em", String.valueOf(postEmployment.end_month),
                      "ed", String.valueOf(postEmployment.end_day),
                      "go", "依條件選出資料")
              .method(Connection.Method.POST)
              .maxBodySize(0)
              .execute();
      Document document = response.parse();
      Elements table_rows = document.select("#form_ck > table:nth-child(1) > tbody > tr");
      for (Element table_row : table_rows) {
        if (table_row.attr("bgcolor").compareToIgnoreCase("#008000") == 0) {
          continue;
        }
        Elements table_data = table_row.children();
        Employment employment = new Employment();
        employment.batch_num = table_data.select("input").attr("name");
        employment.operation = OPERATION;
        employment.date = table_data.get(1).text();
        employment.department = table_data.get(2).text();
        employment.process = table_data.get(3).text();
        employment.weekend = table_data.get(4).text();
        employment.hour_count = table_data.get(5).text();
        employment.content = table_data.get(6).text();
        dao.insert(employment);
        flag = true;
      }
      if(flag) {
        result.put("result", "200");
      } else {
        result.put("result", "201");
      }
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
