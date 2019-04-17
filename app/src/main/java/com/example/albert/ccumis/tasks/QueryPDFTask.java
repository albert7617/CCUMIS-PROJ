package com.example.albert.ccumis.tasks;


import android.app.Application;

import com.example.albert.ccumis.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class QueryPDFTask extends RemoteTask {
  private Callback callback;
  private Application application;
  private String serialNO;
  private int empType;
  public QueryPDFTask(Application application, String serialNo, int empType) {
    super(application);
    this.serialNO = serialNo;
    this.empType = empType;
    this.application = application;
  }

  @Override
  protected Map<String, String> doInBackground(Void... voids) {
    Map<String, String> postData = new HashMap<>(), retval = new HashMap<>();

    String sessid = super.login();
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
        response = Jsoup.connect(application.getString(R.string.url_prt_pdf))
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
          File file = super.writeToFile(response.bodyAsBytes(), postData.get("bsn"), postData.get("ctrow"), postData.get("emp_type"));
          if(this.callback != null) {
            callback.pdfSaved(file.getPath());
          }
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
        retval.put("error", "查無資料");
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

  public interface Callback {
    void result(Map<String, String> result);
    void updateProgress(int progress);
    void pdfSaved(String filePath);
  }

  public void setCallback (Callback callback) {
    this.callback = callback;
  }
}
