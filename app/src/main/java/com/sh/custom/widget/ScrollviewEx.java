package com.sh.custom.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.sh.util.DLog;

public class ScrollviewEx  extends ScrollView {
	/// 스크롤 맨 아래까지 되면 처리할 이벤트 전달용 핸들
	 Handler m_hd = null ;
	 /// 스크롤뷰 영역 체크하려고 두는 변수에용
	 Rect m_rect ;
	 
	 /// 커스텀 View를 사용하려면 반드시!! 아래와 같은 (Context context, AttributeSet attrs) 를 인자로하는
	 /// 생성자를 쓰셔야 합니다!!!
	 public ScrollviewEx(Context context, AttributeSet attrs) 
	 {
		 super(context, attrs);
		 // TODO Auto-generated constructor stub		  
	 }
	 
	 /// 그리기가 끝나면 체크하기 위해 오버라이드
	 @Override
	 protected void onDraw(Canvas canvas) {
	  // TODO Auto-generated method stub
	  super.onDraw(canvas);
	  checkIsLocatedAtFooter( ) ; /// 여기서 그리기 끝나면 함수 콜
	  
	 }
	 
	 /// 제가 만든 사용자용 함수입니다.
	 private void checkIsLocatedAtFooter() 
	 {
		 if( m_rect == null )     /// 처음에는 Rect가 없을테니....
		 {
			 m_rect = new Rect( ) ;    /// new합니다.
			 getLocalVisibleRect( m_rect ) ;  /// 스크롤 영역 구합니다.(저는 0,480,0,696 이던가 했네요)
			 return ;       /// 그리고 걍 리턴합니다.
		 }
		 int oldBottom = m_rect.bottom;   /// 이전 bottom저장 이유는 맨아래인 상태에서 아래로 스크롤 했을떄 쌩까려구요
		  
		 getLocalVisibleRect( m_rect ) ;   /// 현재 스크롤뷰의 영역을 구합니다.
		            /// 이때 스크롤 이동시켰으면 top와 bottom값이 이동한 만큼 변합니다.
		   
		 int height = getMeasuredHeight( ) ;  /// 스크롤 뷰의 높이를 구합니다.
		  
		 View v = getChildAt( 0 ) ;    /// 스크롤 뷰 안에 들어있는 내용물의 높이도 구합니다.
		  
		 if (oldBottom > 0 && height > 0)   /// 스크롤 뷰나 이전 bottom이 0 이상이어야만 처리
		 {
			 /// bottom값의 변화가 없으면 처리 안해요 
			 /// 그리고 현재 bottom이 내용물의 맨 아래까지 왔으면 맨 아래까지 스크롤 한겁니다.
			 if (oldBottom != m_rect.bottom && m_rect.bottom == v.getMeasuredHeight( ) ) 
			 {
				 // 끝에 왔을 때의 처리
				 DLog.d("ghlab", "끝에 왔을 때의 처리");
			 
				 /// 핸들러가 처음에는 널인데 사용자가 셋팅해주면 그 핸들러로 메세지 날립니다.
				 if( m_hd != null )
				 {
					 /// 핸들러에 이벤트 날리면 끗납니다.
					 m_hd.sendEmptyMessage( 1 ) ;
				 }
			 }
		 }
	 }
	 
	 /// 맨 아래까지 갔을때 처리하기 위해 있는 녀석입니다.
	 public void setHandler( Handler hd )
	 {
	  m_hd = hd ; 
	 }
}
