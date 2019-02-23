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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DeleteOnServerTask extends AsyncTask<Void, Void, Integer> {
  private Callback callback;
  private int operation;
  private Application application;
  private List<String> selected;
  private EmploymentDao dao;

  public DeleteOnServerTask(Application application, int operation, List<String> selected) {
    AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
    dao = db.employmentDao();
    this.application = application;
    this.operation = operation;
    this.selected = selected;
  }

  @Override
  protected Integer doInBackground(Void... voids) {
    Map<String, String> postData = new HashMap<>();
    for (String string : selected) {
      postData.put(string, "1");
    }
    try {
      dao.nukeTable(operation);
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
      String phpsessid = sharedPreferences.getString("PHPSESSID", "phpsessid");
      Connection.Response response = Jsoup.connect(application.getString(R.string.url_del_to_db))
              .cookie("PHPSESSID", phpsessid)
              .followRedirects(true)
              .data("go", "確定刪除")
              .data(postData)
              .method(Connection.Method.POST)
              .maxBodySize(0)
              .execute();
      Document document = response.parse();
      if (document.selectFirst("body > center > font:nth-child(1)").text().compareToIgnoreCase("刪除完成!!") == 0) {
        return 200;
      } else {
        return 400;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return 400;
    }
  }

  @Override
  protected void onPostExecute(Integer integer) {
    if (this.callback != null) {
      callback.result(integer);
    }
    super.onPostExecute(integer);
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void result(int result);
  }
}
