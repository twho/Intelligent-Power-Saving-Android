package com.tsungweiho.intelligentpowersaving.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import id.zelory.compressor.Compressor;

/**
 * Created by Tsung Wei Ho on 2015/4/7.
 */

// Singleton class
public class ImageUtils {

    private static Context context;

    // unit KB
    private static final int MAX_SIZE = (200) * 1024;

    private static final ImageUtils ourInstance = new ImageUtils();

    public static ImageUtils getInstance() {
        return ourInstance;
    }

    private ImageUtils() {
        this.context = MainActivity.getContext();
    }

    public static Bitmap decodeBase64ToBitmap(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public String encodeBase64ToString(Bitmap bitmapOrg) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte[] ba = bao.toByteArray();

        return Base64.encodeToString(ba, Base64.DEFAULT);
    }

    public File getCompressedImgFile(File imgFile) throws IOException {
        File compressedImgFile = imgFile;

        // Compress
        if (compressedImgFile.length() > MAX_SIZE)
            compressedImgFile = new Compressor(context).compressToFile(imgFile);

        return compressedImgFile;
    }

    public File getFileFromBitmap(Bitmap bitmap) {
        if (null == bitmap)
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_preload_img);

        File file = new File(context.getCacheDir(), String.valueOf(Calendar.getInstance().getTimeInMillis()));

        try {
            file.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (bitmap.getAllocationByteCount() > MAX_SIZE) {
                int rate = MAX_SIZE / (bitmap.getAllocationByteCount());
                if (rate <= 25)
                    rate = 25;
                bitmap.compress(Bitmap.CompressFormat.JPEG, rate, bos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }

            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return file;
    }

    public static Bitmap getOvalCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius) {
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        } else {
            finalBitmap = bitmap;
        }

        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(),
                finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        RectF oval = new RectF(0, 0, 130, 150);
        canvas.drawOval(oval, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, oval, paint);

        return output;
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        if (null == bitmap)
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_preload_profile);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(bitmap.getWidth() / 2 + 0.7f,
                bitmap.getHeight() / 2 + 0.7f,
                bitmap.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap = bitmap;
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(),
                finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));

        if (Build.VERSION.SDK_INT >= 21) {
            canvas.drawRoundRect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight(), radius, radius, paint);
        } else {
            canvas.drawRect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight(), paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

    public static void setRoundCornerImageViewFromUrl(String url, final ImageView imageView) {

        if (!"".equalsIgnoreCase(url)) {
            Picasso.with(context).load(url).resize((int) context.getResources().getDimension(R.dimen.activity_main_img_size), (int) context.getResources().getDimension(R.dimen.activity_main_img_size)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setImageBitmap(getRoundedRectBitmap(bitmap, bitmap.getWidth() / 15));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_preload_img));
        }
    }

    public static void setImageViewFromUrl(final String url, final ImageView imageView, final ProgressBar progressBar) {

        progressBar.animate();
        progressBar.setVisibility(View.VISIBLE);

        if (!"".equalsIgnoreCase(url)) {
            Picasso.with(context).load(url).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.invalidate();
                    imageView.setImageBitmap(bitmap);
                    progressBar.clearAnimation();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    setImageViewFromUrl(url, imageView, progressBar);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_preload_img));
            progressBar.clearAnimation();
            progressBar.setVisibility(View.GONE);
        }
    }
}
