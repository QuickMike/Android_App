package khodkov.michael.chipin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AfterAction extends AppCompatActivity implements View.OnClickListener{

    Button btnPayAll;
    Button btnEndAction;
    TextView textOneCoast;
    TextView textTotalCoast;

    protected static boolean flagBeginAction;

    protected static String totalCoast;
    protected static String oneCoast;

    protected static String st;
    protected static String reportSt;
    protected static String currentDeposit;

    protected ListView listView;
    protected Toast toast;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_action);

        loadCoast(Message.TEXT_COAST_ONE, Message.TEXT_COAST_ALL);

        textOneCoast = (TextView)findViewById(R.id.textAfterActionOne);
        textTotalCoast = (TextView)findViewById(R.id.textAfterActionTotal);

        btnPayAll = (Button)findViewById(R.id.btnPayAllonAction);
        btnEndAction = (Button)findViewById(R.id.btnEndAction);

        btnPayAll.setOnClickListener(this);
        btnEndAction.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.listEnd);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AddPlayerList.class);
                intent.putExtra("id", id);
                intent.putExtra("status", "pay");
                intent.putExtra("coast", oneCoast);
                startActivity(intent);
            }
        });

        textOneCoast.append(" " + oneCoast);
        textTotalCoast.append(" " +  totalCoast);

        sqlHelper = new DatabaseHelper(getApplicationContext());

        setState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userCursor.close();
        db.close();
    }

    private void setState(){
        db = sqlHelper.getReadableDatabase();
        userCursor =  db.rawQuery("SELECT * FROM "+ Message.SQL_NAME_USERS_TABLE + " WHERE " + DatabaseHelper.COLUMN_CHECKBOX + "=1;", null);
        String[] headers = new String[] {DatabaseHelper.COLUMN_FIO, DatabaseHelper.COLUMN_DEPO};
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        listView.setAdapter(userAdapter);
        if (userCursor.getCount() == 0){
            flagBeginAction = false;
            saveBooleanFlag(Message.TEXT_FLAG_ACTION, flagBeginAction);
            toast = Toast.makeText(this, "Все оплатили", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    private void saveReport(String name, String st) {
        SharedPreferences prefs = getSharedPreferences(Message.REPORT_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, st).apply();
    }

    private String loadReport(String name) {
        SharedPreferences prefs = getSharedPreferences(Message.REPORT_IN_PREF, MODE_PRIVATE);
        String st = prefs.getString(name, "");
        return st;
    }

    private void saveBooleanFlag(String name, boolean flag){
        SharedPreferences pref = getSharedPreferences(Message.BOOLEAN_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(name, flag).apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEndAction:
                finish();
                setNotCheckAll();
                flagBeginAction = false;
                saveBooleanFlag(Message.TEXT_FLAG_ACTION, flagBeginAction);
                break;
            case R.id.btnPayAllonAction:
                setPayAll();
                flagBeginAction = false;
                saveBooleanFlag(Message.TEXT_FLAG_ACTION, flagBeginAction);
                finish();
                break;
        }
    }

    private void setNotCheckAll() {
        db = sqlHelper.getReadableDatabase();
        userCursor =  db.rawQuery("SELECT * FROM "+ Message.SQL_NAME_USERS_TABLE + " WHERE " + DatabaseHelper.COLUMN_CHECKBOX + "=1;", null);
        while (userCursor.moveToNext()){
            st = "-" + oneCoast;
            addInReport(userCursor.getString(1), st);
            db.execSQL("UPDATE " + Message.SQL_NAME_USERS_TABLE + " SET " +
                    DatabaseHelper.COLUMN_CHECKBOX + "=0" +
                    " WHERE " + DatabaseHelper.COLUMN_CHECKBOX + "=1;");
        }

    }

    private void setPayAll() {
        db = sqlHelper.getReadableDatabase();
        userCursor =  db.rawQuery("SELECT * FROM "+ Message.SQL_NAME_USERS_TABLE + " WHERE " + DatabaseHelper.COLUMN_CHECKBOX + "=1;", null);
        while (userCursor.moveToNext()){
            currentDeposit = userCursor.getString(2);
            float currentDepo = Float.parseFloat(currentDeposit);
            float pay = Float.parseFloat(oneCoast);
            currentDepo = currentDepo + pay;

            addInReport(userCursor.getString(1), oneCoast);
            db.execSQL("UPDATE " + Message.SQL_NAME_USERS_TABLE + " SET " +
                    DatabaseHelper.COLUMN_CHECKBOX + "=0, " +
                    DatabaseHelper.COLUMN_DEPO + "='" + currentDepo + "'" +
                    " WHERE " + DatabaseHelper.COLUMN_ID + "=" +
                    userCursor.getString(0) + ";");
        }
    }

    private void addInReport(String fio, String pay){
        reportSt = loadReport(Message.TEXT_REPORT);
        reportSt = reportSt + fio + " " + pay + "\n";
        saveReport(Message.TEXT_REPORT, reportSt);
    }

    private void loadCoast(String name, String name2) {
        SharedPreferences prefs = getSharedPreferences(Message.COAST_ONE_IN_PREF, MODE_PRIVATE);
        oneCoast = prefs.getString(name,"");
        prefs = getSharedPreferences(Message.COAST_ALL_IN_PREF, MODE_PRIVATE);
        totalCoast = prefs.getString(name2,"");
    }
}
