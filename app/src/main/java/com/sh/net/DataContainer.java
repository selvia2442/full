package com.sh.net;

import com.sh.util.DLog;

/*
 * 서버에서 받아온 데이터를 기본적 사용형태로 변경해 가지고 있는 클래스
 */
public class DataContainer {
	public String resulttype;
	public int result;
	public String msg;

	DataItem [] base;
	DataItem [] map;
	DataTable [] lists;

	public DataContainer( DataItem [] base, DataItem [] map, DataTable [] lists ) {
		this.base = base;
		this.map = map;
		this.lists = lists;

		this.resulttype = getBaseItem( "resulttype" ).getValue( );
		String str_result = getBaseItem( "result" ).getValue( );
		try {
			this.result = Integer.parseInt( str_result );
			
		} catch ( Exception e ) {
			result = -100;
			this.msg = "서버에 응답이 없습니다";
			DLog.d( "error message", "결과 코드가 없거나 정상이 아닙니다.(" + str_result + ")" );
		}
		if ( result != -100 ){
			DLog.d( "error message", getBaseItem( "msg" ).getValue( ) );
			this.msg = getBaseItem( "msg" ).getValue( );
		}
	}

	public DataContainer( int result, String msg ) {
		this.result = result;
		this.msg = msg;
//		DLog.d( "error message", getBaseItem( "msg" ).getValue( ) );
	}
	
	public String getMessage( ) {
		return msg;
	}

	public boolean isError( ) {
		if ( result == 0 )
			return false;
		return true;
	}

	public DataItem getMapItem( int index ) {
		if ( map == null || map.length == 0 || index < 0 || index >= map.length )
			return new DataItem( null, null );
		else
			return map[index];
	}

	public DataItem getBaseItem( int index ) {
		if ( base == null || base.length == 0 || index < 0 || index >= base.length )
			return new DataItem( null, null );
		else
			return base[index];
	}

	public DataItem getMapItem( String key ) {
		if ( map == null || map.length == 0 )
			return new DataItem( null, null );

		for ( int i = 0; i < map.length; i++ ) {
			if ( map[i].getKey( ).equals( key ) )
				return map[i];
		}
		return new DataItem( null, null );
	}

	public DataItem getBaseItem( String key ) {
		if ( base == null || base.length == 0 )
			return new DataItem( null, null );

		for ( int i = 0; i < base.length; i++ ) {
			if ( base[i].getKey( ).equals( key ) )
				return base[i];
		}
		return new DataItem( null, null );
	}

	public DataTable getDataTable( int index ) {
		if ( lists == null || lists.length == 0 || index < 0 || index >= lists.length )
			return null;
		else
			return lists[index];
	}

	public DataTable getDataTable( String table_name ) {
		if ( lists == null || lists.length == 0 )
			return null;

		for ( int i = 0; i < lists.length; i++ ) {
			if ( lists[i].getTableName( ).equals( table_name ) )
				return lists[i];
		}
		return null;
	}
}
