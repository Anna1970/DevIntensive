package com.softdesign.devintensive.util;

import okhttp3.HttpUrl;

public interface AppConfig {
    String BASE_URL = "http://devintensive.softdesign-apps.ru/api/";
    int START_DELAY = 1500;
    int MAX_CONNECT_TIMEOUT = 6000;
    int MAX_READ_TIMEOUT = 6000;
}
