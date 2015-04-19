package com.example.mac.cinemabase;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


public class MainActivity extends Activity {

    //TAG for debugging messages
    private final String TAG = "MainActivity";

    //Drawer components
    private String[] mDrawerTitles;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean isSlideOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawerComponents();
    }

    /**
     * Initialize the components of the side drawer.
     */
    private void initDrawerComponents(){
        isSlideOpen = false;
        mDrawerTitles = getResources().getStringArray(R.array.drawerTitles);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        mDrawerToggle = new CustomActionBarDrawerToggle(this,mDrawerLayout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    //Extended ActionBarDrawer
    private class CustomActionBarDrawerToggle extends ActionBarDrawerToggle{

        public CustomActionBarDrawerToggle(Activity mActivity,
                                           DrawerLayout mDrawerLayout) {
            super(mActivity, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        }

        @Override
        public void onDrawerClosed(View view){
            invalidateOptionsMenu();//call to onPrepareOptionsMenu
            isSlideOpen = false;
            Log.d(TAG,"Closing Drawer");
        }

        @Override
        public void onDrawerOpened(View view){
            invalidateOptionsMenu();//call to onPrepareOptionsMenu
            isSlideOpen = true;
            Log.d(TAG, "Opening Drawer");
        }
    }

    //methods onDrawerOpened and onDrawerClosed are called
    public void clickDrawerListener(View view){
        if(isSlideOpen){
            mDrawerLayout.closeDrawer(Gravity.START);
            isSlideOpen = false;
        } else {
            mDrawerLayout.openDrawer(Gravity.START);
            isSlideOpen = true;
        }
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
            /*Toast toast = Toast.makeText(getApplicationContext(),"settings menu",Toast.LENGTH_LONG);
            toast.show();*/
            Intent intent = new Intent(this, SettingsPage.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
