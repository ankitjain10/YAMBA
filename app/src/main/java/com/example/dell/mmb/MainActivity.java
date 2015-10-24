package com.example.dell.mmb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent menuIntent;

        switch (item.getItemId()) {
            case R.id.action_view_refresh_service: {
                menuIntent = new Intent(this, RefreshService.class);
                startService(menuIntent);
                return true;
            }
            case R.id.action_view_preference: {
                menuIntent = new Intent(this, PrefActivity.class);
                startActivity(menuIntent);
                return true;
            }

            case R.id.action_view_database: {
                Intent intent = new Intent(getApplicationContext(),
                        AndroidDatabaseManager.class);
                startActivity(intent);
                return true;
            }
            default:
                return false;
        }
    }
}
