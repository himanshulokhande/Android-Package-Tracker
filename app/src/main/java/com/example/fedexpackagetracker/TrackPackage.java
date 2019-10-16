package com.example.fedexpackagetracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class TrackPackage {
	
	private int trackingId;
	private FeedReaderDbHelper dbHelper;
	private SQLiteDatabase db;
	private PackageModel packageModel;
	private PackageShipModel packageShipModel;
	private Context context;
	
	public TrackPackage() {
		
	}
	public TrackPackage(int id, Context context) {
		this.trackingId = id;
		this.context = context;
	}

	public PackageModel getShippingDetails() {
		open();
			String[] projection = {
					FeedReaderContract.FeedEntry.Sender_Name,
					FeedReaderContract.FeedEntry.Receiver_Name,
					FeedReaderContract.FeedEntry.Source,
					FeedReaderContract.FeedEntry.Destination,
					FeedReaderContract.FeedEntry.Weight,
					FeedReaderContract.FeedEntry.Package_Type,
					FeedReaderContract.FeedEntry.Quantity,
					FeedReaderContract.FeedEntry.Comments,
					FeedReaderContract.FeedEntry.Create_Date,
					FeedReaderContract.FeedEntry.Update_Date
			};
			String selection = FeedReaderContract.FeedEntry.Tracking_Number + " = ?";
			String[] selectionArgs = { String.valueOf(trackingId) };

			Cursor cursor = db.query(
					FeedReaderContract.FeedEntry.TABLE1,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					null
			);

			while ( cursor.moveToNext()) {
				packageModel = new PackageModel();
				packageModel.setTrackingId(trackingId);
				packageModel.setSource(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Source)));
				packageModel.setDestination(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Destination)));
				packageModel.setWeight(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Weight)));
				packageModel.setPackageType(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Package_Type)));
				packageModel.setSenderName(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Sender_Name)));
				packageModel.setReceiverName(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Receiver_Name)));
				packageModel.setQuantity(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Quantity)));
				packageModel.setSpecialServices(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Comments)));
			}
			
			ArrayList<PackageShipModel> list = new ArrayList<>();
			String[] projection2 = {
					FeedReaderContract.FeedEntry.Center_Name,
					FeedReaderContract.FeedEntry.Arrival_Time,
					FeedReaderContract.FeedEntry.Departure_Time
			};

			String selection2 = FeedReaderContract.FeedEntry.Tracking_Number + " = ?";
			String[] selectionArgs2 = { String.valueOf(trackingId) };
			String sortOrder = FeedReaderContract.FeedEntry.Arrival_Time + " DESC";

			Cursor cursor2 = db.query(
					FeedReaderContract.FeedEntry.TABLE2,
					projection2,
					selection2,
					selectionArgs2,
					null,
					null,
					sortOrder
			);

			while(cursor2.moveToNext()){
				PackageShipModel packageShipModel = new PackageShipModel();
				packageShipModel.setCenterName(cursor2.getString(cursor2.getColumnIndex(FeedReaderContract.FeedEntry.Center_Name)));
				Timestamp at = Timestamp.valueOf(cursor2.getString(cursor2.getColumnIndex(FeedReaderContract.FeedEntry.Arrival_Time)));
				packageShipModel.setArrivalTime(at);
				Timestamp dt = Timestamp.valueOf(cursor2.getString(cursor2.getColumnIndex(FeedReaderContract.FeedEntry.Departure_Time)));
				packageShipModel.setDepartureTime(dt);
				list.add(packageShipModel);
			}
			packageModel.setShippingList(list);

		return packageModel;
		
	}
	public void open(){
		dbHelper = new FeedReaderDbHelper(context);
		db = dbHelper.getReadableDatabase();
	}
}
