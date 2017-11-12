package khodkov.michael.Fundraising;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Report extends AppCompatActivity {

    protected TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        setState();
    }

    private void setState(){
        text = (TextView)findViewById(R.id.textState);

        String st = loadReport(Message.TEXT_REPORT);

        text.setText(st);
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
}
