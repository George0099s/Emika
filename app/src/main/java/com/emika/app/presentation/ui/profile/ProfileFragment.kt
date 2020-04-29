package com.emika.app.presentation.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
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
import androidx.fragment.app.Fragment
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
import com.emika.app.presentation.ui.profile.EditProfileActivity
import com.emika.app.presentation.utils.NetworkState
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), TokenCallback {
    @JvmField
    @Inject
    var user: User? = null

    @JvmField
    @Inject
    var companyDi: CompanyDi? = null
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
    private val app = EmikaApplication.getInstance()
    private val getMembers = Observer { members: List<PayloadShortMember> ->
        memberList = members
        companyMemberCount!!.text = String.format("%s %s", memberList.size, resources.getString(R.string.members_string))
        setCoworkers()
        setLeaders()
    }
    private val getUserInfo = Observer { user: Payload ->
        this.user!!.id = user.id
        this.user!!.firstName = user.firstName
        this.user!!.lastName = user.lastName
        this.user!!.bio = user.bio
        this.user!!.pictureUrl = user.pictureUrl
        this.user!!.contacts = user.contacts
        this.user!!.extraCoworkers = user.coworkers
        this.user!!.extraLeaders = user.leaders
        this.user!!.admin = user.isAdmin
        this.user!!.leader = user.isLeader
        contactAdapter = ProfileContactAdapter(user.contacts, context)
        userName!!.text = String.format("%s %s", user.firstName, user.lastName)
        jobTitle!!.text = user.jobTitle
        contactsRecycler!!.adapter = contactAdapter
        if (user.pictureUrl != null) Glide.with(this).load(user.pictureUrl).apply(RequestOptions.circleCropTransform()).into(userImg!!) else Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg!!)
        viewModel!!.memberMutableLiveData.observe(viewLifecycleOwner, getMembers)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        //        getActivity().setActionBar(view.findViewById(R.id.profile_toolbar));
        initView(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add(0, 1, 1, menuIconWithText(resources.getDrawable(R.drawable.ic_rename_task), resources.getString(R.string.edit_account), false))
        menu.add(0, 2, 2, menuIconWithText(resources.getDrawable(R.drawable.ic_log_ou), resources.getString(R.string.log_out), true))
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                val intent = Intent(context, EditProfileActivity::class.java)
                intent.putExtra("viewModel", viewModel)
                startActivity(intent)
            }
            2 -> logOut(view)
        }
        return true
    }

    private fun menuIconWithText(r: Drawable, title: String, red: Boolean): CharSequence {
        r.setBounds(0, 0, r.intrinsicWidth, r.intrinsicHeight)
        val sb = SpannableString("    $title")
        if (red) sb.setSpan(ForegroundColorSpan(context!!.resources.getColor(R.color.red)), 0, sb.length, 0)
        val imageSpan = ImageSpan(r, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    private fun initView(view: View) {
        app.component.inject(this)
        sharedPreferences = EmikaApplication.getInstance().sharedPreferences
        token = EmikaApplication.getInstance().sharedPreferences.getString("token", "")
        view.darkThemeMode.isChecked = EmikaApplication.instance.sharedPreferences.getBoolean("darkMode", true)
        userDbManager = UserDbManager()
        networkManager = AuthNetworkManager(token)
        userName = view.findViewById(R.id.profile_user_name)
        jobTitle = view.findViewById(R.id.profile_user_job_title)
        userImg = view.findViewById(R.id.profile_user_img)
        viewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(ProfileViewModel::class.java)
        viewModel!!.setContext(context)
        viewModel!!.downloadUserData()
        viewModel!!.getDbUserData()
        viewModel!!.userMutableLiveData.observe(viewLifecycleOwner, getUserInfo)
        //        viewModel.getUser().observe(getViewLifecycleOwner(), getUserUpdated);
        viewModel!!.companyInfoMutableLiveData.observe(viewLifecycleOwner, getCompanyInfo)
        contactsRecycler = view.findViewById(R.id.profile_contacts_recycler)
        contactsRecycler!!.layoutManager = LinearLayoutManager(context)
        contactsRecycler!!.setHasFixedSize(true)
        view.profile_see_all_members.setOnClickListener { view: View -> seeAllMembers(view) }
        companyName = view.findViewById(R.id.profile_company_name)
        companyMemberCount = view.findViewById(R.id.profile_company_members)
        companyImg = view.findViewById(R.id.profile_company_img)
        leadRecycler = view.findViewById(R.id.profile_leader_recycler)
        coWorkersRecycler = view.findViewById(R.id.profile_coworkers_recycler)
        leadRecycler!!.setHasFixedSize(true)
        leadRecycler!!.layoutManager = LinearLayoutManager(context)
        coWorkersRecycler!!.setHasFixedSize(true)
        coWorkersRecycler!!.layoutManager = LinearLayoutManager(context)
//        leadRecycler!!.setOnClickListener { view: View -> goToManageInvites(view) }

        view.darkThemeMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                EmikaApplication.instance.sharedPreferences.edit().putBoolean("darkMode", true).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                EmikaApplication.instance.sharedPreferences.edit().putBoolean("darkMode", false).apply()
            }
        }
    }

    private val getUserUpdated = Observer { user: User ->
        userName!!.text = String.format("%s %s", user.firstName, user.lastName)
        jobTitle!!.text = user.jobTitle
        contactAdapter = ProfileContactAdapter(user.contacts, context)
        contactsRecycler!!.adapter = contactAdapter
        if (user.pictureUrl != null) Glide.with(this).load(user.pictureUrl).apply(RequestOptions.circleCropTransform()).into(userImg!!) else Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg!!)
    }

    private val getCompanyInfo = Observer { companyInfo: PayloadCompanyInfo ->
        companyDi!!.balance = companyInfo.balance
        companyDi!!.managers = companyInfo.managers
        companyDi!!.createdAt = companyInfo.createdAt
        companyDi!!.createdBy = companyInfo.createdBy
        companyDi!!.id = companyInfo.id
        companyDi!!.name = companyInfo.name
        companyDi!!.pictureUrl = companyInfo.pictureUrl
        companyDi!!.status = companyInfo.status
        companyDi!!.size = companyInfo.size
        companyName!!.text = companyDi!!.name
        Glide.with(context!!).load(companyDi!!.pictureUrl).apply(RequestOptions.circleCropTransform()).into(companyImg!!)
    }

    private fun goToManageInvites(view: View) {
        val intent = Intent(context, ManageInvites::class.java)
        startActivity(intent)
    }

    private fun setLeaders() {
        val leaders: MutableList<PayloadShortMember> = ArrayList()
        for (leader in memberList) {
            if (user!!.extraLeaders.contains(leader.id)) leaders.add(leader)
        }
        leadAdapter = AllMembersAdapter(leaders, context, user!!.id, activity!!.packageManager)
        leadRecycler!!.adapter = leadAdapter
    }

    private fun setCoworkers() {
        val coWorkers: MutableList<PayloadShortMember> = ArrayList()
        for (coWorker in memberList) {
            if (user!!.extraCoworkers.contains(coWorker.id)) coWorkers.add(coWorker)
        }
        coWorkersAdapter = AllMembersAdapter(coWorkers, context, user!!.id, activity!!.packageManager)
        coWorkersRecycler!!.adapter = coWorkersAdapter
    }

    private fun seeAllMembers(view: View) {
        val intent = Intent(context, AllMembersActivity::class.java)
        intent.putExtra("memberId", user!!.id)
        startActivity(intent)
    }

    private fun logOut(view: View?) {
        if (NetworkState.getInstance(context).isOnline) {
            userDbManager!!.dropAllTable()
            networkManager!!.logOut()
            sharedPreferences!!.edit().remove("token").apply()
            networkManager!!.createToken(this)
        } else {
            Toast.makeText(context, "Lost internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel!!.downloadUserData()
        viewModel!!.userMutableLiveData
    }

    override fun getToken(token: String) {
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra("token", token)
        val sharedPreferences = EmikaApplication.getInstance().sharedPreferences
        sharedPreferences.edit().putBoolean("logged in", false).apply()
        sharedPreferences.edit().putString("token", token).apply()
        startActivity(intent)
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}