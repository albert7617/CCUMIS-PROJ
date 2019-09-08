package com.example.albert.ccumis_proj.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albert.ccumis_proj.R;

import java.util.ArrayList;
import java.util.List;

public class StuffAdapter extends RecyclerView.Adapter<StuffAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private List<String> stuffs;

  public StuffAdapter(Context context, List<String> stuffs) {
    this.inflater = LayoutInflater.from(context);
    this.stuffs = stuffs;
  }

  public void setStuff(List<String> stuffs) {
    this.stuffs = new ArrayList<>();
    this.stuffs= stuffs;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_stuffs, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.item_name.setText(stuffs.get(position));

  }

  @Override
  public int getItemCount() {
    return this.stuffs == null ? 0 : this.stuffs.size();
  }

  public String getStuffAtPosition(int position) {
    return this.stuffs.get(position);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView item_name;
    public ViewHolder(View itemView) {
      super(itemView);
      item_name = itemView.findViewById(R.id.stuffText);
    }
  }
}
