package com.example.dell.inventorystage2;

import android.database.Cursor;
import android.os.Bundle;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.dell.inventorystage2.data.ProductContract.ProductEntry;

public class BookStatus extends CursorAdapter{

    public BookStatus(Context context, Cursor c) {
        super(context, c, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView productUniqueId = view.findViewById(R.id.display_name);
        TextView productISBN = view.findViewById(R.id.display_subject);

        int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int productSubjectColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUBJECT);
        int productUniqueColumnIndex = cursor.getColumnIndex(ProductEntry._ISBN);
        int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);



        TextView productNameTextView = view.findViewById(R.id.display_name);
        TextView productSubjectTextView = view.findViewById(R.id.display_subject);
        TextView productPriceTextView = view.findViewById(R.id.display_price);
        TextView productUniqueTextView = view.findViewById(R.id.uniqueId);

        String productName = cursor.getString(productNameColumnIndex);
        String productSubject = cursor.getString(productSubjectColumnIndex);
        String productPrice = cursor.getString(productPriceColumnIndex);
        String productUnique1 = cursor.getString(productUniqueColumnIndex);


        productNameTextView.setText(productName);
        productSubjectTextView.setText(productSubject);
        productPriceTextView.setText(String.valueOf(productPrice));
        productUniqueTextView.setText(productUnique1);


    }


}

