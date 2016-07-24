package com.sh.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sh.net.SHWebClient.Method;
import com.sh.net.SHWebClient.ProgressCallback;

import android.util.Log;

public class SHJSONYOUTUBEClient {
	
	SHWebClient webclient;
	 
	// constructor
	public SHJSONYOUTUBEClient() {
		webclient = new SHWebClient();		
	}
	
	public SHJSONYOUTUBEClient(String url) {
		webclient = new SHWebClient();
		webclient.setURL(url);
	}
	
	public void init()
	{
		webclient.initNameValuePair();
		webclient.initProgressCallback();
	}
	
	public void setMultipartFormProgressCallback(ProgressCallback progressCallback)
	{
		webclient.setOnProgressCallback(progressCallback);
	}
	
	public void setURL(String url)
	{
		webclient.setURL(url);
	}
	
	public void setMethod(Method method)
	{
		webclient.setMethod(method);
	}
	
	public void addParam(String key, String val)
	{
		webclient.setParam(key,val);
	}
	
	public void addFile(String key, String val)
	{
		webclient.setFileParam(key, val);
	}
	
	public DataContainer Request()
	{
		//Web Request
		String ret;
		try{
			ret = webclient.Request();
			if(webclient.isError())
			{
				Log.e("SHJsonClient.Request", "Error web request " + webclient.getError());
				return new DataContainer(-100, "웹서버 요청중 오류가 발생하였습니다.");
			}

		}catch(Exception ex) {
			Log.e("SHJsonClient.Request", "Error web request " + ex.toString());
			return new DataContainer(-100, "웹서버 요청중 오류가 발생하였습니다.");
		}
		
		//make JSON Object
		try {
			ArrayList<DataItem> arr_base = new ArrayList<DataItem>();
			DataItem[] base = null;
			DataItem[] map = null;
			DataTable[] tables = new DataTable[1];
			
			JSONObject  jObj = new JSONObject(ret);		
			JSONObject  jObj_data = jObj.getJSONObject("data");
			if(jObj_data!=null && jObj_data.length()!=0)
				tables[0] = getDataTable(jObj_data.getJSONArray("items"));
			
			arr_base.add(new DataItem("resulttype","list"));
			arr_base.add(new DataItem("result","0"));
			arr_base.add(new DataItem("msg","성공"));
			base = arr_base.toArray(new DataItem[arr_base.size()]);
			map = arr_base.toArray(new DataItem[arr_base.size()]);
			return new DataContainer(base, map, tables);
			
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			return new DataContainer(-100, "웹서버에의 데이터에 오류가 있습니다.");
		} catch (Exception ex){
			Log.e("JSON Parser", "Error parsing data " + ex.toString());
			return new DataContainer(-100, "웹서버의 데이터에 오류가 있습니다.");
		}
	}
	
	private DataTable getDataTable(JSONArray jArr)
	{	
		try{			
			int size = jArr.length();			
			String[] columns = new String[]{"id","title","description","uploader","duration","uploaded","viewCount"}; //colum값을 정해놓자..
			String[][] rows = new String[size][];
			int i_col_size = columns.length;
			
			for(int i =0 ; i<size ; i++)				
			{
				JSONObject jObj = jArr.getJSONObject(i);
				//row 설정
				rows[i] = new String[i_col_size];
				for(int j =0 ; j<i_col_size ; j++)				
				{
					rows[i][j] = jObj.getString(columns[j]);
				}
				
				
			}
			return new DataTable("list", columns, rows);
		}catch(Exception ex)
		{
			return null;
		}
	}	 
	
}
