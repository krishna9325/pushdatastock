package krishnaapps.com.pushdatastockbreakout.indices;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import krishnaapps.com.pushdatastockbreakout.adapter.ImageAdapter;
import krishnaapps.com.pushdatastockbreakout.adapter.IndicesAdapter;
import krishnaapps.com.pushdatastockbreakout.intradaypush.IntraDayDisplayActivity;
import krishnaapps.com.pushdatastockbreakout.modules.Indices;
import krishnaapps.com.pushdatastockbreakout.modules.IntraDay;

public class IndicesDisplayActivity extends AppCompatActivity implements IndicesAdapter.OnIndicesItemClickListener {
    private RecyclerView mRecyclerView;
    private IndicesAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;
    private FirebaseFirestore mDatabaseRef;

    private String TAG = "IndicesDisplayActivity";

    private List<Indices> mIndices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_indices_activity);

        mRecyclerView = findViewById(R.id.indices_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.indices_progress_circle);

        mIndices = new ArrayList<>();

        mAdapter = new IndicesAdapter(IndicesDisplayActivity.this, mIndices);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnIndicesItemClickListener(IndicesDisplayActivity.this);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseFirestore.getInstance();

        mDatabaseRef.collection("indicesdb")
                .orderBy("indicesKey", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            mIndices.add(dc.getDocument().toObject(Indices.class));
                        }
                        mAdapter.notifyDataSetChanged();
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
        Indices selectedItem = mIndices.get(position);
        final String selectedKey = String.valueOf(selectedItem.getIndicesKey());

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getIndicesImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.collection("indicesdb").document(selectedKey).delete();
                Toast.makeText(IndicesDisplayActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

}