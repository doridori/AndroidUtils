package com.doridori.lib.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * Adapter for GroupedListView
 *
 * * //TODO will drop some of the views to be used for converting so could be more efficent - see getView() for more
 * 
 * @author dorian.cussen
 */
public abstract class GroupedListAdapter extends BaseAdapter
{
	@Override
	public final int getCount()
	{
		int groupCount = getGroupCount();
		int countTotal = 0;
		for(int i = 0; i < groupCount; i++)
		{
			//add one for the group header
			countTotal++;
			//add the group size
			countTotal += getGroupSize(i);
		}

		return countTotal;
	}

	@Override
	public final Object getItem(int position)
	{
		throw new RuntimeException("MethodNotSupported");
	}

	@Override
	public final long getItemId(int position)
	{
		return position;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final View getView(int position, View convertView, ViewGroup parent)
	{		
		GroupedPosition groupedPosition = getGroupAndChildPosition(position);
		if(groupedPosition.representsGroupHeading == true) //if this position is the group itself
		{
			//TODO: can improve by having two groups of views for converting, will be faster
			if(convertView == null ||convertView.getId() != getGroupViewId()) 
			{
				convertView = null;
			}
			return getGroupView(groupedPosition.groupIndex, convertView, parent);
		}
		else
		{
			//TODO: can improve by having two groups of views for converting, will be faster
			if(convertView == null || convertView.getId() != getChildViewId()) 
			{
				convertView = null;
			}				
			return getChildView(groupedPosition.groupIndex, groupedPosition.childIndex, convertView, parent);
		}	
	}
	
	/**
	 * @return how many groups int the model
	 */
	protected abstract int getGroupCount();
	
	/**
	 * @param position - the group index
	 * @return the size of the group at position
	 */
	protected abstract int getGroupSize(int position);
	
	/**
	 * @see android.widget.BaseAdapter.getView(int position, android.view.View convertView, android.view.ViewGroup parent)
	 */
	protected abstract View getGroupView(int groupPosition, View convertView, ViewGroup parent);
	
	protected abstract View getChildView(int groupPosition, int childPosition, View convertView, ViewGroup parent);
	
	/**
	 * @return the id of the root of the layout for group views
	 */
	protected abstract int getGroupViewId();
	
	/**
	 * @return the id of the root of the layout for child views
	 */
	protected abstract int getChildViewId();
	
	/**
	 * given an integer will convert to group and child positions based on data
	 */
	public GroupedPosition getGroupAndChildPosition(int position)
	{
		//the current index of the view being checked
		int indexOfGroupHeading = 0;
		//the current group being checked
		int group = 0;
		while(true)
		{
			//check if group header
			if(indexOfGroupHeading == position) return new GroupedPosition(group);
			
			//check if child
			int groupSize = getGroupSize(group);
			for(int childIndex = 0; childIndex < groupSize; childIndex++)
			{
				if(indexOfGroupHeading + 1 + childIndex == position) 
				{
					return new GroupedPosition(group, childIndex);
				}
			}
			//index of next group heading
			indexOfGroupHeading += groupSize + 1;//+1 for the heading itself
			group++;			
		}
	}
	
	public static final class GroupedPosition
	{
		public final boolean representsGroupHeading;
		public final int groupIndex;
		public final int childIndex;
		
		public GroupedPosition(int groupIndex)
		{
			representsGroupHeading = true;
			this.groupIndex = groupIndex;
			this.childIndex = -1;
		}
		
		public GroupedPosition(int groupIndex, int childIndex)
		{
			representsGroupHeading = false;
			this.groupIndex = groupIndex;
			this.childIndex = childIndex;
		}
	}

}
