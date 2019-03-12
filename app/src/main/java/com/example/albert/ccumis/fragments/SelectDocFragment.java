package com.example.albert.ccumis.fragments;

import android.app.Application;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.example.albert.ccumis.data.Department;
import com.example.albert.ccumis.adapters.DepartmentAdapter;
import com.example.albert.ccumis.DepartmentViewModel;
import com.example.albert.ccumis.data.Employment;
import com.example.albert.ccumis.EmploymentViewModel;
import com.example.albert.ccumis.PostEmployment;
import com.example.albert.ccumis.R;
import com.example.albert.ccumis.RecyclerDecoration;
import com.example.albert.ccumis.adapters.SelectAdapter;
import com.example.albert.ccumis.SelectEmploymentRemoteTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectDocFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectDocFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";



  private String mParam1;
  private String mParam2;

  private final int OPERATION = 1;
  // --Commented out by Inspection (2019/3/7 17:53):private final int DEPARTMENT_TYPE = 1;

  private DepartmentViewModel departmentViewModel;
  private EmploymentViewModel employmentViewModel;
  private FragmentManager fm;
  private EditText startDate, endDate, weekend, status;

  private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.TAIWAN);
  private Calendar start, end;
  private int status_cd = 9, weekend_cd = 2;
  private AlertDialog alertDialog;
  public SelectDocFragment() {
    // Required empty public constructor
  }


  public static SelectDocFragment newInstance(String param1, String param2) {
    SelectDocFragment fragment = new SelectDocFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_select_doc, container, false);
    fm = getChildFragmentManager();

    final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
    final SelectAdapter selectAdapter = new SelectAdapter(getActivity());
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setAdapter(selectAdapter);
    ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    recyclerView.addItemDecoration(new RecyclerDecoration());
    start = Calendar.getInstance();
    start.set(Calendar.DAY_OF_MONTH, 1);
    end = Calendar.getInstance();
    end.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
    final Spinner spinner = rootView.findViewById(R.id.department_spinner);
    final DepartmentAdapter adapter = new DepartmentAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    departmentViewModel = ViewModelProviders.of(this).get(DepartmentViewModel.class);
    employmentViewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);

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

    endDate = rootView.findViewById(R.id.editTimeEnd);
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

    weekend = rootView.findViewById(R.id.weekend);
    weekend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.weekend))
                .setItems(R.array.weekends, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    weekend_cd = which == 2 ? 2 : which == 1 ? 0 : 1;
                    String[] weekends = getResources().getStringArray(R.array.weekends);
                    weekend.setText(weekends[which]);
                  }
                })
                .show();
      }
    });

    status = rootView.findViewById(R.id.processing_status);
    status.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AlertDialog.Builder(getActivity())
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
          postEmployment.weekend = weekend_cd;
          postEmployment.status = status_cd;
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

        } else {
          new AlertDialog.Builder(getActivity())
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

  private void updateView() {
    startDate.setText(df.format(start.getTime()));
    endDate.setText(df.format(end.getTime()));
  }

  SelectEmploymentRemoteTask.Callback callback = new SelectEmploymentRemoteTask.Callback() {
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
