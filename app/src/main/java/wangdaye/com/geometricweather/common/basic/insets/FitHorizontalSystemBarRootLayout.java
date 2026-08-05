package wangdaye.com.geometricweather.common.basic.insets;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FitHorizontalSystemBarRootLayout extends FrameLayout {

    private boolean mFitKeyboardExpanded;

    public FitHorizontalSystemBarRootLayout(Context context) {
        this(context, null);
    }

    public FitHorizontalSystemBarRootLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitHorizontalSystemBarRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFitKeyboardExpanded = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        WindowInsetsCompat compat = WindowInsetsCompat.toWindowInsetsCompat(insets);
        Insets systemInsets = compat.getInsets(WindowInsetsCompat.Type.systemBars());
        Rect r = new Rect(
                systemInsets.left,
                systemInsets.top,
                systemInsets.right,
                systemInsets.bottom
        );
        FitBothSideBarHelper.setRootInsetsCache(
                new Rect(0, r.top, 0, mFitKeyboardExpanded ? 0 : r.bottom));
        setPadding(r.left, 0, r.right, 0);
        return insets;
    }

    public void setFitKeyboardExpanded(boolean fit) {
        mFitKeyboardExpanded = fit;
        ViewCompat.requestApplyInsets(this);
        requestLayout();
    }
}
