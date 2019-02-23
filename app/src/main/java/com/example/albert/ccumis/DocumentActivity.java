package com.example.albert.ccumis;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DocumentActivity extends AppCompatActivity {

  SelectedDate mSelectedDate;
  private final int OPERATION = 0;
  private final int DEPARTMENT_TYPE = 0;
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
    final AutoCompleteTextView autoTextView = findViewById(R.id.content);
//    final SearchableSpinner spinner = findViewById(R.id.spinner_item);
    final TextView spinner = findViewById(R.id.spinner_item);
    final DepartmentAdapter adapter = new DepartmentAdapter(this, R.layout.support_simple_spinner_dropdown_item);
    final DepartmentAdapter listadapter = new DepartmentAdapter(this, android.R.layout.simple_list_item_1);

    if(seri_no != -1) {
      updateSeri_no = seri_no;
      employmentViewModel.getBySer_no(seri_no).observe(this, new Observer<Employment>() {
        @Override
        public void onChanged(@Nullable Employment employment) {
          try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(df.parse(employment.date));
            mSelectedDate = new SelectedDate(calendar);
            startHour   = employment.start_hour;
            startMinute = employment.start_minute;
            endHour     = employment.end_hour;
            endMinute   = employment.end_minute;
            autoTextView.setText(employment.content);
            Department department = new Department();
            department.name = employment.department;
            department.value = employment.department_cd;
            spinner.setTag(department);
            spinner.setText(employment.department);
            updateInfoView();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    } else {
      updateSeri_no = -1;
    }

//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//        final ArrayList<Department> departments = getDepartments();
//        if(departments != null) {
//          runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//              adapter.setDepartments(departments);
//              if(seri_no != -1) {
////                spinner.setSelection(seri_no);
//              }
//            }
//          });
//
//        }
//      }
//    }).start();



//    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DocumentActivity.this);
//    LayoutInflater inflater = getLayoutInflater();
//    View convertView = (View) inflater.inflate(R.layout.dialog_search_department, null);
//    alertDialog.setView(convertView);
//    ListView lv = (ListView) convertView.findViewById(R.id.spinnerList);
//
//
//    lv.setAdapter(listadapter);
//    alertDialog.show();

//    departmentViewModel = ViewModelProviders.of(this).get(DepartmentViewModel.class);
//    departmentViewModel.getAll(DEPARTMENT_TYPE).observe(this, new Observer<List<Department>>() {
//      @Override
//      public void onChanged(@Nullable List<Department> departments) {
//        adapter.setDepartments(departments);
//        if(seri_no != -1) {
//          employmentViewModel.getBySer_no(seri_no).observe(DocumentActivity.this, new Observer<Employment>() {
//            @Override
//            public void onChanged(@Nullable Employment employment) {
//              int position = adapter.getPosition(employment.department);
//              if(position != -1) {
//                spinner.setSelection(position);
//              }
//            }
//          });
//        }
//      }
//    });


//    spinner.setTitle("選擇勞雇單位");
//    spinner.setPositiveButton("確定");
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
        new TimePickerDialog(DocumentActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        new TimePickerDialog(DocumentActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        checkValidity((Department) spinner.getTag());
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
