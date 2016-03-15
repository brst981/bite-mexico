package com.brst.android.bite.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.cropping.CropOption;
import com.brst.android.bite.app.cropping.CropOptionAdapter;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.home.HomeActivity;
import com.brst.android.bite.app.membership.MembershipActivity;
import com.brst.android.bite.app.util.UI;
import com.brst.android.bitemaxico.app.R;

public class LoginBiteBc extends Activity implements OnClickListener,
		OnItemSelectedListener {
	Button login, upload;
	Uri selectedImage;
	String mString_sendserver = null;
	TextView mTextView;
	Context context;
	Bitmap bitmap = null;
	final int PIC_CROP = 2;
	ImageView back, profile, demoProfile;
	EditText mEditTextfirstname, mEditTextlastname, mEditTextemail,
			mEditTextpassword, mEditTextconfirmpassword, mEditTextconfirmemail,
			mEditTextmobileno;
	HashMap<String, String> params = new HashMap<String, String>();
	private User user;
	SharedPreferences sharedpreferences;
	private UserDataHandler uDataHandler;
	private static int RESULT_LOAD_IMAGE = 1;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;

	private static final int CAMERA_CODE = 101, GALLERY_CODE = 201,
			CROPING_CODE = 301;

	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
	// getting unique id for device
	String id;
	String selectedCity;
	Spinner spinnerCity;
	SpinnerAdapter_ sAdapter;
	ArrayAdapter<String> arrayAdapter;
	ArrayList<String> arrayListID, arrayListName;
	int pos;
	String city, city_id;
	private File outPutFile = null;
	CropOption co;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bitbc_login);
		user = new User();
		uDataHandler = UserDataHandler.getInstance();
		sharedpreferences = LoginBiteBc.this.getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
		login = (Button) findViewById(R.id.btn_login);
		// getting unique id for device
		id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

		mTextView = (TextView) findViewById(R.id.text_show_hide);
		profile = (ImageView) findViewById(R.id.proflie_image_register);
		demoProfile = (ImageView) findViewById(R.id.demo_proflie_image_register);

		back = (ImageView) findViewById(R.id.btn_back_login);
		mEditTextfirstname = (EditText) findViewById(R.id.Firstname);
		mEditTextlastname = (EditText) findViewById(R.id.Lastname);
		mEditTextemail = (EditText) findViewById(R.id.Email);
		mEditTextpassword = (EditText) findViewById(R.id.Password);
		mEditTextconfirmpassword = (EditText) findViewById(R.id.ConfirmPassword);
		mEditTextconfirmemail = (EditText) findViewById(R.id.Confirm_Email);
		mEditTextmobileno = (EditText) findViewById(R.id.Mobile);
		spinnerCity = (Spinner) findViewById(R.id.spinner_city);
		spinnerCity.setOnItemSelectedListener(this);
		arrayListID = new ArrayList<String>();
		arrayListName = new ArrayList<String>();

		login.setOnClickListener(this);
		back.setOnClickListener(this);
		profile.setOnClickListener(this);
		arrayListName.add("Seleccionar ciudad");
		outPutFile = new File(
				android.os.Environment.getExternalStorageDirectory(),
				"temp.jpg");
		sAdapter = new SpinnerAdapter_(getApplicationContext(),
				R.layout.layout_spinner_item_, arrayListName);

		spinnerCity.setAdapter(sAdapter);
		requestForCities(Web.CITY);

	}

	private void requestForCities(String city) {
		// TODO Auto-generated method stub
		String url = Web.HOST + city;

		StringRequest strReq = new StringRequest(Method.GET, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("response" + "--On Response", response);
						// msgResponse.setText(response.toString());
						// UI.hideProgressDialog();
						getCities(response);

						// arrayAdapter = new ArrayAdapter<String>(
						// getApplicationContext(),
						// R.layout.layout_spinner_item_, R.id.txt_id,
						// arrayListName);

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: " + error.getMessage());
						// UI.hideProgressDialog();
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				WSConstant.DataKey.TAG_JSON_OBJECT);
	}

	protected void getCities(String response) {
		// TODO Auto-generated method stub

		try {
			arrayListName.clear();
			arrayListName.add("Seleccionar ciudad");
			JSONArray array = new JSONArray(response);

			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String id = jsonObject.getString("id");
				String name = jsonObject.getString("name");

				arrayListID.add(id);

				arrayListName.add(name);

			}

			sAdapter.notifyDataSetChanged();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
		}

	}

	private class SpinnerAdapter_ extends ArrayAdapter<String> {
		Context context;
		// String[] items = new String[] {};
		ArrayList<String> arrayList;

		public SpinnerAdapter_(final Context context,
				final int textViewResourceId, ArrayList<String> arrayList) {
			super(context, textViewResourceId, arrayList);
			this.arrayList = arrayListName;
			this.context = context;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(
						R.layout.layout_spinner_dropdown, parent, false);
			}

			TextView tv = (TextView) convertView.findViewById(R.id.txt_d_id);
			tv.setText(arrayList.get(position));
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(15);
			return convertView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.layout_spinner_item_,
						parent, false);
			}

			TextView tv = (TextView) convertView.findViewById(R.id.txt_id);
			tv.setText(arrayList.get(position));
			tv.setTextColor(Color.BLACK);
			// tv.setHint("City");

			tv.setTextSize(15);

			if (position == 0) {
				tv.setTextColor(Color.GRAY);
			}
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back_login:
			Intent mIntent = new Intent(LoginBiteBc.this, LoginPage.class);
			startActivity(mIntent);
			this.overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			finish();
			break;

		case R.id.btn_login:

			if (validate()) {

				registerOrLoginUser();
			}
			break;
		case R.id.proflie_image_register:
			open_dialog();
			break;
		}

	}

	/**
	 * Calcuate how much to compress the image
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		// final int height = options.outHeight;
		// final int width = options.outWidth;
		// int inSampleSize = 1; // default to not zoom image
		//
		// if (height > reqHeight || width > reqWidth) {
		// final int heightRatio = Math.round((float) height
		// / (float) reqHeight);
		// final int widthRatio = Math.round((float) width / (float) reqWidth);
		// inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		// }
		// Log.e("Insample size", inSampleSize + "");
		final int REQUIRED_SIZE = 512;
		int width_tmp = options.outWidth, height_tmp = options.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		return scale;
	}

	public static int calculateInSampleSize2(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 2;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 1;
			}
		}

		return inSampleSize;
	}

	/**
	 * resize image to 480x800
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {

		File file = new File(filePath);
		long originalSize = file.length();

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize based on a preset ratio
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		Bitmap compressedImage = BitmapFactory.decodeFile(filePath, options);

		return compressedImage;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GALLERY_CODE && resultCode == RESULT_OK
				&& null != data) {
			selectedImage = data.getData();
			// performCrop();

			// String[] filePathColumn = { MediaStore.Images.Media.DATA };
			//
			// Cursor cursor = getContentResolver().query(selectedImage,
			// filePathColumn, null, null, null);
			// cursor.moveToFirst();
			//
			// int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			// String picturePath = cursor.getString(columnIndex);
			//
			// cursor.close();
			//
			// try {
			// // hide video preview
			//
			// bitmap = decodeFile(picturePath);
			// mTextView.setVisibility(View.GONE);
			// demoProfile.setVisibility(View.GONE);
			// profile.setImageBitmap(bitmap);
			//
			// mString_sendserver = convert_image(bitmap);
			// Log.e("mString_sendserver:" + "", mString_sendserver);
			//
			// } catch (NullPointerException e) {
			// e.printStackTrace();
			// }

			CropingIMG();

		}

		else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

			CropingIMG();
		} else if (requestCode == CROPING_CODE && resultCode == RESULT_OK) {

			try {
				if (outPutFile.exists()) {

					bitmap = decodeFile(outPutFile.getPath());
					mTextView.setVisibility(View.GONE);
					demoProfile.setVisibility(View.GONE);
					profile.setImageBitmap(bitmap);
					mString_sendserver = convert_image(bitmap);

				} else {
					Toast.makeText(getApplicationContext(),
							"Error al guardar la imagen", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// else if (resultCode == RESULT_OK) {
		// // successfully captured the image
		// // display it in image view
		// previewCapturedImage();
		// }

		else if (resultCode == RESULT_CANCELED) {
			// user cancelled Image capture
			Toast.makeText(getApplicationContext(),
					"El usuario ha cancelado la captura de imágenes", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 
	 */
	private void CropingIMG() {
		// TODO Auto-generated method stub

		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);
		int size = list.size();
		if (size == 0) {
			Toast.makeText(this, "Si no encuentra aplicación recorte de la imagen",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			intent.setData(selectedImage);
			intent.putExtra("outputX", 512);
			intent.putExtra("outputY", 512);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);

			// TODO: don't use return-data tag because it's not return large
			// image data and crash not given any message
			// intent.putExtra("return-data", true);

			// Create output file here
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, CROPING_CODE);
			} else {
				for (ResolveInfo res : list) {
					co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);
					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));
					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Elegir aplicación de recorte");
				builder.setCancelable(false);
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROPING_CODE);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (selectedImage != null) {
							getContentResolver().delete(selectedImage, null,
									null);
							selectedImage = null;
						}
					}
				});

				AlertDialog alert = builder.create();
				alert.show();
			}
		}

	}

	/**
	 * 
	 */

	public Bitmap decodeFile(String path) {// you can provide file path here

		int orientation;
		try {
			if (path == null) {
				return null;
			}
			// decode image size
			BitmapFactory.Options option1 = new BitmapFactory.Options();
			option1.inJustDecodeBounds = false;

			BitmapFactory.decodeFile(path, option1);

			// decode with inSampleSize
			option1.inSampleSize = calculateInSampleSize(option1, 100, 100);
			option1.inJustDecodeBounds = false;

			Bitmap bm = BitmapFactory.decodeFile(path, option1);
			Bitmap bitmap = bm;

			ExifInterface exif = new ExifInterface(path);

			orientation = exif
					.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

			// exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

			Matrix m = new Matrix();

			if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
				m.postRotate(180);
				// m.postScale((float) bm.getWidth(), (float) bm.getHeight());
				// if(m.preRotate(90)){

				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				m.postRotate(90);

				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				m.postRotate(270);

				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			}
			return bitmap;
		} catch (Exception e) {
			return null;
		}

		// decode image size
		// try {
		// // decode image size
		// BitmapFactory.Options o = new BitmapFactory.Options();
		// o.inJustDecodeBounds = true;
		// Log.e("o-->>", "" + o);
		// Log.e("File", "" + f);
		// BitmapFactory.decodeStream(new FileInputStream(f), null, o);
		//
		// // Find the correct scale value. It should be the power of 2.
		// final int REQUIRED_SIZE = 512;
		// int width_tmp = o.outWidth, height_tmp = o.outHeight;
		// int scale = 1;
		// while (true) {
		// if (width_tmp / 2 < REQUIRED_SIZE
		// || height_tmp / 2 < REQUIRED_SIZE)
		// break;
		// width_tmp /= 2;
		// height_tmp /= 2;
		// scale *= 2;
		// }
		// // decode with inSampleSize
		// // BitmapFactory.Options o2 = new BitmapFactory.Options();
		// // o2.inSampleSize = scale;
		// // return BitmapFactory.decodeStream(new FileInputStream(f), null,
		// o2);
		//
		// } catch (FileNotFoundException e) {
		// // TODO: handle exception
		//
		// Log.e("e-->>", "" + e);
		// }

		// ExifInterface exif = new ExifInterface(path);
		//
		// orientation = exif
		// .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		//
		// // exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);
		//
		// Matrix m = new Matrix();
		//
		// if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
		// m.postRotate(180);
		// // m.postScale((float) bm.getWidth(), (float) bm.getHeight());
		// // if(m.preRotate(90)){
		//
		// bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
		// bm.getHeight(), m, true);
		// return bitmap;
		// } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
		// m.postRotate(90);
		//
		// bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
		// bm.getHeight(), m, true);
		// return bitmap;
		// } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
		// m.postRotate(270);
		//
		// bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
		// bm.getHeight(), m, true);
		// return bitmap;
		// }
		// return bitmap;
		// } catch (Exception e) {
	}

	/**
	 * Display image from a path to ImageView
	 */
	private void previewCapturedImage() {
		try {

			if (outPutFile.exists()) {

				Bitmap photo = decodeFile(outPutFile.getPath());
				mTextView.setVisibility(View.GONE);
				demoProfile.setVisibility(View.GONE);
				profile.setImageBitmap(photo);
				mString_sendserver = convert_image(photo);

			} else {
				Toast.makeText(getApplicationContext(),
						"Error al guardar la imagen", Toast.LENGTH_SHORT).show();
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	// private void previewCapturedImage() {
	// try {
	// // hide video preview
	//
	// // bimatp factory
	// BitmapFactory.Options options = new BitmapFactory.Options();
	//
	// // downsizing image as it throws OutOfMemory Exception for larger
	// // images
	// options.inSampleSize = 8;
	//
	// bitmap = BitmapFactory.decodeFile(selectedImage.getPath(), options);
	// mTextView.setVisibility(View.GONE);
	// demoProfile.setVisibility(View.GONE);
	// profile.setImageBitmap(bitmap);
	//
	// // Bitmap mBitmap_send = StringToBitMap(picturePath);
	// // Log.e("mBitmap_send: ", ""+mBitmap_send);
	//
	// mString_sendserver = convert_image(bitmap);
	// Log.e("mString_sendserverCamera: ", "" + mString_sendserver);
	//
	// } catch (NullPointerException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/*
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	private boolean validate() {
		String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";

		if (mEditTextfirstname.getText().toString().trim().equals("")) {
			showAlertDialog(this, "Error", "Por favor ingresa tu nombre", true);
			return false;
		}
		if (mEditTextlastname.getText().toString().trim().equals("")) {
			showAlertDialog(this, "Error", "Por favor ingresa tu apellido", true);
			return false;
		}
		if (mEditTextemail.getText().toString().trim().equals("")) {
			showAlertDialog(this, "Error", "Por favor ingresa tu correo", true);
			return false;
		}
		if (!mEditTextemail.getText().toString().matches(emailPattern)) {
			showAlertDialog(this, "Error", "Por favor ingresa tu válido correo", true);
			return false;
		}
		if (!mEditTextconfirmemail.getText().toString().matches(emailPattern)) {
			showAlertDialog(this, "Error", "Por favor ingresa tu válido confirmar correo",
					true);
			return false;
		}
		if (!mEditTextemail
				.getText()
				.toString()
				.toLowerCase()
				.equals(mEditTextconfirmemail.getText().toString()
						.toLowerCase())) {
			showAlertDialog(this, "Error",
					"El correo electrónico no coincide con el de confirmación", true);
			return false;
		}

		if (mEditTextmobileno.getText().toString().trim().equals("")) {
			showAlertDialog(this, "Error", "Por favor ingresa un número de celular válido", true);
			return false;
		}

		if (mEditTextpassword.getText().toString().trim().equals("")) {
			showAlertDialog(this, "Error", "Por favor ingresa tu contraseña", true);
			return false;
		}

		if (mEditTextpassword.getText().toString().trim().length() < 6) {
			showAlertDialog(
					this,
					"Error",
					"Por favor ingresa mínimo 6 (seis) caracteres",
					true);
			return false;
		}
		if (mEditTextconfirmpassword.getText().toString().trim().equals("")) {
			showAlertDialog(this, "Error", "Por favor ingresa tu confirmar la contraseña",
					true);
			return false;
		}

		if (!mEditTextconfirmpassword.getText().toString()
				.equals(mEditTextpassword.getText().toString())) {
			showAlertDialog(this, "Error",
					"La contraseña no coincide", true);
			return false;
		}
		pos = spinnerCity.getSelectedItemPosition();

		if (pos == 0) {
			showAlertDialog(this, "Error", "Por favor ingresa tu ciudad ", true);
			return false;
		} else {
		}

		if (bitmap == null) {
			showAlertDialog(this, "Error", "Ups¡, olvidaste añadir una imagen.Necesitamos hacer tu credencial digital para tu membresía.esto es para que nuestros restaurantes puedan ver que realmente eres tú cuando muestres tu aplicación.", true);
			return false;
		}

		return true;
	}

	public void showAlertDialog(Context context, String title, String message,
			boolean error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		if (error)
			alertDialogBuilder.setIcon(R.drawable.fail);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/*
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		selectedImage = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CODE);
	}

	public void open_dialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Seleccione imagen");
		alertDialogBuilder.setPositiveButton("Cámara",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						captureImage();

					}
				});
		alertDialogBuilder.setNegativeButton("Galería",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent i = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

						startActivityForResult(i, GALLERY_CODE);
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	/**
	 * Making json object request to register user; SECOND CALL
	 * */

	private void makeRegisterRequest(String register,
			final Map<String, String> rParams) {
		UI.showProgressDialog(this);
		String url = Web.HOST + register;

		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("CHECK Register Response:" + "", response);
						// msgResponse.setText(response.toString());
						UI.hideProgressDialog();
						login(response);

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d("", "Error: " + error.getMessage());
						UI.hideProgressDialog();
					}
				}) {

			/**
			 * Passing some request headers
			 * */
			// @Override
			// public Map<String, String> getHeaders() throws AuthFailureError {
			// HashMap<String, String> headers = new HashMap<String, String>();
			// headers.put("Content-Type", "application/json");
			// return headers;
			// }

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = rParams;
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				WSConstant.DataKey.TAG_JSON_OBJECT);

	}

	// THIRD CALL
	protected void login(String response) {
		// TODO Auto-generated method stub
		try {
			Log.i("response======", "" + response);
			JSONObject jsonOBject = new JSONObject(response);

			if (jsonOBject.getBoolean("success")) {

				String customerId = jsonOBject.getString("customerid");
				String session = jsonOBject.getString("sessionid");
				// String firstname = jsonOBject.getString("first_name");
				// String lastname = jsonOBject.getString("last_name");

				if (jsonOBject.getString("city") != null) {
					city = jsonOBject.getString("city");
					city_id = jsonOBject.getString("cityid");
				}

				user.setCustomerid(customerId);
				user.setUserSessionId(session);
				user.setFirstName(mEditTextfirstname.getText().toString());
				user.setLastName(mEditTextlastname.getText().toString());
				user.setEmail(mEditTextemail.getText().toString());
				// user.set(customerId);
				user.setUserSessionId(session);
				Editor editor = sharedpreferences.edit();
				editor.putString(BiteBc.USER_ID_FINAL, mEditTextemail.getText()
						.toString());
				editor.putString(BiteBc.USER_PWD_FINAL, mEditTextpassword.getText()
						.toString());
				// editor.putString(BiteBc.USER_PWD_FINAL, user.getuFacebookId());
				editor.putString("WITHOUT_FACEBOOK", "301");
				editor.putString("SECTION_PHP", "email");
				editor.putString("CUSTOMER_ID", customerId);
				editor.putString("SESSION_ID", session);

				editor.putString(BiteBc.FIRSTNAME, mEditTextfirstname.getText()
						.toString());
				editor.putString(BiteBc.LASTNAME, mEditTextlastname.getText()
						.toString());
				editor.putString(BiteBc.CUST_ID, customerId);
				editor.putString(BiteBc.CITY, city);
				editor.putString(BiteBc.CITY_ID, city_id);

				editor.commit();
				uDataHandler.setUser(user);
				confirmAlertDialog(LoginBiteBc.this, "Gracias por registrarte!",
						"Registrado con éxito");
				// finish();
				// checkUserStatus();
			} else {
				String msg = jsonOBject.getString("message");
				showAlertDialog(this, "Error", msg, true);
				// LogMsg.LOG(this, msg);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void confirmAlertDialog(Context context, String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
						Intent mIntent = new Intent(LoginBiteBc.this,
								LoginPage.class);
						startActivity(mIntent);
						finish();
						// isInternetPresent = cd.isConnectingToInternet();
						// checkInternet(isInternetPresent, tag);

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	// four call
	private void checkUserStatus() {
		HashMap<String, String> params;
		params = new HashMap<String, String>();
		params.put(DataKey.CUSTOMER_ID, user.getCustomerid());
		makeRequestForCheckIn(Web.CHECKIN, params);

	}

	/**
	 * Register user to bite bc first time call
	 * 
	 * @param jObject
	 */
	protected void registerOrLoginUser() {

		// //Log.d.e(TAG, "");
		try {
			user = uDataHandler.getUser();

			if (user == null) {
				user = new User();
			}

			user.setFirstName(mEditTextfirstname.getText().toString());
			user.setLastName(mEditTextlastname.getText().toString());
			user.setEmail(mEditTextemail.getText().toString());
			// user.setuFacebookId(pwd);

			user.setFirstName(mEditTextfirstname.getText().toString());
			user.setLastName(mEditTextlastname.getText().toString());
			user.setEmail(mEditTextemail.getText().toString());
			user.setuFacebookId(mEditTextpassword.getText().toString());

			Map<String, String> params = new HashMap<String, String>();
			// params.put("firstname", firstname);
			// params.put("lastname", lastname);
			// params.put("email", email);
			// params.put("password", pwd);

			params.put("firstname", mEditTextfirstname.getText().toString());
			params.put("lastname", mEditTextlastname.getText().toString());
			params.put("email", mEditTextemail.getText().toString());
			params.put("password", mEditTextpassword.getText().toString());
			params.put("cell_number", mEditTextmobileno.getText().toString());
			params.put("device_id", id);
			params.put("section", "email");
			params.put("city", selectedCity);

			if (mString_sendserver == null) {

			} else {
				params.put("profileimg", mString_sendserver);

			}

			params.put("platform", "android");

			makeRegisterRequest(Web.REGISTER, params);

		} catch (Exception e) {
			show_Alert_Dialog(this, "El correo electrónico no existir",
					"Por favor, compruebe su correo electrónico de identificación facebook");
			e.printStackTrace();
		}
	}

	public void show_Alert_Dialog(Context context, String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);
		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	// five call
	private void makeRequestForCheckIn(final String checkin,
			final Map<String, String> params) {

		String url = Web.HOST + checkin;
		UI.showProgressDialog(this);
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("" + "On Response", response);
						// msgResponse.setText(response.toString());
						parseResponseChekin(response);

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("", "Error: " + error.getMessage());
						UI.hideProgressDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	// six call
	protected void parseResponseChekin(String response) {
		UI.hideProgressDialog();

		try {
			JSONObject jsonOBject = new JSONObject(response);

			if (!jsonOBject.getBoolean("success")) {

				if (jsonOBject.getString("status").equals("not purchased")) {
					Editor editor = sharedpreferences.edit();

					editor.putString("profileimg_url",
							jsonOBject.getString("profileimg_url"));
					editor.commit();

					Intent intent;
					intent = new Intent(LoginBiteBc.this,
							MembershipActivity.class);
					startActivity(intent);
					LoginBiteBc.this.overridePendingTransition(
							R.anim.slide_in_left, R.anim.slide_out_right);
					LoginBiteBc.this.finish();
				} else if (jsonOBject.getString("status").equals("paused")) {
					show_Alert_Dialog(LoginBiteBc.this, "No Membership",
							"Your membership plan has expired");
				} else {
					show_Alert_Dialog(LoginBiteBc.this, "Error",
							"Your membership plan has expired");
				}
			} else {
				String planId = jsonOBject.getString("planid");
				Editor editor = sharedpreferences.edit();
				editor.putString(BiteBc.USER_PLAN, planId);
				editor.commit();

				uDataHandler
						.setUserSelectedPlan(jsonOBject.getString("planid"));
				showMembershipActivity();

			}

		} catch (JSONException e) {
			Log.e("", e.getMessage());
		}

	}

	// seven call
	private void showMembershipActivity() {

		Intent intent;
		if (sharedpreferences != null
				&& sharedpreferences.contains(BiteBc.USER_PLAN)) {
			intent = new Intent(LoginBiteBc.this, HomeActivity.class);
		} else {
			intent = new Intent(LoginBiteBc.this, MembershipActivity.class);
		}
		// intent = new Intent(getActivity(), MembershipActivity.class);
		startActivity(intent);
		LoginBiteBc.this.overridePendingTransition(R.anim.slide_in_left,
				R.anim.slide_out_right);
		LoginBiteBc.this.finish();

	}

	public String convert_image(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream); // compress to
		// which
		// format
		// you want.
		byte[] byte_arr = stream.toByteArray();
		String ba1 = BaseConvert.encodeBytes(byte_arr);

		return ba1;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		selectedCity = parent.getItemAtPosition(position).toString();

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		showAlertDialog(this, "Error", "Please select your city ", true);
	}

}
