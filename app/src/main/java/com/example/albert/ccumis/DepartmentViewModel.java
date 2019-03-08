package com.example.albert.ccumis;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.albert.ccumis.data.Department;

import java.util.List;

public class DepartmentViewModel extends AndroidViewModel {
  private DepartmentRepository repository;
  public DepartmentViewModel(@NonNull Application application) {
    super(application);
    repository = new DepartmentRepository(application);
  }

  public LiveData<List<Department>> getAll(int type) {
    return repository.getAll(type);
  }

// --Commented out by Inspection START (2019/3/7 17:53):
//  public void nuke(int type) {
//    repository.nuke(type);
//  }
// --Commented out by Inspection STOP (2019/3/7 17:53)
}
