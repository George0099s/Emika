package com.emika.app.presentation.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.dbmanager.UserDbManager
import com.emika.app.data.network.callback.TokenCallback
import com.emika.app.data.network.networkManager.auth.AuthNetworkManager
import com.emika.app.data.network.pojo.companyInfo.PayloadCompanyInfo
import com.emika.app.data.network.pojo.member.PayloadShortMember
import com.emika.app.data.network.pojo.user.Payload
import com.emika.app.di.CompanyDi
import com.emika.app.di.User
import com.emika.app.presentation.adapter.profile.AllMembersAdapter
import com.emika.app.presentation.adapter.profile.ProfileContactAdapter
import com.emika.app.presentation.ui.auth.AuthActivity
import com.emika.app.presentation.utils.NetworkState
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.profile_see_all_members
import java.util.ArrayList
import javax.inject.Inject

class ProfileActivity : AppCompatActivity(), TokenCallback {
    @Inject
    lateinit var user: User
    @Inject
    lateinit var companyDi: CompanyDi
    private var userDbManager: UserDbManager? = null
    private var networkManager: AuthNetworkManager? = null
    private var sharedPreferences: SharedPreferences? = null
    private var userName: TextView? = null
    private var jobTitle: TextView? = null
    private val editProfile: TextView? = null
    private val logOut: TextView? = null
    private var listAllMembers: TextView? = null
    private var companyName: TextView? = null
    private var companyMemberCount: TextView? = null
    private var manageInvites: TextView? = null
    private var token: String? = null
    private var userImg: ImageView? = null
    private var companyImg: ImageView? = null
    private var contactsRecycler: RecyclerView? = null
    private var leadRecycler: RecyclerView? = null
    private var coWorkersRecycler: RecyclerView? = null
    private var contactAdapter: ProfileContactAdapter? = null
    private var leadAdapter: AllMembersAdapter? = null
    private var coWorkersAdapter: AllMembersAdapter? = null
    private var viewModel: ProfileViewModel? = null
    private var memberList: List<PayloadShortMember> = ArrayList()
    private val app = EmikaApplication.instance
    private val getMembers = Observer { members: List<PayloadShortMember> ->
        memberList = members
        companyMemberCount!!.text = String.format("%s %s", memberList.size, resources.getString(R.string.members_string))
        setCoworkers()
        setLeaders()
    }
    private val getUserInfo = Observer { user: Payload ->
        this.user.id = user.id
        this.user.firstName = user.firstName
        this.user.lastName = user.lastName
        this.user.bio = user.bio
        this.user.pictureUrl = user.pictureUrl
        this.user.contacts = user.contacts
        this.user.extraCoworkers = user.coworkers
        this.user.extraLeaders = user.leaders
        this.user.admin = user.isAdmin
        this.user.leader = user.isLeader
        contactAdapter = ProfileContactAdapter(user.contacts, this)
        userName!!.text = String.format("%s %s", user.firstName, user.lastName)
        jobTitle!!.text = user.jobTitle
        contactsRecycler!!.adapter = contactAdapter
        if (user.pictureUrl != null) Glide.with(this).load(user.pictureUrl).apply(RequestOptions.circleCropTransform()).into(userImg!!) else Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg!!)
        viewModel!!.memberMutableLiveData.observe(this, getMembers)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enterTransition = MaterialFadeThrough.create()
        setContentView(R.layout.fragment_profile)
        val toolbar = findViewById<Toolbar>(R.id.profile_toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        toolbar.overflowIcon!!.setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val upArrow = resources.getDrawable(R.drawable.ic_arrow_back_white)
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        supportActionBar!!.title = "Profile"
        toolbar.setNavigationOnClickListener{ onBackPressed() }
        initView()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu!!.add(0, 1, 1, menuIconWithText(resources.getDrawable(R.drawable.ic_rename_task), resources.getString(R.string.edit_account), false))
        menu.add(0, 2, 2, menuIconWithText(resources.getDrawable(R.drawable.ic_log_ou), resources.getString(R.string.log_out), true))
        return super.onCreateOptionsMenu(menu)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menu!!.add(0, 1, 1, menuIconWithText(resources.getDrawable(R.drawable.ic_rename_task), resources.getString(R.string.edit_account), false))
//        menu.add(0, 2, 2, menuIconWithText(resources.getDrawable(R.drawable.ic_log_ou), resources.getString(R.string.log_out), true))
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("viewModel", viewModel)
                startActivity(intent)
            }
            2 -> logOut()
        }
        return true
    }

    private fun menuIconWithText(r: Drawable, title: String, red: Boolean): CharSequence {
        r.setBounds(0, 0, r.intrinsicWidth, r.intrinsicHeight)
        val sb = SpannableString("    $title")
        if (red) sb.setSpan(ForegroundColorSpan(resources.getColor(R.color.red)), 0, sb.length, 0)
        val imageSpan = ImageSpan(r, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    private fun initView() {
        app.component?.inject(this)
        sharedPreferences = EmikaApplication.instance.sharedPreferences
        token = EmikaApplication.instance.sharedPreferences?.getString("token", "")
//        viewdarkThemeMode.isChecked = EmikaApplication.instance.sharedPreferences!!.getBoolean("darkMode", true)
        userDbManager = UserDbManager()
        networkManager = AuthNetworkManager(token)
        userName = findViewById(R.id.profile_user_name)
        jobTitle = findViewById(R.id.profile_user_job_title)
        userImg = findViewById(R.id.profile_user_img)
        viewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(ProfileViewModel::class.java)
        viewModel!!.setContext(this)
        viewModel!!.downloadUserData()
        viewModel!!.getDbUserData()
        viewModel!!.userMutableLiveData.observe(this, getUserInfo)
        //        viewModel.getUser().observe(getViewLifecycleOwner(), getUserUpdated);
        viewModel!!.companyInfoMutableLiveData.observe(this, getCompanyInfo)
        contactsRecycler = findViewById(R.id.profile_contacts_recycler)
        contactsRecycler!!.layoutManager = LinearLayoutManager(this)
        contactsRecycler!!.setHasFixedSize(true)
        profile_see_all_members.setOnClickListener { view: View -> seeAllMembers(view) }
        companyName = findViewById(R.id.profile_company_name)
        companyMemberCount = findViewById(R.id.profile_company_members)
        companyImg = findViewById(R.id.profile_company_img)
        leadRecycler = findViewById(R.id.profile_leader_recycler)
        coWorkersRecycler = findViewById(R.id.profile_coworkers_recycler)
        leadRecycler!!.setHasFixedSize(true)
        leadRecycler!!.layoutManager = LinearLayoutManager(this)
        coWorkersRecycler!!.setHasFixedSize(true)
        coWorkersRecycler!!.layoutManager = LinearLayoutManager(this)
//        leadRecycler!!.setOnClickListener { view: View -> goToManageInvites(view) }

        darkThemeMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                EmikaApplication.instance.sharedPreferences!!.edit().putBoolean("darkMode", true).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                EmikaApplication.instance.sharedPreferences!!.edit().putBoolean("darkMode", false).apply()
            }
        }
    }

    private val getUserUpdated = Observer { user: User ->
        userName!!.text = String.format("%s %s", user.firstName, user.lastName)
        jobTitle!!.text = user.jobTitle
        contactAdapter = ProfileContactAdapter(user.contacts, this)
        contactsRecycler!!.adapter = contactAdapter
        if (user.pictureUrl != null) Glide.with(this).load(user.pictureUrl).apply(RequestOptions.circleCropTransform()).into(userImg!!) else Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg!!)
    }

    private val getCompanyInfo = Observer { companyInfo: PayloadCompanyInfo ->
        companyDi.balance = companyInfo.balance
        companyDi.managers = companyInfo.managers
        companyDi.createdAt = companyInfo.createdAt
        companyDi.createdBy = companyInfo.createdBy
        companyDi.id = companyInfo.id
        companyDi.name = companyInfo.name
        companyDi.pictureUrl = companyInfo.pictureUrl
        companyDi.status = companyInfo.status
        companyDi.size = companyInfo.size
        companyName!!.text = companyDi!!.name
        Glide.with(this).load(companyDi!!.pictureUrl).apply(RequestOptions.circleCropTransform()).into(companyImg!!)
    }

    private fun goToManageInvites() {
        val intent = Intent(this, ManageInvites::class.java)
        startActivity(intent)
    }

    private fun setLeaders() {
        val leaders: MutableList<PayloadShortMember> = ArrayList()
        for (leader in memberList) {
            if (user!!.extraLeaders.contains(leader.id)) leaders.add(leader)
        }
        leadAdapter = AllMembersAdapter(leaders, this, user.id, packageManager)
        leadRecycler!!.adapter = leadAdapter
    }

    private fun setCoworkers() {
        val coWorkers: MutableList<PayloadShortMember> = ArrayList()
        for (coWorker in memberList) {
            if (user.extraCoworkers.contains(coWorker.id)) coWorkers.add(coWorker)
        }
        coWorkersAdapter = AllMembersAdapter(coWorkers, this, user!!.id, this.packageManager)
        coWorkersRecycler!!.adapter = coWorkersAdapter
    }

    private fun seeAllMembers(view: View) {
        val intent = Intent(this, AllMembersActivity::class.java)
        intent.putExtra("memberId", user!!.id)
        startActivity(intent)
    }
    //TODO: Use the progressBar
    private fun logOut() {
        if (NetworkState.getInstance(this).isOnline) {
            ProgressDialog(this).apply {
                setMessage(resources.getString(R.string.log_out))
                setCancelable(false)
                show()
            }
            userDbManager!!.dropAllTable()
            networkManager!!.logOut()
            sharedPreferences!!.edit().remove("token").apply()
            networkManager!!.createToken(this)
        } else {
            Toast.makeText(this, "Lost internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel!!.downloadUserData()
        viewModel!!.userMutableLiveData
    }

    override fun getToken(token: String) {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra("token", token)
        val sharedPreferences = EmikaApplication.instance.sharedPreferences
        sharedPreferences!!.edit().putBoolean("logged in", false).apply()
        sharedPreferences!!.edit().putString("token", token).apply()
        startActivity(intent)
        viewModel!!.userMutableLiveData.value = Payload()
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}
