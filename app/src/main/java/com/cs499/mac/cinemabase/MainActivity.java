package com.cs499.mac.cinemabase;

import java.util.*;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.Parcelable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends ListActivity implements AdapterView.OnItemSelectedListener {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://flickering-torch-2608.firebaseio.com/";

    //firebase chat components
    private String mUsername;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;

    //TAG for debugging messages
    private final String TAG = "MyTag";

    //drawer options
    private final int SOCIAL    = 0;
    private final int SETTINGS  = 1;
    private final int LOGOUT    = 2;

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

    //top movies components
    private ListView topMoviesList;
    private String[] topMoviesNames;
    private Movie[] topMoviesArray;
    private TopMoviesListAdapter moviesListAdapter;
    private final String SAVED_MOVIES = "SAVED_MOVIES_INSTANCE";

    //top movie database
    private Spinner spinnerDB;
    private String[] spinnerDBUrls;
    private LinearLayout progressLayout;
    private LinearLayout topMoviesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "main oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init side drawer components
        initDrawerComponents();

        //setup searchview
        initSearchView();

        //setup facebook components
        initFacebook();

        //setup firebase
        initFirebase();

        //setup Spinner
        initSpinnerDB();

        //setup top movies view
        initTopMoviesView();
        if(savedInstanceState != null && savedInstanceState.containsKey(SAVED_MOVIES)){
            Parcelable parcelArray[] =savedInstanceState.getParcelableArray(SAVED_MOVIES);
            topMoviesArray = new Movie[parcelArray.length];
            for(int i = 0; i < parcelArray.length; ++i){
                topMoviesArray[i] = (Movie) parcelArray[i];
            }

        }

    }

    private void initFirebase(){
        Firebase.setAndroidContext(this);

        // Make sure we have a mUsername
        setupUsername();

        //setTitle("Chatting as " + mUsername);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child("chat");

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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
        mUsername = prefs.getString("Review", null);
        if (mUsername == null) {
            Random r = new Random();
            // Assign a random user name if we don't have one saved.
            mUsername = "Review" + r.nextInt(100000);
            prefs.edit().putString("Review", mUsername).commit();
        }
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
                Toast.makeText(MainActivity.this, "Connected to Facebook", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Facebook connection cancelled!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Connection error!", Toast.LENGTH_SHORT).show();
            }
        };

        mLoginButton.registerCallback(mCallbackManager, mCallback);

        //listens for access token changes.
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                Log.d(TAG,"access token changed");
            }
        };

        mAccessTokenTracker.startTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        Log.d(TAG, "onSavedInstanceState called");
        savedInstance.putParcelableArray(SAVED_MOVIES, topMoviesArray);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        //used by the facebook api Logs 'install' and 'app activate'
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();

        //log 'deactive' by the facebook api
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onStop(){
        Log.d(TAG,"main onStop");
        super.onStop();
        mAccessTokenTracker.stopTracking();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    /**
     * setup listener for searchview
     */
    private void initSearchView(){

        //initialize movie searching object
        searchMovie = new OMDBRequest(this);

        //initialize movie search view and attach listeners
        searchView = (SearchView) findViewById(R.id.searchBar);
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
            Log.d(TAG, "Movie title query was empty");
            return;
        }
        searchView.setQuery("", false);
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

    /*
    * Implements methods for spinner movie database
    * */

    public void initSpinnerDB(){
        spinnerDB = (Spinner) findViewById(R.id.spinnerDB);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.spinnerMovieDatabase,android.R.layout.simple_spinner_item);
        spinnerDB.setAdapter(adapter);
        spinnerDB.setOnItemSelectedListener(this);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        if(pos == 0) {
            imdbRequestTop(0);
        }
        else if(pos == 1) {
            imdbRequestTop(1);
        }
        else{
            imdbRequestTop(2);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this, "Movie Database Not Selected", Toast.LENGTH_LONG).show();
    }


    /**
     * private class which listens for user selected options
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "Selected " + position);
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

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limit(50), this, R.layout.chat_message, mUsername);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    //Toast.makeText(MainActivity.this, "Connection success! Start a movie review.", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Connected to Firebase");
                } else {
                    //Toast.makeText(MainActivity.this, "Connecting to movie review service...", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Disconnected from Firebase");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    //init components
    private void initTopMoviesView(){
        topMoviesLayout = (LinearLayout)findViewById(R.id.topMoviesLayout);
        topMoviesList = (ListView)findViewById(R.id.topMoviesListView);
        progressLayout = (LinearLayout) findViewById(R.id.progressbar_view);
        spinnerDBUrls = getResources().getStringArray(R.array.spinnerDatabaseUrls);
        imdbRequestTop(0);
    }

    /**
     * request the top movies from the selected spinner option
     * @param option
     */
    private void imdbRequestTop(int option){
        progressLayout.setVisibility(View.VISIBLE);
        topMoviesLayout.setVisibility(View.GONE);
        final JSONObject jsObj = new JSONObject();
        JsonObjectRequest jsObjReq = new JsonObjectRequest(Request.Method.GET,
                spinnerDBUrls[option], jsObj,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject movieResponse){
                        parseImdb(movieResponse);
                        pullMoviesFromServer();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG,"Error with imdb request");
                        Toast.makeText(MainActivity.this,"Error fetching from IMDB",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        MySingleton.getInstance(this).addToRequestQueue(jsObjReq);
    }

    /**
     * parses imdb json response, extracts the name of the movies and stores them in an array
     * @param response
     */
    private void parseImdb(JSONObject response){
        try{
            JSONArray arrayJson = response.getJSONArray("movies");
            topMoviesNames = new String[arrayJson.length()];
            for(int i = 0; i < arrayJson.length(); ++i){
                topMoviesNames[i] = arrayJson.getJSONObject(i).getString("title");
            }
        }catch (Exception e){
            Log.e(TAG, "Error parsing imdb json response");
            Toast.makeText(MainActivity.this,"An error ocurred with imdb response",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void pullMoviesFromServer(){

        topMoviesArray = new Movie[topMoviesNames.length];
        moviesListAdapter = new TopMoviesListAdapter(this,topMoviesArray);
        topMoviesList.setAdapter(moviesListAdapter);

        String movieTitle;
        for(int i = 0; i < topMoviesNames.length; ++i){
            movieTitle = topMoviesNames[i];
            if(movieTitle == null || movieTitle.length() < 1)
                continue;
            movieRequest(movieTitle,i);
        }
    }

    private void movieRequest(String movieName, final int index) {
        String url = new OMDBRequest().constructURL(movieName);
        final JSONObject jsObj = new JSONObject();
        JsonObjectRequest jsObjReq = new JsonObjectRequest(Request.Method.GET,
                url, jsObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject movieResponse) {
                        topMoviesArray[index] = new Movie(movieResponse);
                        topMoviesArray[index].parse();
                        progressLayout.setVisibility(View.GONE);
                        topMoviesLayout.setVisibility(View.VISIBLE);
                        moviesListAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error " + error);
                        Log.d(TAG, "Network Response: " + error.networkResponse.statusCode);
                        Log.d(TAG, "Localized Message: " + error.networkResponse.data.toString());
                    }
                }
        );
        MySingleton.getInstance(this).addToRequestQueue(jsObjReq);
    }
}
