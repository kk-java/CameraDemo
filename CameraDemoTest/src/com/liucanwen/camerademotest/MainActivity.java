package com.liucanwen.camerademotest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * 拍照demo
 * 功能：拍照、剪裁、保存到手机上
 * @author ck
 * @since 2014年3月20日 16:04:57
 */
public class MainActivity extends Activity implements OnClickListener
{
	private ImageView headIv;

	// 设置头像
	private static final int IMAGE_REQUEST_CODE = 0; // 请求码 本地图片
	private static final int CAMERA_REQUEST_CODE = 1; // 拍照
	private static final int RESULT_REQUEST_CODE = 2; // 裁剪
	private static final String SAVE_AVATORNAME = "head.png";// 保存的图片名

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		headIv = (ImageView) findViewById(R.id.image_layout);
		headIv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.image_layout:
			showOptionsDialog();
			break;

		default:
			break;
		}
	}

	// 选择图片来源
	private void showOptionsDialog()
	{
		String[] items = new String[] { "拍照", "选择本地图片" };
		
		DialogInterface.OnClickListener click = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
				case 0://拍照
					Intent intentFromCapture = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							.fromFile(new File(Environment
									.getExternalStorageDirectory(),
									SAVE_AVATORNAME)));
					startActivityForResult(intentFromCapture,
							CAMERA_REQUEST_CODE);
					break;
				case 1://选择本地图片
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery,
							IMAGE_REQUEST_CODE);
					break;
				}
			}
		};
		
		new AlertDialog.Builder(this).setItems(items,
				click).show();
	}

	/**
	 * 回调结果处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != RESULT_CANCELED)
		{
			switch (requestCode)
			{
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				startPhotoZoom(Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), SAVE_AVATORNAME)));
				break;
			case RESULT_REQUEST_CODE:
				if (data != null)
				{
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 */
	public void startPhotoZoom(Uri uri)
	{
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 */
	private void getImageToView(Intent data)
	{
		Bundle extras = data.getExtras();
		if (extras != null)
		{
			Bitmap photo = extras.getParcelable("data");
			saveMyBitmap(photo); // 保存裁剪后的图片到SD
			headIv.setImageBitmap(photo);
		}
	}

	/**
	 * 将头像保存在SDcard
	 */
	public void saveMyBitmap(Bitmap bitmap)
	{
		File f = new File(Environment.getExternalStorageDirectory(),
				SAVE_AVATORNAME);
		try
		{
			f.createNewFile();
			FileOutputStream fOut = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

			fOut.flush();
			fOut.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
