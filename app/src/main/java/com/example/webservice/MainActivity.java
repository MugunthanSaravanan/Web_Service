package com.example.webservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private TextView dataTextView;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataTextView=findViewById(R.id.textView);
        Button fetchDataButton = findViewById(R.id.button);
        fetchDataButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                fetchData();
            }
        });
    }
    private void fetchData() {
        Future<String> future = executorService.submit(() -> {
            try {
                return fetchFromWeb();
            } catch (IOException e) {
                return "Error: " + e.getMessage();}
        });
        try{
            String result = future.get();
            updateUI(result);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private String fetchFromWeb() throws IOException{
        URL url = new URL("https://jsonplaceholder.typicode.com/posts");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result=new StringBuilder();
        String line;
        while ((line = reader.readLine())!=null)
        {
            result.append(line);
        }
        return result.toString();
    }
    private void updateUI(String result)
    {
        runOnUiThread(()->dataTextView.setText(result));
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        executorService.shutdown();
    }
}