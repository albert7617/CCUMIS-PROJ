package com.example.albert.ccumis;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import java.util.List;

public class SelectedEmploymentActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_selected_employment);
    Intent intent = getIntent();
    final int operation = intent.getIntExtra("OPERATION", 2);

    final EmploymentViewModel employmentViewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);
    final RecyclerView recyclerView = findViewById(R.id.recyclerView);
    final SelectAdapter selectAdapter = new SelectAdapter(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    recyclerView.setAdapter(selectAdapter);
    ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    recyclerView.addItemDecoration(new RecyclerDecoration());
    employmentViewModel.getEmployments(operation).observe(this, new Observer<List<Employment>>() {
      @Override
      public void onChanged(@Nullable List<Employment> employments) {
        selectAdapter.setEmployments(employments);
      }
    });
  }
}
