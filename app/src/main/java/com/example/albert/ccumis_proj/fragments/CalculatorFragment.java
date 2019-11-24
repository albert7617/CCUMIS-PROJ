package com.example.albert.ccumis_proj.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.albert.ccumis_proj.R;

public class CalculatorFragment extends DialogFragment {
//  private Context context;
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater layoutInflater = getActivity().getLayoutInflater();
    builder.setView(layoutInflater.inflate(R.layout.dialog_calculator, null));
    return builder.create();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }
}
