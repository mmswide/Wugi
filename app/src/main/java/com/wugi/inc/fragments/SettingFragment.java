package com.wugi.inc.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.activities.EnterActivity;
import com.wugi.inc.activities.MainActivity;
import com.wugi.inc.activities.PassActivity;
import com.wugi.inc.activities.SignActivity;
import com.wugi.inc.activities.TermsActivity;
import com.wugi.inc.models.BrowseEvent;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Type;
import com.wugi.inc.models.User;
import com.wugi.inc.models.Venue;
import com.wugi.inc.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context mContext;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Boolean bPushNotification = false;

    @BindView(R.id.iv_profile)
    CircleImageView iv_profile;
    @BindView(R.id.switch_notification)
    Switch switch_notification;
    @BindView(R.id.edit_first)
    EditText edit_first;
    @BindView(R.id.edit_last)
    EditText edit_last;
    @BindView(R.id.rl_password)
    RelativeLayout rl_password;
    @BindView(R.id.rl_about)
    RelativeLayout rl_about;
    @BindView(R.id.rl_contact)
    RelativeLayout rl_contact;
    @BindView(R.id.rl_privacy)
    RelativeLayout rl_privacy;
    @BindView(R.id.rl_terms)
    RelativeLayout rl_terms;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void getProfile() {

        String uid = mAuth.getCurrentUser().getUid();
        final ProgressDialog progressDialog = Utils.createProgressDialog(mContext);

        db.collection("Users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                edit_first.setText(document.getString("firstName"));
                                edit_last.setText(document.getString("lastName"));
                                bPushNotification = document.getBoolean("pushNotification");

                                switch_notification.setChecked(bPushNotification);

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void saveProfile() {
        Log.d("save", "save profile");
        if (TextUtils.isEmpty(edit_first.getText().toString())) {
            Toast.makeText(mContext, "Please input your first name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edit_last.getText().toString())) {
            Toast.makeText(mContext, "Please input your last name.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        final ProgressDialog progressDialog = Utils.createProgressDialog(mContext);
        db.collection("Users").document(uid)
                .update(
                        "firstName", edit_first.getText().toString(),
                        "lastName", edit_last.getText().toString(),
                        "pushNotification", bPushNotification
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();

                UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                builder.setDisplayName(edit_first.getText().toString() + " " + edit_last.getText().toString());
                UserProfileChangeRequest profileUpdates = builder.build();
                mAuth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, "Profile updated successfully.",
                                            Toast.LENGTH_SHORT).show();
                                    ((MainActivity)mContext).updateProfile();
                                } else {
                                    Toast.makeText(mContext, "Profile update failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(mContext, "Profile update failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);

        String imageUrl = mAuth.getCurrentUser().getPhotoUrl().toString();
        String email = mAuth.getCurrentUser().getEmail().toString();

        Picasso.with(getContext()).load(imageUrl).into(iv_profile);

        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                bPushNotification = b;
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @OnClick(R.id.rl_terms)
    void onTerms() {
        Intent intent = new Intent(mContext, TermsActivity.class);
        intent.putExtra("terms", true);
        startActivity(intent);
    }
    @OnClick(R.id.rl_privacy)
    void onPrivacy() {
        Intent intent = new Intent(mContext, TermsActivity.class);
        intent.putExtra("privacy", true);
        startActivity(intent);
    }
    @OnClick(R.id.rl_about)
    void onAbout() {
        Intent intent = new Intent(mContext, TermsActivity.class);
        intent.putExtra("about", true);
        startActivity(intent);
    }

    @OnClick(R.id.rl_contact)
    void onContact() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "rodk.music@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Wugi App");
        email.putExtra(Intent.EXTRA_TEXT, "");

        //need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @OnClick(R.id.rl_password)
    void onPassword() {
        Intent intent = new Intent(mContext, PassActivity.class);
        startActivity(intent);
    }
}
