package com.example.user;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	ArrayList<HashMap<String, String>> array;
	Button register;
	ListView list;
	MyAdapter adapter;
	
	
	
  
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register=(Button)findViewById(R.id.register);
        list=(ListView)findViewById(R.id.list);
        new ThreadUserList().execute(null,null);
	}

	public void mClick(View v){
		Intent intent=new Intent(this, InsertActivity.class);
		startActivityForResult(intent, 0);
		
	}
	
	

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0){
		new ThreadUserList().execute(null, null);
		}
	}




	class MyAdapter extends BaseAdapter{
		int layout;
		ArrayList<HashMap<String, String>> array;
		
		
		public MyAdapter(int layout, ArrayList<HashMap<String, String>> array) {
			super();
			this.layout = layout;
			this.array = array;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return array.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			HashMap<String, String> map=array.get(position);
			
			final String strUid=map.get("uid");
			String strUname=map.get("uname");
			
			LayoutInflater inflater = getLayoutInflater();
			if(view==null){
				view = inflater.inflate(R.layout.item, null);
			}
			TextView uid=(TextView)view.findViewById(R.id.uid);
			uid.setText(strUid);
			TextView uname=(TextView)view.findViewById(R.id.uname);
			uname.setText(strUname);
			
			TextView delete=(TextView)view.findViewById(R.id.delete);
			delete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					new ThreadUserDelete().execute(strUid, null);
					
				}
			});
			return view;
			
				}
			
			}

    
	class ThreadUserList extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... arg0) {
				try{
					String apiURL = "http://192.168.0.52:8080/User1/list.jsp";
					URL url = new URL(apiURL);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					int responseCode = con.getResponseCode();
					BufferedReader br;
					if(responseCode == 200){
						br = new BufferedReader(new InputStreamReader((con.getInputStream()), "euc-kr"));
					} else {
						br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
					}
					String inputLine;
					StringBuffer response = new StringBuffer();
					while((inputLine = br.readLine()) != null){
						response.append(inputLine);
					}
					br.close();
					Log.i("result:", response.toString());
					return response.toString();
				}catch(Exception e){
					Log.i("error:", e.toString());
					return e.toString();
				}
		}

		@Override
		protected void onPostExecute(String result) {
			parserUser(result);
			adapter=new MyAdapter(R.layout.item, array);
			list.setAdapter(adapter);
			
		}
		
		
		
	}
	public void parserUser(String result){
		array=new ArrayList<HashMap<String, String>>();
		try{
			JSONArray jArray=new JSONArray(result);
			for(int i=0; i< jArray.length(); i++){
			JSONObject data=jArray.getJSONObject(i);
			String uid=data.getString("uid").toString();
			String upw = data.getString("upw").toString();
			String uname = data.getString("uname").toString();
			HashMap<String, String> map=new HashMap<String, String>();
			map.put("uid", uid);
			map.put("upw", upw);
			map.put("uname", uname);
			array.add(map);
			Log.i("data=", uid + ":" + upw + ":" + uname);
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	
	class ThreadUserDelete extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... strings) {
			String result ="";
			try{
				String apiURL = "http://192.168.0.52:8080/User1/delete.jsp";
				URL url = new URL(apiURL);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				
				StringBuffer params=new StringBuffer();
				params.append("uid=" + strings[0]);
				String strParams=params.toString();
				OutputStream os=con.getOutputStream();
				os.write(strParams.getBytes("UTF-8"));
				os.flush();
				os.close();
				
				int responseCode=con.getResponseCode();
				if(responseCode==200){
					result = "SUCCESS";
				} else {
					result = "FAIL";
				}
					
				return result;
			}catch(Exception e){
				return e.toString();
			}
			
		}

		@Override
		protected void onPostExecute(String result) {
			if(result == "SUCCESS"){
				Toast.makeText(MainActivity.this, "삭제되었습니다!", Toast.LENGTH_SHORT).show();
				new ThreadUserList().execute(null, null);
			}
			
		}
		
	}

}
