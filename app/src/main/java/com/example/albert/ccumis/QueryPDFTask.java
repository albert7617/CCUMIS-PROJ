package com.example.albert.ccumis;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.albert.ccumis.fragments.QueryFragment;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

public class QueryPDFTask extends AsyncTask<Void, Void, Map<String, String>> {
  private String serialNO;
  private int empType;
  private Context context;
  public QueryPDFTask(Context context, String serialNo, int empType) {
    this.serialNO = serialNo;
    this.empType = empType;
    this.context = context;
  }

  @Override
  protected Map<String, String> doInBackground(Void... voids) {
    Map<String, String> postData = new HashMap<>(), retval = new HashMap<>();
    postData.put("bsn", this.serialNO);
    postData.put("emp_type", this.empType+"");
    postData.put("go_check", "列印簽到退表");
    postData.put("ctrow", "14");
    String sessid = login();
    if(sessid == null) {
      retval.put("error", "錯誤無法登入");
      return retval;
    }
    try {
      Connection.Response response;
      response = Jsoup.connect(context.getString(R.string.url_prt_pdf))
              .cookie("PHPSESSID", sessid)
              .followRedirects(true)
              .data(postData)
              .data("sid", sessid)
              .method(Connection.Method.POST)
              .maxBodySize(0)
              .ignoreContentType(true)
              .execute();
      Log.d("MIME", "doInBackground: "+response.contentType());
    } catch (Exception e) {
      e.printStackTrace();
      retval.put("error", "未知錯誤");
      return retval;
    }
    retval.put("error", "未知錯誤");
    return retval;
  }

  private String login() {
    try {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
      String username = sharedPreferences.getString(context.getString(R.string.pref_username), "username");
      String password = sharedPreferences.getString(context.getString(R.string.pref_password), "password");

      Connection.Response response = Jsoup.connect(context.getString(R.string.url_login))
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
}
