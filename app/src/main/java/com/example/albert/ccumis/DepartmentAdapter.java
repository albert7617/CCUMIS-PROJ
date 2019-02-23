package com.example.albert.ccumis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

public class DepartmentAdapter extends ArrayAdapter<String>{
  private List<Department> departments;
  public DepartmentAdapter(@NonNull Context context, int resource) {
    super(context, resource);
  }

  @Override
  public int getPosition(String item) {
    if(departments != null) {
      int i=0;
      for (Department department : departments) {
        if (department.name.compareToIgnoreCase(item) == 0) {
          return i;
        }
        i++;
      }
    }
    return -1;
  }

  @Override
  public String getItem(int position) {
    return departments.get(position).name;
  }

  @Override
  public int getCount() {
    return departments == null ? 0 : departments.size();
  }

  public void setDepartments(List<Department> departments) {
    this.departments = departments;
    notifyDataSetChanged();
  }

  public Department getDepartment(int position) {
    return departments.get(position);
  }
}
