package com.example.albert.ccumis_proj;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.albert.ccumis_proj.data.Employment;

import java.util.List;

public class EmploymentRepository {
  private com.example.albert.ccumis_proj.EmploymentDao employmentDao;

  EmploymentRepository(Application application) {
    com.example.albert.ccumis_proj.AppRoomDatabase db = AppRoomDatabase.getDatabase(application.getApplicationContext());
    employmentDao = db.employmentDao();
  }

  LiveData<List<com.example.albert.ccumis_proj.data.Employment>> getEmployment(int operation) {
    return employmentDao.getWithOperation(operation);
  }
  LiveData<Integer> getSum(int operation) {
    return employmentDao.getSum(operation);
  }
  LiveData<com.example.albert.ccumis_proj.data.Employment> getBySeri_no(int seri_no){
    return employmentDao.getBySeri_no(seri_no);
  }

  void insert(com.example.albert.ccumis_proj.data.Employment employment) {
    new insertTask(employmentDao).execute(employment);
  }
  void delete(int seri_no) {
    new deleteTask(employmentDao).execute(seri_no);
  }
  void update(com.example.albert.ccumis_proj.data.Employment employment) {
    new UpdateTask(employmentDao).execute(employment);
  }
  void nukeTable(int operation) {
    new NukeTask(employmentDao).execute(operation);
  }

  private static class insertTask extends AsyncTask<com.example.albert.ccumis_proj.data.Employment, Void, Void> {
    private com.example.albert.ccumis_proj.EmploymentDao dao;
    insertTask(com.example.albert.ccumis_proj.EmploymentDao dao) {
      this.dao = dao;
    }
    @Override
    protected Void doInBackground(com.example.albert.ccumis_proj.data.Employment... employments) {
      dao.insert(employments[0]);
      return null;
    }
  }
  private static class deleteTask extends AsyncTask<Integer, Void, Void>{

    private com.example.albert.ccumis_proj.EmploymentDao dao;
    deleteTask(com.example.albert.ccumis_proj.EmploymentDao dao) {
      this.dao = dao;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
      dao.delete(integers[0]);
      return null;
    }
  }

  private static class NukeTask extends AsyncTask<Integer, Void, Void>{

    private com.example.albert.ccumis_proj.EmploymentDao dao;

    NukeTask(com.example.albert.ccumis_proj.EmploymentDao dao) {
      this.dao = dao;
    }
    @Override
    protected Void doInBackground(Integer... integers) {
      dao.nukeTable(integers[0]);
      return null;
    }

  }
  private static class UpdateTask extends AsyncTask<com.example.albert.ccumis_proj.data.Employment, Void, Void> {

    com.example.albert.ccumis_proj.EmploymentDao dao;
    public UpdateTask(EmploymentDao dao) {
      this.dao = dao;
    }

    @Override
    protected Void doInBackground(Employment... employments) {
      dao.update(employments[0]);
      return null;
    }
  }

}
