package com.example.oceanizeapplication

import MyUtil
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.oceanizeapplication.adapter.RequestAdapter
import com.example.oceanizeapplication.model.DataModelResponse
import com.example.oceanizeapplication.view_model.DataViewModel
import com.jcraft.jsch.*
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity() ,RequestAdapter.adapterListener{
    private lateinit var dataViewModel: DataViewModel
    private var recyclerView: RecyclerView? = null
    private var mAdapter: RequestAdapter? = null
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var btnNext: Button
    private lateinit var txtOutPut: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        recyclerView = findViewById(R.id.rv_users) as RecyclerView
        txtOutPut = findViewById(R.id.txtOutPut) as TextView
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
                mAdapter = RequestAdapter(this,it,this)
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

    override fun onItemSelected(position: Int?, itemSelected: DataModelResponse?) {

        object : AsyncTask<Int?, Void?, Void?>() {

            override fun doInBackground(vararg params: Int?): Void? {
                try {
                    if (itemSelected != null) {
                        if(executeSSHcommand(itemSelected.command ,itemSelected.username,itemSelected.password,itemSelected.host,itemSelected.port).length>50){
                            txtOutPut.text="Text Too Long To Read"
                        }else{
                            txtOutPut.text= executeSSHcommand(itemSelected.command ,itemSelected.username,itemSelected.password,itemSelected.host,itemSelected.port)

                        }

                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                return null
            }
        }.execute(1)
    }
    fun executeSSHcommand(command: String?,username: String?, password: String?, host: String?, port: String?): String {


        try {
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
                Thread.sleep(500)
            } catch (ee: java.lang.Exception) {
            }

            Log.e("XXX-----", String(baos.toByteArray()))

            channel.disconnect()
            return String(baos.toByteArray())


        } catch (e: Exception) {
            return  "Error"
        } finally {

        }
    }
}