package com.safeseason.totoanalytic.Helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.safeseason.totoanalytic.R;

import java.util.Locale;
import java.util.Random;

public class SuggestView extends View {

    //Set global variable
    private final Paint bubbleHighPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint winningText = new Paint();

    private final Paint bubbleLowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lowText = new Paint();
    private final float[] sequenceXno = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};


    public SuggestView(Context context) {
        super(context);
    }

    public SuggestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        PaintSetting(bubbleHighPaint, ContextCompat.getColor(context, R.color.primary_color_transparent));
        PaintSetting(winningText,ContextCompat.getColor(context, R.color.primary_color));
        PaintSetting(bubbleLowPaint, ContextCompat.getColor(context, R.color.invert_primary_transparent));
        PaintSetting(lowText, ContextCompat.getColor(context, R.color.invert_primary_color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!DataProcessing.plotMaxNum.isEmpty() && !DataProcessing.plotMinNum.isEmpty()){
            int position = 0;
            for (int valMax: DataProcessing.plotMaxNum){
                if (position % 2 == 0)
                    BubbleCreate(canvas, valMax, 100 - (10 * position), position/2, true);
                position++;
            }
            position = 0;
            for (int valMin: DataProcessing.plotMinNum){
                if (position % 2 == 0)
                    BubbleCreate(canvas, valMin, 100 - (10 * position), position/2, false);
                position++;
            }
        } else {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.secondary_text_color));
            paint.setAntiAlias(true);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(getWidth()/20.0f);

            canvas.drawText("This function need to set on analysis tab",getWidth()/2.0f, getHeight()/2.0f, paint);
        }

    }

    private void PaintSetting(Paint paint, int color){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);

        //For text
        paint.setTextAlign(Paint.Align.CENTER);
    }

    private void BubbleCreate(Canvas canvas, int number, float percent, int position, boolean winFlag){

        float nX, nY, cX, cY;
        int min, max;
        Paint bubble, text;

        if (winFlag){
            bubble = bubbleHighPaint;
            text = winningText;
        } else {
            bubble = bubbleLowPaint;
            text = lowText;
        }

        //Initial data check
        if (position > 9)
            return;
        if (percent < 30)
            percent = 30;

        //get the window dimensions
        nX = canvas.getWidth()/2.0f;
        nY = canvas.getHeight()/2.0f;

        //Get the radius of bubble according to position
        float radius = (nY/5) * (percent /100.0f);
        String txt = String.format(Locale.US, "%02d", number);
        float textSize = (nY/5) * (percent /100.0f);
        text.setTextSize(textSize);

        //Create the random number of x axial
        System.out.println(sequenceXno[position]);
        if (winFlag) {
            min = (int) radius;
            max = (int) ((nX * 2) - (radius));
        } else {
            //Create function not to overlap with high value
            if (sequenceXno[position] > nX){
                min = (int) radius;
                max = (int) (sequenceXno[position] - radius);
            } else {
                min = (int) (sequenceXno[position] + radius);
                max = (int) ((nX * 2) - (radius));
            }
            //Sequence number shall be set to default state
            sequenceXno[position] = 0;
        }

        System.out.println("Max and Min Number");
        System.out.println(max);
        System.out.println(min);
        cX = new Random().nextInt((max - min) + 1) + min;
        sequenceXno[position] = cX;

        //create the sequence number of y axial
        if (position % 4 == 1)
            cY = nY - ((9 - position) * (canvas.getHeight()/20.0f));
        else if (position % 4 == 2)
            cY = nY + ((9 - position) * (canvas.getHeight()/20.0f));
        else if (position % 4 == 3)
            cY = nY - (position * (canvas.getHeight()/20.0f));
        else
            cY = nY + (position * (canvas.getHeight()/20.0f));


        //Save the canvas first
        canvas.save();
        canvas.translate(cX, cY);
        canvas.drawCircle(0,0,radius, bubble);
        canvas.drawText(txt, 0, -(text.descent() + text.ascent())/2, text);
        canvas.restore();
        //restore the canvas
    }
}
