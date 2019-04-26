package com.example.albert.ccumis.fragments;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albert.ccumis.AutoDocumentActivity;
import com.example.albert.ccumis.DocumentActivity;
import com.example.albert.ccumis.data.Employment;
import com.example.albert.ccumis.adapters.EmploymentListAdapter;
import com.example.albert.ccumis.EmploymentViewModel;
import com.example.albert.ccumis.R;
import com.example.albert.ccumis.tasks.InsertDocTask;
import com.example.albert.ccumis.tasks.RemoteTask;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewDocFragment extends Fragment {
  private Context context;
  private EmploymentViewModel viewModel;
  AlertDialog alertDialog;
  private final int OPERATION = 0;
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_new_doc, container, false);
    RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
    final EmploymentListAdapter adapter = new EmploymentListAdapter(getContext());
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    adapter.setCallback(deleteCallback);
    final TextView hourCount = rootView.findViewById(R.id.hoursCount);
    viewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);
    viewModel.getEmployments(OPERATION).observe(this, new Observer<List<Employment>>() {
      @Override
      public void onChanged(@Nullable List<Employment> employments) {
        adapter.setEmployments(employments);
      }
    });
    viewModel.getSum(OPERATION).observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(@Nullable Integer integer) {
        hourCount.setText(String.format(Locale.TAIWAN, "%.1f", ((float) (integer==null ? 0 : integer)/60)));
      }
    });
    final FloatingActionButton fab = rootView.findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getActivity(), DocumentActivity.class);
        intent.putExtra("SERI_NO", -1);
        startActivity(intent);
      }
    });

    fab.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        Intent intent = new Intent(getActivity(), AutoDocumentActivity.class);
        startActivity(intent);
        return false;
      }
    });



    NestedScrollView scrollView = rootView.findViewById(R.id.nestedScrollView);
    scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
      @Override
      public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if(scrollY-oldScrollY > 0){
          fab.hide();
        } else{
          fab.show();
        }
      }
    });

    CardView nuke = rootView.findViewById(R.id.clearAllCard);
    nuke.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.delete_all_warning)
                .setPositiveButton(R.string.delete_dismiss, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {

                  }
                })
                .setNegativeButton(R.string.delete_confirm, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    viewModel.nukeTable(OPERATION);
                  }
                })
                .show();
      }
    });

    CardView submit = rootView.findViewById(R.id.submitCard);
    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final InsertDocTask task = new InsertDocTask((Application) context.getApplicationContext());
        task.setCallback(callback);
        task.execute();
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
      }
    });

    return rootView;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  EmploymentListAdapter.Callback deleteCallback = new EmploymentListAdapter.Callback() {
    @Override
    public void delete(int seri_no) {
      viewModel.delete(seri_no);
    }
  };

  RemoteTask.Callback callback = new RemoteTask.Callback() {
    @Override
    public void result(Map<String, String> result) {
      alertDialog.dismiss();
      String title = result.get("result").equalsIgnoreCase("200") ? getString(R.string.success) : getString(R.string.error);
      new AlertDialog.Builder(context)
              .setTitle(title)
              .setMessage(result.get("msg"))
              .setPositiveButton(R.string.confirm, null)
              .show();
    }
  };

}
