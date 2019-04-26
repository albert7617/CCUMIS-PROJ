package com.example.albert.ccumis;

import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.example.albert.ccumis.data.Department;
import com.example.albert.ccumis.data.Employment;
import com.example.albert.ccumis.fragments.DatePickerDialogFragment;
import com.example.albert.ccumis.fragments.DepartmentSearchDialogFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class AutoDocumentActivity extends AppCompatActivity {
  private final int OPERATION = 0;
  private SelectedDate mSelectedDate;
  private FragmentManager fm;
  private int startHour=-1, startMinute=-1;
  private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.TAIWAN);
  private EditText editDate, sTime, targetHours;
  private EmploymentViewModel employmentViewModel;
  private AutoCompleteTextView autoCompleteTextView;
  private MaterialDayPicker dayPicker;
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auto_document);

    employmentViewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);
    dayPicker = findViewById(R.id.day_picker);
    final TextView spinner = findViewById(R.id.spinner_item);
    autoCompleteTextView = findViewById(R.id.content);
    editDate = findViewById(R.id.editDate);
    sTime = findViewById(R.id.editTimeStart);
    targetHours = findViewById(R.id.editHours);

    fm = getSupportFragmentManager();

    ArrayList<MaterialDayPicker.Weekday> weekdays = new ArrayList<>();
    weekdays.add(MaterialDayPicker.Weekday.MONDAY);
    weekdays.add(MaterialDayPicker.Weekday.TUESDAY);
    weekdays.add(MaterialDayPicker.Weekday.WEDNESDAY);
    weekdays.add(MaterialDayPicker.Weekday.THURSDAY);
    weekdays.add(MaterialDayPicker.Weekday.FRIDAY);
    dayPicker.setSelectedDays(weekdays);

    spinner.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DepartmentSearchDialogFragment dialog = new DepartmentSearchDialogFragment();
        dialog.setCallback(searchCallback);
        dialog.show(fm, "DepartmentSearchDialogFragment");
      }
    });


    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    List<String> historyList = new ArrayList<>(sharedPreferences.getStringSet(getString(R.string.pref_content), new HashSet<String>()));
    autoCompleteTextView.setThreshold(0);

    final ArrayAdapter<String> historyArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, historyList);
    autoCompleteTextView.setAdapter(historyArrayAdapter);

    editDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DatePickerDialogFragment sublimePickerDialogFragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("TYPE", true);
        sublimePickerDialogFragment.setArguments(bundle);
        sublimePickerDialogFragment.setCallback(mFragmentCallback);
        sublimePickerDialogFragment.show(fm,"SUBLIME_DATE_PICKER");
      }
    });

    sTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(AutoDocumentActivity.this, new TimePickerDialog.OnTimeSetListener() {
          @Override
          public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startHour = hourOfDay;
            startMinute = minute;
            updateInfoView();
          }
        }, hour, minute, true).show();
      }
    });

    autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        autoCompleteTextView.showDropDown();
      }
    });

    Button saveBtn = findViewById(R.id.save);
    saveBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkValidity((Department) spinner.getTag());
      }
    });

  }

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
  }

  private boolean checkWorkHourValidity() {
    return Float.valueOf(targetHours.getText().toString()) * 60 / countDays() + startHour * 60 + startMinute <= 1440;
  }

  private int countDays() {
    if(mSelectedDate.getType() == SelectedDate.Type.SINGLE){
      return 1;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(mSelectedDate.getStartDate().getTime());
    Calendar calendar1 = Calendar.getInstance();
    calendar1.setTime(mSelectedDate.getEndDate().getTime());
    int dayCount = 0;
    List<MaterialDayPicker.Weekday> weekdays = dayPicker.getSelectedDays();
    MaterialDayPicker.Weekday current = MaterialDayPicker.Weekday.MONDAY;
    for (; calendar1.after(calendar); calendar.add(Calendar.DATE, 1)) {
      switch (calendar.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.MONDAY:
          current = MaterialDayPicker.Weekday.MONDAY;
          break;
        case Calendar.TUESDAY:
          current = MaterialDayPicker.Weekday.TUESDAY;
          break;
        case Calendar.WEDNESDAY:
          current = MaterialDayPicker.Weekday.WEDNESDAY;
          break;
        case Calendar.THURSDAY:
          current = MaterialDayPicker.Weekday.THURSDAY;
          break;
        case Calendar.FRIDAY:
          current = MaterialDayPicker.Weekday.FRIDAY;
          break;
        case Calendar.SATURDAY:
          current = MaterialDayPicker.Weekday.SATURDAY;
          break;
        case Calendar.SUNDAY:
          current = MaterialDayPicker.Weekday.SUNDAY;
          break;
      }
      if (weekdays.contains(current))
        dayCount++;
    }
    return dayCount;
  }

  private void checkValidity(Department department) {
    String errorMsg = "";
    if(mSelectedDate == null) {
      errorMsg += getString(R.string.error_no_date);
      errorMsg += "\n";
    } else {
      if(!checkWorkHourValidity()){
        errorMsg += getString(R.string.error_duration_long);
        errorMsg += "\n";
      }
    }
    if(startMinute == -1 || startHour == -1) {
      errorMsg += getString(R.string.error_no_start_time);
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

  private void saveToDB(Department department){
    String content = autoCompleteTextView.getText().toString();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    Set<String> strings = preferences.getStringSet(getString(R.string.pref_content), new HashSet<String>());

    strings.add(content);
    preferences.edit().putStringSet(getString(R.string.pref_content), strings).apply();

    int target = Integer.valueOf(targetHours.getText().toString());
    if(mSelectedDate.getType() == SelectedDate.Type.SINGLE) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(mSelectedDate.getStartDate().getTime());
      addEmployments(calendar, target, department, content);
    } else {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(mSelectedDate.getStartDate().getTime());
      Calendar calendar1 = Calendar.getInstance();
      calendar1.setTime(mSelectedDate.getEndDate().getTime());
      int days = countDays();
      List<MaterialDayPicker.Weekday> weekdays = dayPicker.getSelectedDays();
      MaterialDayPicker.Weekday current = MaterialDayPicker.Weekday.MONDAY;
      int targetPerDay = target/days;
      int remainderHours = target%days;
      for(; calendar1.after(calendar); calendar.add(Calendar.DATE, 1)) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
          case Calendar.MONDAY:
            current = MaterialDayPicker.Weekday.MONDAY;
            break;
          case Calendar.TUESDAY:
            current = MaterialDayPicker.Weekday.TUESDAY;
            break;
          case Calendar.WEDNESDAY:
            current = MaterialDayPicker.Weekday.WEDNESDAY;
            break;
          case Calendar.THURSDAY:
            current = MaterialDayPicker.Weekday.THURSDAY;
            break;
          case Calendar.FRIDAY:
            current = MaterialDayPicker.Weekday.FRIDAY;
            break;
          case Calendar.SATURDAY:
            current = MaterialDayPicker.Weekday.SATURDAY;
            break;
          case Calendar.SUNDAY:
            current = MaterialDayPicker.Weekday.SUNDAY;
            break;
        }
        if (weekdays.contains(current)) {
          if(remainderHours > 0) {
            addEmployments(calendar, targetPerDay+1, department, content);
            remainderHours --;
          } else {
            addEmployments(calendar, targetPerDay, department, content);
          }
        }
      }
    }

    finish();


  }


  private void addEmployments(Calendar calendar, int target, Department department, String content) {
    int offset = 0;
    while (target > 4) {
      Employment employment = generateEmployment(calendar,
              department,
              content,
              480,
              startHour + offset + 4,
              startMinute,
              startHour + offset,
              startMinute);
      employmentViewModel.insert(employment);
      offset += 4;
      target -= 4;
    }
    if(target > 0) {
      Employment employment = generateEmployment(calendar,
              department,
              content,
              target * 60,
              startHour + offset + target,
              startMinute,
              startHour + offset,
              startMinute);
      employmentViewModel.insert(employment);
    }
  }

  /**
   *
   * @param calendar
   * @param department
   * @param content
   * @param duration
   * @param end_hour
   * @param end_minute
   * @param start_hour
   * @param start_minute
   * @return
   */
  private Employment generateEmployment(Calendar calendar, Department department, String content, int duration, int end_hour, int end_minute, int start_hour, int start_minute) {
    Employment employment = new Employment();
    employment.date = df.format(calendar.getTime());
    employment.year = calendar.get(Calendar.YEAR) - 1911;
    employment.month = calendar.get(Calendar.MONTH) + 1;
    employment.day = calendar.get(Calendar.DAY_OF_MONTH);
    employment.department = department.name;
    employment.department_cd = department.value;
    employment.content = content;
    employment.duration = duration;
    employment.end_hour = end_hour;
    employment.end_minute = end_minute;
    employment.start_hour = start_hour;
    employment.start_minute = start_minute;
    employment.operation = OPERATION;
    employment.status = 401;
    return employment;
  }
}
