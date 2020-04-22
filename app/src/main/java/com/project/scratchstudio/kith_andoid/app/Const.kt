package com.project.scratchstudio.kith_andoid.app

object Const {
    var isEntry = false
    const val BASE_URL = "http://znakomye.com/"
    const val APP_URL = "https://play.google.com/store/apps/details?id=com.project.scratchstudio.kith"

    fun getErrorMessage(code: Int): String {
        return when (code) {
            400 -> "Неверный формат данных"
            401 -> "Неверный логин или пароль"
            403 -> "Неверный реферальный код"
            404 -> "Пользователь не существует"
            405 -> "Пользователь не подтвержден"
            406 -> "Логин уже занят"
            else -> "Ошибка сервера" //402
        }
    }
}
