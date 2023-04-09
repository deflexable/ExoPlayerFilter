package com.daasuu.epf;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.daasuu.epf.chooser.EConfigChooser;
import com.daasuu.epf.contextfactory.EContextFactory;
import com.daasuu.epf.filter.GlFilter;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.video.VideoListener;
import com.daasuu.epf.GLResizeMode;

/**
 * Created by sudamasayuki on 2017/05/16.
 */
public class EPlayerView extends GLSurfaceView implements VideoListener {

    private final static String TAG = EPlayerView.class.getSimpleName();

    private final EPlayerRenderer renderer;
    private SimpleExoPlayer player;
    
    private GLResizeMode glResizeMode = GLResizeMode.RESIZE_NONE;
    int contentWidth = 0;
    int contentHeight = 0;

    public EPlayerView(Context context) {
        this(context, null);
    }

    public EPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextFactory(new EContextFactory());
        setEGLConfigChooser(new EConfigChooser());

        renderer = new EPlayerRenderer(this);
        setRenderer(renderer);

    }

    public EPlayerView setSimpleExoPlayer(SimpleExoPlayer player) {
        if (this.player != null) {
            this.player.release();
            this.player = null;
        }
        this.player = player;
        this.player.addVideoListener(this);
        this.renderer.setSimpleExoPlayer(player);
        return this;
    }

    public void setGlFilter(GlFilter glFilter) {
        renderer.setGlFilter(glFilter);
    }
    
    public void setResizeMode(GLResizeMode glResizeMode) {
        this.glResizeMode = glResizeMode;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = getMeasuredWidth();
        int parentHeight = getMeasuredHeight();

        int newWidth = parentWidth;
        int newHeight = parentHeight;

        if (contentWidth != 0 && contentHeight != 0 && parentHeight != 0 && parentWidth != 0) {
            switch (glResizeMode) {
                case GLResizeMode.RESIZE_FIT_WIDTH:
                    newHeight = (contentHeight * parentWidth) / contentWidth;
                    break;
                case GLResizeMode.RESIZE_FIT_HEIGHT:
                    newWidth = (contentWidth * parentHeight) / contentHeight;
                    break;
                case GLResizeMode.RESIZE_CONTAIN:
                case GLResizeMode.RESIZE_NONE:
                    newWidth = parentWidth;
                    newHeight = (contentHeight * newWidth) / contentWidth;

                    if (newHeight > parentHeight) {
                        newHeight = parentHeight;
                        newWidth = (contentWidth * newHeight) / contentHeight;
                    }
                    break;
                case GLResizeMode.RESIZE_COVER:
                    newWidth = (contentWidth * parentHeight) / contentHeight;
                    newHeight = (contentHeight * parentWidth) / contentWidth;

                    if (newWidth < parentWidth || newHeight < parentHeight) {
                        int scale = (parentWidth / newWidth);
                        newWidth = newWidth * scale;
                        newHeight = newHeight * scale;
                    }
                    break;
            }
        }

        setMeasuredDimension(newWidth, newHeight);
    }

    @Override
    public void onPause() {
        super.onPause();
        renderer.release();
    }

    //////////////////////////////////////////////////////////////////////////
    // SimpleExoPlayer.VideoListener

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        contentWidth = width;
        contentHeight = height;
        requestLayout();
    }

    @Override
    public void onRenderedFirstFrame() {
        // do nothing
    }
}
