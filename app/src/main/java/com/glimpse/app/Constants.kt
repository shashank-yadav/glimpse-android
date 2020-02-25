package com.glimpse.app

/**
 * Interface responsible to hold all the constants
 * of the application
 */
interface Constants {
    companion object {
        const val SHARED_PREFERENCES = "APP_PREFS"
        const val PREFERENCES_USER_NAME = "username"
        const val PREFERENCES_USER_EMAIL = "email"
        const val PREFERENCES_USER_ID = "id"
        const val DATABASE_NAME = "chat"
        const val LOG_TAG = "FirebaseChat"
        const val DEFAULT_USER = "User"
        const val DEFAULT_ID = "0000"
    }
}