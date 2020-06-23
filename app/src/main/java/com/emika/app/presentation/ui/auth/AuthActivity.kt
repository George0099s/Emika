package com.emika.app.presentation.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emika.app.R

class AuthActivity : AppCompatActivity() {
    val fm = supportFragmentManager
    private var authFragment: AuthFragment? = null
    private var createCompany: CreateCompany? = null
    private var logo: ImageView? = null
    private var continueRegistr: Boolean? = null
    private val logOut: TextView? = null
    private var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        val upArrow = resources.getDrawable(R.drawable.ic_arrow_back_white)
//        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
//        supportActionBar!!.setHomeAsUpIndicator(upArrow)

        supportActionBar!!.title = "Authorization"
        initViews()
    }

    private fun initViews() {
        continueRegistr = intent.getBooleanExtra("continue", false)
        token = intent.getStringExtra("token")
        if (continueRegistr as Boolean) {
            createCompany = CreateCompany()
            fm.beginTransaction().add(R.id.auth_container, createCompany!!).commit()
        } else {
            authFragment = AuthFragment()
            fm.beginTransaction().add(R.id.auth_container, authFragment!!).commit()
        }
        logo = findViewById(R.id.logo)
        Glide.with(this).asGif().load(R.drawable.emika_gif).apply(RequestOptions.circleCropTransform()).into(logo as ImageView)
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
}