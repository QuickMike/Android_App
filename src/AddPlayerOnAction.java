package khodkov.michael.Fundraising;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class AddPlayerOnAction extends AppCompatActivity implements View.OnClickListener{

    protected static String st;
    protected static String stateChoice;
    protected static String currentFIO;
    protected static String currentDeposit;

    protected Button btnSelect;
    protected Button btnSelectAll;
    protected SparseBooleanArray sparseBooleanArray;

    protected ListView listView;
    protected ArrayAdapter<String> newAdapter;
    protected Toast toast;

    protected boolean flagSelectAll;

    protected static ArrayList<String> arrayListWork;
    protected static ArrayList<String> arrayListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplayeronaction);

        btnSelect = (Button)findViewById(R.id.btnAddonaction);
        btnSelect.setOnClickListener(this);
        btnSelectAll = (Button)findViewById(R.id.btnCheckAll);
        btnSelectAll.setOnClickListener(this);
        btnSelect.setEnabled(false);
        setList();
        flagSelectAll = true;
    }



    private void setList(){
        listView = (ListView) findViewById(R.id.listAddPlayerOnAction);
        registerForContextMenu(listView);
        arrayListWork = loadArrayList(Message.TEXT_LIST_PLAYERS);

        //arrayListView = arrayListWork;

        arrayListView = setForView(arrayListWork);

        newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, arrayListView);
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

    private ArrayList<String> setForView(ArrayList<String> list){
        ArrayList<String> newList = new ArrayList<>();
        if (list.size() == 1 && list.get(0).equals("")){
            return newList;
        }
        for (int i = 0; i < list.size(); i++) {
            String st = list.get(i);
            stateChoice = st.substring(0, 3);
            if (stateChoice.equals(Message.CHOICE)){
                continue;
            }
            st = st.replaceFirst(Message.DEPOSIT, " ");
            st = st.replaceFirst(Message.NOT_CHOICE, "");
            newList.add(st);
        }
        return newList;
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
                for (int i = 0; i < sizeList; i++) {
                    if (sparseBooleanArray.get(i)){
                        int index = searchIndex(listView.getItemAtPosition(i).toString());
                        if (index == -1){
                            continue;
                        }
                        count++;
                        st = Message.CHOICE + currentFIO + Message.DEPOSIT + currentDeposit;
                        arrayListWork.set(index,st);
                    }
                }
                if (count != 0){
                    saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
                    finish();
                }
                break;
        }
    }

    private int searchIndex(String st){
        int index = -1;
        String tempSt;
        for (int i = 0; i < arrayListWork.size(); i++) {
            tempSt = arrayListWork.get(i);
            stateChoice = tempSt.substring(0, 3);
            if (stateChoice.equals(Message.CHOICE)){
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
}
