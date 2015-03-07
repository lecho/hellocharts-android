package lecho.lib.hellocharts.view.hack;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Hacky fix for issue with DrawerLayout https://github.com/chrisbanes/PhotoView/issues/72
 */
public class HackyDrawerLayout extends DrawerLayout {

    public HackyDrawerLayout(Context context) {
        super(context);
    }

    public HackyDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HackyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
