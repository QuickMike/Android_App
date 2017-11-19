package khodkov.michael.chipin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    protected static boolean flagBeginAction;

    protected Button btnMainBegin;
    protected Button btnMainEnd;
    protected Button btnMainList;
    protected Button btnMainReport;
    protected TextView textViewMainVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMainBegin = (Button) findViewById(R.id.btnMainBegin);
        btnMainEnd = (Button) findViewById(R.id.btnMainEnd);
        btnMainList = (Button) findViewById(R.id.btnMainList);
        btnMainReport = (Button) findViewById(R.id.btnMainReport);
        textViewMainVersion = (TextView) findViewById(R.id.labelMainVersion);

        btnMainBegin.setOnClickListener(this);
        btnMainList.setOnClickListener(this);
        btnMainEnd.setOnClickListener(this);
        btnMainReport.setOnClickListener(this);

        textViewMainVersion.setText(getVersion());

        setState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setState();
    }

    private void setState() {
        flagBeginAction = loadBooleanFlag(Message.TEXT_FLAG_ACTION);
        btnMainEnd.setEnabled(false);
        btnMainBegin.setEnabled(true);
        if (flagBeginAction){
            btnMainEnd.setEnabled(true);
            btnMainBegin.setEnabled(false);
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

    private String getVersion(){
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pinfo.versionName;
    }

    private boolean loadBooleanFlag(String name){
        SharedPreferences pref = getSharedPreferences(Message.BOOLEAN_IN_PREF, MODE_PRIVATE);
        return pref.getBoolean(name, false);
    }


}
