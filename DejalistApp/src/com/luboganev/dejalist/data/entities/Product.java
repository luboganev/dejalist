package com.luboganev.dejalist.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
	public Long _id;
	public String name;
	public String uri;
	public int inlist;
	public int checked;
	public long categoryId;
	public int usedCount;
	public long lastUsed;
	
	// Variables for the parcable
		transient private static final int FLAG_SET_ID = 0x01;
		transient private static final int FLAG_SET_NAME = 0x02;
		transient private static final int FLAG_SET_URI = 0x04;
		
	    public int describeContents() {
	        return 0;
	    }

	    public void writeToParcel(Parcel out, int flags) {
	    	// set the field set flags
	    	int fieldsSetFlags = 0;
	    	if(_id != null) fieldsSetFlags = fieldsSetFlags | FLAG_SET_ID;
	    	if(name != null) fieldsSetFlags = fieldsSetFlags | FLAG_SET_NAME;
	    	if(uri != null) fieldsSetFlags = fieldsSetFlags | FLAG_SET_URI;
	    	out.writeInt(fieldsSetFlags);
	    	
	    	if((fieldsSetFlags & FLAG_SET_ID) != 0) out.writeLong(_id);
	    	if((fieldsSetFlags & FLAG_SET_NAME) != 0) out.writeString(name);
	    	if((fieldsSetFlags & FLAG_SET_URI) != 0) out.writeString(uri);
	    	out.writeInt(inlist);
	    	out.writeInt(checked);
	    	out.writeLong(categoryId);
	    	out.writeInt(usedCount);
	    	out.writeLong(lastUsed);
	    }
	    
//	    12414 in binary is:
//	    Binary number: 1  1  0  0  0  0  0  1  1  1  1  1  1  0
//	    -------------------------------------------------------
//	    Bit positions: 13 12 11 10 9  8  7  6  5  4  3  2  1  0
//	    bitmask = TRADEABLE | SELLABLE | STORABLE | STORABLE_IN_WH | STORABLE_IN_LEGION_WH | BREAKABLE | BLACK_CLOUD_TRADERS | CAN_SPLIT;
//	    if(bitmask & TRADEABLE != 0) {
//	        // This item can be traded
//	    } else {
//	        // This item cannot be traded
//	    }
//	    bitmask |= TRADEABLE; // Sets the flag using bitwise OR
//	    bitmask &= ~TRADEABLE; // Clears the flag using bitwise AND and NOT
//	    bitmask ^= TRADEABLE; // Toggles the flag using bitwise XOR 

	    public static final Parcelable.Creator<Product> CREATOR
	            = new Parcelable.Creator<Product>() {
	        public Product createFromParcel(Parcel in) {
	            return new Product(in);
	        }

	        public Product[] newArray(int size) {
	            return new Product[size];
	        }
	    };
	    
	    private Product(Parcel in) {
	    	int fieldsSetFlags = in.readInt();
	    	_id = ((fieldsSetFlags & FLAG_SET_ID) != 0) ? in.readLong() : null;
	    	name = ((fieldsSetFlags & FLAG_SET_NAME) != 0) ? in.readString() : null;
	    	uri = ((fieldsSetFlags & FLAG_SET_URI) != 0) ? in.readString() : null;
	    	inlist = in.readInt();
	    	checked = in.readInt();
	    	categoryId = in.readLong();
	    	usedCount = in.readInt();
	    	lastUsed = in.readLong();
	    }
	    
	    public Product() {
	    	_id = null;
	    	name = null;
	    	uri = null;
	    	inlist = 0;
	    	checked = 0;
	    	categoryId = -1;
	    	usedCount = 0;
	    	lastUsed = -1;
	    }
}
