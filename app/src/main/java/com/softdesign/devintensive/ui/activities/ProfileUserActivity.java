package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.util.ConstantManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileUserActivity extends BaseActivity {

    private static final String TAG = ConstantManager.TAG_PREFIX + "ProfileUser";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.user_photo_img) ImageView mProfileImage;
    @BindView(R.id.bio_et) EditText mUserBio;
    @BindView(R.id.rating) TextView mUserRating;
    @BindView(R.id.strings_count) TextView mUserCodeLines;
    @BindView(R.id.projects_count) TextView mUserProjects;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.repositories_list) ListView mRepoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        ButterKnife.bind(this);

        setupToolbar();
        initProfileData();

    }

    private void setupToolbar(){
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void initProfileData() {

        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final List<String> repositories = userDTO.getRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repositories);

        mRepoListView.setAdapter(repositoriesAdapter);

        mRepoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+repositories.get(position)));
                try {
                    startActivity(openLinkIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Error("Невозможно открыть ссылку!");
                }
            }
        });

        mUserBio.setText(userDTO.getBio());
        mUserRating.setText(userDTO.getRating());
        mUserCodeLines.setText(userDTO.getCodeLines());
        mUserProjects.setText(userDTO.getProjects());

        mCollapsingToolbarLayout.setTitle(userDTO.getFullName());

        Picasso.with(this)
                .load(userDTO.getPhoto())
                /*.resize(this.getResources().getDimensionPixelSize(R.dimen.placeholder_photo_size_90),
                        this.getResources().getDimensionPixelSize(R.dimen.placeholder_photo_size_90))*/
                .fit()
                .error(R.drawable.user_bg)
                .centerCrop()
                .placeholder(R.drawable.user_bg)
                .into(mProfileImage);
    }
}
