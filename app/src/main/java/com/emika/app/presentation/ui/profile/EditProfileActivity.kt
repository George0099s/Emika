package com.emika.app.presentation.ui.profile

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.dbmanager.UserDbManager
import com.emika.app.data.network.callback.TokenCallback
import com.emika.app.data.network.networkManager.auth.AuthNetworkManager
import com.emika.app.data.network.pojo.user.Payload
import com.emika.app.di.User
import com.emika.app.presentation.adapter.profile.ItemTouchHelper.SimpleItemTouchHelperCallback
import com.emika.app.presentation.adapter.profile.ProfileContactAdapter
import com.emika.app.presentation.ui.auth.AuthActivity
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.profile.EditProfileViewModel
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.*
import javax.inject.Inject

class EditProfileActivity : AppCompatActivity(), TokenCallback {
    private var mViewModel: EditProfileViewModel? = null
    private var firstName: EditText? = null
    private var lastName: EditText? = null
    private var jobTitle: EditText? = null
    private var biography: EditText? = null
    private var token: String? = null
    private var cancel: TextView? = null
    private var saveChanges: TextView? = null
    private var contactRecycler: RecyclerView? = null
    private var adapter: ProfileContactAdapter? = null
    private var userImg: ImageView? = null
    private var userDbManager: UserDbManager? = null
    private var networkManager: AuthNetworkManager? = null
    private var sharedPreferences: SharedPreferences? = null
    private var profileViewModel: ProfileViewModel? = null
    @Inject
    lateinit var userDi: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val s = intent.getStringExtra("viewModel")
        profileViewModel = intent.getParcelableExtra("viewModel") as ProfileViewModel
        initView()
    }

    private fun initView() {
        EmikaApplication.getInstance().component.inject(this)
        sharedPreferences = EmikaApplication.getInstance().sharedPreferences
        token = sharedPreferences!!.getString("token", "")
        userDbManager = UserDbManager()
        networkManager = AuthNetworkManager(token)
        edit_cancel.setOnClickListener { view: View -> cancel(view) }
        firstName = findViewById(R.id.edit_first_name)
        lastName = findViewById(R.id.edit_last_name)
        jobTitle = findViewById(R.id.edit_job_title)
        userImg = findViewById(R.id.edit_user_img)
        biography = findViewById(R.id.edit_biography)
        contactRecycler = findViewById(R.id.edit_contacts)
        edit_user_img.setOnClickListener { view: View -> updatePhoto(view) }
        edit_save_changes.setOnClickListener { view: View -> updateInfo(view) }
        contactRecycler!!.setHasFixedSize(true)
        contactRecycler!!.layoutManager = LinearLayoutManager(this)
        adapter = ProfileContactAdapter(userDi!!.contacts, this)
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)

        touchHelper.attachToRecyclerView(contactRecycler)
        contactRecycler!!.adapter = adapter
        mViewModel = ViewModelProviders.of(this, TokenViewModelFactory(token)).get(EditProfileViewModel::class.java)
        mViewModel!!.userMutableLiveData.observe(this, getUserLiveData)
        edit_add_contact.setOnClickListener {v: View -> addContact(v)}
    }

    private fun addContact(v: View) {
        val addContact = AddContactDialogFragment()
        addContact.isCancelable = true
        addContact.show(supportFragmentManager, "addContactDialog")
    }

    private fun cancel(view: View) {
        finish()
    }

    private fun updatePhoto(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && data != null && data.data != null) {
            val uri = data.data
            var os: OutputStream? = null
            var rotation = 0
            try {
                val `is` = contentResolver.openInputStream(uri!!)
                val type = contentResolver.getType(uri)
                val result = File(filesDir, "."
                        + MimeTypeMap.getSingleton().getExtensionFromMimeType(type))
                os = BufferedOutputStream(FileOutputStream(result))
                val exifInterface = ExifInterface(`is`!!)
                val orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        rotation = 90
                        Log.d(TAG, "onActivityResult: 90")
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        Log.d(TAG, "onActivityResult: 180")
                        rotation = 180
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        rotation = 270
                        Log.d(TAG, "onActivityResult: 270")
                    }
                }
                val matrix = Matrix()
                matrix.postRotate(rotation.toFloat())
                val pictureBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri) // obtaining the Bitmap
                val rotatedBitmap = Bitmap.createBitmap(pictureBitmap, 0, 0, pictureBitmap.width, pictureBitmap.height, matrix, true)
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os) // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                mViewModel!!.updateImage(result)
                os.flush()
                os.close()
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateInfo(view: View) {
        userDi.contacts = adapter!!.contacts
        userDi.firstName = firstName!!.text.toString()
        userDi.lastName = lastName!!.text.toString()
        userDi.bio = biography!!.text.toString()
        userDi.jobTitle = jobTitle!!.text.toString()
        mViewModel!!.updateUser(userDi)
//        profileViewModel!!.userMutableLiveData

        val snack = Snackbar.make(view,"All changes saved",Snackbar.LENGTH_SHORT)
        snack.show()
    }

    private val getUserLiveData = Observer { user: Payload? ->
        if (user != null) {
            firstName!!.setText(user.firstName)
            lastName!!.setText(user.lastName)
            biography!!.setText(user.bio)
            jobTitle!!.setText(user.jobTitle)
            if (user.pictureUrl != null) Glide.with(this).load(user.pictureUrl).apply(RequestOptions.circleCropTransform()).into(userImg!!) else Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg!!)
        }
    }

    private fun requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                             Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // show alert dialog navigating to Settings
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {}
                }).withErrorListener { error: DexterError? -> Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    override fun getToken(token: String) {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra("token", token)
        val sharedPreferences = EmikaApplication.getInstance().sharedPreferences
        sharedPreferences.edit().putBoolean("logged in", false).apply()
        sharedPreferences.edit().putString("token", token).apply()
        startActivity(intent)
    }

    companion object {
        private const val TAG = "EditProfileActivity"
        private const val IMAGE_REQUEST = 1
    }
}