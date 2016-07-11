package com.softdesign.devintensive.util;

public interface ConstantManager {

    String TAG_PREFIX = "DEV ";
    String COLOR_MODE_KEY = "COLOR_MODE_KEY";

    String EDIT_MODE_KEY = "EDIT_MODE_KEY";

    String USER_PHONE_KEY = "USER_1_KEY";
    String USER_MAIL_KEY = "USER_2_KEY";
    String USER_VK_KEY = "USER_3_KEY";
    String USER_GIT_KEY = "USER_4_KEY";
    String USER_BIO_KEY = "USER_5_KEY";

    int LOAD_PROFILE_PHOTO = 1;

    int REQUEST_CAMERA_PICTURE = 99;
    int REQUEST_GALLERY_PICTURE = 88;
    String USER_PHOTO_KEY = "USER_PHOTO_KEY";

    int REQUEST_SETTINGS_CODE = 101;
    int CAMERA_REQUEST_PERMISSION_CODE = 102;

    int PHONE_REQUEST_PERMISSION_CODE = 201;


    /*Для валидации ввода*/

    //Регулярные выражения
    String EMAIL_REGEX ="^[_A-Za-z0-9-]{3,}@[a-zA-Z0-9]{2,}.[a-zA-Z0-9]{2,}$";
    String PHONE_REGEX ="\\+[0-9]{1} [0-9]{3} [0-9]{3}-[0-9]{2}-[0-9]{2,11}";
    String VK_REGEX = "^vk.com/[_A-Za-z0-9-]{3,}$";
    String GIT_REGEX = "^github.com/[_A-Za-z0-9-]{3,}$";

    //Сообщения об ошибках
    String REQUIRED_MSG = "Поле обязательно к заполнению";
    String EMAIL_MSG = "Введите e-mail согласно маске xxx@xx.xx";
    String PHONE_MSG = "Номер телефона согласно маске +x xxx xx-xx (не менее 11 цифр, но не более 20)";
    String VK_MSG = "Профиль Vk начинается с vk.com/ и далее не менее 3-х символов";
    String GIT_MSG = "Профиль Github начинается с github.com/ и далее не менее 3-х символов";

}
