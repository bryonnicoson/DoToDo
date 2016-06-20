package com.bryonnicoson.dotodo;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    DBAdapter myDb;
    EditText etTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTask = (EditText)findViewById(R.id.et_Task);
        openDB();
        populate_ListView();
        listViewItemClick();
        listViewItemLongClick();
    }

    private void openDB() {
        myDb = new DBAdapter(this);
        myDb.open();
    }

    public void onClick_AddTask(View v) {
        if(!TextUtils.isEmpty(etTask.getText().toString())) {
            myDb.insertRow(etTask.getText().toString(), getDate());
        }
        populate_ListView();
    }

    private void populate_ListView() {
        // get all rows from db, create arrays to hold data
        Cursor cursor = myDb.getAllRows();
        String[] fromDb = new String[] {DBAdapter.KEY_DATE, DBAdapter.KEY_TASK};
        int[] toView = new int[] {R.id.tv_itemDate, R.id.tv_itemTask};

        // create adapter to connect data to listview
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.item_layout, cursor, fromDb, toView, 0);
        ListView myList = (ListView)findViewById(R.id.listView_Tasks);
        myList.setAdapter(myCursorAdapter);
        etTask.setText("");
    }

    private void updateTask(long id) {
        Cursor cursor = myDb.getRow(id);
        if(cursor.moveToFirst()) {
            String task = etTask.getText().toString();
            myDb.updateRow(id, task, getDate());
        }
        cursor.close();
    }

    private void listViewItemClick() {
        ListView myList = (ListView)findViewById(R.id.listView_Tasks);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateTask(id);
                populate_ListView();
            }
        });
    }

    private void listViewItemLongClick() {
        ListView myList = (ListView)findViewById(R.id.listView_Tasks);
        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                myDb.deleteRow(id);
                populate_ListView();

                return false;
            }
        });
    }

    public void onClick_DeleteTasks(View v) {
        myDb.deleteAll();
        populate_ListView();
    }

    public String getDate(){
        TimeZone tz = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(tz);
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = myFormat.format(calendar.getTime());
        return date;
    }
}
