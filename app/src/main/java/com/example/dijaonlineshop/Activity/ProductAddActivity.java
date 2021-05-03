 package com.example.dijaonlineshop.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dijaonlineshop.R;
import com.example.dijaonlineshop.model.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProductAddActivity extends AppCompatActivity {
     ImageButton btn_done;
     ImageView product_img;
     private final int PICK_IMAGE_REQUEST = 1;
     public final String PRODUCT_TABLE = "PRODUCTS";
     TextInputEditText ProductCategory;
     ProductModel new_product = new ProductModel();
     FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        bind_views();// you are busy listening to politics i finish watching lecture video


    }
    TextInputEditText product_name,product_details,product_price;
    private void bind_views() {
        new_product.product_id = db.collection(PRODUCT_TABLE).document().getId();
        btn_done = findViewById(R.id.btn_done);
        product_details = findViewById(R.id.product_details);
        product_price = findViewById(R.id.product_price);
        product_name = findViewById(R.id.product_name);
        product_img= findViewById(R.id.product_img);
        progressDialog = new ProgressDialog(this);
        ProductCategory = findViewById(R.id.ProductCategory);

        ProductCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_category();
            }
        });

        product_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImg();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                submit_product();
            }
        });
    }

    private void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select pic"),PICK_IMAGE_REQUEST);
      //  startActivityForResult(Intent.createChooser(intent),"Select photo of product",PICK_IMAGE_REQUEST);
    }

    public  static  final String[] Product_Categories = new String[]{
            "Textiles & Accessories","Electronics", "Bags, Shoes & Accessories","Health & Beauty","Home & Construction"
    };

    // you first make ure you are observing a right app in logcat
    // right emulator, right app. do you understand???? ??? respiyesnd???
    int select_category =-1;
    private void select_category(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Category");
        builder.setSingleChoiceItems(Product_Categories, select_category, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              ProductCategory.setText(Product_Categories[i]);
                select_category = i;//this line is triggered immediately after selection
                // even when the user has not clicked on okay,
                // so, you may use this section to update what the user has selected
                // so, on postive click, you may ignore it, because you have already used this secion
                // to update what the user selected.
                // do you understand? yes
                // it is now fine.
                // thanks
                // I am happy you are trying your best. keep pushing.
                // don't give up my dear.
                // nice day.  u too .
            }
        });
        builder.setPositiveButton("OK",null);
        builder.setNegativeButton("CANCEL",null);
        builder.show();
    }
    private Uri imagePath = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                product_img.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    ProgressDialog progressDialog;
    StorageReference ref_main;
    private  void upload(){
        Toast.makeText(this, "Upload here!", Toast.LENGTH_SHORT).show();
        db.collection(PRODUCT_TABLE).document(new_product.product_id).set(new_product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.hide();
                progressDialog.dismiss();
                Toast.makeText(ProductAddActivity.this, "Upload success!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.hide();
                Toast.makeText(ProductAddActivity.this, "Upload Failed!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }
    private void submit_product() {

        new_product.title = product_name.getText().toString();
        if (new_product.title.isEmpty()) {
            Toast.makeText(this, "Product name can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        new_product.category = ProductCategory.getText().toString();
        if (new_product.category.isEmpty()) {
            Toast.makeText(this, "Product category should be selected", Toast.LENGTH_SHORT).show();
            select_category();
            return;
        }
        if (product_price.getText().toString().isEmpty()) {
            Toast.makeText(this, "Product name can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new_product.pirce = Integer.valueOf(product_price.toString());
        } catch (Exception e) {

        }

        if (new_product.pirce < 0) {
            Toast.makeText(this, "Price name can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        new_product.details = product_details.getText().toString();
        if (imagePath == null) {
            Toast.makeText(this, "You must select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        ref_main = FirebaseStorage.getInstance().getReference();
        ref_main.child("products/" + new_product.product_id).putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProductAddActivity.this, "Finish uploading!", Toast.LENGTH_SHORT).show();
                ref_main.child("products/" + new_product.product_id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        new_product.image = uri.toString();
                        upload();
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new_product.image = "https://img.ltwebstatic.com/images3_pi/2020/12/02/1606889814fd20a9f65a9ebc599721ef0cdff0b23b.webp";
                        upload();
                        return;
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductAddActivity.this, "Failed to upload!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                new_product.image = "https://img.ltwebstatic.com/images3_pi/2020/12/02/1606889814fd20a9f65a9ebc599721ef0cdff0b23b.webp";
                upload();
                return;
            }
        });

    }
}

