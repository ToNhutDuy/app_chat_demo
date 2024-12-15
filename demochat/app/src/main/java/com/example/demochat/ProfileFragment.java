package com.example.demochat;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.demochat.model.UserModel;
import com.example.demochat.utils.AndroidUtil;
import com.example.demochat.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.play.core.integrity.r;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    ActivityResultLauncher<Intent> imagePickLauncher;
    EditText profileUserName, profileNumberPhone;
    CircleImageView profileImageView;
    Button btnUpdate;
    UserModel currenUserModel;
    Uri seclectedImageUri;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            seclectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(), seclectedImageUri, profileImageView);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        getUserData();

        btnUpdate.setOnClickListener(v ->{
            updateClick();
        });
        profileImageView.setOnClickListener(v ->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });
        return view;
    }

    private void updateClick() {
        String newUserName = profileUserName.getText().toString();
        if (newUserName.isEmpty() || newUserName.length() < 3) {
            profileUserName.setError(getString(R.string.username_must_be_at_least_3_characters));
            return;
        }
        currenUserModel.setUserName(newUserName);

        if(seclectedImageUri != null){
            FirebaseUtil.getCurrentProfilePicStoraRef().putFile(seclectedImageUri)
                    .addOnCompleteListener(task ->{
                       updateToFirestore();
                    });
        }else{
            updateToFirestore();
        }

    }
    private void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currenUserModel)
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        AndroidUtil.showToats(getActivity(), getString(R.string.update_successful));
                    }else{
                        AndroidUtil.showToats(getActivity(), getString(R.string.update_failed));
                    }
                });

    }
    private void getUserData() {

        FirebaseUtil.getCurrentProfilePicStoraRef().getDownloadUrl()
                        .addOnCompleteListener(task ->{
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(getContext(), uri, profileImageView);
                            }
                        });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task ->{
            currenUserModel = task.getResult().toObject(UserModel.class);
            profileUserName.setText(currenUserModel.getUserName());
            profileNumberPhone.setText(currenUserModel.getPhoneNumber());
        });
    }

    private void init(View view){
        profileImageView = view.findViewById(R.id.profile_image_view);
        profileUserName = view.findViewById(R.id.profile_user_name);
        profileNumberPhone = view.findViewById(R.id.profile_phone);
        btnUpdate = view.findViewById(R.id.profile_update_btn);
    }

}