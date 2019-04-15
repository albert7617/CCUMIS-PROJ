package com.example.albert.ccumis.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.example.albert.ccumis.PostEmployment;
import com.example.albert.ccumis.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class PrintPDFTask extends AsyncTask<Void, Void, Map<String, String>> {
  private PrintPDFTask.Callback callback;
  @SuppressLint("StaticFieldLeak")
  private Context context;
  private PostEmployment postEmployment;
  private String wage, identity, insurance, empType;
  private Map<String, String> printRows;

  public PrintPDFTask(Context context, String wage, String identity, String insurance, String empType, Map<String, String> printRows, PostEmployment postEmployment) {
    this.context = context;
    this.wage = wage;
    this.identity = identity;
    this.insurance = insurance;
    this.empType = empType;
    this.printRows = printRows;
    this.postEmployment = postEmployment;
  }

  public PrintPDFTask(Context context, Map<String, String> printRows, int wage, String identity, String insurance, String empType) {

    this.printRows = printRows;
    this.empType = empType;
    this.context = context;
  }
  @Override
  protected Map<String, String> doInBackground(Void... voids) {
    Map<String, String> postData = new HashMap<>(printRows), retval = new HashMap<>();

    String sessid = login();
    if(sessid == null) {
      retval.put("error", "錯誤無法登入");
      return retval;
    }
    postData.put("hour_money", this.wage);
    postData.put("sutype", identity);
    postData.put("iswork", insurance);
    postData.put("emp_type", empType);
    postData.put("agreethis", "1");
    postData.put("go", "確定送出並列印");
    postData.put("sid", sessid);
    try {
      Connection.Response response;
      boolean flag = false;

      response = Jsoup.connect(context.getString(R.string.url_prt_chk))
              .cookie("PHPSESSID", sessid)
              .followRedirects(true)
              .data(postData)
              .method(Connection.Method.POST)
              .execute();
      Document document = response.parse();
      if (document.select("body > center > font > u").hasText()) {
        postData.clear();
        //Get the form by id.
        Element testForm = document.getElementById("form_ck");
        //Get input parameters of the form.
        Elements inputElements = testForm.getElementsByTag("input");
        //Iterate parameters and print name and value.
        for (Element inputElement : inputElements) {
          String key = inputElement.attr("name");
          String value = inputElement.attr("value");
          if(!key.equalsIgnoreCase("go_back")) {
            postData.put(key, value);
          }
        }
        response = Jsoup.connect(context.getString(R.string.url_prt_pdf))
                .cookie("PHPSESSID", sessid)
                .followRedirects(true)
                .data(postData)
                .method(Connection.Method.POST)
                .maxBodySize(0)
                .ignoreContentType(true)
                .execute();
        if(response.contentType().contains("pdf")) {
          flag = true;
          writeToFile(response.bodyAsBytes(), postData.get("bsn"), postData.get("ctrow"), postData.get("emp_type"));
        }
      }
      if(flag) {
        return postData;
      } else {
        retval.put("error", "錯誤查無資料");
        return retval;
      }
    } catch (Exception e) {
      e.printStackTrace();
      retval.put("error", "未知錯誤");
      return retval;
    }
  }
  @Override
  protected void onPostExecute(Map<String, String> stringStringMap) {
    if(this.callback != null) {
      this.callback.result(stringStringMap);
    }
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
        response = Jsoup.connect(context.getString(R.string.url_prt_sel))
                .cookie("PHPSESSID", phpsessid)
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();
        response = Jsoup.connect(context.getString(R.string.url_prt_row))
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

  private void writeToFile(byte[] data, String bsn, String ctrow, String emp_type) throws Exception {

    File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/");
    File file = new File(root, "ccu_mis_"+bsn+"_"+ctrow+"_"+emp_type+".pdf");
    if (!file.exists()) file.createNewFile();
    FileOutputStream out = new FileOutputStream(file);
    out.write(data);
    out.close();
    if(this.callback != null) {
      callback.pdfSaved(file.getPath());
    }
  }

  public interface Callback {
    void result(Map<String, String> result);
    void pdfSaved(String filePath);
  }

  public void setCallback (PrintPDFTask.Callback callback) {
    this.callback = callback;
  }
}
