package com.example.vitya.opencv;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    private CameraBridgeViewBase mOpenCvCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    Mat src = null;
    int time = 10;
    int xL = 640, yL = 0, xR = 0, yR = 0, xB =0, yB = 0;
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        src = inputFrame.rgba();
        if (time == 10 ) {
            xL = 640;
            yL = 0;
            xR = 0;
            yR = 0;
            xB =0;
            yB = 0;



            Bitmap bmp = null;

            bmp = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(src, bmp);

            for (int i = 0; i < bmp.getWidth(); ++i) {
                for (int j = 0; j < bmp.getHeight(); ++j) {
                    int pixel = bmp.getPixel(i, j);
                    int redValue = Color.red(pixel);
                    int blueValue = Color.blue(pixel);
                    int greenValue = Color.green(pixel);
                    if (redValue < 10 && greenValue < 10 && blueValue < 10) {
                        if (i < xL) {
                            xL = i;
                            yL = j;
                        }
                        if (i > xR) {
                            xR = i;
                            yR = j;
                        }
                        if(j > yB){
                            yB = j;
                            xB = i;
                        }
                    }

                }
            }
            time  = 0;
        }
        time++;
        Imgproc.rectangle(src, new Point(xL, yL - 400), new Point(xB, yB), new Scalar(0, 0, 255), -1);
        Imgproc.rectangle(src, new Point(xB, yB - 400), new Point(xB, yB), new Scalar(255, 0, 0), -1);
        
        return src ;
    }
}
