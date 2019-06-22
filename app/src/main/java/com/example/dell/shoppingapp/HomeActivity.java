package com.example.dell.shoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private Button Logoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Logoutbtn = findViewById(R.id.Logout);

        final ProgressDialog loadingbar = new ProgressDialog(this);

        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Paper.book().destroy();

                Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(intent);
                loadingbar.dismiss();
            }
        });
    }
}
