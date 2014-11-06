package tw.catcafe.materialtabs;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.Properties;

/**
 * Created by Davy on 14/11/5.
 */
public abstract class TabToolbar extends Toolbar {
    public TabToolbar(Context context) {
        this(context, null);
    }

    public TabToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // remove padding left / right
        setContentInsetsRelative(0, 0);
        setBackgroundColor(getTabLayout().getPrimaryColor());
        loadAttrs(attrs);

        addView(
                getLayout(),
                new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                )
        );
    }

    protected abstract ViewGroup getLayout();
    protected abstract TabLayout getTabLayout();

    protected void loadAttrs(AttributeSet attrs) {
        boolean iconMode = false;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TabToolbar, 0, 0);
            try {
                iconMode = a.getBoolean(R.styleable.TabToolbar_iconMode, false);
            } finally {
                a.recycle();
            }
        }

        setIconMode(iconMode);
    }

    public void setIconMode(boolean iconMode) { getTabLayout().setIconMode(iconMode); }
    public boolean getIconMode() { return getTabLayout().getIconMode(); }

    public void setViewPager(ViewPager viewPager) {
        getTabLayout().setViewPager(viewPager);
    }
}
