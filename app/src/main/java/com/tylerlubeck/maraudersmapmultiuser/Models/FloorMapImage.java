package com.tylerlubeck.maraudersmapmultiuser.Models;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.tylerlubeck.maraudersmapmultiuser.R;


/**
 * Created by Hunter on 10/11/14.
 *
 */
public class FloorMapImage implements AdapterView.OnItemSelectedListener {
    private final int width;
    private final int height;
    private int original_width;
    private int original_height;
    private Bitmap unmarked_image;
    private final ImageView image_view;
    private final Context context;

    private final int DEFAULT_WIDTH = 1200;
    private final int DEFAULT_HEIGHT = 800;
    private final int DEFAULT_POINT_RADIUS = 20;


    /**
     * Create a class to represent a FloorMapImage.
     *      This will fetch the correct image from the drawables folder based on the building name and floor number.
     *      It will scale the image based on a screen size of 1200x800.
     *      It handles drawing points on the image
     * @param building_name     The name of the building this image_view represents
     * @param floor_number      The floor of the building this represents
     * @param image_view        The View to put the image on
     * @param context           The context the image_view belongs to
     */
    public FloorMapImage(String building_name, int floor_number, ImageView image_view, Context context){
        /* TODO: Eventually calculate these based on screen size */
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.image_view = image_view;
        this.context = context;
        this.setImage(building_name, floor_number);
    }

    /**
     * Instantiate a FloorMapImage based on a blank image view, with the image itself to be filled in later.
     * @param image_view
     * @param context
     */
    public FloorMapImage(ImageView image_view, Context context) {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.image_view = image_view;
        this.context = context;
    }

    public void setImageFromUrl(String imageUrl, final int x_coordinate, final int y_coordinate) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageUrl, this.image_view, null, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                Log.d("MARAUDERSMAP", "STARTED LOADING IMAGE " + imageUri);
                View parentView = (View) view.getParent();
                TextView loadingText = (TextView) parentView.findViewById(R.id.map_loading_textview);
                loadingText.setText("Magic takes time. So do network requests.");
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.e("MARAUDERSMAP", "FAILED TO LOAD IMAGE " + imageUri);
                Log.e("MARAUDERSMAP", "BECAUSE: " + failReason.toString());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                View parentView = (View) view.getParent();
                TextView loadingText = (TextView) parentView.findViewById(R.id.map_loading_textview);
                ProgressBar progressBar = (ProgressBar) parentView.findViewById(R.id.map_loading_spinner);
                Button updateBtn = (Button) parentView.findViewById(R.id.update_map_btn);
                progressBar.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                updateBtn.setVisibility(View.VISIBLE);
                Log.d("MARAUDERSMAP", "FINISHED LOADING IMAGE FROM " + imageUri);
                FloorMapImage.this.setImageFromBitmap(loadedImage);
                FloorMapImage.this.draw_point_clear(x_coordinate, y_coordinate);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Log.e("MARAUDERSMAP", "CANCELLED LOADING IMAGE " + imageUri);

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                View parentView = (View) view.getParent();
                TextView loadingText = (TextView) parentView.findViewById(R.id.map_loading_textview);
                loadingText.setText("Here we gooooo");
                Log.d("MARAUDERSMAP", String.format("Image %d/%d loaded", current, total));
            }
        });
    }

    /**
     * Sets the image in the image view, and repopulates the other images
     * @param building_name     The string name of the building
     * @param floor_number      The floor number of the building
     */
    void setImage(String building_name, int floor_number) {
        String file_path = building_name.toLowerCase().replaceAll(" ", "_").replaceAll("/", "") + String.valueOf(floor_number);

        this.image_view.setImageResource(this.context.getResources().getIdentifier(file_path,
                "drawable",
                this.context.getPackageName()));
        Bitmap Floorbitmap = ((BitmapDrawable) this.image_view.getDrawable()).getBitmap();
        this.setImageFromBitmap(Floorbitmap);

    }

    private void setImageFromBitmap(Bitmap bitmap) {
        this.original_height = bitmap.getHeight();
        this.original_width = bitmap.getWidth();
        this.unmarked_image = Bitmap.createScaledBitmap(bitmap,
                this.width,
                this.height,
                true);
        this.image_view.setImageBitmap(this.unmarked_image);
    }

    /**
     *  Draws a point on the image_view at (x, y)
     *  Clears all other points drawn on the image_view
     * @param x     The x coordinate to draw at
     * @param y     The y coordinate to draw at
     */
    void draw_point_clear(int x, int y){
        Bitmap bitmap = this.unmarked_image.copy(this.unmarked_image.getConfig(),
                                                true);
        this.draw_point(x, y, bitmap);
    }

    /**
     *  Draws a point on the image_view at (x, y)
     *  Does not clear the image_view before drawing
     * @param x     The x coordinate to draw at
     * @param y     The y coordinate to draw at
     */
    void draw_point_noclear(int x, int y){
        Bitmap bitmap = ((BitmapDrawable)this.image_view.getDrawable()).getBitmap();
        this.draw_point(x, y, bitmap);
    }

    /**
     * Draws a point on the given bitmap, and sets the image_view to display the updated bitmap
     * @param x         The x coordinate to draw at
     * @param y         The y coordinate to draw at
     * @param bitmap    The bitmap to draw on
     */
    private void draw_point(int x, int y, Bitmap bitmap) {
        if ( x >= 0 && y >= 0) {
            float scaled_x = x * (float) this.width / (float) this.original_width;
            float scaled_y = y * (float) this.height / (float) this.original_height;
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawCircle(scaled_x, scaled_y, DEFAULT_POINT_RADIUS, paint);
            this.image_view.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Location location = (Location) adapterView.getItemAtPosition(position);
        this.draw_point_clear(location.getX_coordinate(), location.getY_coordinate());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Bitmap bitmap = this.unmarked_image.copy(this.unmarked_image.getConfig(), true);
        this.image_view.setImageBitmap(bitmap);
    }
}
