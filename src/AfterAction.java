package khodkov.michael.Fundraising;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

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
    protected static String stateChoice;
    protected static String currentFIO;
    protected static String currentDeposit;

    protected ListView listView;
    protected ArrayAdapter<String> newAdapter;
    protected Toast toast;

    protected static ArrayList<String> arrayListWork;
    protected static ArrayList<String> arrayListView;

    CorrectDeposit correctDeposit = new CorrectDeposit();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_action);

        loadCoast(Message.TEXT_COAST);

        textOneCoast = (TextView)findViewById(R.id.textAfterActionOne);
        textTotalCoast = (TextView)findViewById(R.id.textAfterActionTotal);

        st = textOneCoast.getText().toString();
        textOneCoast.setText(st + oneCoast);

        st = textTotalCoast.getText().toString();
        textTotalCoast.setText(st + totalCoast);

        setState();
    }

    private void setState(){
        btnPayAll = (Button)findViewById(R.id.btnPayAllonAction);
        btnEndAction = (Button)findViewById(R.id.btnEndAction);

        btnPayAll.setOnClickListener(this);
        btnEndAction.setOnClickListener(this);



        listView = (ListView) findViewById(R.id.listEnd);
        registerForContextMenu(listView);
        arrayListWork = loadArrayList(Message.TEXT_LIST_PLAYERS);
        arrayListView = setForView(arrayListWork);

        if (arrayListView.isEmpty()){
            flagBeginAction = false;
            saveBooleanFlag(Message.TEXT_FLAG_ACTION, flagBeginAction);
            toast = Toast.makeText(this, "Все оплатили", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }

        newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListView);
        listView.setAdapter(newAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = searchIndex(arrayListView.get(position));
                if (index != -1){
                    newDialog(index);
                }
            }
        });

    }

    private int searchIndex(String st){
        int index = -1;
        String tempSt;
        for (int i = 0; i < arrayListWork.size(); i++) {
            tempSt = arrayListWork.get(i);
            stateChoice = tempSt.substring(0, 3);
            if (stateChoice.equals(Message.NOT_CHOICE)){
                continue;
            }
            currentFIO = tempSt.substring(3, tempSt.indexOf(Message.DEPOSIT));
            currentDeposit = tempSt.substring(tempSt.indexOf(Message.DEPOSIT) + 3, tempSt.length());
            tempSt = currentFIO + " " + currentDeposit;
            if (tempSt.equals(st)){
                index = i;
                break;
            }

        }
        return index;
    }

    private void saveReport(String name, String st) {
        SharedPreferences prefs = getSharedPreferences(Message.REPORT_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, st).apply();
    }

    private String loadReport(String name) {
        SharedPreferences prefs = getSharedPreferences(Message.REPORT_IN_PREF, MODE_PRIVATE);
        String strings = prefs.getString(name, "");
        return strings;
    }

    private void newDialog(final int position){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(currentFIO);
        alert.setMessage("Оплатил:");
        final EditText input = new EditText(this);
        input.setText(oneCoast);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        alert.setView(input);
        alert.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String edit = input.getText().toString();
                float value = Float.parseFloat(edit);
                float currenDepo = Float.parseFloat(currentDeposit);
                value = currenDepo + value;
                st = Message.NOT_CHOICE + currentFIO + Message.DEPOSIT + correctDeposit.setCorrectDeposit(String.valueOf(value));
                arrayListWork.set(position, st);
                saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
                addInReport(currentFIO, edit);
                setState();
            }
        });

        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
        setState();
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
                saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
                flagBeginAction = false;
                saveBooleanFlag(Message.TEXT_FLAG_ACTION, flagBeginAction);
                break;
            case R.id.btnPayAllonAction:
                setPayAll();
                saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
                flagBeginAction = false;
                saveBooleanFlag(Message.TEXT_FLAG_ACTION, flagBeginAction);
                finish();
                break;
        }
    }

    private void setNotCheckAll() {
        for (int i = 0; i < arrayListWork.size(); i++) {
            st = arrayListWork.get(i);
            stateChoice = st.substring(0, 3);
            if (stateChoice.equals(Message.NOT_CHOICE)){
                continue;
            }
            currentFIO = st.substring(3, st.indexOf(Message.DEPOSIT));
            currentDeposit = st.substring(st.indexOf(Message.DEPOSIT) + 3, st.length());
            st = Message.NOT_CHOICE + currentFIO + Message.DEPOSIT + currentDeposit;
            arrayListWork.set(i, st);
            st = "-" + oneCoast;
            addInReport(currentFIO, st);
        }
    }

    private void setPayAll() {
        for (int i = 0; i < arrayListWork.size(); i++) {
            st = arrayListWork.get(i);
            stateChoice = st.substring(0, 3);
            if (stateChoice.equals(Message.NOT_CHOICE)){
                continue;
            }
            currentFIO = st.substring(3, st.indexOf(Message.DEPOSIT));
            currentDeposit = st.substring(st.indexOf(Message.DEPOSIT) + 3, st.length());
            float currentDepo = Float.parseFloat(currentDeposit);
            float pay = Float.parseFloat(oneCoast);
            currentDepo = currentDepo + pay;
            st = Message.NOT_CHOICE + currentFIO + Message.DEPOSIT + correctDeposit.setCorrectDeposit(String.valueOf(currentDepo));
            arrayListWork.set(i, st);
            addInReport(currentFIO, oneCoast);
        }
    }

    private void saveArrayList(String name, ArrayList<String> list) {
        SharedPreferences prefs = getSharedPreferences(Message.ARRAY_LIST_PLAYERS_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder sb = new StringBuilder();
        for (String s : list){
            if (!s.equals("")){
                sb.append(s).append(Message.NEW_ITEM);
            }
        }
        if (sb.length() > 3){
            sb.delete(sb.length() - 3, sb.length());
        }
        editor.putString(name, sb.toString()).apply();
    }

    private void addInReport(String fio, String pay){
        reportSt = loadReport(Message.TEXT_REPORT);
        reportSt = reportSt + fio + " " + pay + "\n";
        saveReport(Message.TEXT_REPORT, reportSt);
    }

    private ArrayList<String> loadArrayList(String name) {
        SharedPreferences prefs = getSharedPreferences(Message.ARRAY_LIST_PLAYERS_IN_PREF, MODE_PRIVATE);
        String[] strings = prefs.getString(name, "").split(Message.NEW_ITEM);
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(strings));
        return list;
    }

    private void loadCoast(String name) {
        SharedPreferences prefs = getSharedPreferences(Message.COAST_IN_PREF, MODE_PRIVATE);
        String[] strings = prefs.getString(name, "").split(Message.NEW_ITEM);
        oneCoast = strings[0];
        totalCoast = strings[1];
    }

    private ArrayList<String> setForView(ArrayList<String> list){
        ArrayList<String> newList = new ArrayList<>();
        if (list.size() == 1 && list.get(0).equals("")){
            return newList;
        }
        for (int i = 0; i < list.size(); i++) {
            String st = list.get(i);
            stateChoice = st.substring(0, 3);
            if (stateChoice.equals(Message.NOT_CHOICE)){
                continue;
            }
            st = st.replaceFirst(Message.DEPOSIT, " ");
            st = st.replaceFirst(Message.CHOICE, "");
            newList.add(st);
        }
        return newList;
    }
}
