package com.softdesign.devintensive.util;

import android.widget.EditText;
import java.util.regex.Pattern;

public class ValidationEditText {

    /*Проверяем введенный телефон*/
    public  static boolean isPhoneNumber(EditText editText, boolean required){
        return isValidEditText(editText,ConstantManager.PHONE_REGEX,ConstantManager.PHONE_MSG,required);
    }

    /*Проверяем введенный e-mail*/
    public static boolean isEmailAddress(EditText editText, boolean required){
        return isValidEditText(editText,ConstantManager.EMAIL_REGEX,ConstantManager.EMAIL_MSG,required);
    }

    /*Проверяем профиль vk*/
    public static boolean isVkProfile(EditText editText, boolean required){
        return isValidEditText(editText,ConstantManager.VK_REGEX,ConstantManager.VK_MSG,required);
    }

    /*Проверяем профиль github*/
    public static boolean isGitProfile(EditText editText, boolean required){
        return isValidEditText(editText,ConstantManager.GIT_REGEX,ConstantManager.GIT_MSG,required);
    }

    private static boolean isValidEditText(EditText editText, String regex, String msg, boolean required) {

        String text = editText.getText().toString().trim();
        //очищаем ранее установленные ошибки
        editText.setError(null);

        //Если поле обязательно к заполнению и нет текста
        if (required && !hasText(editText)) return false;

        //маска не соответствует введенному
        if (!Pattern.matches(regex, text)) {
            editText.setError(msg);
            return false;
        }

        return true;
    }

    /**
     * Проверяет, что введен не пустой текст
     * @param editText
     * @return
     */
    private static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        //Сбрасываем предыдущие ошибки
        editText.setError(null);

        if (text.length() == 0){
            editText.setError(ConstantManager.REQUIRED_MSG);
            return false;
        }

        return true;
    }

}
