package com.example.albert.ccumis;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.albert.ccumis.data.Employment;

import java.util.List;

public class EmploymentRepository {
  private EmploymentDao employmentDao;

  EmploymentRepository(Application application) {
    AppRoomDatabase db = AppRoomDatabase.getDatabase(application.getApplicationContext());
    employmentDao = db.employmentDao();
  }

  LiveData<List<Employment>> getEmployment(int operation) {
    return employmentDao.getWithOperation(operation);
  }
  LiveData<Integer> getSum(int operation) {
    return employmentDao.getSum(operation);
  }
  LiveData<Employment> getBySeri_no(int seri_no){
    return employmentDao.getBySeri_no(seri_no);
  }

  void insert(Employment employment) {
    new insertTask(employmentDao).execute(employment);
  }
  void delete(int seri_no) {
    new deleteTask(employmentDao).execute(seri_no);
  }
  void update(Employment employment) {
    new UpdateTask(employmentDao).execute(employment);
  }
  void nukeTable(int operation) {
    new NukeTask(employmentDao).execute(operation);
  }

  private static class insertTask extends AsyncTask<Employment, Void, Void> {
    private EmploymentDao dao;
    insertTask(EmploymentDao dao) {
      this.dao = dao;
    }
    @Override
    protected Void doInBackground(Employment... employments) {
      dao.insert(employments[0]);
      return null;
    }
  }
  private static class deleteTask extends AsyncTask<Integer, Void, Void>{

    private EmploymentDao dao;
    deleteTask(EmploymentDao dao) {
      this.dao = dao;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
      dao.delete(integers[0]);
      return null;
    }
  }

  private static class NukeTask extends AsyncTask<Integer, Void, Void>{

    private EmploymentDao dao;

    NukeTask(EmploymentDao dao) {
      this.dao = dao;
    }
    @Override
    protected Void doInBackground(Integer... integers) {
      dao.nukeTable(integers[0]);
      return null;
    }

  }
  private static class UpdateTask extends AsyncTask<Employment, Void, Void> {

    EmploymentDao dao;
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
