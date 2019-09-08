package com.example.albert.ccumis_proj.tasks;

import android.app.Application;
import android.util.Log;

import com.example.albert.ccumis_proj.AppRoomDatabase;
import com.example.albert.ccumis_proj.EmploymentDao;
import com.example.albert.ccumis_proj.PostEmployment;
import com.example.albert.ccumis_proj.R;
import com.example.albert.ccumis_proj.tasks.RemoteTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteDocTask extends RemoteTask {

  private Callback callback;
  private final int OPERATION = 2;
  private Application application;
  private List<String> selected;
  private EmploymentDao dao;
  private PostEmployment postEmployment;

  public DeleteDocTask(Application application, List<String> selected, PostEmployment postEmployment) {
    super(application);
    AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
    dao = db.employmentDao();
    this.application = application;
    this.selected = selected;
    this.postEmployment = postEmployment;
  }

  @Override
  protected Map<String, String> doInBackground(Void... voids) {
    Map<String, String> postData = new HashMap<>();
    Map<String, String> result = new HashMap<>();
    for (String string : selected) {
      postData.put(string, "1");
    }
    try {
      dao.nukeTable(OPERATION);
      String phpsessid = super.login();
      if(phpsessid == null) {
        result.put("result", "400");
        result.put("msg", application.getString(R.string.error_login_fail));
        return result;
      }
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
      response = Jsoup.connect(application.getString(R.string.url_del_to_db))
              .cookie("PHPSESSID", phpsessid)
              .followRedirects(true)
              .data("go", "確定刪除")
              .data(postData)
              .method(Connection.Method.POST)
              .maxBodySize(0)
              .execute();
      Document document = response.parse();
      Log.d("DOC", "doInBackground: "+document.body());
      if (document.selectFirst("body > center > font:nth-child(1)").text().compareToIgnoreCase("刪除完成!!") == 0) {
        result.put("result", "200");
        result.put("msg", "刪除完成!");
        return result;
      } else {
        result.put("result", "400");
        result.put("msg", "刪除失敗!");
        return result;
      }

    } catch (Exception e) {
      e.printStackTrace();
      result.put("result", "400");
      result.put("msg", application.getString(R.string.error_unknown));
      return result;
    }
  }

  @Override
  protected void onPostExecute(Map<String, String> stringStringMap) {
    if (this.callback != null) {
      callback.result(stringStringMap);
    }
    super.onPostExecute(stringStringMap);
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }
}
