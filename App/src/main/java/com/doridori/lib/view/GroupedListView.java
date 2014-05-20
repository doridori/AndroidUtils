package com.doridori.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Like ExpandableListView with forever expanded groups!
 *
 * 
 * @author dorian.cussen
 */
public class GroupedListView extends ListView
{
	private GroupedListAdapter mGroupedListAdapter;
	private OnChildClickListener mOnChildClickListener;

    public GroupedListView(Context context)
    {
        super(context);
        init();
    }

    public GroupedListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public GroupedListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        setOnItemClickListener(mOnItemClickListener);
    }

	@Override
	public void setAdapter(ListAdapter adapter)
	{
		throw new RuntimeException("Use setAdapter(GroupedListAdapter adapter) instead");
	}

	public void setAdapter(GroupedListAdapter adapter)
	{
		super.setAdapter(adapter);	
		mGroupedListAdapter = adapter;
	}	
		
	public void setOnChildClickListener(OnChildClickListener onChildClickListener)
	{
		mOnChildClickListener = onChildClickListener;
	}
	
	private final OnItemClickListener mOnItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			//translate to the onChildClickListener
			if(mOnChildClickListener == null) return;
			
			GroupedListAdapter.GroupedPosition groupedPosition = mGroupedListAdapter.getGroupAndChildPosition(position);
			if(groupedPosition.representsGroupHeading == false)
			{
				// only pass on clicks for the child
				mOnChildClickListener.onChildClick(parent, view,  groupedPosition.groupIndex, groupedPosition.childIndex, id);
			}			
		}	
	};
	
	/**
     * Interface definition for a callback to be invoked when a child in this
     * expandable list has been clicked.
     */
    public interface OnChildClickListener {
        /**
         * Callback method to be invoked when a child in this expandable list has 
         * been clicked.
         *
         * @param parent The View where the click happened
         * @param v The view within the expandable list/ListView that was clicked
         * @param groupPosition The group position that contains the child that
         *        was clicked
         * @param childPosition The child position within the group
         * @param id The row id of the child that was clicked
         * @return True if the click was handled
         */
        boolean onChildClick(
                AdapterView<?> parent, View v, int groupPosition,
                int childPosition, long id);
    }
}
