package com.emika.app.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.networkManager.profile.UserNetworkManager
import com.emika.app.di.User
import com.emika.app.presentation.ui.chat.ChatActivity
import com.emika.app.presentation.ui.calendar.BoardFragment
import com.emika.app.presentation.ui.chat.ChatFragment
import com.emika.app.presentation.ui.profile.ProfileFragment
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics

import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @JvmField
    @Inject
    var user: User? = null
    private var navigationView: BottomNavigationView? = null
    private var fragmentManager: FragmentManager? = null
    private val profileFragment = ProfileFragment()
    private val boardFragment = BoardFragment()
    private val chatFragment = ChatFragment()
    private val app = EmikaApplication.instance
    private var networkManager: UserNetworkManager? = null
    private var socket: Socket? = null
    private var token: String? = null
    private var tokenJson: JSONObject? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var chat: ImageView
    var active: Fragment = boardFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        window.requestFeature(Window.FEATURE_ACTION_BAR);
//        setSupportActionBar(findViewById(R.id.main_toolbar))
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.green)))
        initView()
    }

    private fun initView() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        app.component?.inject(this)
        socket = app.socket
        token = intent.getStringExtra("token")
        chat = findViewById(R.id.chat)
        chat.setOnClickListener{
            socket!!.emit("server_read_messages", tokenJson)
            startActivity(Intent(this, ChatActivity::class.java))
//            fragmentManager!!.beginTransaction().add(R.id.main_container, chatFragment, "chatFragment").addToBackStack("boardFragment").commit()
        }
        tokenJson = JSONObject()
        try {
            tokenJson!!.put("token", token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        socket!!.emit("server_create_connection", tokenJson)
        networkManager = UserNetworkManager(token)
        fragmentManager = supportFragmentManager
        navigationView = findViewById(R.id.bottom_navigation)
        Glide.with(this).asGif().load(R.drawable.emika_gif).apply(RequestOptions.circleCropTransform()).into(findViewById(R.id.chat))
        navigationView!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        fragmentManager!!.beginTransaction().replace(R.id.main_container, boardFragment,"boardFragment").commit()
//        fragmentManager!!.beginTransaction().add(R.id.main_container, profileFragment, "profileFragment").commit()
//        fragmentManager!!.beginTransaction().add(R.id.main_container, chatFragment, "chatFragment").commit()
//        window.statusBarColor = ContextCompat.getColor(this, R.color.calendarBackground)
//        KeyboardVisibilityEvent.setEventListener(
//                this@MainActivity
//        ) { isOpen: Boolean ->
//            if (isOpen)
//                navigationView!!.visibility = View.GONE
//            else
//                navigationView!!.visibility = View.VISIBLE
//        }
    }

    override fun onResume() {
        super.onResume()
        socket?.emit("server_create_connection", tokenJson)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    private val mOnNavigationItemSelectedListener =  BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
        when (item.itemId) {
            R.id.menu_calendar -> {
                if (active !== boardFragment) {
                    fragmentManager!!.beginTransaction().hide(active).show(boardFragment).commit()
                    active = boardFragment
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.calendarBackground)
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.menu_profile -> {
                if (active !== profileFragment) {
                    fragmentManager!!.beginTransaction().hide(active).show(profileFragment).commit()
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.profileBackground)
//                    setSupportActionBar(findViewById(R.id.profile_toolbar))
                    supportActionBar!!.setDisplayShowTitleEnabled(false)
                    active = profileFragment
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.menu_chat -> {
                if (active !== chatFragment) {
                    fragmentManager!!.beginTransaction().hide(active).show(chatFragment).commit()
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.profileBackground)
                    socket!!.emit("server_read_messages", tokenJson)
                    active = chatFragment
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

//    override fun onBackPressed() {
//        val startMain = Intent(Intent.ACTION_MAIN)
//        startMain.addCategory(Intent.CATEGORY_HOME)
//        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(startMain)
//    }

    companion object {
        private const val TAG = "MainActivity"
    }


}