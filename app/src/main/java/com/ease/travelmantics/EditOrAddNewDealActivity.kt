package com.ease.travelmantics

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_or_add_new_deal.*

class EditOrAddNewDealActivity : AppCompatActivity() {
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
//    internal var txtTitle: EditText
//    internal var txtDescription: EditText
//    internal var txtPrice: EditText
//    internal var imageView: ImageView
    internal var deal: Deal? = null
    private var btnImage: Button? = null
    private var isNewTravelDeal: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_or_add_new_deal)

        //sets the animation
        val constraintLayout = findViewById<ConstraintLayout>(R.id.deal_constraint_layout)
        val animationDrawable = constraintLayout.background as AnimationDrawable

        animationDrawable.setEnterFadeDuration(2500)

        animationDrawable.setExitFadeDuration(4500)

        animationDrawable.start()



        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
//        txtTitle = findViewById<View>(R.id.txtTitle) as EditText
//        txtDescription = findViewById<View>(R.id.txtDescription) as EditText
//        txtPrice = findViewById<View>(R.id.txtPrice) as EditText
//        imageView = findViewById<View>(R.id.imageView) as ImageView
        val intent = intent
        var deal: Deal? = intent.getSerializableExtra("Deal") as Deal?
        if (deal == null) {
            title = "Add a new deal"
            isNewTravelDeal = true
            deal = Deal()
        } else {
            title = deal.title

        }

        this.deal = deal
        txtTitle.setText(deal.title)
        txtDescription.setText(deal.description)
        txtPrice.setText(deal.price)
        showImage(deal.imageUrl)
        btnImage = findViewById(R.id.get_local_image_button)
        btnImage!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent,
                    "Insert Picture"), PICTURE_RESULT)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                saveDeal()
                return true
            }
            R.id.delete_menu -> {
                deleteDeal()
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show()
                backToList()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.save_menu, menu)
        if (FirebaseUtil.isAdmin) {
            if (!isNewTravelDeal) {
                title = "Edit/delete " + deal!!.title!!
                menu.findItem(R.id.delete_menu).isVisible = true
            } else {
                //hides the delete menu if we are trying to create a new deal
                menu.findItem(R.id.delete_menu).isVisible = false

            }
            menu.findItem(R.id.save_menu).isVisible = true
            btnImage!!.visibility = View.VISIBLE
            enableEditTexts(true)
        } else {
            menu.findItem(R.id.delete_menu).isVisible = false
            menu.findItem(R.id.save_menu).isVisible = false
            btnImage!!.visibility = View.GONE
            enableEditTexts(false)
        }


        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK) {
            val imageUri = data!!.data
            val ref = FirebaseUtil.mStorageRef.child(imageUri!!.lastPathSegment!!)


            val uploadTask = ref.putFile(imageUri)

            uploadTask.continueWithTask { tas ->
                if (!tas.isSuccessful) {

                    throw tas.getException() as Throwable
                }

                // Continue with the task to get the download URL
                val pictureName = tas.result!!.storage.path
                deal!!.imageName = pictureName
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val url: String
                    if (task.result != null) {
                        url = task.result!!.toString()
                    } else {
                        url = ""
                    }

                    //                        String url = taskSnapshot.getDownloadUrl().toString();

                    deal!!.imageUrl = url
                    showImage(url)
                } else {
                    // Handle failures
                    // ...
                }
            }

        }
    }

    private fun saveDeal() {
        if (txtTitle.text != null && txtPrice.text != null && deal!!.imageUrl != null) {
            deal!!.title = txtTitle.text.toString()
            deal!!.description = txtDescription.text.toString()
            deal!!.price = txtPrice.text.toString()
            if (deal!!.id == null) {
                mDatabaseReference!!.push().setValue(deal)
            } else {
                mDatabaseReference!!.child(deal!!.id!!).setValue(deal)
            }
            Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show()
            clean()
            backToList()
        } else {
            Toast.makeText(this, "A deal must have a name, price and image", Toast.LENGTH_LONG).show()

        }
    }

    private fun deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show()
            return
        }
        mDatabaseReference!!.child(deal!!.id!!).removeValue()
        if (deal!!.imageName != null && deal!!.imageName!!.isEmpty() == false) {
            val picRef = FirebaseUtil.mStorage.reference.child(deal!!.imageName!!)
            picRef.delete().addOnSuccessListener { }.addOnFailureListener { }
        }

    }

    private fun backToList() {
        finish()
        //        Intent intent = new Intent(this, DealListActivity.class);
        //        startActivity(intent);
    }

    private fun clean() {
        txtTitle.setText("")
        txtPrice.setText("")
        txtDescription.setText("")
        txtTitle.requestFocus()
    }

    private fun enableEditTexts(isEnabled: Boolean) {
        txtTitle.isEnabled = isEnabled
        txtDescription.isEnabled = isEnabled
        txtPrice.isEnabled = isEnabled
    }

    private fun showImage(url: String?) {
        if (url != null && url.isEmpty() == false) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            Picasso.with(this)
                    .load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView)
        }
    }

    companion object {
        private val PICTURE_RESULT = 42 //the answer to everything
    }

}
