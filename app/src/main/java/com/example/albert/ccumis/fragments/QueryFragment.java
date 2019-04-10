package com.example.albert.ccumis.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albert.ccumis.QueryPDFTask;
import com.example.albert.ccumis.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class QueryFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private ProgressBar progressBar;
  private TextView textView;
  private CoordinatorLayout rootView;

  private String bsn;
  private int emp;

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
    progressBar = view.findViewById(R.id.progressBar);
    textView = view.findViewById(R.id.querying);
    rootView = view.findViewById(R.id.coordinator);
    Button submit = view.findViewById(R.id.pdf_submit);
    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        bsn = editTextBSN.getText().toString();
        emp = 0;
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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
          // Permission is not granted
          ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        } else {
          queryPDF();
        }

      }
    });
    return view;
  }

  private void queryPDF() {
    progressBar.setVisibility(View.VISIBLE);
    textView.setVisibility(View.VISIBLE);
    progressBar.setMax(99);
    progressBar.setProgress(0);
    QueryPDFTask task = new QueryPDFTask(getContext(), bsn, emp);
    task.setCallback(callback);
    task.execute();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch (requestCode) {
      case 200: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // permission was granted, yay! Do the
          // contacts-related task you need to do.
          queryPDF();
        } else {
          // permission denied, boo! Disable the
          // functionality that depends on this permission.
        }
        return;
      }

      // other 'case' lines to check for other
      // permissions this app might request.
    }
  }

  private QueryPDFTask.Callback callback = new QueryPDFTask.Callback() {
    @Override
    public void result(Map<String, String> result) {
      if(result.containsKey("error")) {
        Toast.makeText(getContext(), result.get("error"), Toast.LENGTH_SHORT).show();
      } else {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = result.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry entry = (Map.Entry) iterator.next();
          stringBuilder.append(entry.getKey());
          stringBuilder.append("=");
          try {
            stringBuilder.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }

          if(iterator.hasNext()){
            stringBuilder.append("&");
          }
        }

      }
    }

    @Override
    public void updateProgress(int progress) {
      if(progress == -1){
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
          }
        });
      } else {
        progressBar.setProgress(progress);
      }
    }

    @Override
    public void pdfSaved(final String filePath) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          Snackbar.make(rootView, "工讀單已儲存到下載資料夾", Snackbar.LENGTH_SHORT)
                  .setAction("開啟", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      Intent target = new Intent(Intent.ACTION_VIEW);
                      target.setDataAndType(Uri.parse(filePath),"application/pdf");
                      Intent intent = Intent.createChooser(target, "Open File");
                      try {
                        startActivity(intent);
                      } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                      }
                    }
                  })
                  .show();
        }
      });
    }
  };


}
