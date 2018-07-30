package com.cainam.tuchat.homescreen;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cainam.tuchat.adapter.TabPagerMenuAdapter;
import com.cainam.tuchat.R;
import com.cainam.tuchat.activity.AccountDetailsActivity;
import com.cainam.tuchat.activity.SignInActivity;
import com.cainam.tuchat.activity.UserListActivity;
import com.cainam.tuchat.connectivity.CainamConnectivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private FirebaseAuth mAuthorization;
    private FirebaseUser mUser;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabPagerMenuAdapter mTabPagerMenuAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserDatabase;
    private Context mContext;

    @Override
    protected void onStart() {
        super.onStart();

        if (mUser != null){
            mUserDatabase.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mUser != null){
            mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mContext = MainActivity.this;
        CainamConnectivity connect = new CainamConnectivity();
        setContentView(R.layout.activity_main);

        if (!connect.isConnectivityUp(MainActivity.this)){

        } else{
            connect.buildAlertDialog(MainActivity.this).show();
        }

        mAuthorization = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TuChat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.main_activity_view_pager);
        mTabPagerMenuAdapter = new TabPagerMenuAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabPagerMenuAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_activity_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        mUser = mAuthorization.getCurrentUser();
        if (mUser != null){
            String UID = mUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        }else {
            Intent mainIntent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int itemID = item.getItemId();

        switch (itemID){
            case R.id.action_logout:
                mAuthorization.signOut();
                Intent mainIntent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(mainIntent);
                finish();
                break;
            case R.id.action_profile:
                Intent mainAccountDetailsIntent = new Intent(MainActivity.this, AccountDetailsActivity.class);
                startActivity(mainAccountDetailsIntent);
                break;
            case R.id.action_all_users:
                Intent mainUserListIntent = new Intent(MainActivity.this, UserListActivity.class);
                mainUserListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainUserListIntent);
                break;
            default:
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }
}
