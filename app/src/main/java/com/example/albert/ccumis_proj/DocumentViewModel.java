package com.example.albert.ccumis_proj;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.albert.ccumis_proj.DocumentRepository;
import com.example.albert.ccumis_proj.data.Department;

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
