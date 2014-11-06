package tw.catcafe.materialtabs;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by Davy on 14/11/5.
 */
public class FixedTabLayout extends TabLayout {

    public FixedTabLayout(Context context) {
        super(context, null);
    }

    public FixedTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public FixedTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected ViewGroup.LayoutParams createLayoutParams() {
        return new ViewGroup.LayoutParams(
                getScreenWidth() / 3,// in spec, it's 1/3 of screen
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private int getScreenWidth() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();

        display.getSize(p);

        return p.x;
    }
}
