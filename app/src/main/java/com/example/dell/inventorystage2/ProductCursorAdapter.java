package com.example.dell.inventorystage2;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.inventorystage2.data.ProductContract.ProductEntry;

import java.util.ArrayList;

public class ProductCursorAdapter extends CursorAdapter{

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView productNameTextView = view.findViewById(R.id.display_name);
        TextView productSubjectTextView = view.findViewById(R.id.display_subject);
        TextView productUniqueTextView = view.findViewById(R.id.uniqueId);
        TextView productPriceTextView = view.findViewById(R.id.display_price);

        int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int productSubjectColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUBJECT);
        int productUniqueColumnIndex = cursor.getColumnIndex(ProductEntry._ISBN);
        int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

        final String productName = cursor.getString(productNameColumnIndex);
        String productSubject = cursor.getString(productSubjectColumnIndex);
        String uniqueId1 = cursor.getString(productUniqueColumnIndex);
        String productPrice = cursor.getString(productPriceColumnIndex);

        productNameTextView.setText(productName);
        productSubjectTextView.setText(productSubject);
        productUniqueTextView.setText(uniqueId1);
        productPriceTextView.setText(productPrice);

    }
}
