package com.danielkim.soundrecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class authActivity extends AppCompatActivity {
    EditText name;
    Button sub;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        name=findViewById(R.id.nameEdit);
        sub=findViewById(R.id.name_sub);

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName=name.getText().toString();
                if(userName.isEmpty()){
                    name.setError("Enter Name");
                    name.setFocusable(true);
                }
                else{
                    Intent i=new Intent(authActivity.this,getPassAct.class);
                    startActivity(i);
                }
            }
        });

    }
}
