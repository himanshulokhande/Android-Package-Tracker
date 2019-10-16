package com.example.fedexpackagetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class TrackActivity extends AppCompatActivity {

    private FeedReaderDbHelper dbHelper;
    private SQLiteDatabase db;
    private PackageModel pm;
    EditText trackingNo;
    Button track;
    TextView src, dest, sender, receiver, wt, qty, comment, ptype;
    private RecyclerView recyclerView;
    private ArrayList<PackageShipModel> trackList = new ArrayList<>();
    private TrackingListAdapter trackingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        open();
        trackingNo= findViewById(R.id.trackPackageText);
        track= findViewById(R.id.trackPackageButton);
        recyclerView = findViewById(R.id.track_List);
        recyclerView.setLayoutManager(new LinearLayoutManager(TrackActivity.this));

        src = findViewById(R.id.sText);
        dest = findViewById(R.id.dText);
        sender = findViewById(R.id.sNameText);
        receiver = findViewById(R.id.rNameText);
        wt = findViewById(R.id.wText);
        qty = findViewById(R.id.qText);
        comment = findViewById(R.id.cText);
        ptype = findViewById(R.id.pText);

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TrackPackage t = new TrackPackage(Integer.parseInt(trackingNo.getText().toString()), TrackActivity.this);
                pm = t.getShippingDetails();
                src.setText(pm.getSource());
                dest.setText(pm.getDestination());
                sender.setText(pm.getSenderName());
                receiver.setText(pm.getReceiverName());
                wt.setText(pm.getWeight());
                qty.setText(pm.getQuantity());
                comment.setText(pm.getSpecialServices());
                ptype.setText(pm.getPackageType());
                trackList = pm.getShippingList();
                trackingListAdapter = new TrackingListAdapter(TrackActivity.this, trackList);
                recyclerView.setAdapter(trackingListAdapter);
            }
        });
    }

    public void open(){
        dbHelper = new FeedReaderDbHelper(TrackActivity.this);
        db = dbHelper.getReadableDatabase();
    }

    public void close(){
        dbHelper.close();
    }
}
