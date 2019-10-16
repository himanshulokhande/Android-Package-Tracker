package com.example.fedexpackagetracker;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShipPackage{
	List<Vertex> path = new ArrayList<Vertex>();
	private Handler handler;
	int trackingId;


	public ShipPackage() {

	}

	public ShipPackage(Handler hn, List<Vertex> path, int trackId) {
		this.handler = hn;
		this.trackingId = trackId;
		this.path = path;
	}
	public void start() {
		System.out.println("start thread inserting path in DB "+trackingId);
		try {
			for(int i=0; i<path.size();i++) {
				Bundle bundle = new Bundle();
				bundle.putString("status", "insert");
				bundle.putSerializable("vertex", path.get(i));
				bundle.putInt("trackId", trackingId);
				Message message = new Message();
				message.setData(bundle);
				handler.sendMessage(message);

				System.out.println("INSERT ");

				Thread.sleep(15000);

				Bundle bundle1 = new Bundle();
				bundle1.putString("status", "update");
				bundle1.putSerializable("vertex", path.get(i));
				bundle1.putInt("trackId", trackingId);
				Message message1 = new Message();
				message1.setData(bundle1);
				handler.sendMessage(message1);

				System.out.println("UPDATE ");
				Thread.sleep(15000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}

	/*public void insertCenter(Vertex vertex) {


			ContentValues values = new ContentValues();
			values.put(FeedReaderContract.FeedEntry.Tracking_Number,trackingId);
			values.put(FeedReaderContract.FeedEntry.Center_Name,vertex.name);
			values.put(FeedReaderContract.FeedEntry.Arrival_Time,new Timestamp(new Date().getTime()).toString());
			values.put(FeedReaderContract.FeedEntry.Departure_Time,new Timestamp(new Date().getTime()).toString());
			db.insert(FeedReaderContract.FeedEntry.TABLE2,null,values);

	}

	public void updateCenter(Vertex vertex) {

			ContentValues values = new ContentValues();
			values.put(FeedReaderContract.FeedEntry.Departure_Time,new Timestamp(new Date().getTime()).toString());
			db.update(FeedReaderContract.FeedEntry.TABLE2,values,"tracking_number =? and center_name = ?",new String[]{String.valueOf(trackingId),vertex.name});

	}*/



}
