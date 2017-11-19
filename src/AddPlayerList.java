package khodkov.michael.chipin;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddPlayerList extends AppCompatActivity implements View.OnClickListener{

    protected Toast toast;
    private Button btnSave;
    private Button btnCancel;
    private Button btnDel;
    private EditText editFIO;
    private EditText editDeposit;
    private TextView textView;
    protected CorrectDeposit correctDeposit;
    protected String stStatus;
    protected String stPay;
    protected static String reportSt;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId=0;
    private String beginDepo;
    private String beginFio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplayer);
        textView = (TextView)findViewById(R.id.labelDeposit);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnDel = (Button)findViewById(R.id.btnDel);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnSave.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        editDeposit = (EditText)findViewById(R.id.editDeposit);
        editFIO = (EditText)findViewById(R.id.editFIO);
        btnCancel.setVisibility(View.GONE);

        sqlHelper = new DatabaseHelper(this);
        SQLiteDatabase db;
        db = sqlHelper.getWritableDatabase();

        stStatus="";
        stPay="";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
            stStatus = extras.getString("status");
            stPay = extras.getString("coast");
        }

        if (userId > 0) {
            userCursor = db.rawQuery("SELECT * FROM " + Message.SQL_NAME_USERS_TABLE +
                    " WHERE " + DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            beginFio = String.valueOf(userCursor.getString(1));
            editFIO.setText(beginFio);
            beginDepo = String.valueOf(userCursor.getString(2));
            editDeposit.setText(beginDepo);
            userCursor.close();
            if (stStatus.equals("pay")){
                textView.append(" = " + beginDepo);
                editDeposit.setText(stPay);
                btnCancel.setVisibility(View.VISIBLE);
                editFIO.setEnabled(false);
                btnDel.setVisibility(View.GONE);
                btnSave.setText("Оплатить");
                editDeposit.requestFocus();
            }
        } else {
            btnDel.setVisibility(View.GONE);
            btnCancel.setVisibility(View.VISIBLE);
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

    private void addInReport(String fio, String pay){
        reportSt = loadReport(Message.TEXT_REPORT);
        reportSt = reportSt + fio + " " + pay + "\n";
        saveReport(Message.TEXT_REPORT, reportSt);
    }

    @Override
    public void onClick(View v) {
        db = getBaseContext().openOrCreateDatabase(Message.SQL_NAME_DB, MODE_PRIVATE, null);
        if (v == btnSave && !editFIO.getText().toString().equals("")){
            String stFio = editFIO.getText().toString();
            String stDepo = editDeposit.getText().toString();
            correctDeposit = new CorrectDeposit();
            stDepo = correctDeposit.setCorrectDeposit(stDepo);
            userCursor = db.rawQuery("SELECT * FROM " + Message.SQL_NAME_USERS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_FIO + "='" + stFio +"';", null);
            if (stStatus.equals("edit") && userId > 0 && ((userCursor.getCount() == 0) ||
                    ((beginFio.equals(stFio) && (!beginDepo.equals(stDepo)))))) {
                db.execSQL("UPDATE " + Message.SQL_NAME_USERS_TABLE + " SET " +
                        DatabaseHelper.COLUMN_FIO + "='" + stFio + "', " +
                        DatabaseHelper.COLUMN_DEPO + "='" + stDepo + "' WHERE " +
                        DatabaseHelper.COLUMN_ID + "=" + userId + ";");
                toast = Toast.makeText(this, "Изменен", Toast.LENGTH_SHORT);
            }else if (stStatus.equals("pay")){
                userCursor.moveToFirst();
                String currentDeposit = userCursor.getString(2);
                float currentDepo = Float.parseFloat(currentDeposit);
                float pay = Float.parseFloat(stDepo);
                currentDepo = currentDepo + pay;
                currentDeposit = String.valueOf(currentDepo);
                currentDeposit = correctDeposit.setCorrectDeposit(currentDeposit);
                db.execSQL("UPDATE " + Message.SQL_NAME_USERS_TABLE + " SET " +
                        DatabaseHelper.COLUMN_CHECKBOX + "=0, " +
                        DatabaseHelper.COLUMN_DEPO + "='" + currentDeposit + "' WHERE " +
                        DatabaseHelper.COLUMN_ID + "=" + userId + ";");
                addInReport(editFIO.getText().toString(),
                        correctDeposit.setCorrectDeposit(editDeposit.getText().toString()));
                toast = Toast.makeText(this, "Оплатил", Toast.LENGTH_SHORT);
            }else if (userCursor.getCount() == 0){
                db.execSQL("INSERT INTO " + Message.SQL_NAME_USERS_TABLE +
                        "(fio, depo) VALUES ('" + stFio + "', '" + stDepo + "');");
                toast = Toast.makeText(this, "Добавлен", Toast.LENGTH_SHORT);
            }else {
                toast = Toast.makeText(this, "Такой участник уже есть!", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            toast.show();
            finish();
        }else if (v == btnDel){
            db.execSQL("DELETE FROM " + Message.SQL_NAME_USERS_TABLE + " WHERE " +
                    DatabaseHelper.COLUMN_ID + "=" + userId + ";");
            finish();
        }else if (v == btnCancel){
            finish();
        }
    }
}
