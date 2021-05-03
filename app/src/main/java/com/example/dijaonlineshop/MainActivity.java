package com.example.dijaonlineshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dijaonlineshop.Activity.LoginInMainActivity;
import com.example.dijaonlineshop.Activity.ProductAddActivity;
import com.example.dijaonlineshop.Activity.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void add_product(View view) {
        Intent i = new Intent(this, ProductAddActivity.class);
        startActivity(i);
    }
    public void signUp(View view) {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }

    public void loginIn(View view) {
        Intent i = new Intent(this, LoginInMainActivity.class);
        startActivity(i);
    }
}