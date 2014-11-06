package tw.catcafe.materialtabs;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Davy on 14/11/5.
 */
public abstract class TabLayout extends LinearLayout implements TabListener {
    protected int mPrimaryColor;
    protected int mAccentColor;
    protected int mColor;
    protected boolean mIconMode = false;
    protected List<TabItem> mTabsList;
    protected int mSelectedItem = -1;
    protected ViewPager mViewPager;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // remove padding left / right
        super.setPadding(0, 0, 0, 0);

        extractColorsFromTheme();

        // get attributes
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TabLayout, 0, 0);
            try {
                mIconMode = a.getBoolean(R.styleable.TabLayout_iconMode, false);
            } finally {
                a.recycle();
            }
        }

        // initialize tabs list
        mTabsList = new LinkedList<TabItem>();
    }

    protected void extractColorsFromTheme() {
        Resources.Theme theme = getContext().getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        mPrimaryColor = typedValue.data;
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        mAccentColor = typedValue.data;
        theme.resolveAttribute(R.attr.actionMenuTextColor, typedValue, true);
        mColor = typedValue.data;
    }

    public void setAccentColor(int color) {
        mAccentColor = color;

        for (TabItem tabItem : mTabsList) {
            tabItem.setAccentColor(color);
        }
    }

    public void setPrimaryColor(int color) {
        mPrimaryColor = color;

        for (TabItem tabItem : mTabsList) {
            tabItem.setPrimaryColor(color);
        }
    }

    public void setColor(int color) {
        mColor = color;

        for (TabItem tabItem : mTabsList) {
            tabItem.setColor(color);
        }
    }

    public void setIconMode(boolean iconMode) {
        mIconMode = iconMode;

        for (TabItem tabItem : mTabsList) {
            tabItem.setIconMode(iconMode);
        }
    }

    public int getAccentColor() { return mAccentColor; }

    public int getPrimaryColor() { return mPrimaryColor; }

    public int getColor() { return mColor; }

    public boolean getIconMode() { return mIconMode; }

    public abstract void addTab(TabItem tab);

    public TabItem newTab() {
        return new TabItem(this.getContext());
    }

    protected void styleTab(TabItem tabItem) {
        tabItem.setAccentColor(getAccentColor());
        tabItem.setPrimaryColor(getPrimaryColor());
        tabItem.setColor(getColor());
        tabItem.setIconMode(getIconMode());
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                mTabsList.get(position).activate();
            }
        });
        sync();
    }

    void sync() {
        if (mViewPager != null) {
            int id = mViewPager.getCurrentItem();
            if (id >= mTabsList.size())
            {
                id = 0;
                mViewPager.setCurrentItem(0);
            }
            mTabsList.get(id).activate();
        }
    }

    public void onTabSelected(TabItem tab) {
        if (mSelectedItem != -1)
            mTabsList.get(mSelectedItem).disable();
        mSelectedItem = tab.getPosition();

        if (mViewPager != null)
            mViewPager.setCurrentItem(tab.getPosition());
    }

    public void onTabReselected(TabItem tab) {
        // do nothing
    }

    public void onTabUnselected(TabItem tab) {
        // do nothing
    }
}
