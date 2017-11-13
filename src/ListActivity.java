package khodkov.michael.Fundraising;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

public class ListActivity extends AppCompatActivity{

    protected static String st;
    protected static String stateChoice;
    protected static String currentFIO;
    protected static String currentDeposit;

    protected static ArrayList<String> arrayListWork;
    protected static ArrayList<String> arrayListView;

    protected ArrayAdapter<String> newAdapter;
    protected CorrectDeposit correctDeposit = new CorrectDeposit();
    protected ListView listView;
    protected Toast toast;

    @Override
    protected void onResume() {
        super.onResume();
        setList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, Message.IDM_EDIT, Menu.NONE, "Изменить ФИО");
        menu.add(Menu.NONE, Message.IDM_ADD, Menu.NONE, "Изменить депозит");
        menu.add(Menu.NONE, Message.IDM_DELETE, Menu.NONE, "Удалить");
    }

    private void setList(){
        listView = (ListView) findViewById(R.id.mainList);
        registerForContextMenu(listView);
        arrayListWork = loadArrayList(Message.TEXT_LIST_PLAYERS);
        arrayListView = setForView(arrayListWork);
        newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListView);
        listView.setAdapter(newAdapter);
    }

    private ArrayList<String> setForView(ArrayList<String> list){
        ArrayList<String> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String st = list.get(i);
            st = st.replaceFirst(Message.DEPOSIT, " ");
            st = st.replaceFirst(Message.NOT_CHOICE, "");
            st = st.replaceFirst(Message.CHOICE, "");
            newList.add(st);
        }
        return newList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId())
        {
            case Message.IDM_EDIT:
                newDialog(Message.IDM_EDIT, info.position);
                break;
            case Message.IDM_ADD:
                newDialog(Message.IDM_ADD, info.position);
                break;
            case Message.IDM_DELETE:
                delPlayer(info.position);
                setList();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void delPlayer(int position){
        arrayListWork.remove(position);
        saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
        toast = Toast.makeText(this, "Удален", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (id){
            case R.id.itemDelAll:
                if (arrayListWork.size() == 1 && arrayListWork.get(0).length() == 0){
                    break;
                }
                newDialog(Message.IDM_DEL_ALL);
                break;
            case R.id.itemPlus:
                intent = new Intent(this, AddPlayerList.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.itemSetNull:
                if (arrayListWork.size() == 1 && arrayListWork.get(0).length() == 0){
                    break;
                }
                newDialog(Message.IDM_CLEAR_ALL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void setNull() {
        if (arrayListWork.size() == 1 && arrayListWork.get(0).length() == 0){
            return;
        }
        for (int i = 0; i < arrayListWork.size(); i++) {
            String st = arrayListWork.get(i);
            st = st.substring(0,st.indexOf(Message.DEPOSIT) + 3);
            st+="0.00";
            arrayListWork.set(i, st);
        }
        saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
        toast = Toast.makeText(this, "Обнулены", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void delList(){
        if (arrayListWork.size() == 1 && arrayListWork.get(0).length() == 0){
            return;
        }
        arrayListWork.clear();
        saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
        toast = Toast.makeText(this, "Удалены", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            arrayListWork.add(data.getStringExtra(Message.TEXT_NEW_PLAYER));
            saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
            toast = Toast.makeText(this, "Добавлен", Toast.LENGTH_SHORT);
            toast.show();
        }
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
                    delList();
                }else if (chose == Message.IDM_CLEAR_ALL){
                    setNull();
                }
                setList();
            }
        });

        alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void newDialog(final int chose, final int position){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (chose == Message.IDM_ADD){
            alert.setTitle("Депозит");
            alert.setMessage("Изменить депозит?");
        } else if (chose == Message.IDM_EDIT){
            alert.setTitle("ФИО");
            alert.setMessage("Изменить ФИО?");
        }
        final EditText input = new EditText(this);
        st = arrayListWork.get(position);
        stateChoice = st.substring(0, 3);
        currentFIO = st.substring(3, st.indexOf(Message.DEPOSIT));
        currentDeposit = st.substring(st.indexOf(Message.DEPOSIT) + 3, st.length());
        if (chose == Message.IDM_ADD){
            input.setText(currentDeposit);
            //input.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (chose == Message.IDM_EDIT){
            input.setText(currentFIO);
        }
        alert.setView(input);
        alert.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String value = input.getText().toString();
                if (chose == Message.IDM_ADD){
                    currentDeposit = correctDeposit.setCorrectDeposit(value);
                } else if (chose == Message.IDM_EDIT){
                    value = value.replace("\n","");
                    value = value.replace(Message.DEPOSIT, "");
                    value = value.replace(Message.NOT_CHOICE, "");
                    value = value.replace(Message.CHOICE, "");
                    currentFIO = value;
                }
                st = stateChoice + currentFIO + Message.DEPOSIT + currentDeposit;
                arrayListWork.set(position, st);
                saveArrayList(Message.TEXT_LIST_PLAYERS, arrayListWork);
                setList();
            }
        });

        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
        setList();
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
