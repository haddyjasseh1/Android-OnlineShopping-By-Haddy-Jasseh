package com.example.dijaonlineshop.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dijaonlineshop.MainActivity;
import com.example.dijaonlineshop.R;
import com.example.dijaonlineshop.model.UserModel;
import com.example.dijaonlineshop.tools.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;

import static com.example.dijaonlineshop.tools.Utilities.get_logged_in_user;

public class SignUpActivity extends AppCompatActivity {
    UserModel loggedInUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        bind_views();
        loggedInUser = Utilities.get_logged_in_user();
        if (loggedInUser != null) {
            Toast.makeText(this, "You are  logged in already ", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }
    }
    EditText firstName, lastName,email,contact,password,address;
    Button create_button;
    private void bind_views() {
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        contact = findViewById(R.id.contact);
        address = findViewById(R.id.address);
        create_button = findViewById(R.id.create_button);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_data();
            }
        });



    }

    UserModel new_user = new UserModel();

    private void validate_data() {
        new_user.first_name = firstName.getText().toString();
        if (new_user.first_name.length()<2){
            Toast.makeText(this, "First name is too short", Toast.LENGTH_SHORT).show();
            firstName.requestFocus();
            return;
        }

        new_user.last_name = lastName.getText().toString();
        if (new_user.last_name.length()<2){
            Toast.makeText(this, "Last name is too short", Toast.LENGTH_SHORT).show();
            lastName.requestFocus();
            return;
        }
        new_user.email = email.getText().toString();
        if (new_user.email.length() < 2){
            Toast.makeText(this, "Email name is empty", Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return;
        }
        new_user.passWord = password.getText().toString();
        if (new_user.passWord.length() < 4){
            Toast.makeText(this, "Password cant be empty", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return;
        }
        new_user.address = address.getText().toString();
        new_user.contact = contact.getText().toString();
        new_user.gender = "";
        new_user.user_type = "Customer";
        new_user.prof_photo = "";

        submit_user();
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String USERS_TABLE = "users";
    ProgressDialog progressDialog;

    private void submit_user(){

        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please Wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection(USERS_TABLE).whereEqualTo("email", new_user.email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            Toast.makeText(SignUpActivity.this, "User already Exists", Toast.LENGTH_LONG).show();
                            progressDialog.hide();
                            progressDialog.dismiss();
                            return;
                        }
                        new_user.user_id = db.collection(USERS_TABLE).document().getId();
                        new_user.reg_date = String.valueOf(Calendar.getInstance().getTimeInMillis() + "");
                        db.collection(USERS_TABLE).document(new_user.user_id).set(new_user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignUpActivity.this, "User Created Success", Toast.LENGTH_LONG).show();
                                        progressDialog.hide();
                                        progressDialog.dismiss();
                                        if ( login_user()){
                                            Toast.makeText(SignUpActivity.this, "Your logged in successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent= new Intent(SignUpActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            SignUpActivity.this.startActivity(intent);
                                            return;
                                        }else{
                                            Toast.makeText(SignUpActivity.this, "Failed to login in ", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "Failed to SignUp"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Failed to SignUp"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.hide();
                progressDialog.dismiss();
            }
        });
    }

    private static final String TAG = "SignUpActivity";
    private boolean login_user() {
        try {
            UserModel.save(new_user);
            return true;
        }catch (Exception e){
            Log.d(TAG, "login_user:  user failed to saved " + e.getMessage());
            return false;

        }
    }
}
/*Toast.makeText(this, "Logged in new User............", Toast.LENGTH_SHORT).show();
        new_user.user_id = "1";
        new_user.reg_date = String.valueOf(Calendar.getInstance().getTimeInMillis()+"");*/