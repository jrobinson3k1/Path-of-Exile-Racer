package com.jasonrobinson.racer.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;

public class RawTypeface {

	private static LruCache<Integer, Typeface> sTypefaceCache = new LruCache<Integer, Typeface>(5);

	public static Typeface obtain(Context context, int resId) {

		Typeface typeface = sTypefaceCache.get(resId);

		if (typeface == null) {
			String outPath = context.getCacheDir() + File.separator + resId + ".raw";

			File outFile = new File(outPath);
			if (!outFile.exists()) {
				try {
					copyToFile(context, resId, outFile);
				}
				catch (IOException e) {
					return null;
				}
			}

			typeface = Typeface.createFromFile(outFile);
			outFile.delete();

			sTypefaceCache.put(resId, typeface);
		}

		return typeface;
	}

	private static void copyToFile(Context context, int resId, File outFile) throws IOException {

		InputStream is = context.getResources().openRawResource(resId);

		byte[] buffer = new byte[is.available()];
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));

		try {
			int read = 0;
			while ((read = is.read(buffer)) > 0) {
				bos.write(buffer, 0, read);
			}
		}
		finally {
			bos.close();
			is.close();
		}
	}
}
