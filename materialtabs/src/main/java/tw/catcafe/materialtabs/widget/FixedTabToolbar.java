package tw.catcafe.materialtabs.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import tw.catcafe.materialtabs.FixedTabLayout;
import tw.catcafe.materialtabs.R;

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

        // get attributes
        boolean iconMode = false;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedTabToolbar, 0, 0);
            try {
                iconMode = a.getBoolean(R.styleable.FixedTabToolbar_iconMode, false);
            } finally {
                a.recycle();
            }
        }

        // remove padding left / right
        setContentInsetsRelative(0, 0);

        mFixedTabLayout = new FixedTabLayout(context);
        mFixedTabLayout.setIconMode(iconMode);
        setBackgroundColor(mFixedTabLayout.getPrimaryColor());

        addView(
                mFixedTabLayout,
                new Toolbar.LayoutParams(
                        Toolbar.LayoutParams.MATCH_PARENT,
                        Toolbar.LayoutParams.MATCH_PARENT
                )
        );
    }

    public boolean getIconMode() { return mFixedTabLayout.getIconMode(); }

    public void setViewPager(ViewPager viewPager) {
        mFixedTabLayout.setViewPager(viewPager);
    }
}
