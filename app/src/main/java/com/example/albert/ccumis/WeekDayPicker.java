package com.example.albert.ccumis;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeekDayPicker extends LinearLayout {

  private List<ToggleButton> dayToggles = new ArrayList<>();

  private DayPressedListener dayPressedListener;
  private DaySelectionChangedListener daySelectionChangedListener;

  public WeekDayPicker(Context context) {
    this(context, null);
  }

  public WeekDayPicker(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public WeekDayPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflateLayoutUsing(context);
    bindViews();
    listenToToggleEvents();
  }

  public void setDayPressedListener(DayPressedListener dayPressedListener) {
    this.dayPressedListener = dayPressedListener;
  }

  public void setDaySelectionChangedListener(DaySelectionChangedListener daySelectionChangedListener) {
    this.daySelectionChangedListener = daySelectionChangedListener;
  }

  public List<Weekday> getSelectedDays() {
    List<Weekday> selectedDays = new ArrayList<>();

    for (int i = 0; i < dayToggles.size(); i++) {
      if (dayToggles.get(i).isChecked()) {
        selectedDays.add(Weekday.values()[i]);
      }
    }

    return selectedDays;
  }

  public boolean isSelected(Weekday weekday) {
    return getSelectedDays().contains(weekday);
  }

  public void selectDay(final Weekday weekday) {
    handleSelection(weekday);
  }

  public void deselectDay(final Weekday weekday) {
    handleDeselection(weekday);
  }

  public void setSelectedDays(final Weekday... weekdays) {
    setSelectedDays(Arrays.asList(weekdays));
  }

  public void setSelectedDays(final List<Weekday> weekdays) {
    disableListenerWhileExecuting(new Action() {
      @Override
      public void call() {
        clearSelection();

        for (Weekday weekday: weekdays) {
          selectDay(weekday);
        }
      }
    });
  }

  public void clearSelection() {
    disableListenerWhileExecuting(new Action() {
      @Override
      public void call() {
        for (Weekday selectedDay: getSelectedDays()) {
          deselectDay(selectedDay);
        }
      }
    });
  }

  private void inflateLayoutUsing(Context context) {
    LayoutInflater.from(context).inflate(R.layout.day_of_week_picker, this, true);
  }

  private void bindViews() {
    dayToggles.add((ToggleButton) findViewById(R.id.monday_toggle));
    dayToggles.add((ToggleButton) findViewById(R.id.tuesday_toggle));
    dayToggles.add((ToggleButton) findViewById(R.id.wednesday_toggle));
    dayToggles.add((ToggleButton) findViewById(R.id.thursday_toggle));
    dayToggles.add((ToggleButton) findViewById(R.id.friday_toggle));
    dayToggles.add((ToggleButton) findViewById(R.id.saturday_toggle));
    dayToggles.add((ToggleButton) findViewById(R.id.sunday_toggle));
  }

  private void listenToToggleEvents() {
    for (int i = 0; i < dayToggles.size(); i++) {
      final Weekday weekdayForToggle = Weekday.values()[i];

      dayToggles.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean didGetChecked) {
          // temporally undo what the user just did so the selection mode
          // can evaluate what it should do with the intended action
          // the selection mode will generate the proper actions to
          // carry about based on the users intent in
          // applySelectionChangesUsing(SelectionDifference selectionDifference)
          ignoreToggleEvents();
          compoundButton.setChecked(!didGetChecked);
          listenToToggleEvents();

          if (didGetChecked) {
            handleSelection(weekdayForToggle);
          } else {
            handleDeselection(weekdayForToggle);
          }
        }
      });
    }
  }

  private void ignoreToggleEvents() {
    for (ToggleButton toggleButton: dayToggles) {
      toggleButton.setOnCheckedChangeListener(null);
    }
  }

  private void handleSelection(Weekday dayToSelect) {
    SelectionState currentSelectionState = getSelectionState();
    SelectionState nextSelectionState = getSelectionStateAfterSelecting(currentSelectionState, dayToSelect);
    SelectionDifference selectionDifference = new SelectionDifference(currentSelectionState, nextSelectionState);
    applySelectionChangesUsing(selectionDifference);
  }

  private void handleDeselection(Weekday dayToDeselect) {
    SelectionState currentSelectionState = getSelectionState();
    SelectionState nextSelectionState = getSelectionStateAfterDeselecting(currentSelectionState, dayToDeselect);
    SelectionDifference selectionDifference = new SelectionDifference(currentSelectionState, nextSelectionState);
    applySelectionChangesUsing(selectionDifference);
  }

  private void applySelectionChangesUsing(SelectionDifference selectionDifference) {
    ignoreToggleEvents();

    for (Weekday dayToDeselect: selectionDifference.getDaysToDeselect()) {
      getToggleFor(dayToDeselect).setChecked(false);
      onDayPressed(dayToDeselect, false);
    }

    for (Weekday dayToSelect: selectionDifference.getDaysToSelect()) {
      getToggleFor(dayToSelect).setChecked(true);
      onDayPressed(dayToSelect, true);
    }

    listenToToggleEvents();
    onDaySelectionChanged();
  }

  private void clearSelectionIgnoringSelectionMode() {
    ignoreToggleEvents();

    for (ToggleButton dayToggle: dayToggles) {
      dayToggle.setChecked(false);
    }

    listenToToggleEvents();
  }

  private void disableListenerWhileExecuting(Action action) {
    DaySelectionChangedListener tempListener = daySelectionChangedListener;
    daySelectionChangedListener = null;

    action.call();

    daySelectionChangedListener = tempListener;
    onDaySelectionChanged();
  }

  private ToggleButton getToggleFor(Weekday weekday) {
    return dayToggles.get(weekday.ordinal());
  }

  private SelectionState getSelectionState() {
    return new SelectionState(getSelectedDays());
  }

  private SelectionState getSelectionStateAfterSelecting(SelectionState currentSelectionState, Weekday dayToSelect) {
    return currentSelectionState.withDaySelected(dayToSelect);
  }

  private SelectionState getSelectionStateAfterDeselecting(SelectionState currentSelectionState, Weekday dayToDeselect) {
    return currentSelectionState.withDayDeselected(dayToDeselect);
  }

  private void onDaySelectionChanged() {
    if (daySelectionChangedListener != null) {
      daySelectionChangedListener.onDaySelectionChanged(getSelectedDays());
    }
  }

  private void onDayPressed(Weekday weekday, boolean didGetSelected) {
    if (dayPressedListener != null) {
      dayPressedListener.onDayPressed(weekday, didGetSelected);
    }
  }

  public enum Weekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static List<Weekday> getAllDays() {
      return Arrays.asList(Weekday.values());
    }
  }

  public interface DaySelectionChangedListener {
    void onDaySelectionChanged(List<Weekday> selectedDays);
  }

  public interface DayPressedListener {
    void onDayPressed(Weekday weekday, boolean isSelected);
  }

  private interface Action {
    void call();
  }

  private class SelectionDifference {
    private List<Weekday> daysToSelect;
    private List<Weekday> daysToDeselect;

    SelectionDifference(SelectionState initialSelectionState, SelectionState finalSelectionState) {
      daysToSelect = new ArrayList<>();
      daysToDeselect = new ArrayList<>();

      List<Weekday> initialSelections = initialSelectionState.getSelectedDays();
      List<Weekday> finalSelections = finalSelectionState.getSelectedDays();

      for (Weekday day: initialSelections) {
        // if final selections did not contain an initial selection
        // then we should deselect that day
        if (!finalSelections.contains(day)) {
          daysToDeselect.add(day);
        }
      }

      for (Weekday day: finalSelections) {
        // if initial selection did not contain a final selection
        // then we should select that day
        if (!initialSelections.contains(day)) {
          daysToSelect.add(day);
        }
      }
    }

    List<Weekday> getDaysToSelect() {
      return new ArrayList<>(daysToSelect);
    }

    List<Weekday> getDaysToDeselect() {
      return new ArrayList<>(daysToDeselect);
    }
  }

  private class SelectionState {
    private List<Weekday> selectedDays;

    SelectionState() {
      this(new ArrayList<Weekday>());
    }

    SelectionState(List<Weekday> selectedDays) {
      this.selectedDays = new ArrayList<>(selectedDays);
    }

    List<Weekday> getSelectedDays() {
      return new ArrayList<>(selectedDays);
    }

    public SelectionState withSingleDay(Weekday day) {
      return new SelectionState().withDaySelected(day);
    }

    SelectionState withDaySelected(Weekday dayToSelect) {
      List<Weekday> newSelections = new ArrayList<>(selectedDays);
      newSelections.add(dayToSelect);
      return new SelectionState(newSelections);
    }

    SelectionState withDayDeselected(Weekday dayToDeselect) {
      List<Weekday> newSelections = new ArrayList<>(selectedDays);
      newSelections.remove(dayToDeselect);
      return new SelectionState(newSelections);
    }
  }

}
