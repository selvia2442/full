package com.sh.net;

/*
 * DataContainer의 Table 기본형
 */
public class DataTable {
	private String[] columns = null; // 컬럼값
	private String[][] rows = null; //row 데이터 모음 , 한개의 row는 컬럼과 길이가 같다.
	private String table_name = null; // 각 테이블의 명칭(DataConainer에서 명칭 또는 index로 테이블을 찾는다.)
	
	public DataTable(String name, String[] columns, String[][] rows)
	{
		table_name = name;
		this.columns = columns;
		this.rows = rows;
	}
	
	public String getTableName()
	{
		return table_name;
	} 
	
	public String[] getColumns()
	{
		return columns;
	}
	
	public int getColumnSize()
	{
		if(columns==null) return 0;
		return columns.length;
	}
	
	public String getColumn(int index)
	{
		 if(index<0 || index>=columns.length) return null;
		 return columns[index];
	}
	
	public String[][] getRows()
	{
		return rows;
	}
	
	public String[] getRow(int index)
	{
		if(index<0 || index>=rows.length) return null;
		return rows[index];		
	}
	
	public int getRowsSize()
	{
		if(rows==null) return 0;
		return rows.length;
	}
	
	public String getRowColumnData(int index, int col)
	{
		if(index<0 || col<0 || index>=rows.length || col>=columns.length) return null;
		return rows[index][col];		
	}
	
	public String getRowColumnData(int index, String col)
	{
		if(index<0 || index>=rows.length) return null;
		int iCol = -1;
		for(int i = 0;i<columns.length;i++)
		{
			if(columns[i].equals(col)) 
			{
				iCol = i;
				break;
			}
		}
		if(iCol==-1) return null;
		return rows[index][iCol];
	}
}
