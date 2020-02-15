package com.example.homemaker

/**
 * Class responsible to register all the callbacks necessary
 * for the application
 */
interface ActivityCallback {
    fun openChat()
    fun openUsername()
    fun openCreateAccount()
    fun logout()
    fun setPhoneNumber(phoneNumber:String)
    fun getPhoneNumber(): String
    fun openPhoneVerification()
    fun openPhoneLogin()

    fun setArFragmentUri(uri: String)
    fun getArFragmentUri():String



}