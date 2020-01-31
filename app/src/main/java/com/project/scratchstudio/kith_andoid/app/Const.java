package com.project.scratchstudio.kith_andoid.app;

public class Const {
    public static final String BASE_URL = "http://kith.ga/";
    public static final String APP_URL = "https://play.google.com/store/apps/details?id=com.project.scratchstudio.kith";

    public static String getErrorMessage(int code) {
        switch (code) {
            case 400: return "Неверный формат данных";
            case 401: return "Неверный логин или пароль";
            case 403: return "Неверный реферальный код";
            case 404: return "Пользователь не существует";
            case 405: return "Пользователь не подтвержден";
            case 406: return "Логин уже занят";
            default: return "Ошибка сервера"; //402
        }
    }
}
