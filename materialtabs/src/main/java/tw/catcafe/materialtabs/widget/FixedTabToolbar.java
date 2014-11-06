package tw.catcafe.materialtabs.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import tw.catcafe.materialtabs.FixedTabLayout;
import tw.catcafe.materialtabs.TabLayout;
import tw.catcafe.materialtabs.TabToolbar;

/**
 * Created by Davy on 14/11/5.
 */
public class FixedTabToolbar extends TabToolbar {
    private LinearLayout mLayout;
    private FixedTabLayout mFixedTabLayout;

    public FixedTabToolbar(Context context) {
        this(context, null);
    }

    public FixedTabToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedTabToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
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
        if (mFixedTabLayout == null) {
            mFixedTabLayout = new FixedTabLayout(getContext());
        }
        return mFixedTabLayout;
    }

    public boolean getIconMode() { return mFixedTabLayout.getIconMode(); }

    public void setViewPager(ViewPager viewPager) {
        mFixedTabLayout.setViewPager(viewPager);
    }
}
