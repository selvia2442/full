package com.sh.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.sh.util.DLog;


/* HTTP를 이용하여 서버에 인자값을 전달하고 그 결과를 전송받는다.
 * GET/POST/MULTIPOARTFORM을 이용하여 서버에 전송할 수 있다
 */
public class SHWebClient {

	public interface ProgressCallback {
		//중간
		void onProgressUpdate( int position, int max );

		//종료
		void onPostExecute( );
	}

	private ProgressCallback prgcallback = null;

	public void OnProgressCallback( ProgressCallback progressCallback ) {
		prgcallback = progressCallback;
	}

	public void initProgressCallback( ) {
		prgcallback = null;
	}

	public enum Method {
		GET, POST, MULTIPARTFORM
	};

	protected Method method = Method.GET;
	protected String URL = null;
	protected List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 2 );
	protected List<NameValuePair> fileNames = new ArrayList<NameValuePair>( );
	protected CookieStore cookie = null;
	protected String encoding = "utf-8";
	protected String error_msg = null;
	protected int timeout = 30000;

	public SHWebClient( ) {
	}

	//callback을 설정한다.
	public void setOnProgressCallback( ProgressCallback progressCallback ) {
		OnProgressCallback( progressCallback );
	}

	//서버로 전송할 데이터의 초기화
	public void initNameValuePair( ) {
		nameValuePairs.clear( );
		fileNames.clear( );
	}

	public boolean isError( ) {
		if ( error_msg == null )
			return false;
		return true;
	}

	public String getError( ) {
		if ( error_msg == null )
			return null;
		return error_msg;
	}

	public void setTimeOut( int second ) {
		timeout = second * 1000;
	}

	public void setMethod( SHWebClient.Method method ) {
		this.method = method;
	}

	public void setURL( String url ) {
		URL = url;
	}

	public void setEncoding( String encoding ) {
		this.encoding = encoding;
	}

	public void setParam( String name, String val ) {
		nameValuePairs.add( new BasicNameValuePair( name, val ) );
	}

	public boolean setFileParam( String name, String val ) {
		if ( method != Method.MULTIPARTFORM )
			return false;
		File f = new File( val );
		if ( f.isFile( ) ) {
			fileNames.add( new BasicNameValuePair( name, val ) );

			return true;
		}
		return false;
	}

	public String Request( ) {
		if ( method == Method.MULTIPARTFORM ) {
			return MultipartformRequest( );
		} else if ( nameValuePairs.size( ) == 0 || method == Method.GET ) {
			return GetRequest( );
		} else {
			return PostRequest( );
		}
	}

	public String getResponseContent( HttpResponse response ) {
		StringBuilder sb = new StringBuilder( );
		try {
			int maxlen = (int) ( response.getEntity( ).getContentLength( ) / 100 );
			int position = 0;
			if ( response != null ) {
				BufferedReader br = new BufferedReader( new InputStreamReader( response.getEntity( ).getContent( ) ) );
				String str;
				while ( ( str = br.readLine( ) ) != null ) {
					position++;
					if ( prgcallback != null && maxlen != 0 && 0 == ( position % maxlen ) ) //progressbar에서 사용할 현재 진행현황
						if ( (int) ( position / 100 ) > maxlen )
							prgcallback.onProgressUpdate( maxlen, maxlen );
						else
							prgcallback.onProgressUpdate( (int) ( position / 100 ), maxlen );
					sb.append( str ).append( "\n" );
				}
			}
		} catch ( Exception e ) {
			DLog.e( "Error", "getResponseContent " + e.getLocalizedMessage() );
		}
		return sb.toString( );
	}

	public String MultipartformRequest( ) {
		error_msg = null;

		DefaultHttpClient httpclient = new DefaultHttpClient( );
		//TimeOut setting
		httpclient.getParams( ).setParameter( "http.protocol.expect-continue", false );
		httpclient.getParams( ).setParameter( "http.connection.timeout", timeout );
		httpclient.getParams( ).setParameter( "http.socket.timeout", timeout );

		HttpPost httppost = new HttpPost( URL );
		String result = null;

		try {
			if ( cookie != null )
				httpclient.setCookieStore( cookie );
			MultipartEntity reqEntity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );

			if ( fileNames.size( ) > 0 ) {
				for ( int i = 0; i < fileNames.size( ); i++ ) {
					Log.e("fileNames","...."+fileNames.get( i ).getValue( ));
					File file = new File( fileNames.get( i ).getValue( ) );
					FileBody bin = new FileBody( file );
					String v_key = fileNames.get( i ).getName( );
					reqEntity.addPart( v_key, bin );
				}
			}

			if ( nameValuePairs.size( ) > 0 ) {
				for ( int i = 0; i < nameValuePairs.size( ); i++ ) {
					StringBody stringBody = new StringBody( nameValuePairs.get( i ).getValue( ) );
					String v_key = nameValuePairs.get( i ).getName( );
					reqEntity.addPart( v_key, stringBody );
				}
			}

			//reqEntity.addPart(key, bin);
			httppost.setEntity( reqEntity );
			HttpResponse response = httpclient.execute( httppost );
			//HttpEntity resEntity = response.getEntity();  

			if ( response != null ) {
				result = getResponseContent( response );
				if ( httpclient.getCookieStore( ) != null )
					cookie = httpclient.getCookieStore( );
				nameValuePairs.clear( );
				fileNames.clear( );
			}

			return result;
		} catch ( ClientProtocolException e ) {
			error_msg = e.getMessage( );
			if ( error_msg == null )
				error_msg = "ClientProtocolException at WebClient";
			return null;
		} catch ( IOException e ) {
			error_msg = e.getMessage( );
			if ( error_msg == null )
				error_msg = "IOException at WebClient";
			return null;
		}
	}

	public String PostRequest( ) {
		error_msg = null;

		DLog.d( "request", URL + nameValuePairs.toString( ) );

		DefaultHttpClient httpclient = new DefaultHttpClient( );
		//TimeOut setting
		httpclient.getParams( ).setParameter( "http.protocol.expect-continue", false );
		httpclient.getParams( ).setParameter( "http.connection.timeout", timeout );
		httpclient.getParams( ).setParameter( "http.socket.timeout", timeout );

		HttpPost httppost = new HttpPost( URL );
		String result = null;

		try {
			if ( cookie != null )
				httpclient.setCookieStore( cookie );
			httppost.setEntity( new UrlEncodedFormEntity( nameValuePairs, encoding ) );
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute( httppost );
			if ( response != null ) {
				result = getResponseContent( response );
				if ( httpclient.getCookieStore( ) != null )
					cookie = httpclient.getCookieStore( );
				nameValuePairs.clear( );
			}
			return result;
		} catch ( ClientProtocolException e ) {
			error_msg = e.getMessage( );
			if ( error_msg == null )
				error_msg = "ClientProtocolException at WebClient";
			return null;
		} catch ( IOException e ) {
			error_msg = e.getMessage( );
			if ( error_msg == null )
				error_msg = "IOException at WebClient";
			return null;
		}
	}

	public String GetRequest( ) {
		error_msg = null;
		DefaultHttpClient httpclient = new DefaultHttpClient( );
		//TimeOut setting
		httpclient.getParams( ).setParameter( "http.protocol.expect-continue", false );
		httpclient.getParams( ).setParameter( "http.connection.timeout", timeout );
		httpclient.getParams( ).setParameter( "http.socket.timeout", timeout );

		String strUrl = URL;
		if ( nameValuePairs != null && nameValuePairs.size( ) != 0 ) {
			strUrl += "?";
			strUrl += URLEncodedUtils.format( nameValuePairs, encoding );
		}
		HttpGet httpget = new HttpGet( strUrl );
		String result = null;

		try {
			if ( cookie != null )
				httpclient.setCookieStore( cookie );
			HttpResponse response = httpclient.execute( httpget );
			if ( response != null ) {
				result = getResponseContent( response );
				if ( httpclient.getCookieStore( ) != null )
					cookie = httpclient.getCookieStore( );
				nameValuePairs.clear( );
			}
			return result;
		} catch ( ClientProtocolException e ) {
			error_msg = e.getMessage( );
			if ( error_msg == null )
				error_msg = "ClientProtocolException at WebClient";
			return null;
		} catch ( IOException e ) {
			error_msg = e.getMessage( );
			if ( error_msg == null )
				error_msg = "IOException at WebClient";
			return null;
		}
	}
}
