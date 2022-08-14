package krishnaapps.com.pushdatastockbreakout.swing;

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
import krishnaapps.com.pushdatastockbreakout.modules.Swing;

public class SwingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName, mEditTextDate, mEditTextDesc, mEditTextUid;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private ActivityResultLauncher<Intent> imageSetCrop;
    private ActivityResultLauncher<Intent> imagePickGalleryLauncher;

    private SharedPreferences sharedPreferences;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private FirebaseFirestore db;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swing);
        mButtonChooseImage = findViewById(R.id.swing_button_choose_image);
        mButtonUpload = findViewById(R.id.swing_button_upload);
        mTextViewShowUploads = findViewById(R.id.swing_text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.swing_edit_text_file_name);
        mEditTextDate = findViewById(R.id.swing_edit_text_file_date);
        mEditTextDesc = findViewById(R.id.swing_edit_text_file_desc);
        mImageView = findViewById(R.id.swing_image_view);
        mProgressBar = findViewById(R.id.swing_progress_bar);
        mEditTextUid = findViewById(R.id.swing_edit_text_file_uid);

        imageSetCrop = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    CropImage.ActivityResult resultCrop = CropImage.getActivityResult(data);
                    if (result.getResultCode() == RESULT_OK && resultCrop != null) {
                        mImageUri = resultCrop.getUri();
                        Glide.with(this)
                                .load(mImageUri)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(mImageView);
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
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        mEditTextUid.setText(String.valueOf(sharedPreferences.getInt("swing_id", 0)));


        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        db = FirebaseFirestore.getInstance();

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                imagePickGalleryLauncher.launch(photoPickerIntent);
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(SwingActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
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
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));


            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(SwingActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete()) ;
                            Uri url = uri.getResult();
//                            String id = db.collection("uploads").document().getId();

                            Swing upload = new Swing(mEditTextFileName.getText().toString().trim(), url.toString(),
                                    Integer.parseInt(mEditTextUid.getText().toString()), mEditTextDesc.getText().toString().trim(),
                                    mEditTextDate.getText().toString().trim());

                            db.collection("swingdb").document(mEditTextUid.getText().toString()).set(upload);

                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putInt("swing_id", Integer.parseInt(mEditTextUid.getText().toString()) + 1);
                            myEdit.commit();

                            mEditTextUid.setText(String.valueOf(sharedPreferences.getInt("swing_id", 0)));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SwingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, SwingDisplayActivity.class);
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


