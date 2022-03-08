package com.ws.smarthouse.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ws.smarthouse.R


class DevicesAdapter(
    private val devices: Array<String>,
    private val onClickListener: RecyclerViewClickListener
) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view
            .findViewById<View>(R.id.textView2) as TextView

        val imageView: ImageView = view
            .findViewById<View>(R.id.imageView2) as ImageView

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_device, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = devices.get(position)
        val resources = viewHolder.itemView.context.resources
        val resourceId = resources.getIdentifier(
            "device$position", "drawable",
            viewHolder.itemView.context.packageName
        )
        viewHolder.imageView.setImageResource(resourceId)
        viewHolder.itemView.setOnClickListener { view ->
            onClickListener.recyclerViewListClicked(
                view,
                viewHolder.layoutPosition
            )
        }
    }

    override fun getItemCount(): Int {
        return devices.size + 1
    }
}