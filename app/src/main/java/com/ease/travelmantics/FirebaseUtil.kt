package com.ease.travelmantics

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object FirebaseUtil {

     lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mDatabaseReference: DatabaseReference
    lateinit var mFirebaseAuth: FirebaseAuth
    lateinit var mStorage: FirebaseStorage
    lateinit var mStorageRef: StorageReference
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mDeals: ArrayList<Deal>
    const val RC_SIGN_IN = 123
    private var caller: DealListActivity? = null
    var isAdmin: Boolean = false
    private var firebaseUtil: FirebaseUtil? = null


    fun openFbReference(ref: String, callerActivity: DealListActivity) {

        if (firebaseUtil == null) {

            firebaseUtil = FirebaseUtil
            mFirebaseDatabase = FirebaseDatabase.getInstance()
            mFirebaseAuth = FirebaseAuth.getInstance()
            caller = callerActivity

            mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser == null) {
                    signIn()
                } else {
                    val userId = firebaseAuth.uid
                    checkAdmin(userId)
                }


            }
            connectStorage()

        }

        mDeals = ArrayList()
        mDatabaseReference = mFirebaseDatabase.reference.child(ref)

    }

    private fun signIn() {

        // Choose authentication providers

        val providers = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent

        caller!!.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN)

    }

    private fun checkAdmin(uid: String?) {
        isAdmin = false

        val ref = mFirebaseDatabase.reference.child("administrators")
                .child(uid!!)

        val listener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                isAdmin = true

                caller!!.showMenu()

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        ref.addChildEventListener(listener)

    }

    fun attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener)
    }

    fun detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener)
    }

    fun connectStorage() {
        mStorage = FirebaseStorage.getInstance()
        mStorageRef = mStorage.reference.child("deals_pictures")
    }
}
