package khodkov.michael.Fundraising;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    protected static boolean flagBeginAction;

    protected Button btnMainBegin;
    protected Button btnMainEnd;
    protected Button btnMainList;
    protected Button btnMainReport;
    protected TextView textViewMain;

    protected static ArrayList<String> arrayListWork;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setState();
    }

    private void setState() {
        btnMainBegin = (Button) findViewById(R.id.btnMainBegin);
        btnMainEnd = (Button) findViewById(R.id.btnMainEnd);
        btnMainList = (Button) findViewById(R.id.btnMainList);
        btnMainReport = (Button) findViewById(R.id.btnMainReport);
        textViewMain = (TextView) findViewById(R.id.labelMain);


        btnMainBegin.setOnClickListener(this);
        btnMainList.setOnClickListener(this);
        btnMainEnd.setOnClickListener(this);
        btnMainReport.setOnClickListener(this);

        textViewMain.setVisibility(View.INVISIBLE);

        arrayListWork = loadArrayList(Message.TEXT_LIST_PLAYERS);

        flagBeginAction = loadBooleanFlag(Message.TEXT_FLAG_ACTION);

        btnMainBegin.setEnabled(false);
        btnMainEnd.setEnabled(false);
        if (arrayListWork.size() == 1 && arrayListWork.get(0).equals("") || arrayListWork.isEmpty()){
            btnMainBegin.setEnabled(false);
            btnMainEnd.setEnabled(false);
            textViewMain.setVisibility(View.VISIBLE);
        } else {
            textViewMain.setVisibility(View.INVISIBLE);
            if (flagBeginAction){
                btnMainEnd.setEnabled(true);
            }else {
                btnMainBegin.setEnabled(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.btnMainList:
                intent = new Intent(this, ListActivity.class);
                break;
            case R.id.btnMainBegin:
                intent = new Intent(this, ActionActivity.class);
                break;
            case R.id.btnMainEnd:
                intent = new Intent(this, AfterAction.class);
                break;
            case R.id.btnMainReport:
                intent = new Intent(this, Report.class);
                break;
        }
        startActivity(intent);
    }

//    private void saveArrayList(String name, ArrayList<String> list) {
//        SharedPreferences prefs = getSharedPreferences(Message.ARRAY_LIST_PLAYERS_IN_PREF, MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        StringBuilder sb = new StringBuilder();
//        for (String s : list){
//            if (!s.equals("")){
//                sb.append(s).append(Message.NEW_ITEM);
//            }
//        }
//        if (sb.length() > 3){
//            sb.delete(sb.length() - 3, sb.length());
//        }
//        editor.putString(name, sb.toString()).apply();
//    }

    private ArrayList<String> loadArrayList(String name) {
        SharedPreferences prefs = getSharedPreferences(Message.ARRAY_LIST_PLAYERS_IN_PREF, MODE_PRIVATE);
        String[] strings = prefs.getString(name, "").split(Message.NEW_ITEM);
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(strings));
        return list;
    }

    private boolean loadBooleanFlag(String name){
        SharedPreferences pref = getSharedPreferences(Message.BOOLEAN_IN_PREF, MODE_PRIVATE);
        return pref.getBoolean(name, false);
    }


}
