package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User {

    @SerializedName("_id")
    @Expose

    private String id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("second_name")
    @Expose
    private String secondName;
    @SerializedName("__v")
    @Expose
    private int v;
    @SerializedName("repositories")
    @Expose
    private Repositories repositories;
    @SerializedName("contacts")
    @Expose
    private Contacts contacts;
    @SerializedName("profileValues")
    @Expose
    private ProfileValues profileValues;
    @SerializedName("publicInfo")
    @Expose
    private PublicInfo publicInfo;
    @SerializedName("specialization")
    @Expose
    private String specialization;
    @SerializedName("role")
    @Expose
    private String role;

    public String getUpdated() {
        return updated;
    }

    @SerializedName("updated")

    @Expose
    private String updated;

    public String getId() {
        return id;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public String getFirstName() {
        return firstName;
    }

    public PublicInfo getPublicInfo() {
        return publicInfo;
    }

    public Repositories getRepositories() {
        return repositories;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getFullName() {
        return firstName + " " + secondName;
    }

    public ProfileValues getProfileValues() {
        return profileValues;
    }

    public class Contacts {

        public String getVk() {
            return vk;
        }

        @SerializedName("vk")
        @Expose

        private String vk;

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getUpdated() {
            return updated;
        }

        @SerializedName("phone")

        @Expose
        private String phone;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("updated")
        @Expose
        private String updated;

    }

    public class ProfileValues {
        public int getRating() {
            return rait;
        }

        public int getProjects() {
            return projects;
        }

        public int getLinesCode() {
            return linesCode;
        }

        @SerializedName("homeTask")
        @Expose
        private int homeTask;
        @SerializedName("projects")
        @Expose
        private int projects;
        @SerializedName("linesCode")
        @Expose
        private int linesCode;
        @SerializedName("rait")
        @Expose
        private int rait;
        @SerializedName("updated")
        @Expose
        private String updated;

    }

    public class PublicInfo {

        public String getBio() {
            return bio;
        }

        public String getPhoto() {
            return photo;
        }

        public String getAvatar() {
            return avatar;
        }

        @SerializedName("bio")
        @Expose

        private String bio;
        @SerializedName("avatar")
        @Expose
        private String avatar;
        @SerializedName("photo")
        @Expose
        private String photo;
        @SerializedName("updated")
        @Expose
        private String updated;

    }

    public class Repo {
        public String getGit() {
            return git;
        }

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("git")
        @Expose
        private String git;
        @SerializedName("title")
        @Expose
        private String title;

    }

    public class Repositories {

        public List<Repo> getRepo() {
            return repo;
        }

        @SerializedName("repo")
        @Expose

        private List<Repo> repo = new ArrayList<Repo>();
        @SerializedName("updated")
        @Expose
        private String updated;

    }

}
