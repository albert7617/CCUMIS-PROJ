package com.example.albert.ccumis;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;

public class TimePickerDialogFragment extends DialogFragment {
  TimePickerDialogFragment.Callback callback;
  SublimeListenerAdapter listenerAdapter = new SublimeListenerAdapter() {
    @Override
    public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker, SelectedDate selectedDate, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {
      if (callback != null) {
        callback.onDateSet(hourOfDay, minute);
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
    SublimePicker picker = new SublimePicker(getContext());
    SublimeOptions options = new SublimeOptions();
    options.setDisplayOptions(SublimeOptions.ACTIVATE_TIME_PICKER);
    options.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);
    picker.initializePicker(options,listenerAdapter);
    return picker;
  }

  // Set activity callback
  public void setCallback(TimePickerDialogFragment.Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onCancelled();
    void onDateSet(int hourOfDay, int minute);
  }
}
