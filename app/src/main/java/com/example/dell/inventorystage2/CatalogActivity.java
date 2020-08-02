package com.example.dell.inventorystage2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.dell.inventorystage2.data.ProductContract.ProductEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    RelativeLayout emptyView;
    ListView productsListView;
    ProductCursorAdapter adapter;
    SearchView searchView;
    String filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        ImageButton fab = findViewById(R.id.insert_product_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        productsListView = findViewById(R.id.list_view_products);
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filter = s;
                getLoaderManager().restartLoader(PRODUCT_LOADER,null,CatalogActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Toast.makeText(CatalogActivity.this, s, Toast.LENGTH_SHORT).show();
                filter = s;
                getLoaderManager().restartLoader(PRODUCT_LOADER,null,CatalogActivity.this);
                return true;
            }
        });
        emptyView = findViewById(R.id.empty_view);
        productsListView.setEmptyView(emptyView);

        adapter = new ProductCursorAdapter(this, null);
        productsListView.setAdapter(adapter);

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent i = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                i.setData(currentProductUri);
                startActivity(i);
            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void deleteAllProducts() {

        int rowsDeleted = 0;

        rowsDeleted = getContentResolver().delete(
                ProductEntry.CONTENT_URI,
                null,
                null
        );
        if (rowsDeleted == 0) {

            Toast.makeText(this, R.string.error_while_deleting_products,
                    Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, R.string.all_products_deleted,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {

        if(!(emptyView.getVisibility() == View.VISIBLE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_all_products);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteAllProducts();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
            default:
                return false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
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
        // This loader will execute the ContentProvider's query method on a background thread
        if(filter == null) {
            return  new CursorLoader(this,
                    ProductEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }
        else {
            return  new CursorLoader(this,
                    ProductEntry.CONTENT_URI,
                    projection,
                    ProductEntry.COLUMN_PRODUCT_NAME + " like '%" + filter + "%'",
                    null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        adapter.swapCursor(null);
    }
}
