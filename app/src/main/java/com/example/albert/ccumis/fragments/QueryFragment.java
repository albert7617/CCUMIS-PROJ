package com.example.albert.ccumis.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albert.ccumis.tasks.QueryPDFTask;
import com.example.albert.ccumis.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class QueryFragment extends Fragment {

  private ProgressBar progressBar;
  private TextView textView;
  private CoordinatorLayout rootView;

  private String bsn;
  private int emp;

  public QueryFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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
                  .setDuration(5000)
                  .show();
        }
      });
    }
  };


}
