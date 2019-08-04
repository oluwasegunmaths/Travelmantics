package com.ease.travelmantics

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ease.travelmantics.FirebaseUtil.RC_SIGN_IN
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_deal_list.*

class DealListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_list)

        title = "Deals"

        //        PackageInfo info;
        //        try {
        //            info = getPackageManager().getPackageInfo("com.ease.travelmantics", PackageManager.GET_SIGNATURES);
        //            for (Signature signature : info.signatures) {
        //                MessageDigest md;
        //                md = MessageDigest.getInstance("SHA");
        //                md.update(signature.toByteArray());
        //                String something = new String(Base64.encode(md.digest(), 0));
        //                //String something = new String(Base64.encodeBytes(md.digest()));
        //                Log.i("hash key", something);
        //            }
        //        } catch (PackageManager.NameNotFoundException e1) {
        //            Log.e("name not found", e1.toString());
        //        } catch (NoSuchAlgorithmException e) {
        //            Log.e("no such an algorithm", e.toString());
        //        } catch (Exception e) {
        //            Log.e("exception", e.toString());
        //        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.list_activity_menu, menu)
        val insertMenu = menu.findItem(R.id.insert_menu)
        insertMenu.isVisible=FirebaseUtil.isAdmin

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.insert_menu -> {
                val intent = Intent(this, EditOrAddNewDealActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.logout_menu -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener {
                            FirebaseUtil.attachListener()
                        }
                FirebaseUtil.detachListener()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }

    override fun onResume() {

        super.onResume()

        FirebaseUtil.openFbReference("traveldeals", this)

//        val rvDeals = findViewById<View>(R.id.deals_recyclerView) as RecyclerView
        val adapter = DealAdapter()
        deals_recyclerView.adapter = adapter
        val dealsLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        deals_recyclerView.layoutManager = dealsLayoutManager

        FirebaseUtil.attachListener()


    }

    fun showMenu() {
        invalidateOptionsMenu()
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == Activity.RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

}
