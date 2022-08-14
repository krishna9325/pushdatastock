package krishnaapps.com.pushdatastockbreakout.indices;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;

import krishnaapps.com.pushdatastockbreakout.R;
import krishnaapps.com.pushdatastockbreakout.intradaypush.IntraDayActivity;
import krishnaapps.com.pushdatastockbreakout.intradaypush.IntraDayDisplayActivity;
import krishnaapps.com.pushdatastockbreakout.modules.Indices;
import krishnaapps.com.pushdatastockbreakout.modules.IntraDay;

public class IndicesActivity extends AppCompatActivity {

    private Button indicesButtonChooseImage;
    private Button indicesButtonUpload;
    private TextView indicesTextViewShowUploads;
    private EditText indicesEditTextFileName, indicesEditTextDate, indicesEditTextDesc, indicesEditTextUid;
    private ImageView indicesImageView;
    private ProgressBar indicesProgressBar;

    ActivityResultLauncher<Intent> imageSetCrop;
    ActivityResultLauncher<Intent> imagePickGalleryLauncher;

    SharedPreferences sharedPreferences;

    private Uri indicesImageUri;

    private StorageReference mStorageRef;
    private FirebaseFirestore db;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_indices);

        indicesButtonChooseImage = findViewById(R.id.indices_button_choose_image);
        indicesButtonUpload = findViewById(R.id.indices_button_upload);
        indicesTextViewShowUploads = findViewById(R.id.indices_text_view_show_uploads);
        indicesEditTextFileName = findViewById(R.id.indices_edit_text_file_name);
        indicesEditTextDate = findViewById(R.id.indices_edit_text_file_date);
        indicesEditTextDesc = findViewById(R.id.indices_edit_text_file_desc);
        indicesImageView = findViewById(R.id.indices_image_view);
        indicesProgressBar = findViewById(R.id.indices_progress_bar);
        indicesEditTextUid = findViewById(R.id.indices_edit_text_file_uid);

        imageSetCrop = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    CropImage.ActivityResult resultCrop = CropImage.getActivityResult(data);
                    if (result.getResultCode() == RESULT_OK && resultCrop != null) {
                        indicesImageUri =  resultCrop.getUri();
                        Glide.with(this)
                                .load(indicesImageUri)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(indicesImageView);
                    }
                });

        imagePickGalleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        Uri imageUri;
                        if (data != null) {
                            imageUri = data.getData();
                            launchCropImage(imageUri);

                        }
                    }
                });

        // Storing data into SharedPreferences
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        indicesEditTextUid.setText(String.valueOf(sharedPreferences.getInt("indices_id", 0)));


        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        db = FirebaseFirestore.getInstance();

        indicesButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                imagePickGalleryLauncher.launch(photoPickerIntent);
            }
        });

        indicesButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(IndicesActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        indicesTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (indicesImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(indicesImageUri));


            mUploadTask = fileReference.putFile(indicesImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    indicesProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(IndicesActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();
//                            String id = db.collection("uploads").document().getId();

                            Indices indices = new Indices(indicesEditTextFileName.getText().toString().trim(), url.toString(),
                                    Integer.parseInt(indicesEditTextUid.getText().toString()), indicesEditTextDesc.getText().toString().trim(),
                                    indicesEditTextDate.getText().toString().trim());

                            db.collection("indicesdb").document(indicesEditTextUid.getText().toString()).set(indices);

                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putInt("indices_id", Integer.parseInt(indicesEditTextUid.getText().toString()) + 1);
                            myEdit.commit();

                            indicesEditTextUid.setText(String.valueOf(sharedPreferences.getInt("indices_id", 0)));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(IndicesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            indicesProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, IndicesDisplayActivity.class);
        startActivity(intent);
    }

    private Bitmap getScaledBitmap(Uri selectedImage) throws
            FileNotFoundException {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        System.out.println("Height width is: " + height + " " + width);

        if (height > 800 || width > 800) {
            // Calculate ratios of height and width to requested one
            final int heightRatio = Math.round((float) height / (float) 800);
            final int widthRatio = Math.round((float) width / (float) 800);

            // Choose the smallest ratio as inSampleSize value
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        return inSampleSize;
    }

    private void launchCropImage(Uri uri) {

        Intent i = CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE).getIntent(getApplicationContext());

        imageSetCrop.launch(i);

    }


}