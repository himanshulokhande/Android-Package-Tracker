package com.example.fedexpackagetracker;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.*;

public class MainActivity extends AppCompatActivity {

    Button reset, create;
    EditText sender,receiver,weight,quantity,comment;
    Switch signatureswitch;
    Spinner src,dest,packagetype;
    FeedReaderDbHelper dbHelper;
    SQLiteDatabase db;
    private Handler handler;
    static BlockingQueue<Shortest> bbcp = new ArrayBlockingQueue<>(10);
    static ExecutorService pool = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        open();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent track = new Intent(view.getContext(),TrackActivity.class);
                startActivityForResult(track,0);
            }
        });

        sender=findViewById(R.id.sNameLabel);
        receiver=findViewById(R.id.receiverNameText);
        weight=findViewById(R.id.weightText);
        quantity=findViewById(R.id.quantityText);
        comment=findViewById(R.id.commentsText);
        signatureswitch=findViewById(R.id.main_switch);
        src=findViewById(R.id.srcSpinner);
        dest=findViewById(R.id.destSpinner);
        packagetype=findViewById(R.id.packageTypeSpinner);
        reset=findViewById(R.id.reset);
        create=findViewById(R.id.create);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if(msg.getData().getString("status").equalsIgnoreCase("insert")){
                    insertCenter((Vertex) msg.getData().getSerializable("vertex"), msg.getData().getInt("trackId"));
                }else if(msg.getData().getString("status").equalsIgnoreCase("update")){
                    updateCenter((Vertex) msg.getData().getSerializable("vertex"), msg.getData().getInt("trackId"));
                }
                return true;
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sender.setText("");
                receiver.setText("");
                weight.setText("");
                quantity.setText("");
                comment.setText("");
                signatureswitch.setChecked(false);
                src.setSelection(0);
                dest.setSelection(0);
                packagetype.setSelection(0);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Timestamp d = new Timestamp(new Date().getTime());
                PackageModel pm = new PackageModel(sender.getText().toString(),receiver.getText().toString(),src.getSelectedItem().toString(),
                        dest.getSelectedItem().toString(),packagetype.getSelectedItem().toString(),weight.getText().toString(),
                        quantity.getText().toString(),signatureswitch.isChecked(),comment.getText().toString(),d,d);


                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.Weight,pm.getWeight());
                values.put(FeedReaderContract.FeedEntry.Signature_Required,pm.isSignatureRequired());
                values.put(FeedReaderContract.FeedEntry.Package_Type,pm.getPackageType());
                values.put(FeedReaderContract.FeedEntry.Quantity,pm.getQuantity());
                values.put(FeedReaderContract.FeedEntry.Sender_Name,pm.getSenderName());
                values.put(FeedReaderContract.FeedEntry.Receiver_Name,pm.getReceiverName());
                values.put(FeedReaderContract.FeedEntry.Source,pm.getSource());
                values.put(FeedReaderContract.FeedEntry.Destination,pm.getDestination());
                values.put(FeedReaderContract.FeedEntry.Comments,pm.getSpecialServices());
                values.put(FeedReaderContract.FeedEntry.Create_Date,pm.getCreatedDate().toString());
                values.put(FeedReaderContract.FeedEntry.Update_Date,pm.getUpdatedDate().toString());

                long newRowId= db.insert(FeedReaderContract.FeedEntry.TABLE1,null,values);
                pm.setTrackingId((int)newRowId);
                Runnable shortestRunnableObject = new Shortest(pm, handler);
                pool.execute(shortestRunnableObject);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Tracking Number");
                alertDialog.setMessage("Tracking Number is : "+ newRowId);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void insertCenter(Vertex vertex, int trackingId) {


        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.Tracking_Number,trackingId);
        values.put(FeedReaderContract.FeedEntry.Center_Name,vertex.name);
        values.put(FeedReaderContract.FeedEntry.Arrival_Time,new Timestamp(new Date().getTime()).toString());
        values.put(FeedReaderContract.FeedEntry.Departure_Time,new Timestamp(new Date().getTime()).toString());
        db.insert(FeedReaderContract.FeedEntry.TABLE2,null,values);

    }

    public void updateCenter(Vertex vertex, int trackingId) {

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.Departure_Time,new Timestamp(new Date().getTime()).toString());
        db.update(FeedReaderContract.FeedEntry.TABLE2,values,"tracking_number =? and center_name = ?",new String[]{String.valueOf(trackingId),vertex.name});

    }

    public void open(){
        dbHelper = new FeedReaderDbHelper(MainActivity.this);
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }
}
