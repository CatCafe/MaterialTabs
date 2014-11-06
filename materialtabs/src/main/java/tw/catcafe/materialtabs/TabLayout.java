package tw.catcafe.materialtabs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import at.markushi.ui.util.BakedBezierInterpolator;

/**
 * Created by Davy on 14/11/5.
 */
public abstract class TabLayout extends RelativeLayout {
    protected LinearLayout mLinearTabLayout;
    protected View mIndicatorView;
    protected int mPrimaryColor;
    protected int mAccentColor;
    protected int mColor;
    protected boolean mIconMode = false;
    protected List<TabTouchListener> mTabsList;
    protected ViewPager mViewPager;
    protected ViewPager.OnPageChangeListener mViewPagerPageChangeListener;
    protected OnIndicatorMoveListener mIndicatorMoveListener;

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

        initLayouts();
    }

    protected ViewGroup getMasterLayout() { return this; }

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

    protected void initLayouts() {
        mLinearTabLayout = new LinearLayout(getContext());
        getMasterLayout().addView(
                mLinearTabLayout,
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                )
        );

        mIndicatorView = new View(getContext());
        mIndicatorView.setBackgroundColor(getAccentColor());
        mIndicatorView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        getMasterLayout().addView(
                mIndicatorView,
                new RelativeLayout.LayoutParams(0, 0)
        );
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

            try {
                Field mScroller;
                mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                SwiftScroller scroller = new SwiftScroller(mViewPager.getContext(), BakedBezierInterpolator.getInstance());
                mScroller.set(mViewPager, scroller);
            } catch (NoSuchFieldException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }

            populateTabStrip();
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    protected abstract ViewGroup.LayoutParams createLayoutParams();

    protected void populateTabStrip() {
        if (mViewPager == null || mViewPager.getAdapter() == null)
            return;

        final FragmentPagerAdapter adapter = (FragmentPagerAdapter)mViewPager.getAdapter();
        final ViewGroup.LayoutParams layoutParams = createLayoutParams();

        mLinearTabLayout.removeAllViews();
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

            mLinearTabLayout.addView(tabView, layoutParams);
            updateTabStyle(i);
        }
    }

    protected void updateTabStyle() {
        for (int i = 0; i < mViewPager.getAdapter().getCount(); ++i)
            updateTabStyle(i);
    }

    protected void updateTabStyle(int i) {
        View tabView = mLinearTabLayout.getChildAt(i);
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

    protected void updateIndicator(int leftPosition, int rightPosition, float offset, boolean manually) {
        final int tabCount = mLinearTabLayout.getChildCount();
        final int indicatorHeight = getResources().getDimensionPixelSize(R.dimen.material_tab_indicator_height);

        // Thick colored underline below the current selection
        if (tabCount > 0) {
            View leftTab = mLinearTabLayout.getChildAt(leftPosition);
            int leftTabMiddle = (leftTab.getLeft() + leftTab.getRight()) / 2;
            int rightTabMiddle = leftTabMiddle;
            int indicatorLeft = leftTab.getLeft();
            int indicatorRight = leftTab.getRight();

            if (leftPosition != rightPosition && rightPosition < tabCount) { // We are moving!
                View rightTab = mLinearTabLayout.getChildAt(rightPosition);
                rightTabMiddle = (rightTab.getLeft() + rightTab.getRight()) / 2;
                // Use Bezier line to match Lollipop effect
                float swift = BakedBezierInterpolator.getInstance().getInterpolation(offset);
                float swiftOut = 1.0f - BakedBezierInterpolator.getInstance().getInterpolation(1.0f - offset);
                indicatorLeft = Math.max(
                        (int)(swiftOut * rightTab.getLeft() + (1.0f - swiftOut) * leftTab.getLeft()),
                        leftTab.getLeft()
                );
                indicatorRight = Math.min(
                        (int)(swift * rightTab.getRight() + (1.0f - swift) * leftTab.getRight()),
                        rightTab.getRight()
                );
            }

            mIndicatorView.setTranslationX(indicatorLeft);
            mIndicatorView.setTranslationY(getHeight() - indicatorHeight);
            mIndicatorView.setLayoutParams(new RelativeLayout.LayoutParams(
                    indicatorRight - indicatorLeft, indicatorHeight));

            if (mIndicatorMoveListener != null)
                mIndicatorMoveListener.OnIndicatorMove(indicatorLeft, indicatorRight,
                        leftTabMiddle, rightTabMiddle, manually);
        } else {
            mIndicatorView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        }
    }

    /**
     * Listeners here.
     * Inner class can access parent members directly and make parent simply.
     */

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mLastPosition;
        private int mLastUpdate;
        private int mSlidingMode = 0;

        /**
         * NOTE: We have two method when moving pages.
         *
         *   A. Click on tab (setCurrentItem(target)), may roll multiple pages
         *      status[IDLE] --(Click on tab)--> status[SETTLING] --> onPageSelected(targetPage) -->
         *      onPageScrolled(current ~ target) --> status[IDLE]
         *
         *   B. Sliding through
         *      status[IDLE] --(Sliding)--> status[DRAGGING] -->
         *      onPageScrolled(leftPage ~ rightPage) --(Lift hand, non-manually sliding)-->
         *      status[SETTLING] --> onPageSelected(targetPage) -->
         *      onPageScrolled(leftPage ~ rightPage) --> status[IDLE]
         *      ** position in onPageScrolled is keep as leftPage **
         *
         * We will set a mode flag to identify out which method we are using for making animate.
         */

        /**
         * When ViewPage is scrolling
         * @param position the order of the left page.
         * @param positionOffset the progress of the scrolling (must be in range [0,1))
         * @param positionOffsetPixels abs(the offset (in px) of the left page)
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabCount = mLinearTabLayout.getChildCount();
            if ((tabCount == 0) || (position < 0) || (position >= tabCount)) {
                return;
            }

            int left, right;
            float progress;
            if (mSlidingMode == 1) { // Manually sliding
                // 'Cause position will not be changed, we use this to recognize the pages
                left = position;
                right = position + 1;
                progress = positionOffset;
            }
            else { // Sliding by setCurrentItem
                // We use mLastPosition to record the start, and use getCurrentItem to find the end
                left = mLastPosition;
                right = mViewPager.getCurrentItem();
                if (left > right) { // Sliding to the left side. Swipe.
                    left = right;
                    right = mLastPosition;
                }
                // Calculate the real progress here.
                progress = ((position - left) * 1.0f + positionOffset) / (right - left);
            }

            updateIndicator(left, right, progress, mSlidingMode == 1);

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
            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    mSlidingMode = 0;
                    updateIndicator(mViewPager.getCurrentItem(), mViewPager.getCurrentItem(),
                            0.0f, true);
                    mLastPosition = mViewPager.getCurrentItem();
                    break;
                case ViewPager.SCROLL_STATE_DRAGGING:
                    mSlidingMode = 1; // Manually sliding
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    if (mSlidingMode == 0) // Using setCurrentItem
                        mSlidingMode = 2;
                    break;
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            updateTabStyle(mLastUpdate);
            updateTabStyle(position);
            mLastUpdate = position;

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
            int i = mLinearTabLayout.indexOfChild(tabView);
            if (i >= 0) {
                mViewPager.setCurrentItem(i);
            } else {
                throw new RuntimeException("Cannot active not contained tab.");
            }
        }
    }

    public class SwiftScroller extends Scroller {

        private int mDuration = 500;

        public SwiftScroller(Context context) {
            super(context);
        }

        public SwiftScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public SwiftScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}
