package com.example.albert.ccumis_proj.fragments;

import android.app.ActivityOptions;
import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albert.ccumis_proj.AutoDocumentActivity;
import com.example.albert.ccumis_proj.DocumentActivity;
import com.example.albert.ccumis_proj.data.Employment;
import com.example.albert.ccumis_proj.adapters.EmploymentListAdapter;
import com.example.albert.ccumis_proj.EmploymentViewModel;
import com.example.albert.ccumis_proj.R;
import com.example.albert.ccumis_proj.tasks.InsertDocTask;
import com.example.albert.ccumis_proj.tasks.RemoteTask;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class NewDocFragment extends Fragment {
  private Context context;
  private EmploymentViewModel viewModel;
  AlertDialog alertDialog;
  View rootView;
  private final int OPERATION = 0;
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_new_doc, container, false);
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
    final FloatingActionButton fab = rootView.findViewById(R.id.fab), fab_auto = rootView.findViewById(R.id.fabNewDoc);
    final View blocker = rootView.findViewById(R.id.blocker);
    fab_auto.hide();

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(fab_auto.getVisibility() == View.VISIBLE) {
          Objects.requireNonNull(getActivity()).getWindow().setExitTransition(new Explode());
          Intent intent = new Intent(getActivity(), DocumentActivity.class);
          intent.putExtra("SERI_NO", -1);
          startActivity(intent, ActivityOptions.makeClipRevealAnimation(rootView,
                  (int) fab.getX() + fab.getWidth() / 2,
                  (int) fab.getY() + fab.getHeight() / 2,
                  fab.getWidth() / 2,
                  rootView.getHeight()).toBundle());
        } else {
          blocker.setVisibility(View.VISIBLE);
          fab_auto.show();
        }
      }
    });

    blocker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        blocker.setVisibility(View.INVISIBLE);
        fab_auto.hide();
      }
    });

    fab_auto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), AutoDocumentActivity.class);
        startActivity(intent);
      }
    });


    final ConstraintLayout cardContainer = rootView.findViewById(R.id.cardContainer);
    final NestedScrollView scrollView = rootView.findViewById(R.id.nestedScrollView);
    scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
      @Override
      public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if(scrollY-oldScrollY > 0){
          fab.hide();
        } else{
          fab.show();
        }
        if (scrollView.getHeight() < cardContainer.getHeight() + scrollView.getPaddingTop() + scrollView.getPaddingBottom()) {
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
