package com.example.albert.ccumis_proj;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.albert.ccumis_proj.EmploymentRepository;
import com.example.albert.ccumis_proj.data.Employment;

import java.util.List;

public class EmploymentViewModel extends AndroidViewModel {
  // --Commented out by Inspection (2019/3/7 17:53):private LiveData<List<Employment>> employments;
  private EmploymentRepository repository;
  public EmploymentViewModel(@NonNull Application application) {
    super(application);
    repository = new EmploymentRepository(application);
  }

  public LiveData<List<Employment>> getEmployments(int operation) {
    return repository.getEmployment(operation);
  }

  public void insert(Employment employment) {
    repository.insert(employment);
  }

  public void delete(int seri_no) {
    repository.delete(seri_no);
  }

  public LiveData<Integer> getSum(int operation) {
    return repository.getSum(operation);
  }

  public void nukeTable(int operation) {
    repository.nukeTable(operation);
  }

  public void update(Employment employment) {
    repository.update(employment);
  }

  public LiveData<Employment> getBySer_no(int seri_no) {
    return repository.getBySeri_no(seri_no);
  }
}
