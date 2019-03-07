package com.example.albert.ccumis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

public class HistoryArrayAdapter extends ArrayAdapter<String> {

  private List<String> history;

  public HistoryArrayAdapter(@NonNull Context context, int resource) {
    super(context, resource);
  }


  @Nullable
  @Override
  public String getItem(int position) {
    return history.get(position);
  }

  @Override
  public int getCount() {
    return history == null ? 0 : history.size();
  }

  public void setHistory(List<String> history) {
    this.history  = history;
    notifyDataSetChanged();
  }
}
