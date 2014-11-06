package tw.catcafe.materialtabs;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import at.markushi.ui.RevealColorView;

/**
 * Created by Davy on 14/11/5.
 */
public abstract class TabLayout extends LinearLayout {
    protected int mPrimaryColor;
    protected int mAccentColor;
    protected int mColor;
    protected boolean mIconMode = false;
    protected List<TabTouchListener> mTabsList;
    protected ViewPager mViewPager;
    protected ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

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

        // initialize tabs list
        mTabsList = new LinkedList<TabTouchListener>();
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
        updateTabStyle();
    }

    public void setPrimaryColor(int color) {
        mPrimaryColor = color;
        updateTabStyle();
    }

    public void setColor(int color) {
        mColor = color;
        updateTabStyle();
    }

    public void setIconMode(boolean iconMode) {
        mIconMode = iconMode;
        populateTabStrip(); // Re-build tab views
    }

    public int getAccentColor() { return mAccentColor; }

    public int getPrimaryColor() { return mPrimaryColor; }

    public int getColor() { return mColor; }

    public int getDisabledColor() {
        return Color.argb(
            (int) (Color.alpha(mColor) * 0.6),
            Color.red(mColor),
            Color.green(mColor),
            Color.blue(mColor)
        );
    }

    public boolean getIconMode() { return mIconMode; }

    public void setViewPager(ViewPager viewPager) {
        if (mViewPager == viewPager) // not modified
            return;

        if (mViewPager != null) // release listener
            mViewPager.setOnPageChangeListener(null);

        mViewPager = viewPager;
        if (mViewPager != null) { // set listener for sliding pages
            mViewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    protected abstract ViewGroup.LayoutParams createLayoutParams();

    protected void populateTabStrip() {
        if (mViewPager == null || mViewPager.getAdapter() == null)
            return;

        final FragmentPagerAdapter adapter = (FragmentPagerAdapter)mViewPager.getAdapter();
        final ViewGroup.LayoutParams layoutParams = createLayoutParams();

        this.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            // Create tab view here
            View tabView;

            if (getIconMode()) {
                tabView = LayoutInflater.from(getContext()).inflate(R.layout.material_tab_icon, null);
                ((ImageView) tabView.findViewById(R.id.icon)).setImageDrawable(adapter.getPageIcon(i));
            } else {
                tabView = LayoutInflater.from(getContext()).inflate(R.layout.material_tab, null);
                ((TextView) tabView.findViewById(R.id.text)).setText(adapter.getPageTitle(i));
            }

            tabView.setOnTouchListener(new InternalTabTouchListener());

            this.addView(tabView, layoutParams);
            updateTabStyle(i);
        }
    }

    protected void updateTabStyle() {
        for (int i = 0; i < mViewPager.getAdapter().getCount(); ++i)
            updateTabStyle(i);
    }

    protected void updateTabStyle(int i) {
        View tabView = this.getChildAt(i);
        int textColor, iconAlpha;

        if (i == mViewPager.getCurrentItem()) { // set 100% alpha
            textColor = getColor();
            iconAlpha = 0xFF;
        } else { // set 60% alpha
            textColor = getDisabledColor();
            iconAlpha = 0x99;
        }

        if (getIconMode()) {
            ((ImageView) tabView.findViewById(R.id.icon)).setImageAlpha(iconAlpha);
        } else {
            ((TextView) tabView.findViewById(R.id.text)).setTextColor(textColor);
        }
    }

    /**
     * Listeners here.
     * Inner class can access parent members directly and make parent simply.
     */

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        int mLastPosition;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(
                        position,
                        positionOffset,
                        positionOffsetPixels
                );
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            updateTabStyle(mLastPosition);
            updateTabStyle(position);
            mLastPosition = position;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }
    }

    private class InternalTabTouchListener extends TabTouchListener {
        @Override
        protected int getTouchAccentColor() {
            return Color.argb(
                    Color.alpha(TabLayout.this.getAccentColor()) * 0x88 / 0xff,
                    Color.red(TabLayout.this.getAccentColor()),
                    Color.green(TabLayout.this.getAccentColor()),
                    Color.blue(TabLayout.this.getAccentColor())
            );
        }

        @Override
        protected int getPrimaryColor() {
            return TabLayout.this.getPrimaryColor();
        }

        @Override
        protected void onTabActive(View tabView) {
            int i = indexOfChild(tabView);
            if (i >= 0) {
                mViewPager.setCurrentItem(i);
            } else {
                throw new RuntimeException("Cannot active not contained tab.");
            }
        }
    }
}
