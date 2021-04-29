package com.example.oceanizeapplication

import MyUtil
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.oceanizeapplication.model.DataModelResponse
import com.example.oceanizeapplication.view_model.DataViewModel
import com.jcraft.jsch.*
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var dataViewModel: DataViewModel
    private var recyclerView: RecyclerView? = null
    private var mAdapter: RequestAdapter? = null
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var btnNext: Button

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
        btnNext=findViewById(R.id.btnNext)



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
                    // Log.e("ButtonList", it.get(i).name.toString())
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
            var txtOutPut: TextView

            init {
                btnCommand = view.findViewById(R.id.btnCommand) as Button
                txtOutPut = view.findViewById(R.id.txtOutPut) as TextView

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
            holder.btnCommand.setOnClickListener {

                object : AsyncTask<Int?, Void?, Void?>() {

                    override fun doInBackground(vararg params: Int?): Void? {
                        try {
                            holder.txtOutPut.text=   executeSSHcommand(requestMoneyModel.command ,requestMoneyModel.username,requestMoneyModel.password,requestMoneyModel.host,requestMoneyModel.port)

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        return null
                    }
                }.execute(1)

            }
        }

        override fun getItemCount(): Int {
            return requestCustomersList.size
        }
        fun executeSSHcommand(command: String?,username: String?, password: String?, host: String?, port: String?): String {

            val jsch = JSch()
            val session = port?.let { jsch.getSession(username, host, it.toInt()) }
            if (session != null) {
                session.setPassword(password)
            }

            val prop = Properties()
            prop["StrictHostKeyChecking"] = "no"
            if (session != null) {
                session.setConfig(prop)
            }

            if (session != null) {
                session.connect()
            }

            val channel = session?.openChannel("exec") as ChannelExec
            val baos = ByteArrayOutputStream()
            channel.outputStream = baos
            channel.setCommand(command)
            channel.connect()
            try {
                Thread.sleep(1000)
            } catch (ee: java.lang.Exception) {
            }
            Log.e("XXX-----", String(baos.toByteArray()))

            channel.disconnect()
            return String(baos.toByteArray())
        }
    }
}