package com.github.mmin18.flexlayout.sample;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by mmin18 on 3/5/16.
 */
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flex_layout);

		setupCatalogViews();
	}

	private void setupCatalogViews() {
		ViewGroup frame = (ViewGroup) findViewById(R.id.frame);
		frame.removeAllViews();
		LayoutInflater inflater = getLayoutInflater();

		int i = R.layout.flex_layout & 0xffff0000;
		Pattern pattern = Pattern.compile("sample\\d+[\\d\\w_]*");
		try {
			while (true) {
				String s = getResources().getResourceEntryName(i);
				if (pattern.matcher(s).matches()) {
					TextView tv = (TextView) inflater.inflate(R.layout.catalog_header, frame, false);
					tv.setText("@layout/" + s);
					frame.addView(tv);
					inflater.inflate(i, frame, true);
				}
				i++;
			}
		} catch (Resources.NotFoundException e) {
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	public void sample5Switch(View v) {
		View hv = findViewById(R.id.hide_view);
		hv.setVisibility(hv.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}

	public void sample7Inc(View v) {
		int i = v.getTag() instanceof Integer ? ((Integer) v.getTag()).intValue() : 0;
		v.setTag(i + 1);
		((TextView) v).setText("(" + (i + 1) + ") ++");
		v.requestLayout();
	}

	public void sample8Switch(View v) {
		boolean b = v.getTag() instanceof Boolean ? ((Boolean) v.getTag()).booleanValue() : false;
		v.setTag(!b);
		((TextView) v).setText("" + (!b));
		v.requestLayout();
	}

	public void sample9Add(View v) {
		getLayoutInflater().inflate(R.layout.stack_item, (ViewGroup) v.getParent());
	}
}
