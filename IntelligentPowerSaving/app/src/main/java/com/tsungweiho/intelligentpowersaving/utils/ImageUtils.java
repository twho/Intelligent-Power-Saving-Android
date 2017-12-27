package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;
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
import com.tsungweiho.intelligentpowersaving.IntelligentPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import id.zelory.compressor.Compressor;

/**
 * Class for performing image processing tasks
 *
 * This singleton class is designed as a singleton class and is used to handle all image processing tasks within the app.
 *
 * @author Tsung Wei Ho
 * @version 0218.2017
 * @since 1.0.0
 */
public class ImageUtils {

    private static final int MAX_SIZE = (200) * 1024; // unit KB

    private static final ImageUtils ourInstance = new ImageUtils();

    public static ImageUtils getInstance() {
        return ourInstance;
    }

    private ImageUtils() {}

    /**
     * Get application context for chart use
     *
     * @return application context
     */
    private Context getContext() {
        return IntelligentPowerSaving.getContext();
    }

    /**
     * Decode base64 string to an image as bitmap
     *
     * @param input the base64 string
     * @return the image as bitmap decoded from base64 string
     */
    public Bitmap decodeBase64ToBitmap(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    /**
     * Encode bitmap to base64 format string
     *
     * @param bitmap the bitmap to be encoded to base64 string
     * @return the base64 string encoded from bitmap
     */
    public String encodeBase64ToString(Bitmap bitmap) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte[] ba = bao.toByteArray();

        return Base64.encodeToString(ba, Base64.DEFAULT);
    }

    /**
     * Compress image file to specified size
     *
     * @param imgFile the image file to be compressed
     * @return the compressed image
     * @throws IOException the exception from reading or writing files
     */
    public File getCompressedImgFile(File imgFile) throws IOException {
        File compressedImgFile = imgFile;

        // Compress the image if its size exceed specified max size
        if (compressedImgFile.length() > MAX_SIZE)
            compressedImgFile = new Compressor(getContext()).compressToFile(imgFile);

        return compressedImgFile;
    }

    /**
     * Get file resource of selected bitmap
     *
     * @param bitmap the bitmap to get file resource from
     * @return the file of the selected bitmap
     */
    public File getFileFromBitmap(Bitmap bitmap) {
        if (null == bitmap)
            bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_preload_img);

        File file = new File(getContext().getCacheDir(), String.valueOf(Calendar.getInstance().getTimeInMillis()));

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

    /**
     * Get oval cropped bitmap
     *
     * @param bitmap the bitmap to be edited
     * @param radius the radius of the corner of the bitmap
     * @return the bitmap after being cropped
     */
    public Bitmap getOvalCroppedBitmap(Bitmap bitmap, int radius) {
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

    /**
     * Get rounded cropped bitmap
     *
     * @param bitmap the bitmap to be edited
     * @return the bitmap after being cropped
     */
    public Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        if (null == bitmap)
            return BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_preload_profile);
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

    /**
     * Get bitmap with rounded corner
     *
     * @param bitmap the bitmap to be edited
     * @param radius the corner radius to be set to the bitmap
     * @return the bitmap after being edited
     */
    private Bitmap getRoundedRectBitmap(Bitmap bitmap, int radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));

        if (Build.VERSION.SDK_INT >= 21) {
            canvas.drawRoundRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), radius, radius, paint);
        } else {
            canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // Different types of image will have different editing style
    public final int IMG_TYPE_BUILDING = 0;
    public final int IMG_TYPE_PROFILE = 1;

    /**
     * Set rounded-corner image from web resource using Picasso library
     *
     * @param url the url of the image resource
     * @param imageView the imageView to be set
     * @param imgType the type of image
     */
    public void setRoundedCornerImageViewFromUrl(String url, final ImageView imageView, final int imgType) {

        if (!"".equalsIgnoreCase(url)) {
            Picasso.with(getContext()).load(url).resize((int) getContext().getResources().getDimension(R.dimen.activity_main_img_size), (int) getContext().getResources().getDimension(R.dimen.activity_main_img_size)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    switch (imgType) {
                        case IMG_TYPE_BUILDING:
                            imageView.setImageBitmap(getRoundedRectBitmap(bitmap, bitmap.getWidth() / 15));
                            break;
                        case IMG_TYPE_PROFILE:
                            imageView.setImageBitmap(getRoundedCroppedBitmap(bitmap));
                            break;
                        default:
                            imageView.setImageBitmap(getRoundedCroppedBitmap(bitmap));
                            break;
                    }

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            imageView.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_preload_img));
        }
    }

    /**
     * Set image from web resource using Picasso library
     *
     * @param url the url of the image resource
     * @param imageView the imageView to be set
     * @param progressBar the progressBar shown before image finishes loading
     */
    public void setImageViewFromUrl(final String url, final ImageView imageView, final ProgressBar progressBar) {

        progressBar.animate();
        progressBar.setVisibility(View.VISIBLE);

        if (!"".equalsIgnoreCase(url)) {
            Picasso.with(getContext()).load(url).into(new Target() {
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
            imageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_preload_img));
            progressBar.clearAnimation();
            progressBar.setVisibility(View.GONE);
        }
    }
}
