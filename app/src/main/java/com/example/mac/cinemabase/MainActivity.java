package com.example.mac.cinemabase;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends ListActivity {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://flickering-torch-2608.firebaseio.com/";

    private String mUsername;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;

    //TAG for debugging messages
    private final String TAG = "MyTag";

    //drawer options
    private final int SOCIAL    = 0;
    private final int SETTINGS  = 1;
    private final int HELP      = 2;
    private final int LOGOUT    = 3;

    //Drawer components
    private String[] mDrawerTitles;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean isSlideOpen;

    //movie search
    private OMDBRequest searchMovie;
    private SearchView searchView;

    //facebook login
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallback;
    private AccessTokenTracker mAccessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        // Make sure we have a mUsername
        setupUsername();

        setTitle("Chatting as " + mUsername);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child("chat");

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        //init side drawer components
        initDrawerComponents();

        //setup searchview
        initSearchView();

        //setup facebook components
        initFacebook();
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }

    private void setupUsername() {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);
        /*if (mUsername == null) {
            Random r = new Random();
            // Assign a random user name if we don't have one saved.
            mUsername = "JavaUser" + r.nextInt(100000);
            prefs.edit().putString("username", mUsername).commit();
        }*/
    }

    //initialize facebook api components
    private void initFacebook(){
        FacebookSdk.sdkInitialize(this);

        mCallbackManager = CallbackManager.Factory.create();

        mLoginButton = new LoginButton(this);
        mLoginButton.setReadPermissions("user_friends");

        //setup call back for login attempt
        mCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this,"Login Succesfull", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
            }
        };

        mLoginButton.registerCallback(mCallbackManager, mCallback);

        //listens for access token changes.
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        mAccessTokenTracker.startTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance){
        super.onSaveInstanceState(savedInstance);
        Log.d(TAG,"onSavedInstanceState called");
        savedInstance.putInt("key",1);
    }

    @Override
    protected void onResume(){
        Log.d(TAG,"onResume");
        super.onResume();

        //used by the facebook api Logs 'install' and 'app activate'
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause(){
        Log.d(TAG,"onPause");
        super.onPause();

        //log 'deactive' by the facebook api
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mAccessTokenTracker.stopTracking();
    }

    /**
     * setup listener for searchview
     */
    private void initSearchView(){

        //initialize movie searching object
        searchMovie = new OMDBRequest(this);

        //initialize movie search view and attach listeners
        searchView = (SearchView)findViewById(R.id.searchBar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "submitted query " + query);
                movieSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //handle movie query
    private void movieSearch(String movie){
        if( movie == null || movie.length() == 0){
            Log.d(TAG,"Movie title query was empty");
            return;
        }
        searchView.setQuery("",false);
        searchMovie.requestMovie(movie);
    }

    /**
     * Initialize the components of the side drawer.
     */
    private void initDrawerComponents(){
        isSlideOpen = false;

        //setup functionality of drawer
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new CustomActionBarDrawerToggle(this,mDrawerLayout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //custom shadow which overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        //setup drawer titles
        mDrawerTitles = getResources().getStringArray(R.array.drawerTitles);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item,mDrawerTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }


    /**
     * private class which listens for user selected options
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG,"Selected " + position);
            clickDrawerListener(null); //close drawer
            selected(position);
        }
    }

    /**
     *display appropriate activity based on user selection
     * @param pos array position of user selected
     */
    private void selected(int pos){
        Intent intent = null;
        switch (pos){
            case SOCIAL:
                mLoginButton.performClick();
                break;
            case SETTINGS:
                break;
            case HELP:
                intent = new Intent(this, HelpPage.class);
                break;
            case LOGOUT:
                break;
            default:
                Log.e(TAG,"Error: position " + pos + " is out of range");
                return;
        }
        if(intent != null){
            startActivity(intent);
        }
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
