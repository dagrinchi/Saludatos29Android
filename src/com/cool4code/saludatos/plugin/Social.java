package com.cool4code.saludatos.plugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.net.Uri;
import android.os.Environment;
import android.content.Intent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Social extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if (action.equals("share")) {

			String message = args.getString(0);
			String imageData = args.getString(1);
			Bitmap image = this.getBitmapFromData(imageData);
			File imageFile = this.saveImage(image);

			this.share(message, imageFile, callbackContext);
			return true;
		}
		return false;
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	private Bitmap getBitmapFromData(String data) {
		try {
			byte[] decodedString = Base64.decode(data, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,
					0, decodedString.length);
			return decodedByte;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void share(String message, File imageFile, CallbackContext callbackContext) {
		try {
			Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
			sendIntent.setType("image/jpeg");
			sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Saludatos - Estadísticas de Salud");
			sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
			sendIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
			this.cordova.startActivityForResult(this, sendIntent, 0);
			
			callbackContext.success("OK!");
		} catch (Exception e) {
			e.printStackTrace();
			callbackContext.success("FAIL!");
		}
	}

	private File saveImage(Bitmap bmp) {
		try {
			File imageFileName = null;
			FileOutputStream out = null;
			Calendar c = Calendar.getInstance();
			String date = fromInt(c.get(Calendar.MONTH)) + fromInt(c.get(Calendar.DAY_OF_MONTH)) + fromInt(c.get(Calendar.YEAR)) + fromInt(c.get(Calendar.HOUR_OF_DAY)) + fromInt(c.get(Calendar.MINUTE)) + fromInt(c.get(Calendar.SECOND));

			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			imageFileName = new File(path, date.toString() + ".jpg");

			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			out = null;
			return imageFileName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String fromInt(int val) {
		return String.valueOf(val);
	}
}
