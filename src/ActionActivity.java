package khodkov.michael.chipin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

public class ActionActivity extends AppCompatActivity implements View.OnClickListener{

    protected static boolean flagBeginAction;

    protected static String totalCoast;
    protected static String oneCoast;

    protected Button btnActionAddPlayer;
    protected Button btnActionBeginAction;
    protected TextView textView;
    protected ListView listView;
    protected Toast toast;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    CorrectDeposit correctDeposit = new CorrectDeposit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        btnActionAddPlayer = (Button)findViewById(R.id.btnActionAddPlayer);
        btnActionAddPlayer.setOnClickListener(this);

        btnActionBeginAction = (Button)findViewById(R.id.btnActionBegin);
        btnActionBeginAction.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.listAction);
        textView = (TextView)findViewById(R.id.labelCount);

        sqlHelper = new DatabaseHelper(getApplicationContext());

        setList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setList();
    }

    private void setList(){
        registerForContextMenu(listView);
        textView.setText("Всего участников: 0");
        loadSQLdata();
        btnActionBeginAction.setEnabled(false);
        userCursor =  db.rawQuery("SELECT * FROM "+ Message.SQL_NAME_USERS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_CHECKBOX + "=1;", null);
        if (userCursor.getCount() > 0){
            btnActionBeginAction.setEnabled(true);
            textView.setText("Всего участников: " + userCursor.getCount());
        }
    }

    private void loadSQLdata(){
        db = sqlHelper.getReadableDatabase();
        userCursor =  db.rawQuery("SELECT * FROM "+ Message.SQL_NAME_USERS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_CHECKBOX + "=1;", null);
        String[] headers = new String[] {DatabaseHelper.COLUMN_FIO, DatabaseHelper.COLUMN_DEPO};
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        listView.setAdapter(userAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userCursor.close();
        db.close();
    }

    @Override
    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.btnActionAddPlayer:
                intent = new Intent(this, AddPlayerOnAction.class);
                startActivity(intent);
                break;
            case R.id.btnActionBegin:
                if (isNeedPay()){
                    finish();
                    flagBeginAction = true;
                    saveBooleanFlag(Message.TEXT_FLAG_ACTION, flagBeginAction);
                    saveCoastAction(Message.TEXT_COAST_ONE, Message.TEXT_COAST_ALL);
                    setDebt();
                    setNewReport();
                    intent = new Intent(this, AfterAction.class);
                    startActivity(intent);
                } else {
                    toast = Toast.makeText(this, "Укажите стоимость мероприятия", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
        }
    }

    private void setNewReport(){
        String st = "Мероприятие от: ";
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        st = st + sdf.format(date) + "\n";
        st = st + "Всего участвовало: " + totalPay() + "\n";
        st = st + "Оплата с каждого: " + oneCoast + "\nИтого со всех: " + totalCoast + "\n";
        st = st + "=======================================\n";
        saveReport(Message.TEXT_REPORT, st);
    }

    private void saveReport(String name, String st) {
        SharedPreferences prefs = getSharedPreferences(Message.REPORT_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, st).apply();
    }

    private void setDebt() {
        String currentDepo;
        db = sqlHelper.getReadableDatabase();
        userCursor =  db.rawQuery("SELECT * FROM "+ Message.SQL_NAME_USERS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_CHECKBOX + "=1;", null);
        while (userCursor.moveToNext()){
            currentDepo = userCursor.getString(2);
            float currentFDepo = Float.parseFloat(currentDepo);
            float pay = Float.parseFloat(oneCoast);
            currentFDepo = currentFDepo - pay;
            currentDepo = correctDeposit.setCorrectDeposit(String.valueOf(currentFDepo));
            db.execSQL("UPDATE " + Message.SQL_NAME_USERS_TABLE +
                    " SET " + DatabaseHelper.COLUMN_DEPO + "='" + currentDepo + "'" +
                    " WHERE " + DatabaseHelper.COLUMN_ID + "=" + userCursor.getString(0));
        }
    }

    private void saveCoastAction(String name, String name2) {
        SharedPreferences pref = getSharedPreferences(Message.COAST_ONE_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String putString = oneCoast;
        editor.putString(name, putString).apply();
        pref = getSharedPreferences(Message.COAST_ALL_IN_PREF, MODE_PRIVATE);
        editor = pref.edit();
        putString = totalCoast;
        editor.putString(name2, putString).apply();
    }

    private boolean isNeedPay() {
        EditText coast = (EditText)findViewById(R.id.editActionCost);
        RadioButton rdOne = (RadioButton)findViewById(R.id.radioCostOneAction);
        try {
            float fl = Float.parseFloat(coast.getText().toString());
            float one;
            float total;
            if (fl > 0){
                int countPay = totalPay();
                if (rdOne.isChecked()){
                    one = fl;
                    total = one * countPay;
                } else {
                    total = fl;
                    one = total / countPay;
                }
                oneCoast = correctDeposit.setCorrectDeposit(String.valueOf(one));
                totalCoast = correctDeposit.setCorrectDeposit(String.valueOf(total));
                return true;
            }
        } catch (Exception e){
            return false;
        }
        return false;
    }

    private int totalPay() {
        db = sqlHelper.getReadableDatabase();
        userCursor =  db.rawQuery("SELECT * FROM "+ Message.SQL_NAME_USERS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_CHECKBOX + "=1;", null);
        return userCursor.getCount();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, Message.IDM_BACK, Menu.NONE, "Убрать");
        menu.add(Menu.NONE, Message.IDM_BACK_ALL, Menu.NONE, "Убрать всех");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId())
        {
            case Message.IDM_BACK:
                unCheck(info.id);
                break;
            case Message.IDM_BACK_ALL:
                unCheckAll();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void unCheck(long userId){
        db = sqlHelper.getWritableDatabase();
        db.execSQL("UPDATE " + Message.SQL_NAME_USERS_TABLE +
                " SET " + DatabaseHelper.COLUMN_CHECKBOX + "=0 " +
                " WHERE " + DatabaseHelper.COLUMN_ID + "=" + userId);
        toast = Toast.makeText(this, "Убрали", Toast.LENGTH_SHORT);
        toast.show();
        setList();
    }

    private void unCheckAll() {
        db = sqlHelper.getWritableDatabase();
        db.execSQL("UPDATE " + Message.SQL_NAME_USERS_TABLE +
                " SET " + DatabaseHelper.COLUMN_CHECKBOX + "=0;");
        toast = Toast.makeText(this, "Убрали всех", Toast.LENGTH_SHORT);
        toast.show();
        setList();
    }

    private void saveBooleanFlag(String name, boolean flag){
        SharedPreferences pref = getSharedPreferences(Message.BOOLEAN_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(name, flag).apply();
    }
}
