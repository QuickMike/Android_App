package khodkov.michael.Fundraising;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddPlayerList extends AppCompatActivity implements View.OnClickListener{

    private Button btn;
    private EditText editFIO;
    private EditText editDeposit;
    protected CorrectDeposit correctDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplayer);

        btn = (Button)findViewById(R.id.btnSave);
        btn.setOnClickListener(this);

        editDeposit = (EditText)findViewById(R.id.editDeposit);
        editFIO = (EditText)findViewById(R.id.editFIO);


    }

    @Override
    public void onClick(View v) {
        if (v == btn && !editFIO.getText().toString().equals("")){
            Intent intent = new Intent();
            String stFio = editFIO.getText().toString();
            stFio = stFio.replace("\n","");
            stFio = stFio.replace(Message.DEPOSIT, "");
            stFio = stFio.replace(Message.NOT_CHOICE, "");
            stFio = stFio.replace(Message.CHOICE, "");
            String stDepo = editDeposit.getText().toString();
            correctDeposit = new CorrectDeposit();
            stDepo = correctDeposit.setCorrectDeposit(stDepo);
            String putString = Message.NOT_CHOICE + stFio + Message.DEPOSIT + stDepo;
            intent.putExtra(Message.TEXT_NEW_PLAYER, putString);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


}
