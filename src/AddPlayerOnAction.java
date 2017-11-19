package khodkov.michael.chipin;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AddPlayerOnAction extends AppCompatActivity implements View.OnClickListener{

    protected Button btnSelect;
    protected Button btnSelectAll;
    protected SparseBooleanArray sparseBooleanArray;

    protected ListView listView;
    protected ArrayAdapter<String> newAdapter;

    protected boolean flagSelectAll;


    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplayeronaction);

        btnSelect = (Button)findViewById(R.id.btnAddonaction);
        btnSelect.setOnClickListener(this);
        btnSelectAll = (Button)findViewById(R.id.btnCheckAll);
        btnSelectAll.setOnClickListener(this);
        btnSelect.setEnabled(false);
        flagSelectAll = true;
        sqlHelper = new DatabaseHelper(getApplicationContext());
        db = getBaseContext().openOrCreateDatabase(Message.SQL_NAME_DB, MODE_PRIVATE, null);
        setList();
    }



    private void setList(){
        listView = (ListView) findViewById(R.id.listAddPlayerOnAction);
        registerForContextMenu(listView);
        db = sqlHelper.getReadableDatabase();
        List<String> list = new ArrayList<String>();
        userCursor =  db.rawQuery("SELECT * FROM "+ Message.SQL_NAME_USERS_TABLE + " WHERE " + DatabaseHelper.COLUMN_CHECKBOX +"=0;", null);
        if (userCursor.getCount() == 0){
            btnSelectAll.setEnabled(false);
        }else {
            btnSelectAll.setEnabled(true);
        }
        userCursor.moveToFirst();
        for (int i = 0; i < userCursor.getCount(); i++) {
            list.add(userCursor.getString(1));
            userCursor.moveToNext();
        }
        newAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(newAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int sizeList = listView.getCount();
                sparseBooleanArray = listView.getCheckedItemPositions();
                btnSelect.setEnabled(false);
                for (int i = 0; i < sizeList; i++) {
                    if (sparseBooleanArray.get(i)) {
                        btnSelect.setEnabled(true);
                        break;
                    }
                }
                if (listView.getCheckedItemCount() == listView.getCount()){
                    btnSelectAll.setText("Убрать всех");
                    flagSelectAll = false;
                } else if (listView.getCheckedItemCount() == 0){
                    btnSelectAll.setText(R.string.textSelectAll);
                    flagSelectAll = true;
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userCursor.close();
        db.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCheckAll:
                for (int i = 0; i < listView.getCount(); i++) {
                    listView.setItemChecked(i, flagSelectAll);
                }
                if (!btnSelect.isEnabled()){
                    btnSelect.setEnabled(true);
                }
                flagSelectAll = !flagSelectAll;
                if (!flagSelectAll){
                    btnSelectAll.setText("Убрать всех");
                } else {
                    btnSelectAll.setText(R.string.textSelectAll);
                    btnSelect.setEnabled(false);
                }
                break;
            case R.id.btnAddonaction:
                int sizeList = listView.getCount();
                sparseBooleanArray = listView.getCheckedItemPositions();
                int count = 0;
                for (int i = 0; i < listView.getCount(); i++) {
                    if (sparseBooleanArray.get(i)){
                        String search = listView.getItemAtPosition(i).toString();
                        db = sqlHelper.getReadableDatabase();
                        userCursor = db.rawQuery("SELECT * FROM " + Message.SQL_NAME_USERS_TABLE +
                                " WHERE " + DatabaseHelper.COLUMN_FIO + "=?", new String[]{String.valueOf(search)});
                        if (userCursor.moveToFirst()){
                            db.execSQL("UPDATE " + Message.SQL_NAME_USERS_TABLE + " SET " +
                                    DatabaseHelper.COLUMN_CHECKBOX + "=" + 1 +
                                    " WHERE " + DatabaseHelper.COLUMN_FIO + "='" + search + "';");
                            count++;
                        }
                    }
                }
                if (count != 0){
                    finish();
                }
                break;
        }
    }
}
