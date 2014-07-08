/**
 Copyright [2011] [Dorian Cussen]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package com.doridori.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.androidutils.app.R;


/**
 * Like a normal frameLayout apart from this will swap out its views
 * depending on some data state.<p/>
 *
 * You should pass in layout file ids to the empty and loading resId xml
 * attrbutes and an id into the content view attr<p/>
 *
 * If you supply your own error layout it must have a textView with the id 'R.id.state_error_text'<p/>
 *
 * If your going to use the same loading / error / empty views throughout the app (with differnt text) just set them in the source below and make sure the contents ids for the textViews match. Could make this better by setting default layout id in theme. Can apply in style also.<p/>
 *
 * Will auto hide all children on start<p/>
 *
 * Animations can be setup but using layoutTransitions = true in the manifest (unless they have been globally disabled in the user settings)<p/>
 *
 * If you want to avoid retaining visibility state you can use View.saveEnabled="false" - all childrens state will still be saved<p/>
 *
 * Similar to https://github.com/medyo/dynamicbox
 */
public class StatefulFrameLayout extends FrameLayout
{
    /**
     * WARNING - Samsung s3 running 4.0.4 (possibly a 4.0.4 bug) cannot handle a view changing from GONE to VISIBLE with
     * <code>animateLayoutChanges=true</code>. As this is a Framelayout you can either change to INVISIBLE instead of GONE
     * (less efficent as will still be measured when not vis) OR implement custom show hide anims for this class. Prob best
     * to just not use animateLayoutChanges. Custom animations solution is untested however :)<b/> Think this has something
     * to do with view invlidation as a PTR etc will then show the view<p/>
     */
    private static final int HIDDEN_VIEW_STATE = View.INVISIBLE;

    private ViewState mCurrentViewState = ViewState.NOT_INIT;

    private int mLoadingResId, mEmptyResId, mContentResId, mErrorResId;
    private View mLoadingView, mEmptyView, mContentView, mErrorView;
    private String mErrorText = null;
    private String mEmptyText;

    //=====================================================================================
    // CONSTRUCTORS
    //=====================================================================================

    public StatefulFrameLayout(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        getCustomAttrs(context, attrs);
        inflateStateViews();
    }

    public StatefulFrameLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
        getCustomAttrs(context, attrs);
        inflateStateViews();
    }

    public StatefulFrameLayout(Context context) {

        super(context);
        throw new RuntimeException("Use a constructor with attrs");
    }

    //=====================================================================================
    // SAVED STATE
    //=====================================================================================

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mCurrentViewState = this.mCurrentViewState;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        mCurrentViewState = ss.mCurrentViewState;
        setViewState(mCurrentViewState);
    }

    /**
     * User interface state that is stored by TextView for implementing
     * {@link View#onSaveInstanceState}.
     */
    public static class SavedState extends BaseSavedState
    {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
        {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private ViewState mCurrentViewState;

        /**
         * Use when saving out
         */
        SavedState(Parcelable superState)
        {
            super(superState);
        }

        /**
         * Used when restoring
         */
        private SavedState(Parcel in) {
            super(in);
            mCurrentViewState = (ViewState) in.readSerializable();
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            super.writeToParcel(out, flags);
            out.writeSerializable(mCurrentViewState);
        }

        @Override
        public String toString()
        {
            String str = "StatefulFrameLayout.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " currentState="+ mCurrentViewState;
            return str + "}";
        }
    }

    //=====================================================================================
    // OTHERS
    //=====================================================================================

    private void getCustomAttrs(Context context, AttributeSet attrs) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StatefulFrameLayout);

        // get state layout res id's if present, else use default
        mEmptyResId = array.getResourceId(
                R.styleable.StatefulFrameLayout_emptyView,
                R.layout.element_data_state_empty);

        mLoadingResId = array.getResourceId(
                R.styleable.StatefulFrameLayout_loadingView,
                R.layout.element_data_state_loading);

        mErrorResId = array.getResourceId(
                R.styleable.StatefulFrameLayout_errorView,
                R.layout.element_data_state_error);

        if (!array.hasValue(R.styleable.StatefulFrameLayout_contentView))
            throw new RuntimeException("need to set contentView attr");
        mContentResId = array.getResourceId(R.styleable.StatefulFrameLayout_contentView, -1);

        array.recycle();
    }


    /**
     * adds the child views the same way as done for AOSP views i.e
     * <a href='https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/widget/DatePicker.java'>DatePicker.java</a>.
     * Good approach for custom compound views
     */
    private void inflateStateViews() {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //content view ref is obtained on first state manipulation. This is becuase this method is called in the
        // contructor which would be before any XML views have actually been inflated and added by the system.
        // and method call after the LayoutInflators inflate call has returned will have a valid contentView ref (as long as the xml specifies it)

        mLoadingView = layoutInflater.inflate(mLoadingResId, this, false);
        mLoadingView.setVisibility(HIDDEN_VIEW_STATE);
        addView(mLoadingView);

        mEmptyView = layoutInflater.inflate(mEmptyResId, this, false);
        mEmptyView.setVisibility(HIDDEN_VIEW_STATE);
        addView(mEmptyView);

        mErrorView = layoutInflater.inflate(mErrorResId, this, false);
        mErrorView.setVisibility(HIDDEN_VIEW_STATE);
        addView(mErrorView);
    }

    public void setViewState(ViewState newViewState) {
        setViewState(newViewState, true);
    }

    /**
     * @param newViewState
     * @param animate true if should animate when showing content
     */
    public void setViewState(ViewState newViewState, boolean animate) {

        mCurrentViewState = newViewState;

        showViewBasedOnState(animate);
    }

    /**
     * @param msg can not be null
     */
    public void showErrorViewWithMsg(String msg) {

        mCurrentViewState = ViewState.ERROR;
        mErrorText = msg;

        showViewBasedOnState(true);
        setErrorText(mErrorText);
    }

    /**
     * @param msg can not be null
     */
    public void setEmptyViewWithMsg(String msg) {

        mCurrentViewState = ViewState.EMPTY;
        mEmptyText = msg;

        showViewBasedOnState(true);
        setEmptyText(mEmptyText);
    }

    public void setOnClickForError(OnClickListener onClickListener)
    {
        mErrorView.setOnClickListener(onClickListener);
    }

    /**
     * If a custom error view has been used it will have to include a textView with the ID R.id.state_error_text for
     * this method to not throw an exception!
     *
     * @param errorTxt can not be null
     */
    private void setErrorText(String errorTxt) {
        TextView errorTxtView = (TextView) findViewById(R.id.state_error_text);
        errorTxtView.setText(mErrorText);
    }

    /**
     * If a custom empty view has been used it will have to include a textView with the ID R.id.state_empty_text for
     * this method to not throw an exception!
     *
     * @param emptyText can not be null
     */
    private void setEmptyText(String emptyText){
        TextView emptyTxtView = (TextView) findViewById(R.id.state_empty_text);
        emptyTxtView.setText(emptyText);
    }

    public ViewState getViewState() {

        return mCurrentViewState;
    }

    /**
     * @param animate true if should animate when showing content
     */
    private void showViewBasedOnState(boolean animate) {

        // first time this is called contentView ref should/will be null - see #inflateStateViews
        if(mContentView == null)
        {
            mContentView = findViewById(mContentResId);

            if (mContentView == null) {
                throw new NullPointerException("contentView cannot be null, have you set the contentView attribute");
            }

            if(mContentView.getVisibility() == View.VISIBLE)
                throw new RuntimeException("need to set child view to hidden (GONE | INVISIBLE) state in xml or will flicker");
        }

        switch (mCurrentViewState) {
            case NOT_INIT:
                // hide all
                mLoadingView.setVisibility(HIDDEN_VIEW_STATE);
                mEmptyView.setVisibility(HIDDEN_VIEW_STATE);
                mContentView.setVisibility(HIDDEN_VIEW_STATE);
                mErrorView.setVisibility(HIDDEN_VIEW_STATE);
                break;

            case CONTENT:

                // show content view
                mLoadingView.setVisibility(HIDDEN_VIEW_STATE);
                mEmptyView.setVisibility(HIDDEN_VIEW_STATE);
                mContentView.setVisibility(View.VISIBLE);
                mErrorView.setVisibility(HIDDEN_VIEW_STATE);
                break;

            case EMPTY:
                // show empty view
                mLoadingView.setVisibility(HIDDEN_VIEW_STATE);
                mEmptyView.setVisibility(View.VISIBLE);
                mContentView.setVisibility(HIDDEN_VIEW_STATE);
                mErrorView.setVisibility(HIDDEN_VIEW_STATE);
                break;

            case LOADING:
                // show loading view
                mLoadingView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(HIDDEN_VIEW_STATE);
                mContentView.setVisibility(HIDDEN_VIEW_STATE);
                mErrorView.setVisibility(HIDDEN_VIEW_STATE);
                break;

            case ERROR:
                // show error view
                mLoadingView.setVisibility(HIDDEN_VIEW_STATE);
                mEmptyView.setVisibility(HIDDEN_VIEW_STATE);
                mContentView.setVisibility(HIDDEN_VIEW_STATE);
                mErrorView.setVisibility(View.VISIBLE);
                break;

        }

        invalidate();
    }

    public static enum ViewState{
        /**
         * Loading has not started yet
         */
        NOT_INIT, //default
        /**
         * Loading started
         */
        LOADING,
        /**
         * Loading finished and empty data
         */
        EMPTY,
        /**
         * Loading finished with success
         */
        CONTENT,
        /**
         * Loading finished with error
         */
        ERROR
    }

}

