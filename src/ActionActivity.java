package khodkov.michael.Fundraising;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ActionActivity extends AppCompatActivity implements View.OnClickListener{

    protected static boolean flagBeginAction;

    protected static String totalCoast;
    protected static String oneCoast;

    protected static String st;
    protected static String stateChoice;
    protected static String currentFIO;
    protected static String currentDeposit;

    protected Button btnActionAddPlayer;
    protected Button btnActionBeginAction;
    protected TextView textView;
    protected ListView listView;
    protected ArrayAdapter<String> newAdapter;
    protected Toast toast;

    protected static ArrayList<String> arrayListWork;
    protected static ArrayList<String> arrayListView;

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
        setList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setList();
    }

    private void setList(){
        btnActionBeginAction = (Button)findViewById(R.id.btnActionBegin);
        btnActionAddPlayer = (Button)findViewById(R.id.btnActionAddPlayer);
        listView = (ListView) findViewById(R.id.listAction);
        registerForContextMenu(listView);
        arrayListWork = loadArrayList(Message.TEXT_LIST_PLAYERS);
        arrayListView = setForView(arrayListWork);
        textView = (TextView)findViewById(R.id.labelCount);
        textView.setText(String.valueOf(arrayListView.size()));
        newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListView);
        listView.setAdapter(newAdapter);
        btnActionAddPlayer.setEnabled(false);
        if (arrayListWork.size() == 1 && arrayListWork.get(0).equals("") || arrayListWork.isEmpty()){
            btnActionAddPlayer.setEnabled(false);
        } else {
            for (int i = 0; i < arrayListWork.size(); i++) {
                String st = arrayListWork.get(i);
                stateChoice = st.substring(0, 3);
                if (stateChoice.equals(Message.NOT_CHOICE)){
                    btnActionAddPlayer.setEnabled(true);
                    break;
                }
            }
        }

        btnActionBeginAction.setEnabled(false);
        if (!arrayListView.isEmpty()){
            btnActionBeginAction.setEnabled(true);
        }
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
                    saveCoastAction(Message.TEXT_COAST);
                    setDebt();
                    setNewReport();
                    saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
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
        st = st + "Всего участвовало: " + arrayListView.size() + "\n";
        st = st + "Оплата с каждого: " + oneCoast + " Итого со всех: " + totalCoast + "\n";
        st = st + "=======================================\n";
        saveReport(Message.TEXT_REPORT, st);
    }

    private void saveReport(String name, String st) {
        SharedPreferences prefs = getSharedPreferences(Message.REPORT_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, st).apply();
    }

    private void setDebt() {
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
            currentDepo = currentDepo - pay;
            st = stateChoice + currentFIO + Message.DEPOSIT + correctDeposit.setCorrectDeposit(String.valueOf(currentDepo));
            arrayListWork.set(i, st);
        }
    }

    private void saveCoastAction(String name) {
        SharedPreferences pref = getSharedPreferences(Message.COAST_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String putString = oneCoast + Message.NEW_ITEM + totalCoast;
        editor.putString(name, putString).apply();
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
        int count = 0;
        for (int i = 0; i < arrayListWork.size(); i++) {
            st = arrayListWork.get(i);
            stateChoice = st.substring(0, 3);
            if (stateChoice.equals(Message.CHOICE)) {
                count++;
            }
        }
        return count;
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
                unCheck(info.position);
                break;
            case Message.IDM_BACK_ALL:
                unCheckAll();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void unCheck(int position){
        int index = searchIndex(arrayListView.get(position));
        if ( index != -1){
            st = Message.NOT_CHOICE + currentFIO + Message.DEPOSIT + currentDeposit;
            arrayListWork.set(index,st);
            saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
            toast = Toast.makeText(this, "Убрали", Toast.LENGTH_SHORT);
            toast.show();
            setList();
        }
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

    private void unCheckAll() {
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
        }
        saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
        setList();
        toast = Toast.makeText(this, "Убрали всех", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void saveBooleanFlag(String name, boolean flag){
        SharedPreferences pref = getSharedPreferences(Message.BOOLEAN_IN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(name, flag).apply();
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

    private ArrayList<String> loadArrayList(String name) {
        SharedPreferences prefs = getSharedPreferences(Message.ARRAY_LIST_PLAYERS_IN_PREF, MODE_PRIVATE);
        String[] strings = prefs.getString(name, "").split(Message.NEW_ITEM);
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(strings));
        return list;
    }
}
