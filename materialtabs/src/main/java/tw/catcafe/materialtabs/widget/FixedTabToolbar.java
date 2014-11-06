package tw.catcafe.materialtabs.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import tw.catcafe.materialtabs.FixedTabLayout;
import tw.catcafe.materialtabs.TabItem;

/**
 * Created by Davy on 14/11/5.
 */
public class FixedTabToolbar extends Toolbar {
    private FixedTabLayout mFixedTabLayout;

    public FixedTabToolbar(Context context) {
        this(context, null);
    }

    public FixedTabToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedTabToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // remove padding left / right
        setContentInsetsRelative(0, 0);

        mFixedTabLayout = new FixedTabLayout(context);

        addView(
                mFixedTabLayout,
                new Toolbar.LayoutParams(
                        Toolbar.LayoutParams.MATCH_PARENT,
                        Toolbar.LayoutParams.MATCH_PARENT
                )
        );
    }

    public FixedTabToolbar addTab(TabItem tabItem) {
        mFixedTabLayout.addTab(tabItem);
        return this;
    }

    public void setViewPager(ViewPager viewPager) {
        mFixedTabLayout.setViewPager(viewPager);
    }

    public TabItem newTab() {
        return mFixedTabLayout.newTab();
    }
}
