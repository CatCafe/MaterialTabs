package tw.catcafe.materialtabs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import at.markushi.ui.RevealColorView;

/**
 * Created by Davy on 14/11/5.
 */

public class TabItem implements View.OnTouchListener {
    private Context mContext;

    private View mTabView;
    private ImageView mIconView;
    private TextView mTextView;
    private RevealColorView mRevealColorView;

    private Resources mResources;
    private TabListener listener;
    private Drawable mIconDrawable;
    private String mText;

    private int mColor;
    private int mPrimaryColor;
    private int mAccentColor;
    private boolean mIconMode = false;
    private boolean mTouchingIn = false;

    private boolean mActived = false;
    private int mPosition;

    private static int TOUCH_DURATION_FAST = 250;
    private static int TOUCH_DURATION_SLOW = 700;

    public TabItem(Context context) {
        mContext = context;
        mResources = context.getResources();

        mColor = Color.WHITE;

        inflateView();
    }

    public TabItem setText(CharSequence text) {
        mText = text.toString();
        if (mTextView != null)
            mTextView.setText(mText.toUpperCase(Locale.US));
        return this;
    }

    public TabItem setIcon(Drawable icon) {
        mIconDrawable = icon;
        return this;
    }

    private Context getContext() {
        return mContext;
    }

    private void inflateView() {
        if (getIconMode()) // Show Icon
        {
            mTabView = LayoutInflater.from(getContext()).inflate(R.layout.material_tab_icon, null);
            mIconView = (ImageView) mTabView.findViewById(R.id.icon);
            mRevealColorView = (RevealColorView) mTabView.findViewById(R.id.reveal);

            mIconView.setImageDrawable(mIconDrawable);
        } else // Show Title
        {
            mTabView = LayoutInflater.from(getContext()).inflate(R.layout.material_tab, null);
            mTextView = (TextView) mTabView.findViewById(R.id.text);
            mRevealColorView = (RevealColorView) mTabView.findViewById(R.id.reveal);

            mTextView.setText(mText);
        }

        colorTab();
        // set the listener
        mTabView.setOnTouchListener(this);
    }

    void setAccentColor(int color) {
        this.mAccentColor = color;
    }

    void setPrimaryColor(int color) {
        this.mPrimaryColor = color;
        mRevealColorView.setBackgroundColor(color);
    }

    void setColor(int color) {
        mColor = color;
        colorTab();
    }

    void setIconMode(boolean v) {
        if (mIconMode != v) // Redraw View
        {
            mIconMode = v;
            inflateView();
        }
    }

    private int getAccentColor() {
        return mAccentColor;
    }

    private int getTouchAccentColor() {
        return Color.argb(
                Color.alpha(mAccentColor) * 0x88 / 0xff,
                Color.red(mAccentColor),
                Color.green(mAccentColor),
                Color.blue(mAccentColor)
        );
    }

    private int getPrimaryColor() {
        return mPrimaryColor;
    }

    private int getColor() {
        return mColor;
    }

    private boolean getIconMode() {
        return mIconMode;
    }

    protected void colorTab() {
        if (mActived) {
            // set full color text
            if (mTextView != null)
                mTextView.setTextColor(mColor);
            // set 100% alpha to icon
            if (mIconView != null)
                mIconView.setImageAlpha(0xFF);
        } else {
            // set 60% alpha to text color
            if (mTextView != null)
                mTextView.setTextColor(Color.argb(
                        (int) (Color.alpha(mColor) * 0.6),
                        Color.red(mColor),
                        Color.green(mColor),
                        Color.blue(mColor)
                ));
            // set 60% alpha to icon
            if (mIconView != null)
                mIconView.setImageAlpha(0x99);
        }
    }

    void disable() {
        mActived = false;

        colorTab();

        if (listener != null)
            listener.onTabUnselected(this);
    }

    void activate() {
        boolean wasActived = mActived;
        mActived = true;

        colorTab();

        if (listener != null)
            if (wasActived) // reselected
                listener.onTabReselected(this);
            else
                listener.onTabSelected(this);
    }

    public boolean isSelected() {
        return mActived;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // reveal the tab
                mRevealColorView.reveal(
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
                mRevealColorView.hide(
                    point.x,
                    point.y,
                    getPrimaryColor(),
                    0,
                    TOUCH_DURATION_FAST,
                    null
                );

                mTouchingIn = false ;
                return true;
            case MotionEvent.ACTION_MOVE:
                // detect if getting out or in the box
                Rect box = new Rect(0, 0, mTabView.getWidth(), mTabView.getHeight());
                if (mTouchingIn) // getting out
                {
                    if (!box.contains(point.x, point.y)) {
                        // hide reveal
                        mRevealColorView.hide(
                                point.x,
                                point.y,
                                getPrimaryColor(),
                                0,
                                TOUCH_DURATION_FAST,
                                null
                        );

                        mTouchingIn = false;
                    }
                } else {
                    if (box.contains(point.x, point.y)) {
                        // reveal again
                        mRevealColorView.reveal(
                                point.x,
                                point.y,
                                getTouchAccentColor(),
                                0,
                                TOUCH_DURATION_FAST,
                                null
                        );

                        mTouchingIn = true;
                    }
                }

                return false;
            case MotionEvent.ACTION_UP:
                if (mTouchingIn) {
                    // hide reveal
                    mRevealColorView.reveal(
                            point.x,
                            point.y,
                            getPrimaryColor(),
                            0,
                            TOUCH_DURATION_SLOW,
                            null
                    );

                    activate();
                }

                mTouchingIn = false;
                return true;
            default:
                return false;
        }
    }

    public View getView() {
        return mTabView;
    }

    public TabItem setTabListener(TabListener listener) {
        this.listener = listener;
        return this;
    }

    void setPosition(int position) {
        mPosition = position;
    }

    int getPosition() { return mPosition; }
}