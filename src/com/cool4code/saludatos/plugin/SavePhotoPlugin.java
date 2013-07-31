package com.cool4code.saludatos.plugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class SavePhotoPlugin extends CordovaPlugin {

	private String appVersion = "";

	@Override
	public boolean execute(String action, JSONArray data,
			CallbackContext callbackContext) throws JSONException {

		if (action.equals("saveImageDataToLibrary")) {
			try {
				String imageData = data.getString(0);
				Bitmap bitmap = this.getBitmapFromData(imageData);
				String path = this.savePhoto(bitmap);
				callbackContext.success(path);
			} catch (JSONException jsonEx) {
				callbackContext.error("Could not save the image");
			}
			return true;
		}
		return false;
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	public Bitmap getBitmapFromData(String data) {
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

	private String savePhoto(Bitmap bmp) {
		String retVal = "";
		try {
			File imageFileName = null;
			FileOutputStream out = null;
			Calendar c = Calendar.getInstance();
			String date = fromInt(c.get(Calendar.MONTH))
					+ fromInt(c.get(Calendar.DAY_OF_MONTH))
					+ fromInt(c.get(Calendar.YEAR))
					+ fromInt(c.get(Calendar.HOUR_OF_DAY))
					+ fromInt(c.get(Calendar.MINUTE))
					+ fromInt(c.get(Calendar.SECOND));

			int check = appVersion.compareTo("2.3.3");
			if (check >= 1) {
				File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				imageFileName = new File(path, date.toString() + ".jpg");
			} else {
				imageFileName = new File(Environment.getExternalStorageDirectory(), date.toString() + ".jpg");
			}
			
			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			this.scanPhoto(imageFileName.toString());
			out = null;
			System.out.println(retVal);
			retVal = imageFileName.toString();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(retVal);
			retVal = "No existe el directorio para guardar la imagen";
		}
		return retVal;
	}

	private String fromInt(int val) {
		return String.valueOf(val);
	}

	private void scanPhoto(String imageFileName) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(imageFileName);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.cordova.getActivity().sendBroadcast(mediaScanIntent);
	}
}