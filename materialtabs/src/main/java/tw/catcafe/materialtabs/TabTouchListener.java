package tw.catcafe.materialtabs;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;


import at.markushi.ui.RevealColorView;

/**
 * Created by Davy on 14/11/5.
 */

public abstract class TabTouchListener implements View.OnTouchListener {
    static int TOUCH_DURATION_FAST = 250;
    static int TOUCH_DURATION_SLOW = 700;

    protected boolean mTouchingIn = false;

    public TabTouchListener() { }

    protected abstract int getTouchAccentColor();
    protected abstract int getPrimaryColor();
    protected abstract void onTabActive(View tabView);

    @Override
    public boolean onTouch(View tabView, MotionEvent event) {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        RevealColorView revealColorView = (RevealColorView) tabView.findViewById(R.id.reveal);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // reveal the tab
                revealColorView.reveal(
                        point.x,
                        point.y,
                        getTouchAccentColor(),
                        0,
                        TOUCH_DURATION_FAST,
                        null
                );

                mTouchingIn = true;
                return true;
            case MotionEvent.ACTION_CANCEL:
                revealColorView.hide(
                    point.x,
                    point.y,
                    getPrimaryColor(),
                    0,
                    TOUCH_DURATION_FAST,
                    null
                );

                mTouchingIn = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                // detect if getting out or in the box
                Rect box = new Rect(0, 0, tabView.getWidth(), tabView.getHeight());
                boolean isInBox = box.contains(point.x, point.y);
                if (mTouchingIn && !isInBox) // getting out
                {
                    // hide reveal
                    revealColorView.hide(
                            point.x,
                            point.y,
                            getPrimaryColor(),
                            0,
                            TOUCH_DURATION_FAST,
                            null
                    );
                    mTouchingIn = false;
                } else if (!mTouchingIn && isInBox) {
                    // reveal again
                    revealColorView.reveal(
                            point.x,
                            point.y,
                            getTouchAccentColor(),
                            0,
                            TOUCH_DURATION_FAST,
                            null
                    );
                    mTouchingIn = true;
                }

                return false;
            case MotionEvent.ACTION_UP:
                if (mTouchingIn) {
                    // hide reveal
                    revealColorView.reveal(
                            point.x,
                            point.y,
                            getPrimaryColor(),
                            0,
                            TOUCH_DURATION_SLOW,
                            null
                    );
                    onTabActive(tabView);
                }

                mTouchingIn = false;
                return true;
            default:
                return false;
        }
    }
}