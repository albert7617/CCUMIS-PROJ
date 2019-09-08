package com.example.albert.ccumis_proj.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.albert.ccumis_proj.DocumentActivity;
import com.example.albert.ccumis_proj.data.Employment;
import com.example.albert.ccumis_proj.R;

import java.util.List;

public class EmploymentListAdapter extends RecyclerView.Adapter<com.example.albert.ccumis_proj.adapters.EmploymentListAdapter.EmploymentViewHolder> {
  private List<Employment> employments;
  private LayoutInflater inflater;
  private Context context;
  private Callback callback;

  public EmploymentListAdapter(Context context) {
    this.inflater = LayoutInflater.from(context);
    this.context = context;
  }

  @NonNull
  @Override
  public EmploymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_document_card, parent, false);
    return new EmploymentViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final EmploymentViewHolder holder, int position) {
    if(employments != null) {
      Employment current = employments.get(position);
      String hoursCount = String.format("%.1f", ((float) current.duration)/60) + "Hr";
      holder.hour.setText(hoursCount);
      holder.department.setText(current.department);
      holder.date.setText(current.date);
      String time = String.format("%02d", current.start_hour) + ":" +
              String.format("%02d", current.start_minute) + "~" +
              String.format("%02d", current.end_hour) + ":" +
              String.format("%02d", current.end_minute);
      holder.time.setText(time);
      holder.content.setText(current.content);
      switch (current.status) {
        case 200:
          holder.confirmed.setVisibility(View.VISIBLE);
          holder.unconfirmed.setVisibility(View.GONE);
          holder.error.setVisibility(View.GONE);
          break;
        case 401:
          holder.unconfirmed.setVisibility(View.VISIBLE);
          holder.confirmed.setVisibility(View.GONE);
          holder.error.setVisibility(View.GONE);
          break;
        case 409:
          holder.error.setVisibility(View.VISIBLE);
          holder.unconfirmed.setVisibility(View.GONE);
          holder.confirmed.setVisibility(View.GONE);
          holder.errorMsg.setText(current.error_msg);
          break;
      }
      holder.edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(context, DocumentActivity.class);
          intent.putExtra("SERI_NO", employments.get(holder.getAdapterPosition()).seri_no);
          context.startActivity(intent);
        }
      });
      holder.delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          new AlertDialog.Builder(context)
                  .setTitle(R.string.delete_warning)
                  .setPositiveButton(R.string.delete_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                  })
                  .setNegativeButton(R.string.delete_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      if(callback != null) {
                        callback.delete(employments.get(holder.getAdapterPosition()).seri_no);
                      }
                    }
                  })
                  .show();
        }
      });
    }
  }

  @Override
  public int getItemCount() {
    return employments == null ? 0 : employments.size();
  }

  class EmploymentViewHolder extends RecyclerView.ViewHolder {
    private final TextView confirmed, unconfirmed, errorMsg, hour, department, date, time, content;
    private final LinearLayout error;
    private final Button edit, delete;
    EmploymentViewHolder(View itemView) {
      super(itemView);
      errorMsg = itemView.findViewById(R.id.error_msg);
      confirmed = itemView.findViewById(R.id.confirmed);
      unconfirmed = itemView.findViewById(R.id.unconfirmed);
      error = itemView.findViewById(R.id.error);
      hour = itemView.findViewById(R.id.hoursCount);
      department = itemView.findViewById(R.id.department);
      date = itemView.findViewById(R.id.date);
      time = itemView.findViewById(R.id.time);
      content = itemView.findViewById(R.id.content);
      edit = itemView.findViewById(R.id.edit);
      delete = itemView.findViewById(R.id.delete);
    }
  }



  public void setEmployments(List<Employment> employments) {
    this.employments = employments;
    notifyDataSetChanged();
  }

  public void setCallback (Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void delete(int seri_no);
  }
}
