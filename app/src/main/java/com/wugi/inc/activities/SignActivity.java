package com.wugi.inc.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wugi.inc.R;
import com.wugi.inc.models.User;
import com.wugi.inc.utils.OnSwipeTouchListener;
import com.wugi.inc.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class SignActivity extends AppCompatActivity {

    @BindView(R.id.dismissButton)   ImageButton dismissButton;
    @BindView(R.id.signupMark)      ImageView signupMarkImageView;
    @BindView(R.id.signinMark)      ImageView signinMarkImageView;
    @BindView(R.id.signupLayout)    LinearLayout signupLayout;
    @BindView(R.id.signinLayout)    LinearLayout signinLayout;
    @BindView(R.id.signupButton)    Button signupButton;
    @BindView(R.id.signinButton)    Button signinButton;
    @BindView(R.id.ivImage)         CircleImageView ivImage;
    @BindView(R.id.datePickerButton) Button datePickerButton;
    @BindView(R.id.inEmail)         EditText inInputEmail;
    @BindView(R.id.inPassword)      EditText inInputPassword;
    @BindView(R.id.firstName)       EditText inputFirstName;
    @BindView(R.id.lastName)        EditText inputLastName;
    @BindView(R.id.upEmail)         EditText upInputEmail;
    @BindView(R.id.upPassword)      EditText upInputPassword;
    @BindView(R.id.upConfirmPassword) EditText upInputConfirm;
    @BindView(R.id.spinner)         Spinner genderSpinner;
    @BindView(R.id.btn_up_fb)
    FancyButton signupFbButton;
    @BindView(R.id.fbButton)
    LoginButton fbButton;
    @BindView(R.id.ll_sign)
    LinearLayout ll_sign;

    private boolean isSignup;
    private int REQUEST_CAMERA = 1888, SELECT_FILE = 1889;
    private String userChoosenTask;
    private int mYear, mMonth, mDay;
    private FirebaseAuth mAuth;
    public User user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Bitmap profileBitmap;
    private Uri mUri;
    private String dateStr;
    private String gender;

    //FaceBook callbackManager
    private CallbackManager callbackManager;
    private static final String TAG = "SignActivity";
    private User currentUser;
    boolean bUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        ButterKnife.bind(this);

        inputFirstName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        inputLastName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        Bundle extras = getIntent().getExtras();
        final boolean isSignup = extras.getBoolean("isSignup");
        this.isSignup = isSignup;
        bUp = isSignup;

//        String leftText = getString(R.string.sign_f);
//        String rightText = getString(R.string.fb);
//        String s= leftText + " " + rightText;
//        SpannableString ss1=  new SpannableString(s);
//        ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 12, 0);// set color
//        ss1.setSpan(new RelativeSizeSpan(1.5f), 12, ss1.length(), 0); // set size
//
//        ss1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), leftText.length() + 1, leftText.length() + 1 +rightText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//        signupFbButton.setText(ss1);

        initView(isSignup);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        final String[] genders = getResources().getStringArray(R.array.genders);
        gender = genders[genders.length-1];
        genderSpinner.setSelection(genders.length-1);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = genders[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                                if (object.has("birthday")) {
                                    birthday = object.getString("birthday");
                                }

                                String picture_url = "";
                                if (object.has("picture")) {
                                    JSONObject jsonObject = object.getJSONObject("picture");
                                    if (jsonObject.has("data")) {
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        if (data.has("url")) {
                                            picture_url = data.getString("url");
                                        }
                                    }
                                }


                                User user = new User(fb_id, firstName, lastName, email, gender, picture_url,true, birthday);
                                SignActivity.this.currentUser = user;

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

        ll_sign.setOnTouchListener(new OnSwipeTouchListener(SignActivity.this){
            public void onSwipeRight() {
                if (bUp) {
                    bUp = !bUp;
                    initView(false);
                } else
                    return;
            }
            public void onSwipeLeft() {
                if (bUp)
                    return;
                else {
                    bUp = !bUp;
                    initView(true);
                }
            }
        });
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", mUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        mUri = savedInstanceState.getParcelable("file_uri");
    }

    void initView(boolean isSignup) {
        bUp = isSignup;
        if (isSignup) {
            signupMarkImageView.setVisibility(View.VISIBLE);
            signinMarkImageView.setVisibility(View.GONE);
            signupLayout.setVisibility(View.VISIBLE);
            signinLayout.setVisibility(View.GONE);

        } else {
            signupMarkImageView.setVisibility(View.GONE);
            signinMarkImageView.setVisibility(View.VISIBLE);
            signupLayout.setVisibility(View.GONE);
            signinLayout.setVisibility(View.VISIBLE);
        }
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
                            Toast.makeText(SignActivity.this, "Authentication failed.",
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
                                    Toast.makeText(SignActivity.this, "Profile created successfully.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignActivity.this, MainActivity.class);
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

    @OnClick({R.id.btn_up_fb, R.id.btn_in_fb})
    void fbLogin() {
        fbButton.performClick();
    }

    @OnClick({R.id.signupButton, R.id.signinButton})
    void toggle(Button button) {
        if (button.getId() == R.id.signupButton) {
            initView(true);
        } else {
            initView(false);
        }
    }

    //This method, validates email address and password
    private boolean validateForm() {
        boolean valid = true;

        String email = inInputEmail.getText().toString();
        final String password = inInputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            inInputEmail.setError("Required.");
            valid = false;
        } else {
            inInputEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            inInputPassword.setError("Required.");
            valid = false;
        } else {
            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                valid = false;
            } else {
                inInputPassword.setError(null);
            }
        }

        return valid;
    }

    @OnClick(R.id.sign_in_button)
    void signIn() {
        signIn(inInputEmail.getText().toString(), inInputPassword.getText().toString());
    }
    void signIn(String email, final String password) {
        if (!validateForm()) {
            return;
        }

        final ProgressDialog progressDialog = Utils.createProgressDialog(this);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inInputPassword
                                        .setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(SignActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Intent intent = new Intent(SignActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private boolean validateSignupForm() {
        boolean valid = true;

        String email = upInputEmail.getText().toString();
        final String password = upInputPassword.getText().toString();
        String confirmPassword = upInputConfirm.getText().toString();
        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();

        if (mUri == null) {
            Toast.makeText(getApplicationContext(), "Please choose your photo.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (TextUtils.isEmpty(firstName)) {
            inputFirstName.setError("Required.");
            Toast.makeText(getApplicationContext(), "Please input first name.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputFirstName.setError(null);
        }

        if (TextUtils.isEmpty(lastName)) {
            inputLastName.setError("Required.");
            Toast.makeText(getApplicationContext(), "Please input last name.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputLastName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            upInputEmail.setError("Required.");
            Toast.makeText(getApplicationContext(), "Please input email.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            upInputEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            upInputPassword.setError("Required.");
            Toast.makeText(getApplicationContext(), "Please input password.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                valid = false;
            } else {
                upInputPassword.setError(null);
            }
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            upInputConfirm.setError("Required.");
            Toast.makeText(getApplicationContext(), "Please input confirm password.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            upInputConfirm.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            valid = false;
        }

        return valid;
    }

    void signUp(final String email, final String password, final String firstName, final String lastName) {
        final ProgressDialog progressDialog = Utils.createProgressDialog(this);
        //create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("users").child(currentUser.getUid() + ".jpg");
                            StorageMetadata storageMetadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpeg")
                                    .build();

                            UploadTask uploadTask = storageReference.putFile(mUri, storageMetadata);

                            // Register observers to listen for when the download is done or if it fails
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                    UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                                    builder.setDisplayName(inputFirstName.getText().toString() + " " + inputLastName.getText().toString());
                                    builder.setPhotoUri(downloadUrl);

                                    UserProfileChangeRequest profileUpdates = builder.build();

                                    currentUser.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        User user = new User(mAuth.getCurrentUser().getUid(), firstName, lastName, email, gender, downloadUrl.toString(),true,dateStr);

                                                        DocumentReference reference = db.document("Users/" + currentUser.getUid());
                                                        reference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(SignActivity.this, "Profile created successfully.",
                                                                        Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(SignActivity.this, MainActivity.class);
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
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Profile update failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });

                        }
                    }
                });
    }



    @OnClick(R.id.sign_up_button)
    void signUp() {
        if (!validateSignupForm()) {
            return;
        }
        signUp(upInputEmail.getText().toString(),
                upInputPassword.getText().toString(),
                inputFirstName.getText().toString(),
                inputLastName.getText().toString());
    }

    @OnClick(R.id.dismissButton)
    void dismiss() {
        finish();
    }

    @OnClick(R.id.guestButton)
    void onGuest() {
        Intent intent = new Intent(SignActivity.this, GuestActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
    @OnClick(R.id.forgotButton)
    void onForgotPassword() {
        Intent intent = new Intent(SignActivity.this, ForgotPassActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @OnClick(R.id.ivImage)
    void choosePhoto() {
        selectImage();
    }
    @OnClick(R.id.datePickerButton)
    void selectDate() {
        showDialog(0);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, mYear, mMonth, mDay);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            mDay = selectedDay;
            mMonth = selectedMonth;
            mYear = selectedYear;
            dateStr = selectedDay + "/" + (selectedMonth + 1) + "/"
                    + selectedYear;
            datePickerButton.setText(dateStr);
        }
    };

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SignActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utils.checkPermission(SignActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                mUri = Uri.fromFile(photoFile);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }
        }
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //FaceBook
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                mUri = data.getData();
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mUri);

                bm = rotateImageIfRequired(bm, mUri);
                bm = getResizedBitmap(bm, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profileBitmap = bm;
        ivImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bm=null;
        try {

            // bimatp factory
//            BitmapFactory.Options options = new BitmapFactory.Options();
//
//            // downsizing image as it throws OutOfMemory Exception for larger
//            // images
//            options.inSampleSize = 8;
//
//            final Bitmap bitmap = BitmapFactory.decodeFile(mUri.getPath(),
//                    options);
//
//            ivImage.setImageBitmap(bitmap);

            bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mUri);

//            bm = rotateImageIfRequired(bm, mUri);
            bm = getResizedBitmap(bm, 500);

            ivImage.setImageBitmap(bm);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }


    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}
