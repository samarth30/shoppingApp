package com.example.dell.shoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputName,InputPhoneNumber,InputPassword;
    ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InputName = findViewById(R.id.register_name_input);
        InputPassword = findViewById(R.id.register_password_input);
        InputPhoneNumber = findViewById(R.id.register_phone_number_input);
        CreateAccountButton = findViewById(R.id.register_btn);
        loadingbar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }
    private void CreateAccount() {
     String name = InputName.getText().toString();
     String phone = InputPhoneNumber.getText().toString();
     String password = InputPassword.getText().toString();

     if(TextUtils.isEmpty(name)){
         Toast.makeText(this, "Please Enter some Text...", Toast.LENGTH_SHORT).show();
     }else if(TextUtils.isEmpty(phone)){
         Toast.makeText(this, "Please Enter some Phone number ...", Toast.LENGTH_SHORT).show();
     }else if(TextUtils.isEmpty(password)){
         Toast.makeText(this, "Please Enter some Text...", Toast.LENGTH_SHORT).show();
     }else{
         loadingbar.setTitle("Create Account");
         loadingbar.setMessage("Please Wait, while we are checking the credentials");
         loadingbar.setCanceledOnTouchOutside(false);
         loadingbar.show();

         ValidatePhoneNumber(name,phone,password);

     }

    }

    private void ValidatePhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("users").child(phone).exists())){
                    HashMap<String,Object> userdataMap = new HashMap<>();

                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);

                    RootRef.child("users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      Toast.makeText(RegisterActivity.this, "Congratulations your account has been created", Toast.LENGTH_SHORT).show();
                                      loadingbar.dismiss();

                                      Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                      startActivity(intent);
                                  }
                                  else{
                                      loadingbar.dismiss();
                                      Toast.makeText(RegisterActivity.this, "Network Error Plese Try Again", Toast.LENGTH_SHORT).show();
                                  }
                                }
                            });
                }else{
                    Toast.makeText(RegisterActivity.this,"this " + phone + " Phone number Allready Exists",Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
