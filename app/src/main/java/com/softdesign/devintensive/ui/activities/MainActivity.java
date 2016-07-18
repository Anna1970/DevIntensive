package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.util.ConstantManager;
import com.softdesign.devintensive.util.CropSquareTransformation;
import com.softdesign.devintensive.util.DevTextWatcher;
import com.softdesign.devintensive.util.Helper;
import com.softdesign.devintensive.util.RoundedAvatarDrawable;
import com.softdesign.devintensive.util.ValidationEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    @BindView (R.id.phone) EditText mUserPhone;
    @BindView (R.id.e_mail) EditText mUserMail;
    @BindView (R.id.about) EditText mUserBio;
    @BindView (R.id.git) EditText mUserGit;
    @BindView (R.id.vk) EditText mUserVk;

    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;

    @BindView(R.id.user_photo_img) ImageView mProfileImage;
    @BindView(R.id.call_img) ImageView mCallImg;
    @BindView(R.id.send_img) ImageView mSendImg;
    @BindView(R.id.vk_img) ImageView mVkImg;
    @BindView(R.id.git_img) ImageView mGitImg;

    @BindView(R.id.rating) TextView mUserValueRating;
    @BindView(R.id.strings_count) TextView mUserValueCodeLines;
    @BindView(R.id.projects_count) TextView mUserValueProjects;

    @BindViews({R.id.phone,R.id.e_mail,R.id.vk, R.id.git,R.id.about}) List<EditText> mUserInfoViews;
    @BindViews({R.id.rating,R.id.strings_count,R.id.projects_count}) List<TextView> mUserValuesViews;

    static final ButterKnife.Setter<View, Boolean> Enabled = new ButterKnife.Setter<View, Boolean>(){
        @Override public void set(View view, Boolean value, int index) {
            view.setEnabled(value);
            view.setFocusable(value);
            view.setFocusableInTouchMode(value);
        }
    };

    private DataManager mDataManager;
    private int mCurrentEditMode = 0;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;


    /**
     * Реализация "прослушивателя событий" с помощью Butterknife для некоторых ImageView
     * @param imageView
     */
    @OnClick({R.id.call_img,R.id.send_img,R.id.vk_img,R.id.git_img})
    public void imageClick(ImageView imageView) {
        //Если в режиме просмотра, то выполняем дейтвия
        if (mCurrentEditMode == 0) {
            switch (imageView.getId()) {
                case R.id.call_img:
                    dialToPhone(mUserPhone.getText().toString().trim());
                    break;
                case R.id.send_img:
                    emailSend(mUserMail.getText().toString().trim());
                    break;
                case R.id.vk_img:
                    showInBrowser(mUserVk.getText().toString().trim());
                    break;
                case R.id.git_img:
                    showInBrowser(mUserGit.getText().toString().trim());
                    break;
            }
        }
    }

    /**
     * вызывается при создании или перезапуске активности
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        mUserPhone.addTextChangedListener(new DevTextWatcher(mUserPhone));
        mUserMail.addTextChangedListener(new DevTextWatcher(mUserMail));
        mUserVk.addTextChangedListener(new DevTextWatcher(mUserVk));
        mUserGit.addTextChangedListener(new DevTextWatcher(mUserGit));

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        mCollapsingToolbar.setTitle(mDataManager.getPreferencesManager().loadUserName().get(0)+" " +
                                    mDataManager.getPreferencesManager().loadUserName().get(1));

        setupToolbar();
        setupDrawer();

        initUserFields();
        initUserInfoValue();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_bg)
                .transform(new CropSquareTransformation())
                .into(mProfileImage);

        if (savedInstanceState == null) {
            //активити запускается впервые

        } else {
            //активити уже создавалась
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *Вызывается непосредственно перед тем, как активность становится видимой пользователю
     * Чтение из базы данных
     * Запуск сложной анимации
     * Запуск потоков, отслеживания показаний датчиков, запросов к GPS, таймеров, сервисов или других процессов,
     * которые нужны исключительно для обновления пользовательского интерфейса
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart ");
    }

    /**
     * Методon Resume()вызывается послеonStart().Также может вызываться послеonPause().
     * запуск воспроизведения анимации, аудио и видео
     * инициализации компонентов
     * регистрации любых широковещательных приемников или других процессов, которые вы освободили/приостановили вonPause()
     * выполнение любых другие инициализации, которые должны происходить, когда активность вновь активна (камера).
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * МетодonPause()вызывается после сворачивания текущей активности или перехода к новому.
     * От onPause()можно перейти к вызову либо onResume(), либо onStop().
     *
     * остановка анимации, аудио и видео
     * фиксация несохраненных данных (легкие процессы)
     * освобождение системных ресурсов
     * остановка сервисов, подписок, широковещательных сообщений
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    /**
     * МетодonStop()вызывается, когда окно становится невидимым для пользователя.
     * Это может произойти при её уничтожении, или если была запущена другая активность (существующая или новая),
     * перекрывшая окно текущей активности.
     *
     * запись в базу данных
     * приостановка сложной анимации
     * приостановка потоков, отслеживания показаний датчиков, запросов к GPS, таймеров,
     * сервисов или других процессов, которые нужны исключительно для обновления пользовательского интерфейса
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /**
     * Метод вызывается по окончании работы активности, при вызове методаfinish()или в случае, когда система уничтожает этот экземпляр активности для освобождения ресурсов.
     *
     * высвобождение ресурсов
     * дополнительная перестраховка если ресурсы не были выгружены или процессы не были остановлены ранее
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /**
     * Если окно возвращается в приоритетный режим после вызоваonStop(), то в этом случае вызывается методonRestart().
     * Т.е. вызывается после того, как активность была остановлена и снова была запущена пользователем.
     * Всегда сопровождается вызовом метода onStart().
     *
     * используется для специальных действий, которые должны выполняться только при повторном запуске активности
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    /**
     * Обработка нажатий на элементы экрана
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;
            case R.id.profile_placeholder:
                //Делаем выбор откуда загружать фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
        }
    }

    /**
     * Сохранение текущего состояния
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        TextView userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name_txt);
        userName.setText(mDataManager.getPreferencesManager().loadUserName().get(0)+" "+mDataManager.getPreferencesManager().loadUserName().get(1));

        TextView userEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email_txt);
        userEmail.setText(mDataManager.getPreferencesManager().loadUserProfileData().get(1));

        ImageView userAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Helper.showSnackbar(mCoordinatorLayout,item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                switch (item.getItemId()){
                    case R.id.user_profile_menu:
                        break;
                    case R.id.team_menu:
                        Intent userListActivity = new Intent(MainActivity.this, UserListActivity.class);
                        startActivity(userListActivity);
                        break;
                }
                return false;
            }
        });

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserAvatar())
                .resize(this.getResources().getDimensionPixelSize(R.dimen.size_avatar_60),
                        this.getResources().getDimensionPixelSize(R.dimen.size_avatar_60))
                .placeholder(R.drawable.avatar)
                .transform(new RoundedAvatarDrawable())
                .into(userAvatar);
    }

    /**
     * Получение результата от другой активвити (камеры или галлереи)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }

    /**
     * переключает режим редактирования
     * @param mode если 1 режим редактирования, если 0 - режим просмотра
     */
    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            /*for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }*/
            ButterKnife.apply(mUserInfoViews,Enabled,true);
            /*Устанавливаем фокус на поле с номером телефона и показываем клавиатуру*/
            mUserPhone.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            showProfilePlaceholder();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

        } else {
            //Проверяем введенные данные на валидность
            if (checkValidationEditText()){
                mFab.setImageResource(R.drawable.ic_create_black_24dp);

                ButterKnife.apply(mUserInfoViews,Enabled,false);
                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

                saveUserFields();
            } else {
                showToast("Есть неправильно заполненные поля.\nПроверте правильность заполнения и введите валидные данные");
            }
        }
    }

    /**
     * Проверка правильности заполнения полей
     * @return
     */
    private boolean checkValidationEditText() {
        boolean ret = true;
        //Мобильный телефн
        if (!ValidationEditText.isPhoneNumber(mUserPhone,false)){
            mUserPhone.requestFocus();
            ret = false;
        }
        //EMail
        if (!ValidationEditText.isEmailAddress(mUserMail,false)){
            mUserMail.requestFocus();
            ret = false;
        }
        //Профиль VK
        if (!ValidationEditText.isVkProfile(mUserVk,false)){
            mUserVk.requestFocus();
            ret = false;
        }
        //Профиль Github
        if (!ValidationEditText.isGitProfile(mUserGit,false)) {
            mUserGit.requestFocus();
            ret = false;
        }
        return ret;
    }

    /**
     * Загружаем сохраненные данные пользователя
     */
    private void initUserFields() {

        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохраняем текущие данные пользователя
     */
    private void saveUserFields() {
        List<String> userData = new ArrayList<>();

        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * Данные пользователя - рейтинг, проеты, кол-во строк кода
     */
    private void initUserInfoValue(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i =0; i < userData.size(); i++){
            mUserValuesViews.get(i).setText(userData.get(i));
        }
    }

    /**
     *     обрабатывем нажатие кнопки Back
     */
    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Загружаем фото из галлереи
     */
    private void loadPhotoFromGallery() {

        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        try {
            startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_choose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Невозможно открыть фото из галлереи!");
        }

    }

    /**
     * Загружаем фото из камеры
     */
    private void loadPhotoFromCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                mPhotoFile = Helper.createImageFile(this);
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Не загружается фото с камеры!");
            }

            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                try {
                    startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
                }catch (Exception e) {
                    e.printStackTrace();
                    throw new Error("Невозможно получить изображение с камеры!");
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, "Для корректной работы приложения необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    /**
     * Получение разрешений на работу с камерой и сохранение изображений
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPhotoFromCamera();
            }
        }

        if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            loadPhotoFromGallery();
        }
    }

    /**
     * Показываем placeholder
     */
    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     * Скрываем placeholder
     */
    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * Блокировка Toolbar
     */
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Разблокировка Toolbar
     */
    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Диалог загрузки фото из камеры или из галлереи
      * @param id
     * @return
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItem = {getString(R.string.user_profile_dialog_gallery), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                //Загрузить из галлереи
                                loadPhotoFromGallery();;
                                break;
                            case 1:
                                //Загрузить из камеры
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                //Отменить
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }

    /**
     * Отправляем фото на сервер
     */
    private void uploadPhoto (File photoFile){
        Call<ResponseBody> call = mDataManager.uploadPhoto(mDataManager.getPreferencesManager().getUserId(),photoFile);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG," In uploadPhoto on response");
                if (response.isSuccessful()){
                    Helper.showSnackbar(mCoordinatorLayout,"Фотография загружена на сервер");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG," In uploadPhoto on failure: " + t.getMessage());
                Helper.showSnackbar(mCoordinatorLayout,"Не удалось загрузить фотографию:\n" + t.getMessage());
            }
        });
    }

    /**
     * Получает путь к файлу из uri
     * @param contentUri
     * @return
     */
    public String getPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            else {
                return null;
            }
        }
    }

    /**
     * Вставляем выбранное фото
     * @param selectedImage
     */
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .resize(768,512)
                .placeholder(R.drawable.user_bg)
                .transform(new CropSquareTransformation())
                .into(mProfileImage);

        //если фото изменилось
        if (!selectedImage.equals(mDataManager.getPreferencesManager().loadUserPhoto())){

            File file = null;

            if (selectedImage.getLastPathSegment().endsWith(".jpeg")){
                //фото с камеры
                file = new File(selectedImage.getPath());
            }
            else{
                //из галлереи
                file = new File(getPathFromURI(selectedImage));
            }

            if (file != null) {
                uploadPhoto(file);
                mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
            }
        }
    }

    /**
     * Звонок на телефон, указанный в профиле
     * @param phone
     */
    private void dialToPhone(String phone) {

        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        //Проверка, что существует приложение, способное обработать Intent
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            //Проверяем разрешение на звонок
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CALL_PHONE
                }, ConstantManager.PHONE_REQUEST_PERMISSION_CODE);

                Snackbar.make(mCoordinatorLayout, "Для корректной работы приложения необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                        .setAction("Разрешить", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openApplicationSettings();
                            }
                        }).show();
                return;
            }
        } else {
            showToast("Нет приложения для вызова телефона");
            return;
        }
        try {
            startActivity(dialIntent);
        } catch (Exception e){
            e.printStackTrace();
            throw new Error("Не удалось сделать вызов!");
        }
    }

    /**
     * Вызов настроек устройства
     */
    private void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("packege:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.REQUEST_SETTINGS_CODE);
    }

    /**
     * Отправка письма по e-mail, указанном в профиле
     * @param email
     */
    private void emailSend(String email) {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        //Кому
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { email});
        // Тема письма
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Тема письма");
        // Текст письма
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Текст письма");
        //проверяем, есть ли приложения, отправляющие почту
        if (emailIntent.resolveActivity(getPackageManager()) == null){
            showToast("Нет приложения для отправки e-mail");
            return;
        }
        try {
            startActivity(Intent.createChooser(emailIntent, "Отправка письма на e-mail"));
        } catch (Exception e){
            e.printStackTrace();
            throw new Error("Ошибка отправки сообщения!");
        }
    }

    /**
     * Открыть ссылку в браузере
     * @param address
     */
    private void showInBrowser(String address) {
        Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+address));
        try {
            startActivity(openLinkIntent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Невозможно открыть ссылку!");
        }
    }

}
