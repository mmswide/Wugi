package com.wugi.inc.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wugi.inc.R;
import com.wugi.inc.models.User;
import com.wugi.inc.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.id;

public class EnterActivity extends AppCompatActivity {
    @BindView(R.id.signupButton)Button signupButton;
    @BindView(R.id.signinButton) Button signinButton;
    @BindView(R.id.fbButton)
    LoginButton fbButton;
    @BindView(R.id.btn_fb)
    Button btn_fb;
    @BindView(R.id.guestButton) Button guestButton;
    @BindView(R.id.termsButton) Button termsButton;

    private static final String TAG = "Wugi";

    private FirebaseAuth mAuth;
    //FaceBook callbackManager
    private CallbackManager callbackManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        ButterKnife.bind(this);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(EnterActivity.this, MainActivity.class));
            finish();
        }

        //FaceBook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        fbButton.setReadPermissions("email", "public_profile");
        fbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                String accessToken = loginResult.getAccessToken()
                        .getToken();
                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {@Override
                        public void onCompleted(JSONObject object,
                                                GraphResponse response) {

                            Log.i("LoginActivity",
                                    response.toString());
                            try {
                                String fb_id = object.getString("id");
                                try {
                                    URL profile_pic = new URL(
                                            "http://graph.facebook.com/" + fb_id + "/picture?type=large");
                                    Log.i("profile_pic",
                                            profile_pic + "");

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                String name = object.getString("name");
                                String firstName = object.getString("first_name");
                                String lastName = object.getString("last_name");
                                String email = object.getString("email");
                                String gender = "other";
                                if (object.has("gender")) {
                                    gender = object.getString("gender");
                                }
                                String birthday = "";
                                if (object.has("gender")) {
                                    gender = object.getString("birthday");
                                }

                                String picture_url = "";
                                if (object.has("picture")) {
                                    object.getJSONObject("picture").getJSONObject("data").getString("url");
                                }


                                User user = new User(mAuth.getCurrentUser().getUid(), firstName, lastName, email, gender, picture_url,true, birthday);
                                EnterActivity.this.currentUser = user;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields",
                        "id,name,first_name, last_name, picture.type(large), email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();


                signInWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void signInWithFacebook(AccessToken token) {
        Log.d(TAG, "signInWithFacebook:" + token);

        final ProgressDialog progressDialog = Utils.createProgressDialog(this);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(EnterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            String uid=task.getResult().getUser().getUid();
//                            String name=task.getResult().getUser().getDisplayName();
//                            String email=task.getResult().getUser().getEmail();
//                            String image=task.getResult().getUser().getPhotoUrl().toString();
//
//                            //Create a new User and Save it in Firebase database
//                            User user = new User(uid,name,null,email,null);
//
//                            DocumentReference documentReference = db.document("Users/" + uid);
//                            documentReference.set(user);

                            DocumentReference reference = db.document("Users/" + uid);
                            reference.set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EnterActivity.this, "Profile created successfully.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                        }
                                    });

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        progressDialog.dismiss();
                    }
                });
    }

    //FaceBook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btn_fb)
    public void fbLogin() {
        fbButton.performClick();
    }

    @OnClick(R.id.signupButton)
    public void signUp() {
        Intent intent = new Intent(EnterActivity.this, SignActivity.class);
        intent.putExtra("isSignup", true);
        startActivity(intent);
    }

    @OnClick(R.id.signinButton)
    public void signIn() {
        Intent intent = new Intent(EnterActivity.this, SignActivity.class);
        intent.putExtra("isSignup", false);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @OnClick(R.id.guestButton)
    public void guest() {
        Intent intent = new Intent(EnterActivity.this, GuestActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
    @OnClick(R.id.termsButton)
    public void terms() {
        Intent intent = new Intent(EnterActivity.this, TermsActivity.class);
        intent.putExtra("accept", true);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
