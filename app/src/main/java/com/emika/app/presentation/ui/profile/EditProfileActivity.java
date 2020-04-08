package com.emika.app.presentation.ui.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.dbmanager.UserDbManager;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.networkManager.auth.AuthNetworkManager;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.presentation.ui.auth.AuthActivity;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity implements TokenCallback{
    private ProfileViewModel mViewModel;
    private EditText firstName, lastName, jobTitle, biography;
    private String token;
    private TextView cancel, saveChanges;
    private ImageView userImg;
    private static final String TAG = "EditProfileActivity";
    private static final int IMAGE_REQUEST = 1;
    private  UserDbManager userDbManager;
    private AuthNetworkManager networkManager;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();
    }

    private void initView() {
        sharedPreferences = EmikaApplication.getInstance().getSharedPreferences();
        token = sharedPreferences.getString("token", "");
        userDbManager = new UserDbManager();
        networkManager = new AuthNetworkManager(token);
        cancel  = findViewById(R.id.edit_cancel);
        cancel.setOnClickListener(this::cancel);
        firstName = findViewById(R.id.edit_first_name);
        lastName = findViewById(R.id.edit_last_name);
        jobTitle = findViewById(R.id.edit_job_title);
        userImg = findViewById(R.id.edit_user_img);
        biography = findViewById(R.id.edit_biography);
        userImg.setOnClickListener(this::updatePhoto);
        saveChanges = findViewById(R.id.edit_save_changes);
        saveChanges.setOnClickListener(this::updateInfo);
        mViewModel = ViewModelProviders.of(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        mViewModel.getUserMutableLiveData().observe(this, getUserLiveData);
    }

    private void cancel(View view) {
       finish();
    }

    private void updatePhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_PICK);
        startActivityForResult(intent, IMAGE_REQUEST);
//        requestStoragePermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && data != null
                && data.getData() != null) {
            Uri uri = data.getData();
            OutputStream os = null;
            int rotation = 0;

            try {
                InputStream is = getContentResolver().openInputStream(uri);
                String type = getContentResolver().getType(uri);
                File result = new File(getFilesDir(),  "."
                        + MimeTypeMap.getSingleton().getExtensionFromMimeType(type));
                os = new BufferedOutputStream(new FileOutputStream(result));
                ExifInterface exifInterface = new ExifInterface(is);
                int orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        Log.d(TAG, "onActivityResult: 90");
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Log.d(TAG, "onActivityResult: 180");
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        Log.d(TAG, "onActivityResult: 270");
                        break;
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);

                Bitmap pictureBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri); // obtaining the Bitmap
                Bitmap rotatedBitmap = Bitmap.createBitmap(pictureBitmap, 0, 0, pictureBitmap.getWidth(), pictureBitmap.getHeight(), matrix, true);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,50 , os); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                mViewModel.updateImage(result);
                os.flush();
                os.close();
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateInfo(View view) {
        mViewModel.updateUser(firstName.getText().toString(), lastName.getText().toString(), biography.getText().toString(), jobTitle.getText().toString());
        Toast.makeText(this, "All changes are saved", Toast.LENGTH_SHORT).show();
    }

    private Observer<Payload> getUserLiveData = user ->{
        if (user!=null) {
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            biography.setText(user.getBio());
            jobTitle.setText(user.getJobTitle());
            if (user.getPictureUrl() != null)
            Glide.with(this).load(user.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(userImg);
        else
            Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg);
        }
    };


    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                             Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    @Override
    public void getToken(String token) {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.putExtra("token", token);
        SharedPreferences sharedPreferences = EmikaApplication.getInstance().getSharedPreferences();
        sharedPreferences.edit().putBoolean("logged in", false).apply();
        sharedPreferences.edit().putString("token", token).apply();
        startActivity(intent);
    }
}
