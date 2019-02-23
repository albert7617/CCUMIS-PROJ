package com.example.albert.ccumis;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class DocumentViewModel extends AndroidViewModel {
  private DocumentRepository repository;
  private LiveData<List<Department>> allDepartment;
  public DocumentViewModel(@NonNull Application application) {
    super(application);
    repository = new DocumentRepository(application);
    allDepartment = repository.getAll();
  }

  public void insert(Department department) {
    repository.insert(department);
  }
  public LiveData<List<Department>> getAll() {
    allDepartment = repository.getAll();
    return allDepartment;
  }

}
