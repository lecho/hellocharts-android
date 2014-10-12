package lecho.lib.hellocharts.samples;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutActivity extends ActionBarActivity {
	public static final String TAG = AboutActivity.class.getSimpleName();
	public static final String GITHUB_URL = "github.com/lecho/hellocharts-android";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_about, container, false);

			TextView version = (TextView) rootView.findViewById(R.id.version);
			version.setText(getAppVersionAndBuild(getActivity()).first);

			TextView gotToGithub = (TextView) rootView.findViewById(R.id.go_to_github);
			gotToGithub.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					launchWebBrowser(getActivity(), GITHUB_URL);

				}
			});

			return rootView;
		}
	}

	public static Pair<String, Integer> getAppVersionAndBuild(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return new Pair<String, Integer>(pInfo.versionName, pInfo.versionCode);
		} catch (Exception e) {
			Log.e(TAG, "Could not get version number");
			return new Pair<String, Integer>("", 0);
		}
	}

	@SuppressLint("DefaultLocale")
	public static boolean launchWebBrowser(Context context, String url) {
		try {
			url = url.toLowerCase();
			if (!url.startsWith("http://") || !url.startsWith("https://")) {
				url = "http://" + url;
			}

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			if (null == resolveInfo) {
				Log.e(TAG, "No activity to handle web intent");
				return false;
			}
			context.startActivity(intent);
			Log.i(TAG, "Launching browser with url: " + url);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Could not start web browser", e);
			return false;
		}
	}
}
