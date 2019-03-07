package com.example.albert.ccumis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class DepartmentAdapter extends ArrayAdapter<String>{
  private List<Department> departments,departments_bak;
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

  @NonNull
  @Override
  public Filter getFilter() {
    Filter filter = new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        ArrayList<Department> filtered = new ArrayList<>();

        departments = departments_bak;
        // perform your search here using the searchConstraint String.
        if(constraint != null) {
          constraint = constraint.toString().toLowerCase();
          for (int i = 0; i < departments.size(); i++) {
            String dataNames = departments.get(i).name;
            if (dataNames.toLowerCase().contains(constraint))  {
              filtered.add(departments.get(i));
            }
          }
          results.count = filtered.size();
          results.values = filtered;
        } else {
          results.count = departments.size();
          results.values = departments;
        }
        return results;
      }

      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
        departments = (List<Department>) results.values;
        notifyDataSetChanged();
      }
    };
    return filter;
  }

  public void setDepartments(List<Department> departments) {
    this.departments = departments;
    this.departments_bak = departments;
    notifyDataSetChanged();
  }

  public Department getDepartment(int position) {
    return departments.get(position);
  }

}
