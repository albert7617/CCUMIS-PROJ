package com.example.albert.ccumis_proj.fragments;

import android.app.Application;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.albert.ccumis_proj.data.Department;
import com.example.albert.ccumis_proj.adapters.DepartmentAdapter;
import com.example.albert.ccumis_proj.DepartmentViewModel;
import com.example.albert.ccumis_proj.data.Employment;
import com.example.albert.ccumis_proj.EmploymentViewModel;
import com.example.albert.ccumis_proj.PostEmployment;
import com.example.albert.ccumis_proj.R;
import com.example.albert.ccumis_proj.adapters.SelectAdapter;
import com.example.albert.ccumis_proj.tasks.RemoteTask;
import com.example.albert.ccumis_proj.tasks.SelectDocTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SelectDocFragment extends Fragment {

  private final int OPERATION = 1;
  private Context context;
  private EditText startDate, endDate, status;

  private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.TAIWAN);
  private Calendar start, end;
  private int status_cd = 9;
  private AlertDialog alertDialog;
  public SelectDocFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_select_doc, container, false);

    final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
    final SelectAdapter selectAdapter = new SelectAdapter(context);
    recyclerView.setLayoutManager(new LinearLayoutManager(context));
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setAdapter(selectAdapter);
    ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    start = Calendar.getInstance();
    start.set(Calendar.DAY_OF_MONTH, 1);
    end = Calendar.getInstance();
    end.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
    final Spinner spinner = rootView.findViewById(R.id.department_spinner);
    final DepartmentAdapter adapter = new DepartmentAdapter(context, R.layout.support_simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    DepartmentViewModel departmentViewModel = ViewModelProviders.of(this).get(DepartmentViewModel.class);
    EmploymentViewModel employmentViewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);

    employmentViewModel.nukeTable(OPERATION);

    final TextView empty = rootView.findViewById(R.id.emptyDataSet);
    empty.setVisibility(selectAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

    departmentViewModel.getAll(OPERATION).observe(this, new Observer<List<Department>>() {
      @Override
      public void onChanged(@Nullable List<Department> departments) {
        adapter.setDepartments(departments);
      }
    });

    startDate = rootView.findViewById(R.id.editTimeStart);
    startDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int month, int day) {
            start.set(Calendar.YEAR, year);
            start.set(Calendar.MONTH, month);
            start.set(Calendar.DAY_OF_MONTH, day);
            updateView();
          }
        }, year, month, day).show();
      }
    });

    endDate = rootView.findViewById(R.id.editTimeEnd);
    endDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            end.set(Calendar.YEAR, year);
            end.set(Calendar.MONTH, month);
            end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateView();
          }
        }, year, month, day).show();
      }
    });

    status = rootView.findViewById(R.id.processing_status);
    status.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.status))
                .setItems(R.array.statuses, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    status_cd = which == 3 ? 9 : which;
                    String[] statuses = getResources().getStringArray(R.array.statuses);
                    status.setText(statuses[which]);
                  }
                })
                .show();
      }
    });

    Button submit = rootView.findViewById(R.id.submit);
    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(spinner.getSelectedItemPosition() == -1) {
          return;
        }
        String errorMsg = "";
        if(start == null) {
          errorMsg += getString(R.string.error_no_start_date);
          errorMsg += "\n";
        }
        if(end == null) {
          errorMsg += getString(R.string.error_no_end_date);
          errorMsg += "\n";
        }
        if(start != null && end != null) {
          if(start.getTime().after(end.getTime())) {
            errorMsg += getString(R.string.error_start_later_than_end_date);
            errorMsg += "\n";
          }
        }
        if(errorMsg.compareToIgnoreCase("") == 0) {
          Department department = adapter.getDepartment(spinner.getSelectedItemPosition());
          PostEmployment postEmployment = new PostEmployment();
          postEmployment.department = department.value;
          postEmployment.status = status_cd;
          postEmployment.start_year = start.get(Calendar.YEAR) - 1911;
          postEmployment.start_month = start.get(Calendar.MONTH) + 1;
          postEmployment.start_day = start.get(Calendar.DAY_OF_MONTH);
          postEmployment.end_year = end.get(Calendar.YEAR) - 1911;
          postEmployment.end_month = end.get(Calendar.MONTH) + 1;
          postEmployment.end_day = end.get(Calendar.DAY_OF_MONTH);
          final SelectDocTask task = new SelectDocTask((Application) context.getApplicationContext(), postEmployment);
          task.setCallback(callback);
          alertDialog = new AlertDialog.Builder(context)
                  .setView(R.layout.dialog_progress)
                  .setCancelable(false)
                  .setPositiveButton(R.string.delete_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      task.cancel(true);
                      alertDialog.dismiss();
                    }
                  })
                  .show();
          task.execute();

        } else {
          new AlertDialog.Builder(context)
                  .setTitle(getString(R.string.error))
                  .setMessage(errorMsg)
                  .setPositiveButton(getString(R.string.confirm), null)
                  .show();
        }

      }
    });

    employmentViewModel.getEmployments(OPERATION).observe(this, new Observer<List<Employment>>() {
      @Override
      public void onChanged(@Nullable List<Employment> employments) {
        selectAdapter.setEmployments(employments);
        empty.setVisibility(selectAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
      }
    });
    updateView();
    return rootView;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  private void updateView() {
    startDate.setText(df.format(start.getTime()));
    endDate.setText(df.format(end.getTime()));
  }

  RemoteTask.Callback callback = new RemoteTask.Callback() {
    @Override
    public void result(Map<String, String> result) {
      alertDialog.dismiss();
      if(result.get("result").equalsIgnoreCase("400")) {
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.error))
                .setMessage(result.get("msg"))
                .setPositiveButton(R.string.confirm, null)
                .show();
      }
    }
  };
}
