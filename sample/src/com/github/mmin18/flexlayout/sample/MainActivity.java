package com.github.mmin18.flexlayout.sample;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
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
}
