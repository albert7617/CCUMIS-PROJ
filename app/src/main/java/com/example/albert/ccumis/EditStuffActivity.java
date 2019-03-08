package com.example.albert.ccumis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.albert.ccumis.adapters.StuffAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditStuffActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_stuff);

    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    final List<String> stuffs = new ArrayList<>(sharedPreferences.getStringSet(getString(R.string.pref_content), new HashSet<String>()));

    RecyclerView recyclerView = findViewById(R.id.stuffRecycler);
    final StuffAdapter adapter = new StuffAdapter(this, stuffs);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setAdapter(adapter);

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
      @Override
      public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        return false;
      }

      @Override
      public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        final String stuff = adapter.getStuffAtPosition(position);
        stuffs.remove(stuff);
        sharedPreferences.edit().putStringSet(getString(R.string.pref_content), new HashSet<String>(stuffs)).commit();
        adapter.setStuff(stuffs);
        Snackbar.make(findViewById(R.id.stuffCoordinatorLayout),"常用勞雇資料\""+stuff+"\"已刪除",Snackbar.LENGTH_LONG)
                .setAction("復原", new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    stuffs.add(stuff);
                    sharedPreferences.edit().putStringSet(getString(R.string.pref_content), new HashSet<String>(stuffs)).commit();
                    adapter.setStuff(stuffs);
                  }
                })
                .show();
      }
    });

    itemTouchHelper.attachToRecyclerView(recyclerView);

    FloatingActionButton fab = findViewById(R.id.addStuffActionButton);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditStuffActivity.this);
        builder.setTitle(R.string.new_stuff_title);
        LayoutInflater inflater = EditStuffActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_stuff, null);
        builder.setView(dialogView);
        // Set up the input

        // Set up the buttons
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            EditText editText = dialogView.findViewById(R.id.stuffEditText);
            stuffs.add(editText.getText().toString());
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            pref.edit().putStringSet(getString(R.string.pref_content), new HashSet<String>(stuffs)).commit();
            adapter.setStuff(stuffs);
          }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });

        builder.show();
      }
    });

  }
}
