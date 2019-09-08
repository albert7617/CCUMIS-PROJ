package com.example.albert.ccumis_proj.tasks;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.example.albert.ccumis_proj.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public abstract class RemoteTask extends AsyncTask<Void, Void, Map<String, String>> {
  private Application application;

  RemoteTask(Application application) {
    this.application = application;
  }



  protected String login() {
    try {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
      String username = sharedPreferences.getString(application.getString(R.string.pref_username), "username");
      String password = sharedPreferences.getString(application.getString(R.string.pref_password), "password");

      Connection.Response response = Jsoup.connect(application.getString(R.string.url_login))
              .followRedirects(true)
              .data("staff_cd", username, "passwd", password, "proj_type", "4")
              .method(Connection.Method.POST)
              .execute();
      String phpsessid = response.cookie("PHPSESSID");
      Document document = response.parse();
      if(document.title().equals("兼任助理、臨時工工作日誌登錄系統")) {
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

  File writeToFile(byte[] data, String bsn, String ctrow, String emp_type) throws Exception {
    File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/");
    File file = new File(root, "ccu_mis_"+bsn+"_"+ctrow+"_"+emp_type+".pdf");
    if (!file.exists())
      file.createNewFile();
    FileOutputStream out = new FileOutputStream(file);
    out.write(data);
    out.close();
    return file;
  }

  public interface Callback {
    void result(Map<String, String> result);
  }

  public interface FileCallback {
    void fileSaved(String filePath);
  }
}
