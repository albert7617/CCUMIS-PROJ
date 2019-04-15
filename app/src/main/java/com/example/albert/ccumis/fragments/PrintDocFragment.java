package com.example.albert.ccumis.fragments;


import android.Manifest;
import android.app.Application;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.albert.ccumis.tasks.PrintPDFTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrintDocFragment extends Fragment {

  private final int OPERATION = 3;
  private final int DISPLAY_OPERATION = 4;
  private EditText startDate, endDate;
  private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.TAIWAN);
  private Calendar start, end;
  private AlertDialog alertDialog;
  private LinearLayout step1, step2, step3;
  private ConstraintLayout step4, step5;
  private View rootView;
  private CoordinatorLayout coordinatorLayout;
  public PrintDocFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    final int DEPARTMENT_TYPE = 3;
    rootView = inflater.inflate(R.layout.fragment_print_doc, container, false);
    coordinatorLayout = rootView.findViewById(R.id.coordinator);
    step1 = rootView.findViewById(R.id.print_step_1);
    step2 = rootView.findViewById(R.id.print_step_2);
    step3 = rootView.findViewById(R.id.print_step_3);
    step4 = rootView.findViewById(R.id.print_step_4);
    step5 = rootView.findViewById(R.id.print_step_5);

    step2.setVisibility(View.GONE);
    step3.setVisibility(View.GONE);
    step4.setVisibility(View.GONE);
    step5.setVisibility(View.GONE);

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

    final RecyclerView displayRecyclerView = rootView.findViewById(R.id.selectedRecyclerView);
    final SelectAdapter displaySelectAdapter = new SelectAdapter(getActivity());
    displayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    displayRecyclerView.setNestedScrollingEnabled(false);
    displayRecyclerView.setAdapter(displaySelectAdapter);

    final EmploymentViewModel employmentViewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);
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
          employmentViewModel.nukeTable(DISPLAY_OPERATION);
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
        if(employments.size() != 0 && step2.getVisibility() == View.VISIBLE) {
          step2.setVisibility(View.GONE);
          step3.setVisibility(View.VISIBLE);
        }
      }
    });

    employmentViewModel.getEmployments(DISPLAY_OPERATION).observe(this, new Observer<List<Employment>>() {
      @Override
      public void onChanged(@Nullable List<Employment> employments) {
        displaySelectAdapter.setEmployments(employments);
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

    Button step_3_next = rootView.findViewById(R.id.step_3_next);
    step_3_next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(selectAdapter.getSelectedEmployments().size() == 0) {
          Toast.makeText(getContext(), R.string.print_select_no_selection, Toast.LENGTH_SHORT).show();
        } else {
          List<Employment> selections = selectAdapter.getSelectedEmploymentsObject();
          for (Employment selection : selections) {
            Employment employment = new Employment();
            employment.operation = DISPLAY_OPERATION;
            employment.batch_num = selection.batch_num;
            employment.date = selection.date;
            employment.department = selection.department;
            employment.process = selection.process;
            employment.weekend = selection.weekend;
            employment.hour_count = selection.hour_count;
            employment.content = selection.content;
            employmentViewModel.insert(employment);
          }
          step3.setVisibility(View.GONE);
          step4.setVisibility(View.VISIBLE);
        }

      }
    });

    final EditText editWage = rootView.findViewById(R.id.editWage),
            displayWage = rootView.findViewById(R.id.displayWage),
            displayIdentity = rootView.findViewById(R.id.displayIdentity);
    final TextView hiddenIdentityType = rootView.findViewById(R.id.displayIdentityHidden),
            hiddenInsurance = rootView.findViewById(R.id.displayInsuranceHidden),
            hiddenEmpType = rootView.findViewById(R.id.displayEmpTypeHidden);
    final RadioGroup employmentType = rootView.findViewById(R.id.editEmpType), insuranceType = rootView.findViewById(R.id.editInsurance);
    final RadioGroup displayEmploymentType = rootView.findViewById(R.id.displayEmpType), displayInsuranceType = rootView.findViewById(R.id.displayInsurance);
    final Spinner identitySpinner = rootView.findViewById(R.id.editIdentity);
    final CheckBox agreeTerms = rootView.findViewById(R.id.agreeTerms), displayAgreeTerms = rootView.findViewById(R.id.displayAgreeTerms);

    final HashMap<String, String> identityMap = new HashMap<String, String>();
    String[] identities = getResources().getStringArray(R.array.identities);
    ArrayList<String> identitiesDisplay = new ArrayList<>();
    for(String identity : identities) {
      String index = identity.split("\\|")[0], value = identity.split("\\|")[1];
      identityMap.put(value, index);
      identitiesDisplay.add(value);
    }
    ArrayAdapter<String> identityAdapter =new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, identitiesDisplay);
    identityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    identitySpinner.setAdapter(identityAdapter);

    Button step_4_next = rootView.findViewById(R.id.step_4_next);
    step_4_next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        displayWage.setText(editWage.getText().toString());
        switch (employmentType.getCheckedRadioButtonId()) {
          case R.id.admin_assistant:
            hiddenEmpType.setText("1");
            displayEmploymentType.check(R.id.display_admin_assistant);
            break;
          case R.id.part_time_labor:
            hiddenEmpType.setText("2");
            displayEmploymentType.check(R.id.display_part_time_labor);
            break;
          case R.id.teacher_assistant:
            hiddenEmpType.setText("3");
            displayEmploymentType.check(R.id.display_teacher_assistant);
            break;
        }
        switch (insuranceType.getCheckedRadioButtonId()) {
          case R.id.yes:
            hiddenInsurance.setText("1");
            displayInsuranceType.check(R.id.displayYes);
            break;
          case R.id.no:
            hiddenInsurance.setText("0");
            displayInsuranceType.check(R.id.displayNo);
            break;
        }
        displayIdentity.setText(identitySpinner.getSelectedItem().toString());
        hiddenIdentityType.setText(identityMap.get(identitySpinner.getSelectedItem().toString()));
        if (!agreeTerms.isChecked()) {
          Toast.makeText(getContext(), R.string.print_terms_not_agreed, Toast.LENGTH_SHORT).show();
        } else {
          if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
          } else {
            displayAgreeTerms.setChecked(true);
            step4.setVisibility(View.GONE);
            step5.setVisibility(View.VISIBLE);
          }
        }
      }
    });

    Button step_5_next = rootView.findViewById(R.id.step_5_next);
    step_5_next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(getActivity() != null) {
          Department department = adapter.getDepartment(spinner.getSelectedItemPosition());
          PostEmployment postEmployment = new PostEmployment();
          postEmployment.department = department.value;
          postEmployment.start_year = start.get(Calendar.YEAR) - 1911;
          postEmployment.start_month = start.get(Calendar.MONTH) + 1;
          postEmployment.start_day = start.get(Calendar.DAY_OF_MONTH);
          postEmployment.end_year = end.get(Calendar.YEAR) - 1911;
          postEmployment.end_month = end.get(Calendar.MONTH) + 1;
          postEmployment.end_day = end.get(Calendar.DAY_OF_MONTH);
          Map<String, String> printRows = new HashMap<>();
          List<String> selections = displaySelectAdapter.getSelectedEmployments();
          for (String bsn : selections) {
            printRows.put(bsn, "1");
          }
          final PrintPDFTask printPDFTask = new PrintPDFTask(getActivity().getApplicationContext(),
                                            displayWage.getText().toString(),
                                            hiddenIdentityType.getText().toString(),
                                            hiddenInsurance.getText().toString(),
                                            hiddenEmpType.getText().toString(),
                                            printRows,
                                            postEmployment);
          printPDFTask.setCallback(printCallback);
          printPDFTask.execute();
          alertDialog = new AlertDialog.Builder(getActivity())
                  .setView(R.layout.dialog_progress)
                  .setCancelable(false)
                  .setPositiveButton(R.string.delete_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      printPDFTask.cancel(true);
                      alertDialog.dismiss();
                    }
                  })
                  .show();
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
      if(result == 400) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.error))
                .setMessage(getString(R.string.error_no_connection))
                .setPositiveButton(R.string.confirm, null)
                .show();
      } else if (result == 201) {
        Toast.makeText(getContext(), R.string.print_select_nothing, Toast.LENGTH_SHORT).show();
      }
    }
  };

  private PrintPDFTask.Callback printCallback = new PrintPDFTask.Callback() {
    @Override
    public void result(Map<String, String> result) {
      if(result.containsKey("error")) {
        Toast.makeText(getContext(), result.get("error"), Toast.LENGTH_SHORT).show();
      }
    }

    @Override
    public void pdfSaved(final String filePath) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          alertDialog.dismiss();
          Snackbar.make(coordinatorLayout, "工讀單已儲存到下載資料夾", Snackbar.LENGTH_SHORT)
                  .setAction("開啟", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      Intent target = new Intent(Intent.ACTION_VIEW);
                      target.setDataAndType(Uri.parse(filePath),"application/pdf");
                      Intent intent = Intent.createChooser(target, "Open File");
                      try {
                        startActivity(intent);
                      } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                      }
                    }
                  })
                  .setDuration(5000)
                  .show();
        }
      });
    }
  };
}
