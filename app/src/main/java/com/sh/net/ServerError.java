package com.sh.net;

public class ServerError {
	
	public static String getErrorMessage(String code)
	{
		int i_error_code = Integer.parseInt(code);
		switch(i_error_code)
		{
		case -98:
			return "조회 결과가 없습니다.";
		case -99:
			return "데이터베이스 연결에 실패하였습니다.";
		default:
			return "알수 없는 에러 코드("+code+")";
		}		
	}

}
