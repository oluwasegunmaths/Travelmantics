package com.ease.travelmantics

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.util.*

class DealAdapter : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {
    internal var deals: ArrayList<Deal>
    private val mFirebaseDatabase: FirebaseDatabase
    private val mDatabaseReference: DatabaseReference
    private val mChildListener: ChildEventListener
    private var imageDeal: ImageView? = null

    init {
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        this.deals = FirebaseUtil.mDeals
        mChildListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val td = dataSnapshot.getValue(Deal::class.java)
                td!!.id = dataSnapshot.key
                deals.add(td)
                notifyItemInserted(deals.size - 1)
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
        mDatabaseReference.addChildEventListener(mChildListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context)
                .inflate(R.layout.deal_list_item, parent, false)
        return DealViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = deals[position]
        holder.bind(deal)
    }

    override fun getItemCount(): Int {
        return deals.size
    }

    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var tvTitle: TextView
        internal var tvDescription: TextView
        internal var tvPrice: TextView

        init {
            tvTitle = itemView.findViewById<View>(R.id.tvTitle) as TextView
            tvDescription = itemView.findViewById<View>(R.id.tvDescription) as TextView
            tvPrice = itemView.findViewById<View>(R.id.tvPrice) as TextView
            imageDeal = itemView.findViewById<View>(R.id.deal_imageView) as ImageView
            itemView.setOnClickListener(this)
        }

        fun bind(deal: Deal) {
            tvTitle.text = deal.title
            tvDescription.text = deal.description
            tvPrice.text = deal.price
            showImage(deal.imageUrl)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            val selectedDeal = deals[position]
            val intent = Intent(view.context, EditOrAddNewDealActivity::class.java)
            intent.putExtra("Deal", selectedDeal)
            view.context.startActivity(intent)
        }

        private fun showImage(url: String?) {
            if (url != null && url.isNotEmpty()) {

                Picasso.with(imageDeal!!.context)
                        .load(url)
                        //                        .resize(200, 200)
                        .fit()
                        .into(imageDeal)
            }
        }
    }
}
