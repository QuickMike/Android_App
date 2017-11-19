package khodkov.michael.chipin;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity{

    protected ListView listView;
    protected Toast toast;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        loadSQLdata();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView)findViewById(R.id.listUsers);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AddPlayerList.class);
                intent.putExtra("id", id);
                intent.putExtra("status", "edit");
                startActivity(intent);
            }
        });
        sqlHelper = new DatabaseHelper(getApplicationContext());
        db = getBaseContext().openOrCreateDatabase(Message.SQL_NAME_DB, MODE_PRIVATE, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (id){
            case R.id.itemDelAll:
                newDialog(Message.IDM_DEL_ALL);
                break;
            case R.id.itemPlus:
                intent = new Intent(this, AddPlayerList.class);
                startActivity(intent);
                break;
            case R.id.itemSetNull:
                newDialog(Message.IDM_CLEAR_ALL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNull() {
        db.execSQL("UPDATE " + Message.SQL_NAME_USERS_TABLE +
                " SET " + DatabaseHelper.COLUMN_DEPO + "='0.00';");
        toast = Toast.makeText(this, "Обнулены", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void delSQLTable(){
        sqlHelper.delSQLTable(db);
        toast = Toast.makeText(this, "Удалены", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void newDialog(final int chose){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (chose == Message.IDM_DEL_ALL){
            alert.setTitle("Удалить всех?");
        } else if (chose == Message.IDM_CLEAR_ALL){
            alert.setTitle("Обнулить всех?");
        }
        alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (chose == Message.IDM_DEL_ALL){
                    delSQLTable();
                    sqlHelper.onCreate(db);
                    loadSQLdata();
                }else if (chose == Message.IDM_CLEAR_ALL){
                    setNull();
                    loadSQLdata();
                }
            }
        });

        alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void loadSQLdata(){
        listView = (ListView)findViewById(R.id.listUsers);
        db = sqlHelper.getReadableDatabase();
        userCursor =  db.rawQuery("SELECT * FROM "+ Message.SQL_NAME_USERS_TABLE + ";", null);
        String[] headers = new String[] {DatabaseHelper.COLUMN_FIO, DatabaseHelper.COLUMN_DEPO};
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        listView.setAdapter(userAdapter);
    }
}
