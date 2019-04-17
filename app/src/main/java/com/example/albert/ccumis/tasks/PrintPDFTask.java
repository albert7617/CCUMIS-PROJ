package com.example.albert.ccumis.tasks;

import android.app.Application;
import android.util.Log;

import com.example.albert.ccumis.PostEmployment;
import com.example.albert.ccumis.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PrintPDFTask extends RemoteTask {
  private RemoteTask.Callback callback;
  private RemoteTask.FileCallback fileCallback;
  private Application application;
  private PostEmployment postEmployment;
  private String wage, identity, insurance, empType;
  private Map<String, String> printRows;

  public PrintPDFTask(Application application, String wage, String identity, String insurance, String empType, Map<String, String> printRows, PostEmployment postEmployment) {
    super(application);
    this.application = application;
    this.wage = wage;
    this.identity = identity;
    this.insurance = insurance;
    this.empType = empType;
    this.printRows = printRows;
    this.postEmployment = postEmployment;
  }

  @Override
  protected Map<String, String> doInBackground(Void... voids) {
    Map<String, String> postData = new HashMap<>(printRows), result = new HashMap<>();

    String sessid = login();
    if(sessid == null) {
      result.put("result", "400");
      result.put("msg", application.getString(R.string.error_login_fail));
      return result;
    }
    try {
      Connection.Response response = Jsoup.connect(application.getString(R.string.url_prt_sel))
              .cookie("PHPSESSID", sessid)
              .followRedirects(true)
              .method(Connection.Method.GET)
              .maxBodySize(0)
              .execute();
      response = Jsoup.connect(application.getString(R.string.url_prt_row))
              .cookie("PHPSESSID", sessid)
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

      postData.put("hour_money", this.wage);
      postData.put("sutype", identity);
      postData.put("iswork", insurance);
      postData.put("emp_type", empType);
      postData.put("agreethis", "1");
      postData.put("go", "確定送出並列印");

      boolean flag = false;

      response = Jsoup.connect(application.getString(R.string.url_prt_chk))
              .cookie("PHPSESSID", sessid)
              .followRedirects(true)
              .data(postData)
              .method(Connection.Method.POST)
              .execute();
      Document document = response.parse();
      if (document.select("body > center > font > u").hasText()) {
        postData.clear();
        Element testForm = document.getElementById("form_ck");
        Elements inputElements = testForm.getElementsByTag("input");
        for (Element inputElement : inputElements) {
          String key = inputElement.attr("name");
          String value = inputElement.attr("value");
          if(!key.equalsIgnoreCase("go_back")) {
            postData.put(key, value);
          }
        }
        response = Jsoup.connect(application.getString(R.string.url_prt_pdf))
                .cookie("PHPSESSID", sessid)
                .followRedirects(true)
                .data(postData)
                .method(Connection.Method.POST)
                .maxBodySize(0)
                .ignoreContentType(true)
                .execute();
        if(response.contentType().contains("pdf")) {
          flag = true;
          File file = super.writeToFile(response.bodyAsBytes(), postData.get("bsn"), postData.get("ctrow"), postData.get("emp_type"));
          if(this.fileCallback != null) {
            fileCallback.fileSaved(file.getPath());
          }
          result.put("result", "200");
          return result;
        }
      }
      result.put("result", "400");
      result.put("msg", application.getString(R.string.error_nothing));
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
    if(this.callback != null) {
      this.callback.result(stringStringMap);
    }
  }

  public void setCallback (RemoteTask.Callback callback, RemoteTask.FileCallback fileCallback) {
    this.callback = callback;
    this.fileCallback = fileCallback;
  }
}
