package com.example.albert.ccumis.fragments;

import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.albert.ccumis.MainActivity;
import com.example.albert.ccumis.QueryPDFTask;
import com.example.albert.ccumis.R;

public class QueryFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  public QueryFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment QueryFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static QueryFragment newInstance(String param1, String param2) {
    QueryFragment fragment = new QueryFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_query, container, false);
    final EditText editTextBSN = view.findViewById(R.id.pdf_batchNum);
    final RadioGroup radioGroup = view.findViewById(R.id.pdf_empTypeGroup);
    Button submit = view.findViewById(R.id.pdf_submit);
    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String bsn = editTextBSN.getText().toString();
        int emp = 0;
        switch (radioGroup.getCheckedRadioButtonId()) {
          case R.id.emptype1:
            emp = 1;
            break;
          case R.id.emptype2:
            emp = 2;
            break;
          case R.id.emptype3:
            emp = 3;
            break;
        }
        new QueryPDFTask(getContext(), "1080326061", 1).execute();

      }
    });
    return view;
  }
}
