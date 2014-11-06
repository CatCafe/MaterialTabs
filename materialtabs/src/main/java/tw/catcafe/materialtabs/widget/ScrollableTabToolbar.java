package tw.catcafe.materialtabs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import tw.catcafe.materialtabs.ScrollableTabLayout;
import tw.catcafe.materialtabs.TabLayout;
import tw.catcafe.materialtabs.TabToolbar;

/**
 * Created by Davy on 14/11/5.
 */
public class ScrollableTabToolbar extends TabToolbar {
    private LinearLayout mLayout;
    private ScrollableTabLayout mScrollableTabLayout;

    public ScrollableTabToolbar(Context context) {
        this(context, null);
    }

    public ScrollableTabToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableTabToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected ViewGroup getLayout() {
        if (mLayout == null) {
            mLayout = new LinearLayout(getContext());
            mLayout.addView(getTabLayout(), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        return mLayout;
    }

    @Override
    protected TabLayout getTabLayout() {
        if (mScrollableTabLayout == null) {
            mScrollableTabLayout = new ScrollableTabLayout(getContext());
        }
        return mScrollableTabLayout;
    }
}
