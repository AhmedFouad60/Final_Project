package com.example.tom.otgstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText mailtxt, passwordtxt;
    private String URL = "http://www.mommmmom.esy.es/api/v1/users/signin";
    String token="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mailtxt = (MaterialEditText) findViewById(R.id.mailtxt);
        passwordtxt = (MaterialEditText) findViewById(R.id.passwordtxt);

    }

    public void onLogin(View view) {

        String mail, password;
        mail = mailtxt.getText().toString();
        password = passwordtxt.getText().toString();
        if(mail.equals("")){
            mailtxt.setError("Please Enter your email");
        }else if (password.equals("")){
            passwordtxt.setError("Please enter your password" );
        }else {
            new Login().execute(mail, password);
        }

    }

    public void onSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }


    private class Login extends AsyncTask<String, Void, Void> {

        String json="", error="", message="";
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this); // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... arg) {
            // TODO Auto-generated method stub
            String email = arg[0];
            String password = arg[1];


            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));

            ServiceHandler serviceClient = new ServiceHandler();

            json = serviceClient.makeServiceCall(URL,
                    ServiceHandler.POST, params);


            Log.d("CreatePredictionRequest", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    error = jsonObj.getString("error");
                    message = jsonObj.getString("msg");
                    token = jsonObj.getString("token");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "JSON data error!");
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            dialog.dismiss();

            if(error.equals("0")) {
                CheckPics chechPics= new CheckPics();
                chechPics.execute();
            }else if(error.equals("1")){
                Toast.makeText(LoginActivity.this,"Server Error: "+message, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this,"Unknown Error occur, please try again ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class CheckPics extends AsyncTask<String, Void, Void> {

        String json="", error="", message="", pics_uploaded="";
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this); // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... arg) {
            // TODO Auto-generated method stub



            ServiceHandler serviceClient = new ServiceHandler();

            json = serviceClient.makeServiceCall("http://www.mommmmom.esy.es//api/v1/users/profile?token="+token,
                    ServiceHandler.GET);


            Log.d("CreatePredictionRequest", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    error = jsonObj.getString("error");
                    message = jsonObj.getString("msg");
                    String data = jsonObj.getString("data");
                    JSONObject dataJson = new JSONObject(data);
                    pics_uploaded = dataJson.getString("pics_uploaded");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "JSON data error!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            dialog.dismiss();

            if(error.equals("0")) {
                if (pics_uploaded.equals("1")) {
                    Toast.makeText(LoginActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                    editor.putBoolean("isLogin", true);
                    editor.putString("token", token);
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if (pics_uploaded.equals("0")){
                    Toast.makeText(LoginActivity.this, "Sorry, You have to submit pics of your face !!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, UploadPics.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this,"Unknown Error occur, please try again ", Toast.LENGTH_SHORT).show();
                }

            }else if(error.equals("1")){
                Toast.makeText(LoginActivity.this,"Server Error: "+message, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this,"Unknown Error occur, please try again ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {

    }

}
