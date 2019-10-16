package com.example.fedexpackagetracker;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE1 = "package_details";
        public static final String Tracking_Number = "tracking_number";
        public static final String Weight = "weight";
        public static final String Signature_Required = "signature_required";
        public static final String Package_Type = "package_type";
        public static final String Quantity = "quantity";
        public static final String Sender_Name = "senderName";
        public static final String Receiver_Name = "receiverName";
        public static final String Source = "source";
        public static final String Destination = "destination";
        public static final String Comments = "specialServices";
        public static final String Create_Date = "createDate";
        public static final String Update_Date = "updateDate";

        public static final String TABLE2 = "track_package";
        public static final String Center_Name = "center_name";
        public static final String Arrival_Time = "arrivalTime";
        public static final String Departure_Time = "departureTime";
    }

}