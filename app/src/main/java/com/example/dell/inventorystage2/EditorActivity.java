package com.example.dell.inventorystage2;
import com.example.dell.inventorystage2.data.ProductContract.ProductEntry;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.inventorystage2.data.ProductContract.ProductEntry;
import com.example.dell.inventorystage2.data.ProductDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int MINIMUM_QUANTITY_VALUE = 0;

    private final int MAXIMUM_QUANTITY_VALUE = 999;


    private boolean productHasChanged = false;

    private String supplierContact;

    private static final int EXISTING_PRODUCT_LOADER = 1;

    private Uri currentProductUri;

    private EditText productNameEditText;

    private EditText productSubjectEditText;

     private EditText productPriceEditText;

    private EditText productQuantityEditText;

    private EditText productBorrowedEditText;

    private EditText supplierNameEditText;

    private EditText supplierUniqueBookEditText;

    private Button subtractQuantityButton;

    private Button addQuantityButton;

    private Button borrowButton;

    private Button returnButton;

    public ProductDbHelper dbHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        if(currentProductUri == null){

            setTitle(getString(R.string.add_a_product));

            invalidateOptionsMenu();
        }else{

            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        productNameEditText = findViewById(R.id.product_name);
        productSubjectEditText = findViewById(R.id.product_subject);
        productPriceEditText = findViewById(R.id.product_price);
        productQuantityEditText = findViewById(R.id.product_quantity);
        productBorrowedEditText = findViewById(R.id.product_borrow);
        supplierNameEditText = findViewById(R.id.supplier_name);
        supplierUniqueBookEditText = findViewById(R.id.uniquebook);
        subtractQuantityButton = findViewById(R.id.subtract_quantity);
        addQuantityButton = findViewById(R.id.add_quantity);
        borrowButton=findViewById(R.id.borrow1);
        returnButton=findViewById(R.id.return1);
        subtractQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantityString = productQuantityEditText.getText().toString();
                int currentQuantityInt;
                if(currentQuantityString.length() == 0){
                    currentQuantityInt = 0;
                    productQuantityEditText.setText(String.valueOf(currentQuantityInt));
                }else{
                    currentQuantityInt = Integer.parseInt(currentQuantityString) - 1;
                    if(currentQuantityInt >=MINIMUM_QUANTITY_VALUE) {
                        productQuantityEditText.setText(String.valueOf(currentQuantityInt));
                    }
                }

            }
        });
        addQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantityString = productQuantityEditText.getText().toString();
                int currentQuantityInt;
                if(currentQuantityString.length() == 0){
                    currentQuantityInt = 1;
                    productQuantityEditText.setText(String.valueOf(currentQuantityInt));
                }else{
                    currentQuantityInt = Integer.parseInt(currentQuantityString) + 1;
                    if(currentQuantityInt<=MAXIMUM_QUANTITY_VALUE) {
                        productQuantityEditText.setText(String.valueOf(currentQuantityInt));
                    }
                }

            }
        });

        borrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentBorrowString = productBorrowedEditText.getText().toString();
                int currentBorrowInt=Integer.parseInt(currentBorrowString)+1;
                if(currentBorrowInt==1) {
                    productBorrowedEditText.setText(String.valueOf(currentBorrowInt));
                }
            }

        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentBorrowString = productBorrowedEditText.getText().toString();
                int currentBorrowInt=Integer.parseInt(currentBorrowString)-1;
                if(currentBorrowInt==0) {
                    productBorrowedEditText.setText(String.valueOf(currentBorrowInt));
                }
            }

        });



        dbHelper = new ProductDbHelper(this);

        productNameEditText.setOnTouchListener(mTouchListener);
        productSubjectEditText.setOnTouchListener(mTouchListener);
        productPriceEditText.setOnTouchListener(mTouchListener);
        productQuantityEditText.setOnTouchListener(mTouchListener);
        productBorrowedEditText.setOnTouchListener(mTouchListener);
        subtractQuantityButton.setOnTouchListener(mTouchListener);
        addQuantityButton.setOnTouchListener(mTouchListener);
        borrowButton.setOnTouchListener(mTouchListener);
        returnButton.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierUniqueBookEditText.setOnTouchListener(mTouchListener);



    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }

    };

    @Override
    public void onBackPressed() {

        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };


        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void saveProduct() {


        String productNameString = productNameEditText.getText().toString().trim();
        String productSubjectString = productSubjectEditText.getText().toString().trim();
        String productPriceString = productPriceEditText.getText().toString().trim();
        String productQuantityString = productQuantityEditText.getText().toString().trim();
        String productBorrowedString = productBorrowedEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierUniqueBookString = supplierUniqueBookEditText.getText().toString().trim();



        if (TextUtils.isEmpty(productNameString)) {
            productNameEditText.setError(getString(R.string.required));
            return;
        }

        if (TextUtils.isEmpty(productSubjectString)) {
            productSubjectEditText.setError(getString(R.string.required));
            return;
        }

        if (TextUtils.isEmpty(productPriceString)) {
            productPriceEditText.setError(getString(R.string.required));
            return;
        }
        if (TextUtils.isEmpty(productQuantityString)) {
            productQuantityEditText.setError(getString(R.string.required));
            return;
        }

        if (TextUtils.isEmpty(supplierNameString)) {
            supplierNameEditText.setError(getString(R.string.required));
            return;
        }
        if (TextUtils.isEmpty(supplierUniqueBookString)) {
            supplierUniqueBookEditText.setError(getString(R.string.required));
            return;
        }

        int productPriceInt = Integer.parseInt(productPriceString);
        int productQuantityInt = Integer.parseInt(productQuantityString);

        if (productPriceInt < 0) {
            productPriceEditText.setError(getString(R.string.price_cannot_be_negative));
            return;
        }
        if (productQuantityInt < 0) {
            productQuantityEditText.setError(getString(R.string.quantity_cannot_be_negative));
            return;
        }

        for(int i=0;i<productQuantityInt;i++)
        {
        ContentValues values = new ContentValues();


        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUBJECT, productSubjectString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPriceInt);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantityInt);
        values.put(ProductEntry.COLUMN_PRODUCT_BORROWED, productBorrowedString);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_UNIQUE_BOOK, supplierUniqueBookString);


        if (currentProductUri == null) {

            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);


            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_product_successful), Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowAffected = getContentResolver().update(currentProductUri, values, null, null);

            if (rowAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
        finish();
    }

    private void deleteProduct() {
        if (currentProductUri != null) {
            int rowsDeleted = 0;

            rowsDeleted = getContentResolver().delete(
                    currentProductUri,
                    null,
                    null
            );
            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.error_deleting_product),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.product_deleted),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.discard_changes_and_quit_editing));
        builder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep_editing), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_this_product));
        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void display(){
        Intent intent = new Intent(this, BookStatus.class);
        this.startActivity(intent);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentProductUri == null) {
            MenuItem menuItem;
            menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
//            menuItem = menu.findItem(R.id.action_borrow);
//            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_save:
                saveProduct();
                return true;

//            case R.id.action_borrow:
//                display();
//                break;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;

            case android.R.id.home:

                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };


                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ProductEntry._ISBN,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_SUBJECT,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_BORROWED,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_UNIQUE_BOOK,
        };

        return  new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor.moveToFirst()){


            int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int productSubjectColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUBJECT);
            int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int productBorrowedColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_BORROWED);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierContactColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_UNIQUE_BOOK);


            String productName = cursor.getString(productNameColumnIndex);
            String productSubject = cursor.getString(productSubjectColumnIndex);
            int productPrice = cursor.getInt(productPriceColumnIndex);
            int productQuantity = cursor.getInt(productQuantityColumnIndex);
            int productBorrowed = cursor.getInt(productBorrowedColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            supplierContact = cursor.getString(supplierContactColumnIndex);


            productNameEditText.setText(productName);
            productSubjectEditText.setText(productSubject);
            productPriceEditText.setText(String.valueOf(productPrice));
            productQuantityEditText.setText(String.valueOf(productQuantity));
            productBorrowedEditText.setText(String.valueOf(productBorrowed));
            supplierNameEditText.setText(String.valueOf(supplierName));
            supplierUniqueBookEditText.setText(String.valueOf(supplierContact));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        productNameEditText.setText("");
        productSubjectEditText.setText("");
        productPriceEditText.setText("");
        productQuantityEditText.setText("");
        productBorrowedEditText.setText("");
        supplierNameEditText.setText("");
        supplierUniqueBookEditText.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }


    private void displayDatabaseInfo() {
        dbHelper = new ProductDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String[] projection = {
                ProductEntry._ISBN,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_UNIQUE_BOOK,
                ProductEntry.COLUMN_PRODUCT_BORROWED
        };



        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = (TextView) findViewById(R.id.text_view_data);

        try {

            displayView.setText("There are  " + cursor.getCount() + " books."+" \n\n");
            displayView.append(
                    "unique id"+ " - " +
                    "name"+ " - " +
                    "id" + "-"+ "borrowed"+
                            "\n");

            int isbnColumnIndex = cursor.getColumnIndex(ProductEntry._ISBN);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int uniqueBookColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_UNIQUE_BOOK);
            int borrowedColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_BORROWED);

            while (cursor.moveToNext()) {

                String currentName = cursor.getString(nameColumnIndex);
                String currentISBN = cursor.getString(isbnColumnIndex);
                int currentUnique = cursor.getInt(uniqueBookColumnIndex);
                int currentBorrowed = cursor.getInt(borrowedColumnIndex);

                displayView.append(("\n" +currentUnique + " - " +
                        currentName + " - " + currentISBN + " - " +currentBorrowed));
            }
        } finally {
            cursor.close();
        }
    }

}