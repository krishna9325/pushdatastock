package krishnaapps.com.pushdatastockbreakout.swing;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import krishnaapps.com.pushdatastockbreakout.R;
import krishnaapps.com.pushdatastockbreakout.adapter.SwingAdapter;
import krishnaapps.com.pushdatastockbreakout.modules.Swing;

public class SwingDisplayActivity extends AppCompatActivity implements SwingAdapter.OnSwingItemClickListener{

    private RecyclerView mRecyclerView;
    private SwingAdapter mSwingAdapter;

    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;
    private FirebaseFirestore mDatabaseRef;

    private String TAG = "ImagesSwingActivity";

    private List<Swing> mSwings;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_swing_activity);
        mRecyclerView = findViewById(R.id.swing_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.swing_progress_circle);

        mSwings = new ArrayList<>();

        mSwingAdapter = new SwingAdapter(SwingDisplayActivity.this, mSwings);

        mRecyclerView.setAdapter(mSwingAdapter);

        mSwingAdapter.setOnSwingItemClickListener(SwingDisplayActivity.this);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseFirestore.getInstance();
        mDatabaseRef.collection("swingdb")
                .orderBy("swingKey", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    System.out.println("jwfesdgfsfnhgt");
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            mSwings.add(dc.getDocument().toObject(Swing.class));
                            System.out.println("jwfe" + mSwings.get(0).getSwingName());
                        }
                        mSwingAdapter.notifyDataSetChanged();
                    }
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }

                if(error != null) {
                    Log.e(TAG, "Error while uploading data to database" + error);
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        Swing selectedItem = mSwings.get(position);
        final String selectedKey = String.valueOf(selectedItem.getSwingKey());

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getSwingImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.collection("swingdb").document(selectedKey).delete();
                Toast.makeText(SwingDisplayActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }


}