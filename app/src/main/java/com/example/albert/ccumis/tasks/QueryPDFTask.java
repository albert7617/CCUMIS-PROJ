package com.example.albert.ccumis.tasks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.example.albert.ccumis.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public class QueryPDFTask extends AsyncTask<Void, Void, Map<String, String>> {
  private Callback callback;
  @SuppressLint("StaticFieldLeak")
  private Context context;
  private String serialNO;
  private int empType;
  public QueryPDFTask(Context context, String serialNo, int empType) {
    this.serialNO = serialNo;
    this.empType = empType;
    this.context = context;
  }

  @Override
  protected Map<String, String> doInBackground(Void... voids) {
    Map<String, String> postData = new HashMap<>(), retval = new HashMap<>();

    String sessid = login();
    if(sessid == null) {
      retval.put("error", "錯誤無法登入");
      return retval;
    }
    postData.put("bsn", this.serialNO);
    postData.put("emp_type", Integer.toString(this.empType));
    postData.put("go_check", "列印簽到退表");
    postData.put("sid", sessid);
    try {
      Connection.Response response;
      boolean flag = false;
      for (int i = 1; i < 100; i++) {
        postData.put("ctrow", Integer.toString(i));
        response = Jsoup.connect(context.getString(R.string.url_prt_pdf))
                .cookie("PHPSESSID", sessid)
                .followRedirects(true)
                .data(postData)
                .method(Connection.Method.POST)
                .maxBodySize(0)
                .ignoreContentType(true)
                .execute();
        if(this.callback != null) {
          this.callback.updateProgress(i);
        }
        if(response.contentType().contains("pdf")) {
          flag = true;
          writeToFile(response.bodyAsBytes());
          break;
        }
        postData.remove("ctrow");
      }
      if(this.callback != null) {
        this.callback.updateProgress(-1);
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

  private void writeToFile(byte[] data) throws Exception {

    File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/");
    File file = new File(root, "ccu_mis_"+SHAsum(data)+".pdf");
    if (!file.exists()) file.createNewFile();
    FileOutputStream out = new FileOutputStream(file);
    out.write(data);
    out.close();
    if(this.callback != null) {
      callback.pdfSaved(file.getPath());
    }
  }

  public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-1");
    return byteArray2Hex(md.digest(convertme));
  }

  private static String byteArray2Hex(final byte[] hash) {
    Formatter formatter = new Formatter();
    for (byte b : hash) {
      formatter.format("%02x", b);
    }
    return formatter.toString();
  }

  public interface Callback {
    void result(Map<String, String> result);
    void updateProgress(int progress);
    void pdfSaved(String filePath);
  }

  public void setCallback (Callback callback) {
    this.callback = callback;
  }
}
