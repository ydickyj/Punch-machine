package com.pemt.pda.punchmachine.punch_machine.db;

import java.io.File;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

public class SingleMediaScanner implements MediaScannerConnectionClient
{
	private MediaScannerConnection mMsc;
	private File mFile;

	public SingleMediaScanner(Context context, File f)
	{
		// TODO Auto-generated constructor stub
		mFile	= f;
		mMsc	= new MediaScannerConnection(context, this);
		mMsc.connect();
	}

	@Override
	public void onMediaScannerConnected()
	{
		mMsc.scanFile(mFile.getAbsolutePath(), null);
//		mMsc.scanFile(mFile.getAbsolutePath(), "media/*");
	}

	@Override
	public void onScanCompleted(String path, Uri uri)
	{
		mMsc.disconnect();
	}
}