package com.junkcleaner.memorycleaner.utill;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AndyUtils {
	static float density = 1;
	private static ProgressDialog mProgressDialog;

	public static void showSimpleProgressDialog(Context context, String title,
			String msg, boolean isCancelable) {
		try {
			if (mProgressDialog == null) {
				mProgressDialog = ProgressDialog.show(context, title, msg);
				mProgressDialog.setCancelable(isCancelable);
			}

			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
			}

		} catch (IllegalArgumentException ie) {
			ie.printStackTrace();
		} catch (RuntimeException re) {
			re.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showSimpleProgressDialog(Context context) {
		showSimpleProgressDialog(context, null, "Loading...", false);
	}

	public static void removeSimpleProgressDialog() {
		try {
			if (mProgressDialog != null) {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
			}
		} catch (IllegalArgumentException ie) {
			ie.printStackTrace();

		} catch (RuntimeException re) {
			re.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void showNetworkErrorMessage(final Context context) {
		Builder dlg = new AlertDialog.Builder(context);
		dlg.setCancelable(false);
		dlg.setTitle("Error");
		dlg.setMessage("Network error has occured. Please check the network status of your phone and retry");
		dlg.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		dlg.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				((Activity) context).finish();
				System.exit(0);
			}
		});
		dlg.show();
	}

	public static void showOkDialog(String title, String msg, Activity act) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(act);
		if (title != null) {

			TextView dialogTitle = new TextView(act);
			dialogTitle.setText(title);
			dialogTitle.setPadding(10, 10, 10, 10);
			dialogTitle.setGravity(Gravity.CENTER);
			dialogTitle.setTextColor(Color.WHITE);
			dialogTitle.setTextSize(20);
			dialog.setCustomTitle(dialogTitle);

		}
		if (msg != null) {
			dialog.setMessage(msg);
		}
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog dlg = dialog.show();
		TextView messageText = (TextView) dlg
				.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);

	}

	public static float getDisplayMetricsDensity(Context context) {
		density = context.getResources().getDisplayMetrics().density;

		return density;
	}

	public static int getPixel(Context context, int p) {
		if (density != 1) {
			return (int) (p * density + 0.5);
		}
		return p;
	}

	public static Animation FadeAnimation(float nFromFade, float nToFade) {
		Animation fadeAnimation = new AlphaAnimation(nToFade, nToFade);

		return fadeAnimation;
	}

	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean eMailValidation(String emailstring) {
		if (null == emailstring || emailstring.length() == 0) {
			return false;
		}
		Pattern emailPattern = Pattern
				.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher emailMatcher = emailPattern.matcher(emailstring);
		return emailMatcher.matches();
	}

	public static String urlBuilderForGetMethod(Map<String, String> g_map) {

		StringBuilder sbr = new StringBuilder();
		int i = 0;
		if (g_map.containsKey("url")) {
			sbr.append(g_map.get("url"));
			g_map.remove("url");
		}
		for (String key : g_map.keySet()) {
			if (i != 0) {
				sbr.append("&");
			}
			sbr.append(key + "=" + g_map.get(key));
			i++;
		}
		System.out.println("Builder url = " + sbr.toString());
		return sbr.toString();
	}

	public static int isValidate(String... fields) {
		if (fields == null) {
			return 0;
		}

		for (int i = 0; i < fields.length; i++) {
			if (TextUtils.isEmpty(fields[i])) {
				return i;
			}

		}
		return -1;
	}

	public static void showToast(String msg, Activity activity) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}

	public static String UppercaseFirstLetters(String str) {
		boolean prevWasWhiteSp = true;
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (Character.isLetter(chars[i])) {
				if (prevWasWhiteSp) {
					chars[i] = Character.toUpperCase(chars[i]);
				}
				prevWasWhiteSp = false;
			} else {
				prevWasWhiteSp = Character.isWhitespace(chars[i]);
			}
		}
		return new String(chars);
	}

	@TargetApi(15)
	public static void buttonEffect(ImageButton button, final int alpha) {

		button.setOnTouchListener(new OnTouchListener() {

			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				ImageButton btn = (ImageButton) v;
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN: {

					btn.setAlpha(alpha);

					break;
				}
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:

					btn.setAlpha(255);

					break;
				}
				return false;
			}
		});

	}

	public static final SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	}

	public static String convertToUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

}
