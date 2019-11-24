package com.example.albert.ccumis_proj;

import android.animation.Animator;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.example.albert.ccumis_proj.DepartmentViewModel;
import com.example.albert.ccumis_proj.EmploymentViewModel;
import com.example.albert.ccumis_proj.R;
import com.example.albert.ccumis_proj.fragments.DatePickerDialogFragment;
import com.example.albert.ccumis_proj.fragments.DepartmentSearchDialogFragment;
import com.example.albert.ccumis_proj.data.Department;
import com.example.albert.ccumis_proj.data.Employment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class DocumentActivity extends AppCompatActivity {

  SelectedDate mSelectedDate;
  private final int OPERATION = 0;
  private int startHour=-1, startMinute=-1, endHour=-1, endMinute=-1;
  private FragmentManager fm;
  private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.TAIWAN);
  private EditText editDate, sTime, eTime;

  private DepartmentViewModel departmentViewModel;
  private EmploymentViewModel employmentViewModel;
  private int updateSeri_no;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_document);
    Intent intent = getIntent();
    final int seri_no = intent.getIntExtra("SERI_NO", -1);
    fm = getSupportFragmentManager();
    employmentViewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);
    departmentViewModel = ViewModelProviders.of(this).get(DepartmentViewModel.class);

    final AutoCompleteTextView autoTextView = findViewById(R.id.content);
    final Spinner spinner = findViewById(R.id.spinner_item);

//    final List<Department> items = new ArrayList<>();
    final List<String> items = new ArrayList<>(), values = new ArrayList<>();
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
    spinner.setAdapter(adapter);

    final Department department_edit = new Department();
    department_edit.type = -999;

    if(seri_no != -1) {
      updateSeri_no = seri_no;
      employmentViewModel.getBySer_no(seri_no).observe(this, new Observer<Employment>() {
        @Override
        public void onChanged(Employment employment) {
          try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(df.parse(employment.date));
            mSelectedDate = new SelectedDate(calendar);
            startHour   = employment.start_hour;
            startMinute = employment.start_minute;
            endHour     = employment.end_hour;
            endMinute   = employment.end_minute;
            autoTextView.setText(employment.content);
            department_edit.name = employment.department;
            department_edit.value = employment.department_cd;
            department_edit.type = 200;
            updateInfoView();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    } else {
      updateSeri_no = -1;
    }

    departmentViewModel.getAll(OPERATION).observe(this, new Observer<List<Department>>() {
      @Override
      public void onChanged(@Nullable List<Department> departments) {
        if (departments != null) {
          for (Department department :departments) {
            items.add(department.name);
            values.add(department.value);
          }
          adapter.notifyDataSetChanged();
          if(department_edit.type == 200) {
            spinner.setSelection(values.indexOf(department_edit.value));
          }
        }
      }
    });

//    spinner.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        DepartmentSearchDialogFragment dialog = new DepartmentSearchDialogFragment();
//        dialog.setCallback(searchCallback);
//        dialog.show(fm, "DepartmentSearchDialogFragment");
//      }
//    });


    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    List<String> historyList = new ArrayList<>(sharedPreferences.getStringSet(getString(R.string.pref_content), new HashSet<String>()));
    autoTextView.setThreshold(0);

    final ArrayAdapter<String> historyArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, historyList);
    autoTextView.setAdapter(historyArrayAdapter);



    editDate = findViewById(R.id.editDate);
    editDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DatePickerDialogFragment sublimePickerDialogFragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        if(seri_no == -1) {
          bundle.putBoolean("TYPE", true);
        } else {
          bundle.putBoolean("TYPE", false);
        }
        sublimePickerDialogFragment.setArguments(bundle);
        sublimePickerDialogFragment.setCallback(mFragmentCallback);
        sublimePickerDialogFragment.show(fm,"SUBLIME_DATE_PICKER");
      }
    });

    sTime = findViewById(R.id.editTimeStart);
    sTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(com.example.albert.ccumis_proj.DocumentActivity.this, new TimePickerDialog.OnTimeSetListener() {
          @Override
          public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startHour = hourOfDay;
            startMinute = minute;
            updateInfoView();
          }
        }, hour, minute, true).show();
      }
    });

    eTime = findViewById(R.id.editTimeEnd);
    eTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(com.example.albert.ccumis_proj.DocumentActivity.this, new TimePickerDialog.OnTimeSetListener() {
          @Override
          public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endHour = hourOfDay;
            endMinute = minute;
            updateInfoView();
          }
        }, hour, minute, true).show();
      }
    });

    autoTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        autoTextView.showDropDown();
      }
    });

    Button saveBtn = findViewById(R.id.save);
    saveBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Department department = new Department();
        department.name = items.get(spinner.getSelectedItemPosition());
        department.value = values.get(spinner.getSelectedItemPosition());
        checkValidity(department);
      }
    });
  }

  DatePickerDialogFragment.Callback mFragmentCallback = new DatePickerDialogFragment.Callback() {
    @Override
    public void onCancelled() {
      FragmentManager fm = getSupportFragmentManager();
      fm.beginTransaction().remove(fm.findFragmentByTag("SUBLIME_DATE_PICKER")).commit();
    }

    @Override
    public void onDateSet(SelectedDate selectedDate) {
      mSelectedDate = selectedDate;
      updateInfoView();
    }
  };

  DepartmentSearchDialogFragment.Callback searchCallback = new DepartmentSearchDialogFragment.Callback() {
    @Override
    public void onCancelled() {
      FragmentManager fm = getSupportFragmentManager();
      fm.beginTransaction().remove(fm.findFragmentByTag("DepartmentSearchDialogFragment")).commit();
    }

    @Override
    public void onSelect(Department department) {
      TextView spinner = findViewById(R.id.spinner_item);
      spinner.setTag(department);
      spinner.setText(department.name);
      fm.beginTransaction().remove(fm.findFragmentByTag("DepartmentSearchDialogFragment")).commit();

    }
  };

  private void updateInfoView() {
    if (mSelectedDate != null) {
      if (mSelectedDate.getType() == SelectedDate.Type.SINGLE) {
        editDate.setText(df.format(mSelectedDate.getStartDate().getTime()));
      } else if (mSelectedDate.getType() == SelectedDate.Type.RANGE) {
        String date = df.format(mSelectedDate.getStartDate().getTime()) + "~" +
                df.format(mSelectedDate.getEndDate().getTime());
        editDate.setText(date);
      }
    }
    if(startHour != -1 && startMinute != -1) {
      String time = String.format("%02d", startHour) + ":" + String.format("%02d", startMinute);
      sTime.setText(time);
    }
    if(endHour != -1 && endMinute != -1) {
      String time = String.format("%02d", endHour) + ":" + String.format("%02d", endMinute);
      eTime.setText(time);
    }
  }

  private void checkValidity(Department department) {
    String errorMsg = "";
    if(mSelectedDate == null) {
      errorMsg += getString(R.string.error_no_date);
      errorMsg += "\n";
    }
    if(startMinute == -1 || startHour == -1) {
      errorMsg += getString(R.string.error_no_start_time);
      errorMsg += "\n";
    }
    if(endHour == -1 || endMinute== -1) {
      errorMsg += getString(R.string.error_no_end_time);
      errorMsg += "\n";
    }
    if(endHour*60+endMinute < startHour*60+startMinute) {
      errorMsg += getString(R.string.error_start_later_than_end);
      errorMsg += "\n";
    }
    if(endHour*60+endMinute - startHour*60-startMinute > 240) {
      errorMsg += getString(R.string.error_duration_long);
      errorMsg += "\n";
    }
    AutoCompleteTextView autoCompleteTextView = findViewById(R.id.content);
    if(autoCompleteTextView.getText().toString().isEmpty()) {
      errorMsg += getString(R.string.error_no_content);
      errorMsg += "\n";
    }
    if(department == null) {
      errorMsg += getString(R.string.error_no_department);
      errorMsg += "\n";
    }
    if(!errorMsg.isEmpty()) {
      new AlertDialog.Builder(this)
              .setTitle(R.string.error)
              .setMessage(errorMsg)
              .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
              })
              .show();
    } else {
      saveToDB(department);
    }
  }

  private void saveToDB(Department department){

    AutoCompleteTextView autoCompleteTextView = findViewById(R.id.content);
    String content = autoCompleteTextView.getText().toString();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    Set<String> strings = preferences.getStringSet(getString(R.string.pref_content), new HashSet<String>());

    strings.add(content);
    preferences.edit().putStringSet(getString(R.string.pref_content), strings).apply();

    if(updateSeri_no != -1) {
      Employment employment = new Employment();
      employment.seri_no = updateSeri_no;
      employment.date = df.format(mSelectedDate.getStartDate().getTime());
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(mSelectedDate.getStartDate().getTime());
      employment.year = calendar.get(Calendar.YEAR) - 1911;
      employment.month = calendar.get(Calendar.MONTH) + 1;
      employment.day = calendar.get(Calendar.DAY_OF_MONTH);
      employment.department = department.name;
      employment.department_cd = department.value;
      employment.content = content;
      employment.duration = endHour * 60 + endMinute - startHour * 60 - startMinute;
      employment.end_hour = endHour;
      employment.end_minute = endMinute;
      employment.start_hour = startHour;
      employment.start_minute = startMinute;
      employment.operation = OPERATION;
      employment.status = 401;
      employmentViewModel.update(employment);
    } else {
      if(mSelectedDate.getType() == SelectedDate.Type.SINGLE) {
        Employment employment = new Employment();
        employment.date = df.format(mSelectedDate.getStartDate().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mSelectedDate.getStartDate().getTime());
        employment.year = calendar.get(Calendar.YEAR) - 1911;
        employment.month = calendar.get(Calendar.MONTH) + 1;
        employment.day = calendar.get(Calendar.DAY_OF_MONTH);
        employment.department = department.name;
        employment.department_cd = department.value;
        employment.content = content;
        employment.duration = endHour * 60 + endMinute - startHour * 60 - startMinute;
        employment.end_hour = endHour;
        employment.end_minute = endMinute;
        employment.start_hour = startHour;
        employment.start_minute = startMinute;
        employment.operation = OPERATION;
        employment.status = 401;
        employmentViewModel.insert(employment);
      } else {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mSelectedDate.getStartDate().getTime());
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(mSelectedDate.getEndDate().getTime());
        for(; calendar1.after(calendar); calendar.add(Calendar.DATE, 1)) {
          Employment employment = new Employment();
          employment.date = df.format(calendar.getTime());
          employment.year = calendar.get(Calendar.YEAR) - 1911;
          employment.month = calendar.get(Calendar.MONTH) + 1;
          employment.day = calendar.get(Calendar.DAY_OF_MONTH);
          employment.department = department.name;
          employment.department_cd = department.value;
          employment.content = content;
          employment.duration = endHour * 60 + endMinute - startHour * 60 - startMinute;
          employment.end_hour = endHour;
          employment.end_minute = endMinute;
          employment.start_hour = startHour;
          employment.start_minute = startMinute;
          employment.operation = OPERATION;
          employment.status = 401;
          employmentViewModel.insert(employment);
        }
      }
    }

    finish();


  }
}
