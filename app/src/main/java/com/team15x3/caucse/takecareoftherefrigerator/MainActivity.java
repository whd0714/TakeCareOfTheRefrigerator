package com.team15x3.caucse.takecareoftherefrigerator;

import android.app.TabActivity;
import android.content.Intent;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static android.util.Log.e;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> dataAdapter;

    private Button inputBtn;
    private ListView listView;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public String datapasing="";
    public String datap;
    public String strContact;

    private Gson gson;

    String myBarcode;
    Button btnOpenBarcode;
    TextView text;

    APIInterface apiInterface;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        myBarcode = result.getContents(); //get barcode number
        Toast.makeText(this,""+myBarcode,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<ResponseBody> call = apiInterface.doGetListResources("ALL","barcode","18801073181905",null
                ,null,null,null,0,2);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    // Gson 인스턴스 생성
                    gson = new GsonBuilder().create();
                    // JSON 으로 변환
                    strContact = gson.toJson(response.body().string());


                    Log.d("QQQ", "" + response.body().string());
                    datapasing =  response.body().toString();

                    datap =  response.body().string().toString();
                    Log.d("QQQ", datapasing);
                    Log.d("QQQ", datap);
                    Log.d("QQQ", strContact);
                }catch(Exception e){

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


        inputBtn = (Button) findViewById(R.id.inputBtn);
        listView = (ListView) findViewById(R.id.listView);


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());

        listView.setAdapter(dataAdapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataAdapter.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String msg = messageData.getValue().toString();

                    dataAdapter.add(msg);
                }
                dataAdapter.notifyDataSetChanged();
                listView.setSelection(dataAdapter.getCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("aaaaaa", datapasing);
                myRef.push().setValue(strContact);
                Log.d("bbbbb", datapasing);

            }
        });


/*        text =(TextView)findViewById(R.id.text);
        btnOpenBarcode = (Button)findViewById(R.id.btnOpenBarcode);
        btnOpenBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MainActivity.this).initiateScan();
            }



        });*/



    }

}
