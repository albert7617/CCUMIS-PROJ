package com.example.albert.ccumis_proj;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.albert.ccumis_proj.R;
import com.example.albert.ccumis_proj.data.Department;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class DepartmentRepository {
  private com.example.albert.ccumis_proj.DepartmentDao departmentDao;
  private Application application;
  public DepartmentRepository(Application application) {
    com.example.albert.ccumis_proj.AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
    this.application = application;
    departmentDao = db.departmentDao();
  }

  private static class InsertTask extends AsyncTask<com.example.albert.ccumis_proj.data.Department, Void, Void> {
    com.example.albert.ccumis_proj.DepartmentDao departmentDao;
    public InsertTask(com.example.albert.ccumis_proj.DepartmentDao departmentDao) {
      this.departmentDao = departmentDao;
    }

    @Override
    protected Void doInBackground(com.example.albert.ccumis_proj.data.Department... departments) {
      departmentDao.insert(departments[0]);
      return null;
    }
  }

  public void nuke(int type) {
    new NukeTask(departmentDao).execute(type);
  }

  private static class NukeTask extends AsyncTask<Integer, Void, Void> {
    private com.example.albert.ccumis_proj.DepartmentDao departmentDao;
    public NukeTask(DepartmentDao departmentDao) {
      this.departmentDao = departmentDao;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
      departmentDao.nukeTable(integers[0]);
      return null;
    }
  }

  public LiveData<List<Department>> getAll(int type) {
    nuke(type);
    new GetRemoteTask(departmentDao, application).execute(type);
    return departmentDao.getAll(type);
  }

  private static class GetRemoteTask extends AsyncTask<Integer, Void, Void> {
    private DepartmentDao departmentDao;
    private Application application;
    public GetRemoteTask(DepartmentDao departmentDao, Application application) {
      this.departmentDao = departmentDao;
      this.application = application;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
      departmentDao.nukeTable(integers[0]);
      String url = "";
      switch (integers[0]) {
        case 0:
          url = "https://miswww1.ccu.edu.tw/pt_proj/main2.php";
          break;
        case 1:
          url = "https://miswww1.ccu.edu.tw/pt_proj/sp_sel.php";
          break;
        case 2:
          url = "https://miswww1.ccu.edu.tw/pt_proj/delete_sel.php";
          break;
        case 3:
          url = "https://miswww1.ccu.edu.tw/pt_proj/print_sel.php";
          break;
      }

      try {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
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
          response = Jsoup.connect(url)
                  .cookie("PHPSESSID", phpsessid)
                  .followRedirects(true)
                  .method(Connection.Method.GET)
                  .execute();
          document = response.parse();
          Elements options = document.selectFirst("select").select("option");
          for(Element option : options) {
            Department department = new Department();
            department.type = integers[0];
            department.name = option.text();
            department.value = option.val();
            departmentDao.insert(department);
          }
        } else {
          // Login fail
          return null;
        }
      } catch (Exception e) {
        e.printStackTrace();
        // Connection error
        return null;
      }

      return null;
    }
  }
}
