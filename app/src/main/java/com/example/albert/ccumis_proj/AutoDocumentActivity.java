package com.example.albert.ccumis_proj;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.example.albert.ccumis_proj.data.Department;
import com.example.albert.ccumis_proj.data.Employment;
import com.example.albert.ccumis_proj.fragments.CalculatorFragment;
import com.example.albert.ccumis_proj.fragments.DatePickerDialogFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AutoDocumentActivity extends AppCompatActivity {
  private final int OPERATION = 0;
  private SelectedDate mSelectedDate;
  private FragmentManager fm;
  private int startHour=-1, startMinute=-1;
  private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.TAIWAN);
  private EditText editDate, sTime, targetHours;
  private EmploymentViewModel employmentViewModel;
  private AutoCompleteTextView autoCompleteTextView;
  private AutoCompleteTextView autoCompleteTextView2;
  private AutoCompleteTextView autoCompleteTextView3;
  private AutoCompleteTextView autoCompleteTextView4;
  private AutoCompleteTextView autoCompleteTextView5;
  private AutoCompleteTextView autoCompleteTextView6;
  private AutoCompleteTextView autoCompleteTextView7;
  private AutoCompleteTextView autoCompleteTextView8;
  private AutoCompleteTextView autoCompleteTextView9;
  private AutoCompleteTextView autoCompleteTextView10;
  private Spinner spinnerContent;
  private WeekDayPicker dayPicker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auto_document);

    employmentViewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);
    dayPicker = findViewById(R.id.week_of_day_picker);
    final Spinner spinner = findViewById(R.id.spinner_item);
    autoCompleteTextView = findViewById(R.id.content);
    autoCompleteTextView2 = findViewById(R.id.content2);
    autoCompleteTextView3 = findViewById(R.id.content3);
    autoCompleteTextView4 = findViewById(R.id.content4);
    autoCompleteTextView5 = findViewById(R.id.content5);
    autoCompleteTextView6 = findViewById(R.id.content6);
    autoCompleteTextView7 = findViewById(R.id.content7);
    autoCompleteTextView8 = findViewById(R.id.content8);
    autoCompleteTextView9 = findViewById(R.id.content9);
    autoCompleteTextView10 = findViewById(R.id.content10);
    editDate = findViewById(R.id.editDate);
    sTime = findViewById(R.id.editTimeStart);
    targetHours = findViewById(R.id.editHours);

    spinnerContent = findViewById(R.id.spinner_content);
    Integer[] spinnerContentItems = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    ArrayAdapter<Integer> spinnerContentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerContentItems);
    spinnerContent.setAdapter(spinnerContentAdapter);
    spinnerContent.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        hideSoftKeyboard(AutoDocumentActivity.this, view);
        return false;
      }
    });
    spinnerContent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
          case 0:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.GONE);
            autoCompleteTextView3.setVisibility(View.GONE);
            autoCompleteTextView4.setVisibility(View.GONE);
            autoCompleteTextView5.setVisibility(View.GONE);
            autoCompleteTextView6.setVisibility(View.GONE);
            autoCompleteTextView7.setVisibility(View.GONE);
            autoCompleteTextView8.setVisibility(View.GONE);
            autoCompleteTextView9.setVisibility(View.GONE);
            autoCompleteTextView10.setVisibility(View.GONE);
            break;
          case 1:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView2.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.VISIBLE);
            autoCompleteTextView3.setVisibility(View.GONE);
            autoCompleteTextView4.setVisibility(View.GONE);
            autoCompleteTextView5.setVisibility(View.GONE);
            autoCompleteTextView6.setVisibility(View.GONE);
            autoCompleteTextView7.setVisibility(View.GONE);
            autoCompleteTextView8.setVisibility(View.GONE);
            autoCompleteTextView9.setVisibility(View.GONE);
            autoCompleteTextView10.setVisibility(View.GONE);
            break;
          case 2:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView3.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.VISIBLE);
            autoCompleteTextView3.setVisibility(View.VISIBLE);
            autoCompleteTextView4.setVisibility(View.GONE);
            autoCompleteTextView5.setVisibility(View.GONE);
            autoCompleteTextView6.setVisibility(View.GONE);
            autoCompleteTextView7.setVisibility(View.GONE);
            autoCompleteTextView8.setVisibility(View.GONE);
            autoCompleteTextView9.setVisibility(View.GONE);
            autoCompleteTextView10.setVisibility(View.GONE);
            break;
          case 3:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView4.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.VISIBLE);
            autoCompleteTextView3.setVisibility(View.VISIBLE);
            autoCompleteTextView4.setVisibility(View.VISIBLE);
            autoCompleteTextView5.setVisibility(View.GONE);
            autoCompleteTextView6.setVisibility(View.GONE);
            autoCompleteTextView7.setVisibility(View.GONE);
            autoCompleteTextView8.setVisibility(View.GONE);
            autoCompleteTextView9.setVisibility(View.GONE);
            autoCompleteTextView10.setVisibility(View.GONE);
            break;
          case 4:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView4.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView5.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.VISIBLE);
            autoCompleteTextView3.setVisibility(View.VISIBLE);
            autoCompleteTextView4.setVisibility(View.VISIBLE);
            autoCompleteTextView5.setVisibility(View.VISIBLE);
            autoCompleteTextView6.setVisibility(View.GONE);
            autoCompleteTextView7.setVisibility(View.GONE);
            autoCompleteTextView8.setVisibility(View.GONE);
            autoCompleteTextView9.setVisibility(View.GONE);
            autoCompleteTextView10.setVisibility(View.GONE);
            break;
          case 5:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView4.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView5.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView6.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.VISIBLE);
            autoCompleteTextView3.setVisibility(View.VISIBLE);
            autoCompleteTextView4.setVisibility(View.VISIBLE);
            autoCompleteTextView5.setVisibility(View.VISIBLE);
            autoCompleteTextView6.setVisibility(View.VISIBLE);
            autoCompleteTextView7.setVisibility(View.GONE);
            autoCompleteTextView8.setVisibility(View.GONE);
            autoCompleteTextView9.setVisibility(View.GONE);
            autoCompleteTextView10.setVisibility(View.GONE);
            break;
          case 6:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView4.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView5.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView6.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView7.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.VISIBLE);
            autoCompleteTextView3.setVisibility(View.VISIBLE);
            autoCompleteTextView4.setVisibility(View.VISIBLE);
            autoCompleteTextView5.setVisibility(View.VISIBLE);
            autoCompleteTextView6.setVisibility(View.VISIBLE);
            autoCompleteTextView7.setVisibility(View.VISIBLE);
            autoCompleteTextView8.setVisibility(View.GONE);
            autoCompleteTextView9.setVisibility(View.GONE);
            autoCompleteTextView10.setVisibility(View.GONE);
            break;
          case 7:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView4.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView5.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView6.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView7.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView8.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.VISIBLE);
            autoCompleteTextView3.setVisibility(View.VISIBLE);
            autoCompleteTextView4.setVisibility(View.VISIBLE);
            autoCompleteTextView5.setVisibility(View.VISIBLE);
            autoCompleteTextView6.setVisibility(View.VISIBLE);
            autoCompleteTextView7.setVisibility(View.VISIBLE);
            autoCompleteTextView8.setVisibility(View.VISIBLE);
            autoCompleteTextView9.setVisibility(View.GONE);
            autoCompleteTextView10.setVisibility(View.GONE);
            break;
          case 8:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView4.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView5.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView6.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView7.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView8.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView9.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.VISIBLE);
            autoCompleteTextView3.setVisibility(View.VISIBLE);
            autoCompleteTextView4.setVisibility(View.VISIBLE);
            autoCompleteTextView5.setVisibility(View.VISIBLE);
            autoCompleteTextView6.setVisibility(View.VISIBLE);
            autoCompleteTextView7.setVisibility(View.VISIBLE);
            autoCompleteTextView8.setVisibility(View.VISIBLE);
            autoCompleteTextView9.setVisibility(View.VISIBLE);
            autoCompleteTextView10.setVisibility(View.GONE);
            break;
          case 9:
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView4.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView5.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView6.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView7.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView8.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView9.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            autoCompleteTextView10.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView2.setVisibility(View.VISIBLE);
            autoCompleteTextView3.setVisibility(View.VISIBLE);
            autoCompleteTextView4.setVisibility(View.VISIBLE);
            autoCompleteTextView5.setVisibility(View.VISIBLE);
            autoCompleteTextView6.setVisibility(View.VISIBLE);
            autoCompleteTextView7.setVisibility(View.VISIBLE);
            autoCompleteTextView8.setVisibility(View.VISIBLE);
            autoCompleteTextView9.setVisibility(View.VISIBLE);
            autoCompleteTextView10.setVisibility(View.VISIBLE);
            break;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });


    final List<String> items = new ArrayList<>(), values = new ArrayList<>();
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
    spinner.setAdapter(adapter);
    DepartmentViewModel departmentViewModel = ViewModelProviders.of(this).get(DepartmentViewModel.class);
    departmentViewModel.getAll(OPERATION).observe(this, new Observer<List<Department>>() {
      @Override
      public void onChanged(@Nullable List<Department> departments) {
        if (departments != null) {
          items.clear();
          values.clear();
          for (Department department :departments) {
            items.add(department.name);
            values.add(department.value);
          }
          adapter.notifyDataSetChanged();
        }
      }
    });

    fm = getSupportFragmentManager();

    ArrayList<WeekDayPicker.Weekday> weekdays = new ArrayList<>();
    weekdays.add(WeekDayPicker.Weekday.MONDAY);
    weekdays.add(WeekDayPicker.Weekday.TUESDAY);
    weekdays.add(WeekDayPicker.Weekday.WEDNESDAY);
    weekdays.add(WeekDayPicker.Weekday.THURSDAY);
    weekdays.add(WeekDayPicker.Weekday.FRIDAY);
    dayPicker.setSelectedDays(weekdays);

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    List<String> historyList = new ArrayList<>(sharedPreferences.getStringSet(getString(R.string.pref_content), new HashSet<String>()));
    autoCompleteTextView.setThreshold(0);
    autoCompleteTextView2.setThreshold(0);
    autoCompleteTextView3.setThreshold(0);
    autoCompleteTextView4.setThreshold(0);
    autoCompleteTextView5.setThreshold(0);
    autoCompleteTextView6.setThreshold(0);
    autoCompleteTextView7.setThreshold(0);
    autoCompleteTextView8.setThreshold(0);
    autoCompleteTextView9.setThreshold(0);

    final ArrayAdapter<String> historyArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, historyList);
    autoCompleteTextView.setAdapter(historyArrayAdapter);
    autoCompleteTextView2.setAdapter(historyArrayAdapter);
    autoCompleteTextView3.setAdapter(historyArrayAdapter);
    autoCompleteTextView4.setAdapter(historyArrayAdapter);
    autoCompleteTextView5.setAdapter(historyArrayAdapter);
    autoCompleteTextView6.setAdapter(historyArrayAdapter);
    autoCompleteTextView7.setAdapter(historyArrayAdapter);
    autoCompleteTextView8.setAdapter(historyArrayAdapter);
    autoCompleteTextView9.setAdapter(historyArrayAdapter);

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

    ImageButton calculator = findViewById(R.id.imageBtnCalculator);
    calculator.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        CalculatorFragment calculatorFragment = new CalculatorFragment();
        calculatorFragment.show(getFragmentManager(), "CALCULATOR");

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
    List<WeekDayPicker.Weekday> weekdays = dayPicker.getSelectedDays();
    WeekDayPicker.Weekday current = WeekDayPicker.Weekday.MONDAY;
    for (; calendar1.after(calendar); calendar.add(Calendar.DATE, 1)) {
      switch (calendar.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.MONDAY:
          current = WeekDayPicker.Weekday.MONDAY;
          break;
        case Calendar.TUESDAY:
          current = WeekDayPicker.Weekday.TUESDAY;
          break;
        case Calendar.WEDNESDAY:
          current = WeekDayPicker.Weekday.WEDNESDAY;
          break;
        case Calendar.THURSDAY:
          current = WeekDayPicker.Weekday.THURSDAY;
          break;
        case Calendar.FRIDAY:
          current = WeekDayPicker.Weekday.FRIDAY;
          break;
        case Calendar.SATURDAY:
          current = WeekDayPicker.Weekday.SATURDAY;
          break;
        case Calendar.SUNDAY:
          current = WeekDayPicker.Weekday.SUNDAY;
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

  private void saveToDB(Department department){
    String content = autoCompleteTextView.getText().toString();
    int contentNum = spinnerContent.getSelectedItemPosition();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    Set<String> strings = preferences.getStringSet(getString(R.string.pref_content), new HashSet<String>());
    List<String> contents = new ArrayList<>();
    Log.d("content", "saveToDB: "+contentNum);
    switch (contentNum) {
      case 0:
        contents.add(0, autoCompleteTextView.getText().toString());
        break;
      case 1:
        contents.add(0, autoCompleteTextView.getText().toString());
        contents.add(1, autoCompleteTextView2.getText().toString());
        break;
      case 2:
        contents.add(0, autoCompleteTextView.getText().toString());
        contents.add(1, autoCompleteTextView2.getText().toString());
        contents.add(2, autoCompleteTextView3.getText().toString());
        break;
      case 3:
        contents.add(0, autoCompleteTextView.getText().toString());
        contents.add(1, autoCompleteTextView2.getText().toString());
        contents.add(2, autoCompleteTextView3.getText().toString());
        contents.add(3, autoCompleteTextView4.getText().toString());
        break;
      case 4:
        contents.add(0, autoCompleteTextView.getText().toString());
        contents.add(1, autoCompleteTextView2.getText().toString());
        contents.add(2, autoCompleteTextView3.getText().toString());
        contents.add(3, autoCompleteTextView4.getText().toString());
        contents.add(4, autoCompleteTextView5.getText().toString());
        break;
      case 5:
        contents.add(0, autoCompleteTextView.getText().toString());
        contents.add(1, autoCompleteTextView2.getText().toString());
        contents.add(2, autoCompleteTextView3.getText().toString());
        contents.add(3, autoCompleteTextView4.getText().toString());
        contents.add(4, autoCompleteTextView5.getText().toString());
        contents.add(5, autoCompleteTextView6.getText().toString());
        break;
      case 6:
        contents.add(0, autoCompleteTextView.getText().toString());
        contents.add(1, autoCompleteTextView2.getText().toString());
        contents.add(2, autoCompleteTextView3.getText().toString());
        contents.add(3, autoCompleteTextView4.getText().toString());
        contents.add(4, autoCompleteTextView5.getText().toString());
        contents.add(5, autoCompleteTextView6.getText().toString());
        contents.add(6, autoCompleteTextView7.getText().toString());
        break;
      case 7:
        contents.add(0, autoCompleteTextView.getText().toString());
        contents.add(1, autoCompleteTextView2.getText().toString());
        contents.add(2, autoCompleteTextView3.getText().toString());
        contents.add(3, autoCompleteTextView4.getText().toString());
        contents.add(4, autoCompleteTextView5.getText().toString());
        contents.add(5, autoCompleteTextView6.getText().toString());
        contents.add(6, autoCompleteTextView7.getText().toString());
        contents.add(7, autoCompleteTextView8.getText().toString());
        break;
      case 8:
        contents.add(0, autoCompleteTextView.getText().toString());
        contents.add(1, autoCompleteTextView2.getText().toString());
        contents.add(2, autoCompleteTextView3.getText().toString());
        contents.add(3, autoCompleteTextView4.getText().toString());
        contents.add(4, autoCompleteTextView5.getText().toString());
        contents.add(5, autoCompleteTextView6.getText().toString());
        contents.add(6, autoCompleteTextView7.getText().toString());
        contents.add(7, autoCompleteTextView8.getText().toString());
        contents.add(8, autoCompleteTextView9.getText().toString());
        break;
      case 9:
        contents.add(0, autoCompleteTextView.getText().toString());
        contents.add(1, autoCompleteTextView2.getText().toString());
        contents.add(2, autoCompleteTextView3.getText().toString());
        contents.add(3, autoCompleteTextView4.getText().toString());
        contents.add(4, autoCompleteTextView5.getText().toString());
        contents.add(5, autoCompleteTextView6.getText().toString());
        contents.add(6, autoCompleteTextView7.getText().toString());
        contents.add(7, autoCompleteTextView8.getText().toString());
        contents.add(8, autoCompleteTextView9.getText().toString());
        contents.add(9, autoCompleteTextView10.getText().toString());
        break;

    }
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
      int dayContentRatio = days/(contentNum+1);
      List<Integer> contentIdx = new ArrayList<>();
      int idx = 0;
      for (int i = 0; i <= days; i++) {
        idx = idx > contentNum ? 0 : idx;
        contentIdx.add(idx);
        idx++;
      }
      Collections.sort(contentIdx);
      List<WeekDayPicker.Weekday> weekdays = dayPicker.getSelectedDays();
      WeekDayPicker.Weekday current = WeekDayPicker.Weekday.MONDAY;
      int targetPerDay = target/days;
      int remainderHours = target%days;
      int dayCount = 0;
      for(; calendar1.after(calendar); calendar.add(Calendar.DATE, 1)) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
          case Calendar.MONDAY:
            current = WeekDayPicker.Weekday.MONDAY;
            break;
          case Calendar.TUESDAY:
            current = WeekDayPicker.Weekday.TUESDAY;
            break;
          case Calendar.WEDNESDAY:
            current = WeekDayPicker.Weekday.WEDNESDAY;
            break;
          case Calendar.THURSDAY:
            current = WeekDayPicker.Weekday.THURSDAY;
            break;
          case Calendar.FRIDAY:
            current = WeekDayPicker.Weekday.FRIDAY;
            break;
          case Calendar.SATURDAY:
            current = WeekDayPicker.Weekday.SATURDAY;
            break;
          case Calendar.SUNDAY:
            current = WeekDayPicker.Weekday.SUNDAY;
            break;
        }
        if (weekdays.contains(current)) {
          dayCount++;
          if(remainderHours > 0) {
            addEmployments(calendar, targetPerDay+1, department, contents.get(contentIdx.get(dayCount)));
            remainderHours --;
          } else {
            addEmployments(calendar, targetPerDay, department, contents.get(contentIdx.get(dayCount)));
          }
        }

      }
    }
    finish();
  }

  private String getContent(int day, int dayContentRatio, List<String> contents) {
    Log.d("day", "getContent: day    " + day);
    Log.d("day", "getContent: ratio  " + dayContentRatio);
    Log.d("day", "getContent: d/r    " + day/dayContentRatio);
    if (day/dayContentRatio < contents.size()) {
      if (day/dayContentRatio == 0) {
        Log.d("day", "getContent: content" + contents.get(day/dayContentRatio));
        return contents.get(day/dayContentRatio);
      } else {
        Log.d("day", "getContent: content" + contents.get(day/dayContentRatio-1));
        return contents.get(day/dayContentRatio-1);
      }
    } else {
      Log.d("day", "getContent: content" + contents.get(contents.size()-1));
      return contents.get(contents.size()-1);
    }
  }


  private void addEmployments(Calendar calendar, int target, Department department, String content) {
    int offset = 0;
    while (target > 4) {
      Employment employment = generateEmployment(calendar,
              department,
              content,
              240,
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
  public static void hideSoftKeyboard (Activity activity, View view) {
    InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
  }
}
