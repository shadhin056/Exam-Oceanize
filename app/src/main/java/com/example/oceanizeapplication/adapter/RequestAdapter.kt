package com.example.oceanizeapplication.adapter

import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oceanizeapplication.R
import com.example.oceanizeapplication.model.DataModelResponse
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import java.io.ByteArrayOutputStream
import java.util.*

class RequestAdapter(private val requestCustomersList: List<DataModelResponse>,private val listener: adapterListener) : RecyclerView.Adapter<RequestAdapter.MyViewHolder>(), View.OnClickListener {

    override fun onClick(v: View) {

    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var btnCommand: Button

        init {
            btnCommand = view.findViewById(R.id.btnCommand) as Button

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val requestMyModel = requestCustomersList[position]

        if(requestMyModel.status == 1){
            holder.btnCommand.setText(requestMyModel.name)
        }
        holder.btnCommand.setOnClickListener {
            listener.onItemSelected(position,requestCustomersList[position])
        }
    }

    override fun getItemCount(): Int {
        return requestCustomersList.size
    }



    interface adapterListener {
        fun onItemSelected(position: Int?,itemSelected: DataModelResponse?)
    }

}