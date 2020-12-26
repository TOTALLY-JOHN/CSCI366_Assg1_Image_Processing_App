package com.csci366_2020.jihwanjeong.imagefilter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    // TO DECLARE VARIABLES
    final int PICK_IMAGE_REQUEST = 111;
    boolean imageLoad = false;
    ImageView imageViewPart1, imageViewPart2;
    SeekBar seekBar;
    private int seekBarLevel = 0;
    FloatingActionButton loadButton;
    Button backButton;
    Bitmap inputBM, outputBM;
    TextView textViewOriginalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TO CONNECT VARIABLES TO EACH ELEMENT
        imageViewPart1 = findViewById(R.id.imageViewPart1);
        imageViewPart2 = findViewById(R.id.imageViewPart2);
        seekBar = findViewById(R.id.seekBarLevel);
        loadButton = findViewById(R.id.floatingActionButtonLoad);
        backButton = findViewById(R.id.backButton);
        textViewOriginalImage = findViewById(R.id.textView1);

        // TO SET SEEK BAR CHANGE LISTENER
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // IF THE PROGRESS IS CHANGED, IT UPDATES SEEK BAR LEVEL.
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                seekBarLevel = progress - 3;
            }

            public void onStartTrackingTouch(SeekBar sb) {}

            // IF THE USER STOPS TRACKING TOUCH, IT SHOWS THE MESSAGE OF LEVEL WITH TOAST.
            public void onStopTrackingTouch(SeekBar sb) {
                if (seekBarLevel == 0)
                {
                    Toast.makeText(MainActivity.this, "Original Image", Toast.LENGTH_LONG).show();
                    updateImage(seekBarLevel);
                }
                else if (seekBarLevel > 0)
                {
                    Toast.makeText(MainActivity.this, "Sharpening Level: " + seekBarLevel, Toast.LENGTH_LONG).show();
                    updateImage(seekBarLevel);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Softening Level: " + (-1*seekBarLevel), Toast.LENGTH_LONG).show();
                    updateImage(seekBarLevel);
                }
            }
        });
    }

    // IF A USER CLICKS THE BACK BUTTON
    public void backButtonClick(View view) {
        imageViewPart1.setImageBitmap(null);
        imageViewPart2.setImageBitmap(null);

        backButton.setVisibility(View.INVISIBLE);
        loadButton.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
    }

    // IF A USER CLICKS THE LOAD IMAGE BUTTON
    public void loadImage(View view) {
        // DECLARE INTENT
        Intent i = new Intent();

        // SET THE TYPE FOR ONLY IMAGES
        i.setType("image/*");

        // SET ACTION TO GET CONTENT
        i.setAction(Intent.ACTION_GET_CONTENT);

        // START ACTIVITY
        startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // IF AN IMAGE IS SUCCESSFULLY LOADED
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                inputBM = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageViewPart1.setImageBitmap(inputBM);
                loadButton.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.VISIBLE);
                imageLoad = true;
                seekBar.setVisibility(View.VISIBLE);
                seekBarLevel = 0;
                seekBar.setProgress(3);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TO PROCESS THE IMAGE BASED ON SEEK BAR LEVEL
    private void updateImage(int level) {
        if (imageLoad) {
            // IF THE SEEK BAR LEVEL IS MEDIUM (0), IT SHOWS THE ORIGINAL IMAGE.
            if(level == 0)
            {
                outputBM = inputBM;
                imageViewPart2.setImageBitmap(outputBM);
                return;
            }
            switch(level) {
                // SOFTENING (PROGRESS BAR -3)
                case -3:
                {
                    int[][] matrix = {
                            {1,1,1,1,1,1,1},
                            {1,1,1,1,1,1,1},
                            {1,1,1,1,1,1,1},
                            {1,1,1,1,1,1,1},
                            {1,1,1,1,1,1,1},
                            {1,1,1,1,1,1,1},
                            {1,1,1,1,1,1,1}
                    };
                    outputBM = processImage(7, matrix, level);
                    imageViewPart2.setImageBitmap(outputBM);
                    break;
                }

                // SOFTENING (PROGRESS BAR -2)
                case -2:
                {
                    int[][] matrix = {
                            {1,1,1,1,1},
                            {1,1,1,1,1},
                            {1,1,1,1,1},
                            {1,1,1,1,1},
                            {1,1,1,1,1}
                    };
                    outputBM = processImage(5, matrix, level);
                    imageViewPart2.setImageBitmap(outputBM);
                    break;
                }

                // SOFTENING (PROGRESS BAR -1)
                case -1:
                {
                    int[][] matrix = {
                            {1,1,1},
                            {1,1,1},
                            {1,1,1}
                    };
                    outputBM = processImage(3, matrix, level);
                    imageViewPart2.setImageBitmap(outputBM);
                    break;
                }

                // SHARPENING (PROGRESS BAR 1)
                case 1:
                {
                    // 3x3 Sharpening Filter
                    int[][] matrix = {
                            {-1,-1,-1},
                            {-1,9,-1},
                            {-1,-1,-1}
                    };
                    outputBM = processImage(3, matrix, level);
                    imageViewPart2.setImageBitmap(outputBM);
                    break;
                }

                // SHARPENING (PROGRESS BAR 2)
                case 2:
                {
                    // 5x5
                    int[][] matrix = {
                            {-1,-1,-1,-1,-1},
                            {-1,-1,-1,-1,-1},
                            {-1,-1,25,-1,-1},
                            {-1,-1,-1,-1,-1},
                            {-1,-1,-1,-1,-1}
                    };
                    outputBM = processImage(5, matrix, level);
                    imageViewPart2.setImageBitmap(outputBM);
                    break;
                }

                // SHARPENING (PROGRESS BAR 3)
                case 3:
                {
                    int[][] matrix = {
                            {-1,-1,-1,-1,-1,-1,-1},
                            {-1,-1,-1,-1,-1,-1,-1},
                            {-1,-1,-1,-1,-1,-1,-1},
                            {-1,-1,-1,49,-1,-1,-1},
                            {-1,-1,-1,-1,-1,-1,-1},
                            {-1,-1,-1,-1,-1,-1,-1},
                            {-1,-1,-1,-1,-1,-1,-1}
                    };
                    outputBM = processImage(7, matrix, level);
                    imageViewPart2.setImageBitmap(outputBM);
                    break;
                }
            }
        }
    }

    // TO PROCESS THE IMAGE USING THE MATRIX FILTER
    public Bitmap processImage(int size, int[][] matrix, int level) {
        // TO GET WIDTH FROM THE IMAGE
        int width = inputBM.getWidth();

        // TO GET HEIGHT FROM THE IMAGE
        int height = inputBM.getHeight();

        // TO CREATE OUTPUT BITMAP FILE
        outputBM = Bitmap.createBitmap(width, height, inputBM.getConfig());

        // TO DECLARE VARIABLES
        int sumR, sumG, sumB;
        int A, R, G, B;
        A = R = G = B = 0;

        // TO DECLARE TWO-DIMENSIONAL ARRAY FOR PIXELS
        int[][] pixels = new int[size][size];

        for (int y = 1; y <= height - size; y++) {
            for (int x = 1; x <= width - size; x++) {

                // TO GET PIXEL MATRIX
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        pixels[i][j] = inputBM.getPixel(x + i, y + j);
                    }
                }

                // TO GET ALPHA OF THE CENTER PIXEL
                if(level == 1 || level == -1) A = Color.alpha(pixels[1][1]);
                else if(level == 2 || level == -2) A = Color.alpha(pixels[2][2]);
                else if(level == 3 || level == -3) A = Color.alpha(pixels[3][3]);

                // TO INITIALIZE THE COLOR SUM
                sumR = sumG = sumB = 0;

                // TO GET SUM OF RGB ON MATRIX
                for(int i = 0; i < size; i++) {
                    for(int j = 0; j < size; j++) {
                        sumR += (Color.red(pixels[i][j]) * matrix[i][j]);
                        sumG += (Color.green(pixels[i][j]) * matrix[i][j]);
                        sumB += (Color.blue(pixels[i][j]) * matrix[i][j]);
                    }
                }

                // TO GET THE FINAL RGB VALUES
                // IF IT IS SHARPENING
                if (level >= 1 && level <= 3) {
                    R = (int)(sumR);
                    G = (int)(sumG);
                    B = (int)(sumB);
                }
                // IF IT IS SOFTENING
                else if (level >= -3 && level <= -1) {
                    R = (int)(sumR / (size*size));
                    G = (int)(sumG / (size*size));
                    B = (int)(sumB / (size*size));
                }

                // TO CHECK FOR WHETHER EACH RGB VALUE IS OUT OF BOUNDS OR NOT
                if(R < 0) R = 0;
                else if(R > 255) R = 255;
                if(G < 0) G = 0;
                else if(G > 255) G = 255;
                if(B < 0) B = 0;
                else if(B > 255) B = 255;

                // TO APPLY NEW PIXEL
                outputBM.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
            }
        }
        return outputBM;
    }
}