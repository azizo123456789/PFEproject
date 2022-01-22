package com.example.pfeproject.fragments.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.ui.SignInActivity;
import com.example.pfeproject.ui.UserHistoryActivity;
import com.example.pfeproject.ui.UserPanierActivity;
import com.example.pfeproject.ui.UserPointActivity;
import com.example.pfeproject.ui.UserProfileActivity;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TagProfileFrag";
    @BindView(R.id.btn_edit_profile)
    Button btn_edit_profile;
    @BindView(R.id.btn_history)
    Button btn_history;
    @BindView(R.id.btn_point)
    Button btn_point;
    @BindView(R.id.btn_panier)
    ImageButton btn_panier;
    @BindView(R.id.btn_deconnect)
    Button btn_logout;
    @BindView(R.id.btn_edit_image)
    ImageButton btn_edit_image;

    @BindView(R.id.user_avatar)
    CircleImageView avatar;

    @BindView(R.id.myProgBar)
    AVLoadingIndicatorView progBar;

    @BindView(R.id.user_email)
    TextView user_email;
    @BindView(R.id.user_name)
    TextView user_name;

    private SessionManager manager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);

        manager = new SessionManager(getActivity());

        setUserData();

        btn_edit_profile.setOnClickListener(this);
        btn_history.setOnClickListener(this);
        btn_point.setOnClickListener(this);
        btn_panier.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_edit_image.setOnClickListener(this);

        return root;
    }

    private void setUserData() {
        Ion.with(getActivity())
                .load(ApiUrl.GET, ApiUrl.apiGetUserData + manager.getUserId())
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: error in profile fragment load data = " + e.getMessage());
                        } else {
                            Log.d(TAG, "onCompleted: load data with success");
                            user_email.setText("" + result.get("email").getAsString());
                            user_name.setText("" + result.get("firstName").getAsString() + " " + result.get("lastName").getAsString());

                            if (!result.get("imageLink").isJsonNull()) {
                                String userimageLink = result.get("imageLink").getAsString();
                                String replacedImageLink = userimageLink.replace(ApiUrl.hostnameHost, ApiUrl.hostname);
                                Picasso.get().load(replacedImageLink).placeholder(R.drawable.avatar_icon).into(avatar);
                            } else
                                Log.d(TAG, "onCompleted: image link == null");
                        }

                    }
                });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_image:
                openGallery();
                break;

            case R.id.btn_edit_profile:
                startActivity(new Intent(getActivity(), UserProfileActivity.class));
                break;

            case R.id.btn_history:
                startActivity(new Intent(getActivity(), UserHistoryActivity.class));
                break;

            case R.id.btn_point:
                startActivity(new Intent(getActivity(), UserPointActivity.class));
                break;

            case R.id.btn_panier:
                startActivity(new Intent(getActivity(), UserPanierActivity.class));
                break;

            case R.id.btn_deconnect:
                manager.UserLogOut();
                startActivity(new Intent(getActivity(), SignInActivity.class));
                getActivity().finish();
                break;
        }

    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 20);
        } else {
            ImagePicker.Companion.with(ProfileFragment.this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            Log.w(TAG, "onActivityResult: result okay ");
            if (data != null) {
                Uri image_uri = data.getData();
                if (image_uri != null)
                    showImage(image_uri);
                else
                    Log.e(TAG, "onActivityResult: image uri == null");
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR)
            Toast.makeText(getActivity(), getString(R.string.error_upload_image), Toast.LENGTH_SHORT).show();
        else if (resultCode == Activity.RESULT_CANCELED)
            Toast.makeText(getActivity(), getString(R.string.toast_photo_cancled), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), getString(R.string.error_task), Toast.LENGTH_SHORT).show();
    }

    private void showImage(Uri uri) {
        Log.d(TAG, "showImage: good ..." + uri);

        avatar.setVisibility(View.GONE);
        progBar.setVisibility(View.VISIBLE);
        final File fileToUpload = new File(uri.getPath());
        Ion.with(getContext())
                .load(ApiUrl.POST, ApiUrl.apiUploadUserPic + manager.getUserId())
//                .setHeader(Types.ContentType, Types.ContentTypeFileValue)
                .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                .setMultipartFile("file", "*/*", fileToUpload)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + getString(R.string.error_task), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: error in profile fragment = " + e.getMessage());

                            avatar.setVisibility(View.VISIBLE);
                            progBar.setVisibility(View.GONE);
                        } else {
                            int code = result.getHeaders().code();
                            if (code == 200) {
                                avatar.setVisibility(View.VISIBLE);
                                progBar.setVisibility(View.GONE);
                                avatar.setImageURI(uri);
                            } else {
                                Log.e(TAG, "onCompleted: " + result.getResult().get("message").getAsString());
                                Toast.makeText(getActivity(), "code = " + code, Toast.LENGTH_SHORT).show();
                                avatar.setVisibility(View.VISIBLE);
                                progBar.setVisibility(View.GONE);
                            }

                        }
                    }
                });
    }

}