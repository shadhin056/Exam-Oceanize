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
    var filterList: ArrayList<DataModelResponse> = ArrayList<DataModelResponse>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Dialog for Better wait
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)


        recyclerView = findViewById(R.id.rv_users) as RecyclerView

        //Output from command will show here
        txtOutPut = findViewById(R.id.txtOutPut) as TextView

        //button is hidden
        btnNext=findViewById(R.id.btnNext)

        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        //Observe Live Data
        observeViewModel()

        //get List Of Data from API
        getResponse()

        //FOR Recyclerview
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.setLayoutManager(mLayoutManager)
        recyclerView!!.setItemAnimator(DefaultItemAnimator())
    }


    fun observeViewModel() {
        //ANY TYPE OF Error From Network call
        dataViewModel.response_error .observe(this, androidx.lifecycle.Observer {
            it?.let {
                pDialog.dismiss()
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Warning")
                    .setContentText("Can not fetch")
                    .show()

            }
        })

        //list of response from API
        dataViewModel.listResponse .observe(this, androidx.lifecycle.Observer {

            it?.let {
                pDialog.dismiss()
                //json status 1 will only show ,o will hide
                for (i in 0 until it.size) {
                    if(it.get(i).status==1)
                    filterList.add(DataModelResponse(it.get(i).id,
                            it.get(i).name,
                            it.get(i).host,
                            it.get(i).port,
                            it.get(i).username,
                            it.get(i).password,
                            it.get(i).command,
                            it.get(i).createdAt,
                            it.get(i).updatedAt,
                            it.get(i).status
                    ) )
                }
                Toast.makeText(getApplication(), "Fetch From API ", Toast.LENGTH_SHORT).show()
                mAdapter = RequestAdapter(this,filterList,this)
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
        if (itemSelected != null) {
            txtOutPut.text="Your Command : "+itemSelected.command
        }
        object : AsyncTask<Int?, Void?, Void?>() {

            override fun doInBackground(vararg params: Int?): Void? {
                try {
                    if (itemSelected != null) {
                        if(executeSSHcommand(itemSelected.command ,itemSelected.username,itemSelected.password,itemSelected.host,itemSelected.port).length>150){
                            txtOutPut.text="Text Too Long To Read"
                        }else{
                            txtOutPut.text=executeSSHcommand(itemSelected.command ,itemSelected.username,itemSelected.password,itemSelected.host,itemSelected.port)
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

            //For SSH OUTPUT
            val baos = ByteArrayOutputStream()
            channel.outputStream = baos
            channel.setCommand(command)
            channel.connect()

            try {
                Thread.sleep(500)
            } catch (ee: java.lang.Exception) {
            }
            channel.disconnect()
            return String(baos.toByteArray())


        } catch (e: Exception) {
            return  "Error"
        } finally {
        }
    }
}