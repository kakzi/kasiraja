package com.bmtnuinstitute.pointofsales.page.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.page.home.HomeActivity
import com.bmtnuinstitute.pointofsales.page.menu.MenuActivity
import com.bmtnuinstitute.pointofsales.page.report.ReportActivity
import com.bmtnuinstitute.pointofsales.preferences.PrefManager
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import com.bmtnuinstitute.pointofsales.retrofit.response.login.Login
import com.bmtnuinstitute.pointofsales.retrofit.response.login.LoginResponse
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val api by lazy { ApiService.auth }
    private val pref by lazy { PrefManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupView()
        setupListener()
    }

    private fun setupView(){
        supportActionBar!!.hide()
        loading(false)
    }

    private fun setupListener(){
        button_login.setOnClickListener {
            if (edit_username.text.isNullOrEmpty() || edit_password.text.isNullOrEmpty()) {
                message( "Isi data dengan benar" )
            } else {
                login( edit_username.text.toString(), edit_password.text.toString() )
            }
        }
    }

    private fun login(username: String, password: String){
        loading(true)
        api.login(username, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loading(false)
                }
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    loading(false)
                    if (response.isSuccessful) {
                        Log.d("Respnse:", response.body().toString())
                        response.body()?.let {
                            loginResponse( it )
                        }
                    }
                }
            })
    }

    private fun loginResponse(response: LoginResponse) {
        when (response.error) {
            true -> message( response.message )
            false -> {
                message( response.message )
                saveUser(response.data)
                when (response.data.level) {
                    "kasir" -> startActivity(Intent(this, MenuActivity::class.java))
                    "owner" -> startActivity(Intent(this, ReportActivity::class.java))
                }
                finish()
            }
        }
    }

    private fun saveUser(data: Login){
        pref.put("pref_user_login", true)
        pref.put("pref_user_name", data.nama)
        pref.put("pref_user_username", data.username)
        pref.put("pref_user_namatoko", data.nama_toko)
        pref.put("pref_user_address", data.address)
        pref.put("pref_user_phone", data.phone)
        data.apikey?.let { pref.put("pref_apikey", it) }
        pref.put("pref_user_level", data.level)
    }

    private fun loading(loading: Boolean) {
        when(loading) {
            true -> {
                progress.visibility = View.VISIBLE
                button_login.visibility = View.GONE
            }
            false -> {
                progress.visibility = View.GONE
                button_login.visibility = View.VISIBLE
            }
        }
    }

    private fun message(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
