package com.example.dijaonlineshop.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
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

import static com.example.dijaonlineshop.Activity.SignUpActivity.USERS_TABLE;

public class LoginInMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in_main);
        context = this;

        bind_views();
        UserModel loggedInUser = Utilities.get_logged_in_user();
        if (loggedInUser != null) {
            Toast.makeText(this, "You are logged in already ", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }
    }

    EditText email, password;
    Button login_button;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void bind_views() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_user();
            }
        });

    }

    String email_value = " ";
    String pass_value = " ";
    ProgressDialog progressDialog;
    Context context;

    private void login_user() {
        email_value = email.getText().toString().trim();
        pass_value = password.getText().toString().trim();

        if (email_value.isEmpty() || pass_value.isEmpty()) {
            Toast.makeText(this, "Plz fill both  Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection(USERS_TABLE).whereEqualTo("email", email_value)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots==null){
                            progressDialog.hide();
                            progressDialog.dismiss();
                            Toast.makeText(context,  "Email not found ", Toast.LENGTH_LONG).show();
                            return ;
                        }
                        if (queryDocumentSnapshots.isEmpty()){
                            progressDialog.hide();
                            progressDialog.dismiss();
                            Toast.makeText(context, "Email not found ", Toast.LENGTH_LONG).show();
                            return ;
                        }
                        List<UserModel> users = queryDocumentSnapshots.toObjects(UserModel.class);

                        if (!users.get(0).passWord.equals(pass_value)){
                            progressDialog.hide();
                            progressDialog.dismiss();
                            Toast.makeText(context, "Wrong password", Toast.LENGTH_LONG).show();
                            return;
                        }
                        progressDialog.hide();
                        progressDialog.dismiss();
                        if ( login_user(users.get(0))){
                            Toast.makeText(context, "" +"Your logged in successfully", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                            return;
                        }else{
                            Toast.makeText(context, "Failed to login in ", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginInMainActivity.this, "Failed to SignUp"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        progressDialog.dismiss();

                    }
                });



    }
    private boolean login_user(UserModel u) {
        try {
            UserModel.save(u);
            return true;
        }catch (Exception e){
            return false;

        }
    }
}