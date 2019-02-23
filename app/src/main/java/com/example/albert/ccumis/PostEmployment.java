package com.example.albert.ccumis;

public class PostEmployment {
  @Override
  public String toString() {
    return "PostEmployment{" +
            "department='" + department + '\'' +
            ", start_year=" + start_year +
            ", start_month=" + start_month +
            ", start_day=" + start_day +
            ", end_year=" + end_year +
            ", end_month=" + end_month +
            ", end_day=" + end_day +
            ", status=" + status +
            ", weekend=" + weekend +
            '}';
  }

  public String department;

  public int start_year;

  public int start_month;

  public int start_day;

  public int end_year;

  public int end_month;

  public int end_day;

  public int status;

  public int weekend;

}
