package tw.catcafe.materialtabs;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

/**
 * Created by Davy on 14/11/5.
 */
public class ScrollableTabLayout extends TabLayout {
    private RelativeLayout mMasterLayout;
    private HorizontalScrollView mHorizontalScrollView;
    private ViewPager.OnPageChangeListener mHostViewPagerPageChangeListener;
    public ScrollableTabLayout(Context context) {
        this(context, null);
    }

    public ScrollableTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mViewPagerPageChangeListener = new InternelViewPagerPageChangeListener();
    }

    private HorizontalScrollView getHorizontalScrollView() {
        if (mHorizontalScrollView == null) {
            mHorizontalScrollView = new HorizontalScrollView(getContext());
            mHorizontalScrollView.setHorizontalScrollBarEnabled(false);
            this.addView(mHorizontalScrollView,
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        return mHorizontalScrollView;
    }

    @Override
    protected ViewGroup getMasterLayout() {
        if (mMasterLayout == null) {
            mMasterLayout = new RelativeLayout(getContext());
            final int padding = getResources().getDimensionPixelSize(R.dimen.material_scrollable_tab_padding);
            mMasterLayout.setPadding(padding, 0, padding, 0);
            getHorizontalScrollView().addView(mMasterLayout,
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        }
        return mMasterLayout;
    }

    @Override
    protected ViewGroup.LayoutParams createLayoutParams() {
        return new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        // Because we want to scroll to tab automatically ,
        // we have to wrap mViewPagerPageChangeListener here.
        mHostViewPagerPageChangeListener = listener;
    }

    private class InternelViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixel) {

            if (mHostViewPagerPageChangeListener != null) {
                mHostViewPagerPageChangeListener.onPageScrolled(position,
                        positionOffset, positionOffsetPixel);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mHostViewPagerPageChangeListener != null) {
                mHostViewPagerPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mHostViewPagerPageChangeListener != null) {
                mHostViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    }
}
