package com.example.oceanizeapplication

import MyUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.oceanizeapplication.model.DataModelResponse
import com.example.oceanizeapplication.view_model.DataViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var dataViewModel: DataViewModel
    private var recyclerView: RecyclerView? = null
    private var mAdapter: RequestAdapter? = null
    private lateinit var pDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        recyclerView = findViewById(R.id.rv_users) as RecyclerView
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        observeViewModel()
        getResponse()
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.setLayoutManager(mLayoutManager)
        recyclerView!!.setItemAnimator(DefaultItemAnimator())

    }
    fun observeViewModel() {
        dataViewModel.response_error .observe(this, androidx.lifecycle.Observer {
            it?.let {
                pDialog.dismiss()
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Warning")
                    .setContentText("Can not fetch")
                    .show()

            }
        })

        dataViewModel.listResponse .observe(this, androidx.lifecycle.Observer {

            it?.let {
                pDialog.dismiss()
                for (i in 0 until it.size) {
                    Log.e("ButtonList",it.get(i).name.toString())
                }
                Toast.makeText(getApplication(), "Fetch From Database ", Toast.LENGTH_SHORT).show()
                mAdapter = RequestAdapter(it)
                recyclerView!!.setAdapter(mAdapter)
                mAdapter?.notifyDataSetChanged();

            }
        })
    }
    private fun getResponse() {

        if(MyUtil.isOnline(this)){
            pDialog.show()
            this.let { it1 -> dataViewModel.responseList() }
        }else{
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning")
                .setContentText("Need Internet Connection")
                .show()
        }

    }
    inner class RequestAdapter(private val requestCustomersList: List<DataModelResponse>) :
        RecyclerView.Adapter<RequestAdapter.MyViewHolder>(), View.OnClickListener {

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
            val requestMoneyModel = requestCustomersList[position]
            holder.btnCommand.setText(requestMoneyModel.name)
        }

        override fun getItemCount(): Int {
            return requestCustomersList.size
        }
    }
}