package com.example.albert.ccumis.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;

public class DatePickerDialogFragment extends DialogFragment {
  Callback callback;
  Context context;

  SublimeListenerAdapter listenerAdapter = new SublimeListenerAdapter() {
    @Override
    public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker, SelectedDate selectedDate, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {
      if (callback != null) {
        callback.onDateSet(selectedDate);
      }
      // Should actually be called by activity inside `Callback.onCancelled()`
      dismiss();
    }

    @Override
    public void onCancelled() {
      if (callback!= null) {
        callback.onCancelled();
      }
      // Should actually be called by activity inside `Callback.onCancelled()`
      dismiss();
    }
  };

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    getDialog().setCanceledOnTouchOutside(true);
    SublimePicker picker = new SublimePicker(context);
    SublimeOptions options = new SublimeOptions();
    assert getArguments() != null;
    options.setCanPickDateRange(getArguments().getBoolean("TYPE"));
    options.setDisplayOptions(SublimeOptions.ACTIVATE_DATE_PICKER);
    picker.initializePicker(options,listenerAdapter);
    return picker;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  // Set activity callback
  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onCancelled();
    void onDateSet(SelectedDate selectedDate);
  }

}