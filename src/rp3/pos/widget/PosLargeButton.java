package rp3.pos.widget;

import rp3.pos.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class PosLargeButton extends FrameLayout {

	private String text;	
	private int drawableId;
	private Context context;
	private View contentView;
	
	public PosLargeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.PosLargeButton, 0, 0);

		try {
			drawableId = a.getInt(R.styleable.PosLargeButton_drawable,0);
			text = a.getString(R.styleable.PosLargeButton_text);			
		} finally {
			a.recycle();
		}
		
		initView();
	}	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {				
						
		int eventaction = event.getAction(); 
	      switch (eventaction ) {
	      case MotionEvent.ACTION_POINTER_DOWN:
	      case MotionEvent.ACTION_DOWN:
	    	  contentView.setBackgroundColor(getResources().getColor(R.color.button_face_ontouch));
	    	  invalidate();
	  		  requestLayout();	  		  
	    	  break;
	      case MotionEvent.ACTION_UP:
	    	  contentView.setBackgroundColor(getResources().getColor(R.color.button_face));
	    	  invalidate();
	  		  requestLayout();
	    	  break;
	      }
		
		return true;
	}
	
	private void initView(){
		View v = inflate(context, R.layout.customview_largebutton, null);
		addView(v);				
		contentView = findViewById(android.R.id.content);		
	}
		
	
	@Override
	protected void onDraw(Canvas canvas) {	
		super.onDraw(canvas);
		
		
	}
	
	public String getText(){
		return this.text;
	}
	
	public void setText(String text){
		this.text = text;
		
		invalidate();
		requestLayout();
	}
	
	public void setText(int id){
		this.text = getResources().getText(id).toString();
	}
	
	public int getDrawable(){
		return drawableId;
	}
	
	public void setDrawable(int id){
		this.drawableId = id;
		
		
		invalidate();
		requestLayout();
	}
}
