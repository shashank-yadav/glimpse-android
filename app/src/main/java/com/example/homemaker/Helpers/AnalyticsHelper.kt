package com.example.homemaker.Helpers

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AnalyticsHelper(context: Context){

    val firebaseId = FirebaseAuth.getInstance().currentUser.toString()
    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    init {
        firebaseAnalytics.setUserProperty("firebaseId", firebaseId)
    }

    fun logSelectContent(contentType: String, itemId: String, itemName: String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun logEvent(name: String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT, name)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }


    fun logEvent(bundle: Bundle){
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun logEvent(event:String , bundle: Bundle){
        firebaseAnalytics.logEvent(event, bundle)
    }

}