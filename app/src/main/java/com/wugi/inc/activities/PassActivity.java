package com.wugi.inc.activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.wugi.inc.R;
import com.wugi.inc.utils.Utils;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_confirm)
    EditText et_confirm;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.button_save)
    Button button_save;

    private FirebaseAuth mAuth;
    private final static String TAG = "PasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        ButterKnife.bind(this);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.button_save)
    void onSave() {
        if (TextUtils.isEmpty(et_confirm.getText().toString())) {
            Toast.makeText(this, "Please input password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_password.getText().toString())) {
            Toast.makeText(this, "Please input confirm password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!et_confirm.getText().toString().equals(et_confirm.getText().toString())) {
            Toast.makeText(this, "Password doesn't match.", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = Utils.createProgressDialog(this);
        mAuth.getCurrentUser().updatePassword(et_confirm.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(PassActivity.this,
                            "Password has been updated",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error in updating passowrd",
                            task.getException());
                    Toast.makeText(PassActivity.this,
                            task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
