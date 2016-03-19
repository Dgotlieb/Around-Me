package com.example.daniel.aroundme.activities_and_fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.database.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Daniel on 2/2/2016.
 */

//not available yet
public class RecommendedFragment extends Fragment {
    private TextView recoTV;
    private ListView recoLV;
    private ArrayList<Place> placesArry;
    private ArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recommended, container, false);
        recoTV = (TextView)v.findViewById(R.id.recommended_textView);
        recoLV = (ListView)v.findViewById(R.id.recommended_listView);
        placesArry = new ArrayList<>();
        adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,placesArry);
        recoLV.setAdapter(adapter);

        String url = "http://52.23.180.34:8080/Im/im/5147";

//                String url = "http://10.30.0.252:8080/Html/trivia/cards/4600";
        MyTask task = new MyTask();
        task.execute(new String[]{url});


        return v;

    }

    public void addNewContent(String s){
        try {

            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    class MyTask extends AsyncTask<String,Void,String> {


        @Override
        protected void onPreExecute() {
            ProgressBar bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
            bar.setVisibility(ProgressBar.VISIBLE);


        }

        @Override
        protected String doInBackground(String... strings) {

            String response = sendHttpRequest(strings[0]);



            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            ProgressBar bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
            bar.setVisibility(ProgressBar.INVISIBLE);

            try {


                JSONObject object = new JSONObject(s);

//                String trivia = object.getString("triviaCard");

                JSONArray array = object.getJSONArray("message");

                for (int i = 0; i <array.length() ; i++) {

                    String content = array.getJSONObject(i).getString("content");
                    String message_id = array.getJSONObject(i).getString("messageId");

                    addNewContent(content);

                }









            } catch (JSONException |NullPointerException e) {
                e.printStackTrace();

            }

        }




        private String sendHttpRequest(String urlString) {
            BufferedReader input = null;
            HttpURLConnection httpCon = null;
            InputStream input_stream =null;
            InputStreamReader input_stream_reader = null;
            StringBuilder response = new StringBuilder();
            try{
                URL url = new URL(urlString);
                httpCon = (HttpURLConnection)url.openConnection();
                if(httpCon.getResponseCode()!=HttpURLConnection.HTTP_OK){
                    return null;
                }

                input_stream = httpCon.getInputStream();
                input_stream_reader = new InputStreamReader(input_stream);
                input = new BufferedReader(input_stream_reader);
                String line ;
                while ((line = input.readLine())!= null){
                    response.append(line +"\n");
                }



            }catch (Exception e) {
                e.printStackTrace();
            }finally{
                if(input!=null){
                    try {
                        input_stream_reader.close();
                        input_stream.close();
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(httpCon != null){
                        httpCon.disconnect();
                    }
                }
            }
            return response.toString();
        }
    }

}
