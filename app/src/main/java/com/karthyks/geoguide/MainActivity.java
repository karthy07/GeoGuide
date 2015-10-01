package com.karthyks.geoguide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Location mLastLocation;

    // UI components
    TextView _currentLocation;
    TextView _latitudeText;
    TextView _longitudeText;


    String resultText;
    String mLastUpdateTime;
    String mLatitude;
    String mLongitude;
    private boolean mRequestingLocationUpdates;

    Intent mLocationServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _currentLocation = (TextView) findViewById(R.id.curr_loc);
        _currentLocation.setClickable(false);
        _latitudeText = (TextView) findViewById(R.id.latitude);
        _longitudeText = (TextView) findViewById(R.id.longitude);
        mLocationServiceIntent = new Intent(this, LocationUpdateService.class);
        updateValuesFromBundle(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void UpdateUIText() {
        _currentLocation.setText(resultText);
        _latitudeText.setText(mLatitude);
        _longitudeText.setText(mLongitude);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        resultText = savedInstanceState.getString("lastLocation");
        mLatitude = savedInstanceState.getString("lastLocationLatitude");
        mLongitude = savedInstanceState.getString("lastLocationLongitude");
        UpdateUIText();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        outState.putParcelable(Constants.LOCATION_KEY, mLastLocation);
        outState.putString(Constants.LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(outState);
        outState.putString("lastLocation", resultText);
        outState.putString("lastLocationLatitude", mLatitude);
        outState.putString("lastLocationLongitude", mLongitude);
    }


    @Override
    protected void onResume() {
        super.onResume();
        startService(mLocationServiceIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(locationUpdate, new IntentFilter("LocationResult"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(mLocationServiceIntent);
        _currentLocation.setClickable(false);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdate);
    }

    @Override
    protected void onStop() {
        stopService(mLocationServiceIntent);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(mLocationServiceIntent);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    public void GeoLocate(View v) {
        //startService(mLocationServiceIntent);
    }

    private BroadcastReceiver locationUpdate =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            resultText = intent.getStringExtra("location");
            mLatitude = intent.getStringExtra("latitude");
            mLongitude = intent.getStringExtra("longitude");
            mLastUpdateTime = intent.getStringExtra("lastUpdateTime");
            _currentLocation.setClickable(true);
            UpdateUIText();
        }
    };

    public void OpenMapActivity(View v) {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtra("Latitude", Double.parseDouble(mLatitude));
        mapIntent.putExtra("Longitude", Double.parseDouble(mLongitude));
        startActivity(mapIntent);
    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(Constants.REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        Constants.REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(Constants.LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocation is not null.
                mLastLocation = savedInstanceState.getParcelable(Constants.LOCATION_KEY);
                mLatitude = String.valueOf(mLastLocation.getLatitude());
                mLongitude = String.valueOf(mLastLocation.getLongitude());
                UpdateUIText();
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(Constants.LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        Constants.LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }
}
