package net.ethernaught.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.ethernaught.R;
import net.ethernaught.services.OVpnService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connect = findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = OVpnService.prepare(getApplicationContext());
                if(intent != null){
                    startActivityForResult(intent, 0);
                }else{
                    onActivityResult(0, RESULT_OK, null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            Intent intent = new Intent(this, OVpnService.class);
            startService(intent);
        }
    }
}
