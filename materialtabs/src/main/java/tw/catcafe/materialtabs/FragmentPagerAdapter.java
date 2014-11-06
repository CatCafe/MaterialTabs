package tw.catcafe.materialtabs;

import android.graphics.drawable.Drawable;

/**
 * Created by Davy on 14/11/6.
 */
public abstract class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    public FragmentPagerAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    public abstract Drawable getPageIcon(int i);
}
