package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private Context mContext;
    private List<UserDTO> mUsers, mUserBackup;
    private UserViewHolder.CustomClickListener mCustomClickListener;

    public UsersAdapter(List<UserDTO> users, UserViewHolder.CustomClickListener customClickListener){
        mUsers = users;
        mUserBackup = new ArrayList<>();
        mUserBackup.addAll(mUsers);
        this.mCustomClickListener = customClickListener;
    }

    public void setUsers(List<UserDTO> users){
        mUsers = users;
    }

    public List<UserDTO> getUsers(){
        return mUsers;
    }

    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_list, parent,false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.UserViewHolder holder, int position) {
       UserDTO user = mUsers.get(position);

        String photo = user.getPhoto();

        if (photo != null && !photo.equals("")) {

            Picasso.with(mContext)
                    .load(photo)
                    .resize(mContext.getResources().getDimensionPixelSize(R.dimen.placeholder_photo_size_90),
                            mContext.getResources().getDimensionPixelSize(R.dimen.placeholder_photo_size_90))
                    .placeholder(mContext.getResources().getDrawable(R.drawable.user_bg))
                    .error(mContext.getResources().getDrawable(R.drawable.user_bg))
                    .into(holder.userPhoto);

            holder.mFullName.setText(user.getFullName());
            holder.mRating.setText(String.valueOf(user.getRating()));
            holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
            holder.mProjects.setText(String.valueOf(user.getProjects()));
        }
        if (user.getBio() == null || user.getBio().isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        }
        else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(user.getBio());
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected AspectRatioImageView userPhoto;
        protected TextView mFullName, mRating, mCodeLines, mProjects, mBio;
        protected Button mShowMore;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            this.mListener = customClickListener;

            userPhoto =(AspectRatioImageView) itemView.findViewById(R.id.user_photo_image);
            mFullName =(TextView) itemView.findViewById(R.id.user_full_name_txt);
            mRating =(TextView) itemView.findViewById(R.id.rating_txt);
            mCodeLines =(TextView) itemView.findViewById(R.id.code_lines_txt);
            mProjects =(TextView) itemView.findViewById(R.id.projects_txt);
            mBio =(TextView) itemView.findViewById(R.id.bio_txt);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);

            mShowMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            if (mListener != null){
                mListener.onUserItemClickListener(getAdapterPosition());
            }
        }

        public interface CustomClickListener{
            void onUserItemClickListener(int position);
        }

    }

    public void setFilter(String text){
        if (text.isEmpty()){

            mUsers.clear();
            mUsers.addAll(mUserBackup);

        } else {
            List<UserDTO> users = new ArrayList<>();
            for (UserDTO user : mUsers){
                if (user.getFullName().toLowerCase().contains(text.toLowerCase())){
                    users.add(user);
                }
            }
            mUsers.clear();
            mUsers.addAll(users);
        }

        notifyDataSetChanged();
    }
}
