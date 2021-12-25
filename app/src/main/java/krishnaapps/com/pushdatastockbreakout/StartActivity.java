package krishnaapps.com.pushdatastockbreakout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import krishnaapps.com.pushdatastockbreakout.intradaypush.MainActivity;

public class StartActivity extends AppCompatActivity {

    Button intraDay, swingTrade, indicesTrade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        intraDay = findViewById(R.id.intra_day);
        swingTrade = findViewById(R.id.swing);
        indicesTrade = findViewById(R.id.indices);

        intraDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}