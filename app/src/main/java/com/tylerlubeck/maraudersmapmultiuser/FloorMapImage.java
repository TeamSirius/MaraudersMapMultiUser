package com.tylerlubeck.maraudersmapmultiuser;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Canvas;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

/**
 * Created by Hunter on 10/11/14.
 *
 */
public class FloorMapImage implements AdapterView.OnItemSelectedListener {
    private final int width;
    private final int height;
    private int original_width;
    private int original_height;
    private final int point_radius;
    private Bitmap unmarked_image;
    private final ImageView image_view;
    private final Context context;

    private final int DEFAULT_WIDTH = 1200;
    private final int DEFAULT_HEIGHT = 800;
    private final int DEFAULT_POINT_RADIUS = 10;


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
    FloorMapImage(String building_name, int floor_number, ImageView image_view, Context context){
        /* TODO: Eventually calculate these based on screen size */
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.point_radius = DEFAULT_POINT_RADIUS;
        this.image_view = image_view;
        this.context = context;
        this.setImage(building_name, floor_number);
    }

    /**
     * Instantiate a FloorMapImage based on a blank image view, with the image itself to be filled in later.
     * @param image_view
     * @param context
     */
    FloorMapImage(ImageView image_view, Context context) {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.point_radius = DEFAULT_POINT_RADIUS;
        this.image_view = image_view;
        this.context = context;
    }

    /**
     * Sets the image in the image view, and repopulates the other images
     * @param building_name     The string name of the building
     * @param floor_number      The floor number of the building
     */
    void setImage(String building_name, int floor_number) {
        String file_path = building_name.toLowerCase().replaceAll(" ", "_").replaceAll("/", "") + String.valueOf(floor_number);

        this.image_view.setImageResource(this.context.getResources().getIdentifier(file_path ,
                "drawable",
                this.context.getPackageName()));
        Bitmap Floorbitmap = ((BitmapDrawable) this.image_view.getDrawable()).getBitmap();
        this.original_height = Floorbitmap.getHeight();
        this.original_width = Floorbitmap.getWidth();
        this.unmarked_image = Bitmap.createScaledBitmap(Floorbitmap,
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
            canvas.drawCircle(scaled_x, scaled_y, this.point_radius, paint);
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
