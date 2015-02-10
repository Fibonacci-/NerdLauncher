package com.helwigdev.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Tyler on 2/10/2015.
 * Copyright 2015 by Tyler Helwig
 */
public class NerdLauncherFragment extends ListFragment {
	private static final String TAG = "NerdLauncherFragment";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Intent startupIntent = new Intent(Intent.ACTION_MAIN);
		startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		PackageManager pm = getActivity().getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

		Toast.makeText(getActivity(), "I've found " + activities.size() + " activities", Toast.LENGTH_SHORT).show();

		Collections.sort(activities, new Comparator<ResolveInfo>() {
			@Override
			public int compare(ResolveInfo lhs, ResolveInfo rhs) {
				PackageManager pm = getActivity().getPackageManager();
				return String.CASE_INSENSITIVE_ORDER.compare(
						lhs.loadLabel(pm).toString(),
						rhs.loadLabel(pm).toString()
				);
			}
		});

		ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(getActivity(),
				android.R.layout.simple_list_item_1, activities){
			public View getView(int pos, View convertView, ViewGroup parent){
				PackageManager pm = getActivity().getPackageManager();
				View v = super.getView(pos, convertView, parent);
				//docs say simple_list_item_1 is a textview - treat it as such
				TextView tv = (TextView) v;

				ResolveInfo ri = getItem(pos);
				tv.setCompoundDrawablesWithIntrinsicBounds(ri.loadIcon(pm),null,null,null);
				tv.setCompoundDrawablePadding(150);
				tv.setText(ri.loadLabel(pm));
				return v;
			}
		};

		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ResolveInfo resolveInfo = (ResolveInfo) l.getAdapter().getItem(position);
		ActivityInfo activityInfo = resolveInfo.activityInfo;

		if(activityInfo == null) return;

		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(i);
	}
}
