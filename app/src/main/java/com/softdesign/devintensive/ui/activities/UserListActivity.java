package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.User;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.ui.fragments.MyFragment;
import com.softdesign.devintensive.util.ConstantManager;
import com.softdesign.devintensive.util.Helper;
import com.softdesign.devintensive.util.NetworkStatusChecked;
import com.softdesign.devintensive.util.RoundedAvatarDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends BaseActivity  implements android.support.v7.widget.SearchView.OnQueryTextListener{

    private static final String TAG = ConstantManager.TAG_PREFIX + " UserListActivity";
    private static final String TAG_FRAGMENT = "networkRequestFragment";

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private RecyclerView mRecyclerView;

    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private MyFragment mMyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Log.d(TAG, "onCreate");

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mRecyclerView = (RecyclerView) findViewById(R.id.user_list);

        mDataManager = DataManager.getInstance();

        mMyFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (mMyFragment == null) {
            mMyFragment = new MyFragment();
            getSupportFragmentManager().beginTransaction().add(mMyFragment,TAG_FRAGMENT).commit();
            mMyFragment.setData(getUsers());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        setupToolBar();
        setupDrawer();
        setupUsersListAdapter(mMyFragment.getData());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mUsersAdapter.setFilter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mUsersAdapter.setFilter(newText);

        /*List<UserDTO> listUsers = new ArrayList<>();

        for (UserDTO itemUser : mMyFragment.getData()){
            if (itemUser.getFullName().toUpperCase().contains(newText.toUpperCase())){
                listUsers.add(itemUser);
            }
        }

        mUsersAdapter.setUsers(listUsers);
        mUsersAdapter.notifyDataSetChanged();
*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void loadUsers() {

        mUsersAdapter = new UsersAdapter(mMyFragment.getData(), new UsersAdapter.UserViewHolder.CustomClickListener(){
            @Override
            public void onUserItemClickListener(int position) {
                Helper.showSnackbar(mCoordinatorLayout,"Пользователь " + position);
            }
        });
        mRecyclerView.setAdapter(mUsersAdapter);

        *//*if (NetworkStatusChecked.isNetworkAvailable(this)) {
           // showProgress();
            Call<UserListRes> call = mDataManager.getUserList();
            call.enqueue(new Callback<UserListRes>() {
                @Override
                public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                    try {
                        if (response.code() == 200) {
                            setupUsersListAdapter(mMyFragment.getData());
                        } else {
                            Helper.showSnackbar(mCoordinatorLayout, "Ошибка: " + response.code());
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG, e.toString());
                        Helper.showSnackbar(mCoordinatorLayout, "Что-то пошло не так");
                    }
                }

                @Override
                public void onFailure(Call<UserListRes> call, Throwable t) {
                    Helper.showSnackbar(mCoordinatorLayout, "Ошибка: " + t.getMessage());
                }
            });
        } else {
            Helper.showSnackbar(mCoordinatorLayout,"В данный момент сеть недоступна.");
        }*//*
    }*/

    private void setupUsersListAdapter(final List<UserDTO> users){
        showProgress();
        mUsersAdapter = new UsersAdapter(users, new UsersAdapter.UserViewHolder.CustomClickListener() {
        @Override
        public void onUserItemClickListener(int position) {
                UserDTO userDTO = mMyFragment.getData().get(position);
                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                startActivity(profileIntent);
            }
        });
        mRecyclerView.setAdapter(mUsersAdapter);
        hideProgress();
    }

    private List<UserDTO> getUsers() {
        final List<UserDTO> list = new ArrayList<>();
        if (NetworkStatusChecked.isNetworkAvailable(this)) {
            showProgress();
            Call<UserListRes> call = mDataManager.getUserList();

            call.enqueue(new Callback<UserListRes>() {
                @Override
                public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                    try {
                        if (response.code() == 200) {
                            List<User> users = response.body().getData();

                            for (User user : users) {
                                list.add(new UserDTO(user));
                            };
                            setupUsersListAdapter(list);
                        }

                    } catch (NullPointerException e) {
                        Log.e(TAG, "Ошибка: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<UserListRes> call, Throwable t) {
                    Log.e(TAG, "Ошибка: " + t.getMessage());
                    Helper.showSnackbar(mCoordinatorLayout, "Что-то пошло не так");
                }
            });
            hideProgress();
        } else {
            Helper.showSnackbar(mCoordinatorLayout,"В данный момент сеть недоступна.");
        }
        return list;
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Helper.showSnackbar(mCoordinatorLayout,item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                switch (item.getItemId()){
                    case R.id.user_profile_menu:
                        Intent mainActivity = new Intent(UserListActivity.this, MainActivity.class);
                        startActivity(mainActivity);
                        break;
                    case R.id.team_menu:

                        break;
                }
                return false;
            }
        });

        ImageView userAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);

        if (mDataManager.getPreferencesManager().loadUserAvatar()!=null && !mDataManager.getPreferencesManager().loadUserAvatar().equals("")) {
            Picasso.with(this)
                    .load(mDataManager.getPreferencesManager().loadUserAvatar())
                    .resize(this.getResources().getDimensionPixelSize(R.dimen.size_avatar_60),
                            this.getResources().getDimensionPixelSize(R.dimen.size_avatar_60))
                    .placeholder(R.drawable.avatar)
                    .transform(new RoundedAvatarDrawable())
                    .into(userAvatar);
        }
    }

    private void setupToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        mMyFragment.setData(mMyFragment.getData());
        super.onDestroy();
    }
}
