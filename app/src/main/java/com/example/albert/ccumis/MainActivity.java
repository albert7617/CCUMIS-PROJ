package com.example.albert.ccumis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Create a new Fragment to be placed in the activity layout
    NewDocFragment newDocFragment = new NewDocFragment();

    // Add the fragment to the 'fragment_container' FrameLayout
    getSupportFragmentManager().beginTransaction()
            .add(R.id.main_frame, newDocFragment)
            .commit();



    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    View headerView = navigationView.getHeaderView(0);
    TextView textView = headerView.findViewById(R.id.username);
    textView.setText(pref.getString(getString(R.string.pref_username), getString(R.string.not_login)));

    if(!pref.getBoolean(getString(R.string.pref_logined), false)) {
      startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
    Bitmap userIcon = IdenticonGenerator.generate(pref.getString(getString(R.string.pref_username), "NA"));
    ImageView imageView = headerView.findViewById(R.id.imageView);
    imageView.setImageBitmap(userIcon);
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

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    FrameLayout fragmentContainer = findViewById(R.id.main_frame);
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

    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
