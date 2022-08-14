package krishnaapps.com.pushdatastockbreakout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import krishnaapps.com.pushdatastockbreakout.indices.IndicesActivity;
import krishnaapps.com.pushdatastockbreakout.intradaypush.IntraDayActivity;
import krishnaapps.com.pushdatastockbreakout.swing.SwingActivity;

public class StartActivity extends AppCompatActivity {

    Button intraDay, swingTrade, indicesTrade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        intraDay = findViewById(R.id.intra_day);
        swingTrade = findViewById(R.id.swing);
        indicesTrade = findViewById(R.id.indices);

        indicesTrade.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), IndicesActivity.class)));

        swingTrade.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SwingActivity.class)));

        intraDay.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), IntraDayActivity.class)));
    }
}