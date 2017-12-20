package com.wugi.inc.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import com.wugi.inc.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

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

    private boolean isSignup;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private int mYear, mMonth, mDay;
    private FirebaseAuth mAuth;
    public User user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Bitmap profileBitmap;
    private Uri mUri;
    private String dateStr;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        ButterKnife.bind(this);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        Bundle extras = getIntent().getExtras();
        boolean isSignup = extras.getBoolean("isSignup");
        this.isSignup = isSignup;

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
    }

    void initView(boolean isSignup) {
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
            dateStr = selectedDay + "-" + (selectedMonth + 1) + "-"
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
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
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profileBitmap = bm;
        ivImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            mUri = data.getData();
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        profileBitmap = thumbnail;
        ivImage.setImageBitmap(thumbnail);
    }
}
