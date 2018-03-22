package com.example.user;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class InsertActivity extends Activity  {
	EditText uid, upw, uname;
	String strUid, strUpw, strUname;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        uid = (EditText) findViewById(R.id.uid);
        upw = (EditText) findViewById(R.id.upw);
        uname = (EditText) findViewById(R.id.uname);
    }
	public void mClick(View v){
		strUid = uid.getText().toString();
		strUpw = upw.getText().toString();
		strUname = uname.getText().toString();
		new ThreadUser().execute(null, null);
	}
	
	class ThreadUser extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... strings) {
			String result ="";
			try{
				String apiURL = "http://192.168.0.52:8080/User1/register.jsp";
				URL url = new URL(apiURL);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				
				StringBuffer params=new StringBuffer();
				params.append("uid=" + strUid);
				params.append("&upw=" + strUpw);
				params.append("&uname=" + strUname);
				String strParams=params.toString();
				OutputStream os=con.getOutputStream();
				os.write(strParams.getBytes("UTF-8"));
				os.flush();
				os.close();
				
				int responseCode=con.getResponseCode();
				if(responseCode==200){
					result = "SUCCESS";
					Log.i("result.....", strParams.toString());
				} else {
					result = "FAIL";
					Log.i("eerrooorr....", strParams.toString());
				}
					
				return result;
			}catch(Exception e){
				return e.toString();
			}
			
		}

		@Override
		protected void onPostExecute(String result) {
			if(result == "SUCCESS"){
				Toast.makeText(InsertActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
				finish();
			}
			
		}
		
	}

}
