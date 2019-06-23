package com.example.dell.shoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {


    private String CategoryName,Description,Price,Pname,saveCureentDate,saveCurrentTime;
    private Button AddNewProductButton;
    private TextView InputProductName,InputProductDescription,InputProductPrice;
    private ImageView InputProductImage;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String ProductRandomKey,downloadImageUrl;
    private StorageReference ProductImageRef;

    private DatabaseReference ProductRefrence;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);


        CategoryName = getIntent().getExtras().get("category").toString();

        AddNewProductButton = findViewById(R.id.add_new_product);
        InputProductImage = findViewById(R.id.select_product_Image);
        InputProductName = findViewById(R.id.product_name);
        InputProductDescription = findViewById(R.id.product_description);
        InputProductPrice = findViewById(R.id.product_price);

        loadingBar = new ProgressDialog(this);

        ProductImageRef = FirebaseStorage.getInstance().getReference().child("product images");

        ProductRefrence = FirebaseDatabase.getInstance().getReference().child("Products");

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }


    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!=null){
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData() {
         Description = InputProductDescription.getText().toString();
         Pname = InputProductName.getText().toString();
         Price = InputProductPrice.getText().toString();

         if(ImageUri == null){
             Toast.makeText(this, "Product image is mandatory", Toast.LENGTH_SHORT).show();
         }
         else if(TextUtils.isEmpty(Description)){
             Toast.makeText(this, "Product Description is mandatory", Toast.LENGTH_SHORT).show();
         }
         else if(TextUtils.isEmpty(Price)){
             Toast.makeText(this, "Product Price is mandatory", Toast.LENGTH_SHORT).show();
         }
         else if(TextUtils.isEmpty(Pname)){
             Toast.makeText(this, "Product Product name is mandatory", Toast.LENGTH_SHORT).show();
         }else{
             StoreProductInformation();
         }
    }

    private void StoreProductInformation() {
        loadingBar.setTitle("Adding product");
        loadingBar.setMessage("Please Wait, while we are adding the product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currenDate = new SimpleDateFormat("MM ,dd , yyyy");
        saveCureentDate = currenDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        ProductRandomKey = saveCureentDate + saveCurrentTime;

        final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + ProductRandomKey + ".jpeg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        })

         .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 Toast.makeText(AdminAddNewProductActivity.this, "Image Uploaded succesfully", Toast.LENGTH_SHORT).show();

                 Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                     @Override
                     public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                         if (!task.isSuccessful()) {
                             throw task.getException();
                         }
                         downloadImageUrl = filePath.getDownloadUrl().toString();

                         return filePath.getDownloadUrl();

                     }
                 })
                  .addOnCompleteListener(new OnCompleteListener<Uri>() {
                      @Override
                      public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AdminAddNewProductActivity.this, "got product image Url succesfully", Toast.LENGTH_SHORT).show();

                            SaveInfoToDatabase();
                        }
                      }
                  });

             }
         });
    }

    private void SaveInfoToDatabase() {
        HashMap<String , Object> productMap = new HashMap<>();
        productMap.put("pid",ProductRandomKey);
        productMap.put("date",saveCureentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("price",Price);
        productMap.put("pname",Pname);

        ProductRefrence.child(ProductRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "prosuct is added succesfully", Toast.LENGTH_SHORT).show();


                        }else{
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
