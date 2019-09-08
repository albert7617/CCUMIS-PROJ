package com.example.albert.ccumis_proj.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.albert.ccumis_proj.data.Employment;
import com.example.albert.ccumis_proj.R;

import java.util.ArrayList;
import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<com.example.albert.ccumis_proj.adapters.SelectAdapter.ViewHolder> {
  private LayoutInflater inflater;
  private List<Employment> employments;
  private List<Integer> expanded = new ArrayList<>();
  private List<Integer> selected = new ArrayList<>();
  private int selectedPosition  = -1;
  public SelectAdapter(Context context) {
    this.inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_select_employment, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    final boolean isExpanded = expanded.contains(position);
    final boolean isChecked  = selected.contains(position);
    final boolean isSelected = selectedPosition == position;
    if(employments != null) {
      Employment current = employments.get(position);
      if (current.operation == 1) {
        holder.checkBox.setVisibility(View.GONE);
      }
      if (current.operation == 2) {
        holder.batch.setVisibility(View.GONE);
        holder.batchTitle.setVisibility(View.GONE);
        holder.status.setVisibility(View.GONE);
        holder.statusTitle.setVisibility(View.GONE);
      }
      if( current.operation == 3) {
        holder.batch.setVisibility(View.GONE);
        holder.batchTitle.setVisibility(View.GONE);
        holder.statusTitle.setText(R.string.work_hours);
      }
      holder.title.setText(current.content);
      holder.time.setText(current.date);
      holder.batch.setText(current.batch_num);
      holder.department.setText(current.department);
      holder.hours.setText(current.hour_count);
      holder.weekend.setText(current.weekend);
      holder.status.setText(current.process);
      holder.linearLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
      holder.checkBox.setChecked(isChecked);
      holder.title.setSelected(isSelected);
      if(current.operation == 4) {
        holder.checkBox.setChecked(true);
        holder.checkBox.setClickable(false);
        holder.statusTitle.setText(R.string.work_hours);
        holder.checkBox.setEnabled(false);
        selected.add(position);
      }
      holder.imageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(expanded.contains(holder.getAdapterPosition())) {
            expanded.remove(Integer.valueOf(holder.getAdapterPosition()));
          } else {
            expanded.add(holder.getAdapterPosition());
          }
          notifyItemChanged(holder.getAdapterPosition());
        }
      });
      holder.title.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          selectedPosition = isSelected ? -1 : holder.getAdapterPosition();
          notifyItemChanged(holder.getAdapterPosition());
        }
      });
      holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          if(isChecked) {
            selected.add(holder.getAdapterPosition());
          } else {
            selected.remove(Integer.valueOf(holder.getAdapterPosition()));
          }
        }
      });
    }
  }

  @Override
  public int getItemCount() {
    return employments == null ? 0 : employments.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView title, time, batch, department, hours, weekend, status;
    private final TextView batchTitle, statusTitle;
    private final CheckBox checkBox;
    private final ImageButton imageButton;
    private final LinearLayout linearLayout;
    public ViewHolder(View itemView) {
      super(itemView);
      linearLayout = itemView.findViewById(R.id.linearLayoutDetail);
      imageButton = itemView.findViewById(R.id.imageBtn);
      checkBox = itemView.findViewById(R.id.checkboxSelectAll);
      title = itemView.findViewById(R.id.title);
      time = itemView.findViewById(R.id.time);
      batch = itemView.findViewById(R.id.batch);
      department = itemView.findViewById(R.id.department);
      hours = itemView.findViewById(R.id.hours);
      weekend = itemView.findViewById(R.id.weekend);
      status = itemView.findViewById(R.id.status);
      batchTitle = itemView.findViewById(R.id.batchTitle);
      statusTitle = itemView.findViewById(R.id.statusTitle);
    }
  }

  public void setEmployments(List<Employment> employments) {
    this.expanded = new ArrayList<>();
    this.selected = new ArrayList<>();
    this.employments = employments;
    notifyDataSetChanged();
  }

  public List<String> getSelectedEmployments() {
    List<String> selectEmployments = new ArrayList<>();
    for (Integer i : selected) {
      selectEmployments.add(employments.get(i).batch_num);
    }
    return selectEmployments;
  }

  public List<Employment> getSelectedEmploymentsObject() {
    List<Employment> selectEmployments = new ArrayList<>();
    for (Integer i : selected) {
      selectEmployments.add(employments.get(i));
    }
    return selectEmployments;
  }

  public void selectAll() {
    selected = new ArrayList<>();
    for (int i = 0; i < employments.size(); i++) {
      selected.add(i);
    }
    for (int i = 0; i < employments.size(); i++) {
      notifyItemChanged(i);
    }
  }
  public void unSelectAll() {
    selected = new ArrayList<>();
    for (int i = 0; i < employments.size(); i++) {
      notifyItemChanged(i);
    }
  }
}
