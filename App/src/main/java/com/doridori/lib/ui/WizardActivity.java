package com.doridori.lib.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidutils.app.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A view pager with a number of simple pages (title, text content, image). Will push through and finish on end.
 *
 * User: doriancussen
 * Date: 15/01/2013
 */
public abstract class WizardActivity extends Activity
{
    private List<WizardPage> mWizardPages = new ArrayList<WizardPage>();
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        mViewPager = (ViewPager) findViewById(R.id.wizard_pager);
        populatePages(mWizardPages);
        mViewPager.setAdapter(new MyPagerAdapter());
    }

    public void onClick(View v)
    {
        if(v.getId() == R.id.wizard_button_next)
        {
            //if showing last then close activity else push forward in wizard
            if(mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1)
            {
                exitingWizardAtEnd();
                finish();
            }
            else
            {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            }
        }
    }

    /**
     * Fill up the passed in list with pages to show in the wizard.
     *
     * @param pages
     */
    public abstract void populatePages(List<WizardPage> pages);

    /**
     * In case you want to save a pref so the wizard is not shown again
     */
    public abstract void exitingWizardAtEnd();

    private class MyPagerAdapter extends PagerAdapter
    {
        public int getCount() {
            return mWizardPages.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View page = getLayoutInflater().inflate(R.layout.element_wizard_page, null);
            mViewPager.addView(page);

            TextView title = (TextView) page.findViewById(R.id.wizard_page_title);
            TextView content = (TextView) page.findViewById(R.id.wizard_page_content);
            ImageView image = (ImageView) page.findViewById(R.id.wizard_page_image);

            title.setText(mWizardPages.get(position).titleResId);
            content.setText(mWizardPages.get(position).contentResId);
            image.setImageResource(mWizardPages.get(position).imageResId);

            return page;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            mViewPager.removeView((View)object);
        }
    }


    protected class WizardPage {
        public final int titleResId;
        public final int contentResId;
        public final int imageResId;

        public WizardPage(int titleResId, int contentResId, int imageResId)
        {
            this.titleResId = titleResId;
            this.contentResId = contentResId;
            this.imageResId = imageResId;
        }
    }
}
