package com.example.albert.ccumis.fragments;


import android.app.Application;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.albert.ccumis.DepartmentViewModel;
import com.example.albert.ccumis.EmploymentViewModel;
import com.example.albert.ccumis.PostEmployment;
import com.example.albert.ccumis.R;
import com.example.albert.ccumis.SelectEmploymentRemoteTask;
import com.example.albert.ccumis.adapters.DepartmentAdapter;
import com.example.albert.ccumis.adapters.SelectAdapter;
import com.example.albert.ccumis.data.Department;
import com.example.albert.ccumis.data.Employment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrintDocFragment extends Fragment {

  private final int OPERATION = 3;
  private EditText startDate, endDate;
  private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.TAIWAN);
  private Calendar start, end;
  private AlertDialog alertDialog;
  public PrintDocFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final int DEPARTMENT_TYPE = 3;
    View rootView = inflater.inflate(R.layout.fragment_print_doc, container, false);

    final LinearLayout step1 = rootView.findViewById(R.id.print_step_1),
            step2 = rootView.findViewById(R.id.print_step_2),
            step3 = rootView.findViewById(R.id.print_step_3);
    step2.setVisibility(View.GONE);
    step3.setVisibility(View.GONE);

    start = Calendar.getInstance();
    start.set(Calendar.DAY_OF_MONTH, 1);
    end = Calendar.getInstance();
    end.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
    startDate = rootView.findViewById(R.id.editDateStart);
    endDate = rootView.findViewById(R.id.editDateEnd);
    updateView();
    final Spinner spinner = rootView.findViewById(R.id.printDepartmentSpinner);
    final DepartmentAdapter adapter = new DepartmentAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);

    final RecyclerView recyclerView = rootView.findViewById(R.id.printRecyclerView);
    final SelectAdapter selectAdapter = new SelectAdapter(getActivity());
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setAdapter(selectAdapter);

    EmploymentViewModel employmentViewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);
    employmentViewModel.nukeTable(OPERATION);

    DepartmentViewModel departmentViewModel = ViewModelProviders.of(this).get(DepartmentViewModel.class);
    departmentViewModel.getAll(DEPARTMENT_TYPE).observe(this, new Observer<List<Department>>() {
      @Override
      public void onChanged(@Nullable List<Department> departments) {
        adapter.setDepartments(departments);
      }
    });

    startDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

    endDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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


    Button submit = rootView.findViewById(R.id.printSubmit);
    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(spinner.getSelectedItemPosition() == -1) {
          return;
        }
        if(start.getTime().after(end.getTime())) {
          new AlertDialog.Builder(getActivity())
                  .setTitle(getString(R.string.error))
                  .setMessage(getString(R.string.error_start_later_than_end_date))
                  .setPositiveButton(getString(R.string.confirm), null)
                  .show();
        } else {
          Department department = adapter.getDepartment(spinner.getSelectedItemPosition());
          PostEmployment postEmployment = new PostEmployment();
          postEmployment.department = department.value;
          postEmployment.start_year = start.get(Calendar.YEAR) - 1911;
          postEmployment.start_month = start.get(Calendar.MONTH) + 1;
          postEmployment.start_day = start.get(Calendar.DAY_OF_MONTH);
          postEmployment.end_year = end.get(Calendar.YEAR) - 1911;
          postEmployment.end_month = end.get(Calendar.MONTH) + 1;
          postEmployment.end_day = end.get(Calendar.DAY_OF_MONTH);
          final SelectEmploymentRemoteTask task = new SelectEmploymentRemoteTask(((Application) getContext().getApplicationContext()), OPERATION, postEmployment);
          task.setCallback(callback);
          alertDialog = new AlertDialog.Builder(getActivity())
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
        }
      }
    });

    Button confirmWarning = rootView.findViewById(R.id.confirmWarning);
    confirmWarning.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        step1.setVisibility(View.GONE);
        step2.setVisibility(View.VISIBLE);
      }
    });

    employmentViewModel.getEmployments(OPERATION).observe(this, new Observer<List<Employment>>() {
      @Override
      public void onChanged(@Nullable List<Employment> employments) {
        selectAdapter.setEmployments(employments);
        if (employments.size() != 0) {
          step2.setVisibility(View.GONE);
          step3.setVisibility(View.VISIBLE);
        }
      }
    });

    final CheckBox checkBox = rootView.findViewById(R.id.checkbox);
    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
          selectAdapter.selectAll();
        } else {
          selectAdapter.unSelectAll();
        }
      }
    });

    ConstraintLayout checkboxConstraintLayout = rootView.findViewById(R.id.checkboxConstraintLayout);
    checkboxConstraintLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(checkBox.isChecked()) {
          checkBox.setChecked(false);
        } else {
          checkBox.setChecked(true);
        }
      }
    });

    return rootView;
  }

  private void updateView() {
    startDate.setText(df.format(start.getTime()));
    endDate.setText(df.format(end.getTime()));
  }


  private SelectEmploymentRemoteTask.Callback callback = new SelectEmploymentRemoteTask.Callback() {
    @Override
    public void result(int result) {
      alertDialog.dismiss();
      if(result != 200) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.error))
                .setMessage(getString(R.string.error_no_connection))
                .setPositiveButton(R.string.confirm, null)
                .show();
      }
    }
  };
}
