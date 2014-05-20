package com.doridori.lib.view;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ImageView that does not cause its parent to requestLAyout when the bitmap is changed
 * (stops lists skipping when their images are being loaded async). This means the dimensions
 * of this view should be set explicitly. Another approach can be updating the exisiting drawable
 * object for an imageView (not calling setDrawable) as this seems to have the same effect - like the Picasso lib (ImageViewAction) - saying that the lib does end up calling setImageDrawable...
 * 
 * @author dorian.cussen
 */
public class ImageViewNoLayoutRefresh extends ImageView
{
	public ImageViewNoLayoutRefresh(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public ImageViewNoLayoutRefresh(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ImageViewNoLayoutRefresh(Context context)
	{
		super(context);
	}

	@Override
	public void requestLayout()
	{
		// do nothing - for this to work well this image view should have its dims
		// set explicitly
	}
}
