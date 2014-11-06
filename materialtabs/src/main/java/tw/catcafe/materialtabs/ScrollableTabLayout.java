package tw.catcafe.materialtabs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

/**
 * Created by Davy on 14/11/5.
 */
public class ScrollableTabLayout extends TabLayout {
    private RelativeLayout mMasterLayout;
    private HorizontalScrollView mHorizontalScrollView;
    public ScrollableTabLayout(Context context) {
        this(context, null);
    }

    public ScrollableTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mIndicatorMoveListener = new InternalIndicatorMoveListener();
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

    private class InternalIndicatorMoveListener implements OnIndicatorMoveListener {
        private int mLastIndicatorPosition;
        private int mScrollStartAtMiddlePosition = -1;

        @Override
        public void OnIndicatorMove(int left, int right, int leftTabMiddle, int rightTabMiddle, boolean manually) {
            final int padding = getResources().getDimensionPixelSize(R.dimen.material_scrollable_tab_padding);
            final int indicatorMiddlePosition = (right + left) / 2;
            final int scrollWidth = mHorizontalScrollView.getWidth();
            int middlePosition = indicatorMiddlePosition;

            if (!manually) {
                // If not manually sliding, we want to scroll swiftly.
                if (mLastIndicatorPosition == indicatorMiddlePosition) // not yet moved
                    return;
                if (mScrollStartAtMiddlePosition == -1)
                    mScrollStartAtMiddlePosition = mHorizontalScrollView.getScrollX() - padding + scrollWidth / 2;

                final int movementRoadWidth = rightTabMiddle - leftTabMiddle;
                boolean movingRight = mLastIndicatorPosition < indicatorMiddlePosition;

                float indicatorMoveProgress = 1.0f *
                        (indicatorMiddlePosition - leftTabMiddle) / movementRoadWidth;
                if (movingRight) {
                    middlePosition = (int) (
                            mScrollStartAtMiddlePosition * (1.0f - indicatorMoveProgress) +
                            rightTabMiddle * indicatorMoveProgress
                    );
                } else {
                    indicatorMoveProgress = 1.0f - indicatorMoveProgress;
                    middlePosition = (int) (
                            mScrollStartAtMiddlePosition * (1.0f - indicatorMoveProgress) +
                            leftTabMiddle * indicatorMoveProgress
                    );
                }
            }

            if (leftTabMiddle == rightTabMiddle || manually) // Movement completed
                mScrollStartAtMiddlePosition = -1;

            mLastIndicatorPosition = indicatorMiddlePosition;
            mHorizontalScrollView.scrollTo(middlePosition + padding - scrollWidth / 2, 0);
        }
    }
}
