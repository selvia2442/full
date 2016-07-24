package com.sh.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sh.net.SHWebClient.Method;
import com.sh.net.SHWebClient.ProgressCallback;
import com.sh.util.DLog;

public class SHJSONClient {

	SHWebClient webclient;

	// constructor
	public SHJSONClient() {
		webclient = new SHWebClient();
	}

	public SHJSONClient( String url ) {
		webclient = new SHWebClient();
		webclient.setURL( url );
	}

	public void init() {
		webclient.initNameValuePair();
		webclient.initProgressCallback();
	}

	public void setMultipartFormProgressCallback( ProgressCallback progressCallback ) {
		webclient.setOnProgressCallback( progressCallback );
	}

	public void setURL( String url ) {
		webclient.setURL( url );
	}

	public void setMethod( Method method ) {
		webclient.setMethod( method );
	}

	public void addParam( String key, String val ) {
		webclient.setParam( key, val );
	}

	public void addFile( String key, String val ) {
		webclient.setFileParam( key, val );
	}

	public DataContainer Request() {
		// Web Request
		String ret;
		try {
			ret = webclient.Request();
			if ( webclient.isError() ) {
				DLog.e( "SHJsonClient.Request", "Error web request " + webclient.getError() );
				return new DataContainer( -100, "네트워크에 장애가 발생했습니다. 네트워크를 확인해 주세요.(-1)" );
			}

		} catch ( Exception ex ) {
			DLog.e( "SHJsonClient.Request", "Error web request " + ex.toString() );
			return new DataContainer( -100, "네트워크에 장애가 발생했습니다. 네트워크를 확인해 주세요.(-2)" );
		}

		// make JSON Object
		try {
			ArrayList<DataItem> arr_base = new ArrayList<DataItem>();
			DataItem[] base = null;
			DataItem[] map = null;
			DataTable[] tables = new DataTable[1];
			JSONObject jObj = new JSONObject( ret );
			JSONArray names = jObj.names();
			int size = names.length();
			for ( int i = 0; i < size; i++ ) {
				String name = (String) names.get( i );
				if ( name.equals( "map" ) ) {
					map = getDataItems( jObj.getJSONObject( name ) );
				} else if ( name.equals( "list" ) ) {
					tables[0] = getDataTable( jObj.getJSONArray( name ) );
				} else {
					DataItem item = new DataItem( name, jObj.getString( name ) );
					arr_base.add( item );
				}
			}
			base = arr_base.toArray( new DataItem[arr_base.size()] );
			DataContainer cont = new DataContainer( base, map, tables );
			return cont;// new DataContainer( base, map, tables );

		} catch ( JSONException e ) {
			DLog.e( "JSON Parser", "Error parsing data " + e.toString() );
			return new DataContainer( -100, "네트워크에 장애가 발생했습니다. 네트워크를 확인해 주세요.(-3)" );
		} catch ( Exception ex ) {
			DLog.e( "JSON Parser", "Error parsing data " + ex.toString() );
			return new DataContainer( -100, "네트워크에 장애가 발생했습니다. 네트워크를 확인해 주세요.(-4)" );
		}
	}

	private DataItem[] getDataItems( JSONObject jObj ) {
		try {
			ArrayList<DataItem> arr_map = new ArrayList<DataItem>();
			JSONArray names = jObj.names();
			int size = names.length();

			for ( int i = 0; i < size; i++ ) {
				String name = (String) names.get( i );
				DataItem item = new DataItem( name, jObj.getString( name ) );
				arr_map.add( item );
			}
			return arr_map.toArray( new DataItem[arr_map.size()] );
		} catch ( Exception ex ) {
			return null;
		}
	}

	private DataTable getDataTable( JSONArray jArr ) {
		try {
			int size = jArr.length();
			String[] columns = null;
			String[][] rows = new String[size][];
			int i_col_size = 0;

			for ( int i = 0; i < size; i++ ) {
				JSONObject jObj = jArr.getJSONObject( i );
				// column정보 가져오자
				if ( i == 0 ) {
					i_col_size = jObj.names().length();
					columns = new String[i_col_size];
					for ( int j = 0; j < i_col_size; j++ ) {
						columns[j] = jObj.names().getString( j );
					}
				}
				// row 설정
				rows[i] = new String[i_col_size];
				for ( int j = 0; j < i_col_size; j++ ) {
					rows[i][j] = jObj.getString( columns[j] );
				}

			}
			return new DataTable( "list", columns, rows );
		} catch ( Exception ex ) {
			return null;
		}
	}

}
