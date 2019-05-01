package com.example.albert.ccumis;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albert.ccumis.fragments.AboutFragment;
import com.example.albert.ccumis.fragments.DeleteDocFragment;
import com.example.albert.ccumis.fragments.NewDocFragment;
import com.example.albert.ccumis.fragments.PrintDocFragment;
import com.example.albert.ccumis.fragments.SelectDocFragment;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    AppUpdater appUpdater = new AppUpdater(this);
    appUpdater.setUpdateFrom(UpdateFrom.GITHUB)
            .setGitHubUserAndRepo("albert7617", "CCUMIS")
            .setTitleOnUpdateAvailable("Update available")
            .setContentOnUpdateAvailable("Check out the latest version")
            .setButtonUpdate("Update now?")
            .setButtonUpdateClickListener(new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/albert7617/CCUMIS/releases"));
                startActivity(browserIntent);
              }
            })
	          .setButtonDismiss("Maybe later")
            .setButtonDismissClickListener(new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            })
            .setButtonDoNotShowAgain(null)
            .setCancelable(false)
            .start();

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Create a new Fragment to be placed in the activity layout
    NewDocFragment newDocFragment = new NewDocFragment();

    // Add the fragment to the 'fragment_container' FrameLayout
    getSupportFragmentManager().beginTransaction()
            .add(R.id.main_frame, newDocFragment)
            .commit();


    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    View headerView = navigationView.getHeaderView(0);

    headerView.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
      }
    });
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    NavigationView navigationView = findViewById(R.id.nav_view);
    View headerView = navigationView.getHeaderView(0);
    TextView textView = headerView.findViewById(R.id.username);
    textView.setText(pref.getString(getString(R.string.pref_username), getString(R.string.not_login)));

    if (!pref.getBoolean(getString(R.string.pref_logined), false)) {
      startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
    Bitmap userIcon = IdenticonGenerator.generate(pref.getString(getString(R.string.pref_username), "NA"));
    ImageView imageView = headerView.findViewById(R.id.imageView);
    imageView.setImageBitmap(Bitmap.createScaledBitmap(userIcon, 120, 120, false));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_edit) {
      this.startActivity(new Intent(this, EditStuffActivity.class));
      return true;
    } else if (id == R.id.action_logout) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString(getString(R.string.pref_username), "");
      editor.putString(getString(R.string.pref_password), "");
      editor.putBoolean(getString(R.string.pref_logined), false);
      editor.commit();
      startActivity(new Intent(MainActivity.this, LoginActivity.class));
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    if (id == R.id.nav_new_doc) {
      getSupportFragmentManager().beginTransaction()
              .replace(R.id.main_frame, new NewDocFragment())
              .commit();
    } else if (id == R.id.nav_sel_doc) {
      getSupportFragmentManager().beginTransaction()
              .replace(R.id.main_frame, new SelectDocFragment())
              .commit();
    } else if (id == R.id.nav_del_doc) {
      getSupportFragmentManager().beginTransaction()
              .replace(R.id.main_frame, new DeleteDocFragment())
              .commit();
    } else if (id == R.id.nav_prt_doc) {
      getSupportFragmentManager().beginTransaction()
              .replace(R.id.main_frame, new PrintDocFragment())
              .commit();
    } else if (id == R.id.nav_abt_doc) {
      getSupportFragmentManager().beginTransaction()
              .replace(R.id.main_frame, new AboutFragment())
              .commit();
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch (requestCode) {
      case 200: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // permission was granted, yay! Do the
          // contacts-related task you need to do.
        } else {
          Toast.makeText(this, "請授予寫入檔案權限", Toast.LENGTH_SHORT).show();
          // permission denied, boo! Disable the
          // functionality that depends on this permission.
        }
      }

      // other 'case' lines to check for other
      // permissions this app might request.
    }
  }
}
