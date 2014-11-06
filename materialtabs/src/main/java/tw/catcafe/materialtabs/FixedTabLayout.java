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
    public void addTab(TabItem tab) {
        if (mTabsList.size() == 3) // full
            throw new RuntimeException("Number of tab is max to 3.");

        styleTab(tab);

        tab.setPosition(mTabsList.size());
        tab.setTabListener(this);
        mTabsList.add(tab);
        if (mTabsList.size() == 1) // the only one
            tab.activate();

        // get the tab width
        int tabWidth = getScreenWidth() / 3; // in spec, it's 1/3 of screen
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                tabWidth,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(tab.getView(), layoutParams);
    }

    private int getScreenWidth() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();

        display.getSize(p);

        return p.x;
    }
}
