package com.softdesign.devintensive.ui.activities;

import android.content.Intent;

import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.data.storage.models.UserDb;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.ui.fragments.MyFragment;
import com.softdesign.devintensive.util.ConstantManager;
import com.softdesign.devintensive.util.DbOperation;
import com.softdesign.devintensive.util.Helper;
import com.softdesign.devintensive.util.NetworkStatusChecked;
import com.softdesign.devintensive.util.RoundedAvatarDrawable;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListActivity extends BaseActivity{

    private static final String TAG = ConstantManager.TAG_PREFIX + " UserListActivity";
    private static final String TAG_FRAGMENT = "MyFragment";

    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.user_list) RecyclerView mRecyclerView;


    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private MyFragment mMyFragment;
    private  MenuItem mSearchItem;
    private List<UserDb> mUsers;
    private String mQuery;

    private Handler mHandler;

    private ChronosConnector mConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        ButterKnife.bind(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);

        mDataManager = DataManager.getInstance();

        mConnector = new ChronosConnector();
        mConnector.onCreate(this,savedInstanceState);

        mMyFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (mMyFragment == null) {
            mMyFragment = new MyFragment();
            getSupportFragmentManager().beginTransaction().add(mMyFragment, TAG_FRAGMENT).commit();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mHandler = new Handler();
        setupToolBar();
        setupDrawer();

        if (savedInstanceState == null){
            loadUsersFromDb();
        } else {
            mUsers = mMyFragment.getData();
            showUsers(mUsers);
        }

    }

    //Удаление карточки пользователя из списка свайпом
    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();

            UserDb prev = mUsers.remove(fromPosition);
            mUsers.add(toPosition,prev);
            mUsersAdapter.notifyItemMoved(fromPosition, toPosition);

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mUsers.remove(position);
            mUsersAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mConnector.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnector.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mConnector.onSaveInstanceState(outState);
        mMyFragment.setData(mUsers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUsersFromDb() {

        if (NetworkStatusChecked.isNetworkAvailable(this)) {

            showProgress();

            try {
                mConnector.runOperation(new DbOperation(), false);
            } catch (Exception e){
                e.printStackTrace();;
            }
        } else {
            Helper.showSnackbar(mCoordinatorLayout,"В данный момент сеть недоступна.");
        }
    }

    public void onOperationFinished(final DbOperation.Result result) {

        List<UserDb> users;

        if (result.isSuccessful()){
            users = result.getOutput();
            if (users.size() == 0) {
                Helper.showSnackbar(mCoordinatorLayout, "Список пользователей не может быть пустым");
            } else {
                showUsers(users);
            }
        }
        else {
            Log.d(TAG,"Не получен результет от Chronos");
            Helper.showSnackbar(mCoordinatorLayout,"Не удалось получить список пользователей из потока");
        }

        hideProgress();
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
               // Helper.showSnackbar(mCoordinatorLayout, item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                switch (item.getItemId()) {
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

        if (mDataManager.getPreferencesManager().loadUserAvatar() != null && !mDataManager.getPreferencesManager().loadUserAvatar().equals("")) {
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

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        mSearchItem = menu.findItem(R.id.search_action);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setQueryHint("Введите имя пользователя");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showUsersByQuery(newText);
                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void showUsers(List<UserDb> users) {

        mUsers = users;
        mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int position) {

                UserDTO userDTO = new UserDTO(mUsers.get(position));

                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                startActivity(profileIntent);

            }
        });
        mRecyclerView.swapAdapter(mUsersAdapter, false);
    }

    private void showUsersByQuery(String query) {
        mQuery = query;
        if (mQuery.isEmpty()){
            mConnector.runOperation(new DbOperation(mQuery),false);
            //showUsers(mDataManager.getUserListByName(mQuery));
        } else {
            Runnable searchUsers = new Runnable() {
                @Override
                public void run() {
                    mConnector.runOperation(new DbOperation(mQuery),false);
//                    showUsers(mDataManager.getUserListByName(mQuery));
                }
            };
            mHandler.removeCallbacks(searchUsers);
            mHandler.postDelayed(searchUsers, ConstantManager.SEARCH_DELAY);
        }
    }
}
