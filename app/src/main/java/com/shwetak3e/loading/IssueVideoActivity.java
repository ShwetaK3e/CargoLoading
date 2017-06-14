
package com.shwetak3e.loading;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class IssueVideoActivity extends AppCompatActivity {
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int REAR_CAM_ID = 0;

    AudioManager audioManager = null;
    int imageRotation;
    private int previousVolume = 0;
    private Boolean isSDPresent = true;
    private int camToOpen;
    private long startTime = 60000;




    public static String TAG = IssueVideoActivity.class.getSimpleName();


    private FrameLayout preview;
    private Button captureButton;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private CameraSurfaceView mPreview;
    private CameraManager cameraManager;
    private boolean isVideoRecordingStarted = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == 27 || keyCode == 228 || keyCode == 80 || keyCode == KeyEvent.KEYCODE_BACK;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_issue_video);

        //Backup the bundle that came with the starting intent.
        OrientationEventListener myOrientationEventListener = new OrientationEventListener(IssueVideoActivity.this, SensorManager.SENSOR_DELAY_NORMAL) {
            public void onOrientationChanged(int iAngle) {
                if (iAngle != ORIENTATION_UNKNOWN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        setImageRotation(iAngle);
                    }
                } else {
                    setImageRotation(0);
                    Log.e("onOrientationChanged", "Orientation angle is ORIENTATION_UNKNOWN");
                }
            }
        };

        // To display if orientation detection will work and enable it
        if (myOrientationEventListener.canDetectOrientation()) {
            Log.i(TAG + "onCreate", "Enabling orientationListener", null);
            myOrientationEventListener.enable();
        } else {
            Log.i(TAG + "onCreate", "Failed to enable orientationListener", null);
            Toast.makeText(this, "Cannot detect device orientation. Your image may not be oriented correctly.", Toast.LENGTH_LONG).show();
        }


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setSpeakerphoneOn(true);
        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        camToOpen = getCamNumber();
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        captureButton = (Button) findViewById(R.id.button_capture_video);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void recordVideo() {
        if(!isVideoRecordingStarted) {
            isVideoRecordingStarted = true;
            Camera.Parameters rearCamParams = mCamera.getParameters();
            try {
                List<String> modes = rearCamParams.getSupportedFlashModes();
                if (modes != null) {
                    if (modes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                        rearCamParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    } else if (modes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                        rearCamParams.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    }
                    setParameters(rearCamParams, "onClick");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    Camera.Parameters parameters = mCamera.getParameters();
                    if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        setParameters(parameters, "onClick1");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG + "OnClick", "Exception_Point1");
            }
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            }
            Log.i(TAG, "beforePreparingVideo");
            if (prepareVideoRecorder()) {
                Log.i(TAG + "onClick", "DebugTime_afterPrepareRecorder");
                try {
                    playVideoRecordingStartedSound();
                    mMediaRecorder.start();
                    mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                        @Override
                        public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
                            if (arg1 == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                                releaseMediaRecorder(); // release the MediaRecorder
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
                                Intent i = new Intent(IssueVideoActivity.this, MainActivity.class);
                                i.putExtra("Activity","TO_LOAD");
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                    mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                        @Override
                        public void onError(MediaRecorder mediaRecorder, int i, int i1) {
                            Log.e(TAG + "onError", "onError_in_MediaRecorder");
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG + "onClick", "Exception_Point2");
                    exitOnException("Error starting Camera Video Test.");
                }
            } else {
                // prepare didn't work, release the camera and recorder.
                releaseMediaRecorder();
            }
        }else{
            isVideoRecordingStarted=false;
            playVideoRecordingStartedSound();
            releaseMediaRecorder();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
            Intent i = new Intent(IssueVideoActivity.this, MainActivity.class);
            i.putExtra("Activity","TO_LOAD");
            startActivity(i);
            finish();
        }
    }

    private void setParameters(Camera.Parameters parameters, String miscellaneous) {
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            Log.e(TAG + "SetParameters", miscellaneous);
        }
    }

    private void playVideoRecordingStartedSound() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setSpeakerphoneOn(true);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
            audioManager.setMode(AudioManager.MODE_NORMAL);

            /*
            In Camera api, the system plays media sounds(when video recording starts and end) by default,
            but not when ringer mode is silent(this only an observation, not a fact).
            So since we are playing our own audio, we need to ensure that the System sound is not played simultaneously.
            So, we set ringer mode to silent, and prevent system played media sound from surfacing.
            */
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            MediaPlayer media = MediaPlayer.create(this, R.raw.beep_tone);
            media.start();
        } catch (Exception e) {
            Log.e(TAG + "RecordingStartedSound", "Exception");
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            captureButton.setEnabled(true);
            isVideoRecordingStarted = false;
            audioManager.setMode(AudioManager.MODE_NORMAL);
            isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            isSDPresent = isSDPresent && !(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY));
            if (!isSDPresent) {
                AlertDialog.Builder builder = new AlertDialog.Builder(IssueVideoActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("SD card is not mounted.");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(IssueVideoActivity.this, MainActivity.class);
                        i.putExtra("Activity","TO_LOAD");
                        startActivity(i);
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                Dialog d = builder.create();
                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                d.show();
            } else {
                startCam();
            }
        } catch (Exception e) {
            e.printStackTrace();
            exitOnException("Error in recording video");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        releaseMediaRecorder();
        stopCam();
    }

    private boolean prepareVideoRecorder() {
        try {
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            } else {
                Log.i(TAG + "Alpha", "MediaRecoder is Not Null");
            }

            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            if (Build.VERSION.SDK_INT >= 8) {
                mMediaRecorder.setProfile(cameraManager.getBestCamcorderProfile());
            }

            //  Set output file
            File ff = getOutputMediaFile(MEDIA_TYPE_VIDEO);
            if (null != ff) {
                mMediaRecorder.setOutputFile(ff.toString());
                Intent mediaScanIntent1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(ff);
                mediaScanIntent1.setData(contentUri);
                sendBroadcast(mediaScanIntent1);
            } else {
                Toast.makeText(IssueVideoActivity.this, "Insufficient Storage...", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(IssueVideoActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
            mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
            setVideoRotation();
            try {
                mMediaRecorder.setMaxDuration((int) startTime);
                mMediaRecorder.setMaxFileSize(32 * 1024 * 1024);
                mMediaRecorder.prepare();
            } catch (IllegalStateException e) {
                Log.e("Alpha", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
                Log.e(TAG + "prepareMediaRecorder", "IllegalStateException");
                releaseMediaRecorder();
                exitOnException(TAG + "Error:" + e.toString());
                return false;
            } catch (IOException e) {
                Log.e("Alpha", "IOException preparing MediaRecorder: " + e.getMessage());
                Log.e(TAG + "prepareMediaRecorder", "IOException");
                releaseMediaRecorder();
                exitOnException(TAG + "Error :" + e.toString());
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG + "prepareMediaRecorder", "Exception");
            releaseMediaRecorder();
            exitOnException(TAG + "Error-:" + e.toString());
            return false;
        }
    }


    private void stopCam() {
        Log.i(TAG, "stopCam()");
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            preview.removeAllViews();
            mCamera.release();
            mPreview = null;
            mCamera = null;
        }
    }

    private void releaseMediaRecorder() {
        Log.i(TAG, " releaseMediaRecorder() ");
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private void setImageRotation(int iAngle) {
        try {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            Camera.getCameraInfo(camToOpen, info);
            int orientation = (iAngle + 45) / 90 * 90;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                imageRotation = (info.orientation - orientation + 360) % 360;
            } else {
                imageRotation = (info.orientation + orientation) % 360;
            }
            Log.i(TAG + "setImageRotation", Integer.toString(imageRotation));
        } catch (Exception e) {
            Log.e(TAG + "exception", e.getMessage());
        }
    }



    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        File issue_video_file;
        long requiredMemAvailable = 32 * 1024;
        boolean isSpaceAvailable = isMemoryAvailable(requiredMemAvailable);
        if (isSpaceAvailable) {
            File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(null), "Issues");
            if (!mediaStorageDir.exists()) {
                Log.i(TAG + "getOutputMediaFile", "mediaStorageDir does not exist");
                if (!mediaStorageDir.mkdirs()) {
                    Log.i(TAG + "getOutputMediaFile", "failed to create mediaStorageDir");
                    return null;
                } else {
                    Log.i(TAG + "getOutputMediaFile", "mediaStorageDir created");
                }
            }
            // Create a video file name
            if (type == MEDIA_TYPE_VIDEO) {
                issue_video_file = new File(mediaStorageDir.getPath() + File.separator + "ISSUE_VIDEO.mp4");
                Log.i(TAG + "MediaFile path", issue_video_file.getAbsolutePath());
            } else {
                return null;
            }
            return issue_video_file;
        } else {
            Toast.makeText(IssueVideoActivity.this, "Insufficient Storage....", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    boolean isMemoryAvailable(long requiredMemory) {
        if (getAvailableExternalMemorySize() > requiredMemory)
            return true;
        if (getAvailableInternalMemorySize() > requiredMemory)
            return true;
        return false;
    }


    private void startCam() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                mCamera = Camera.open(camToOpen);
                mCamera.stopPreview();
                mCamera.lock();
                cameraManager = new CameraManager(mCamera);
                if (camToOpen == REAR_CAM_ID) {
                    Camera.Parameters rearCamParams = mCamera.getParameters();
                    List<String> focusModes = rearCamParams.getSupportedFocusModes();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        rearCamParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        rearCamParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        mCamera.setDisplayOrientation(getCameraOrientation());
                    }
                    setParameters(rearCamParams, "startCam");
                }
            }


            try {
                Log.e("Brand", Build.BRAND);
                Log.e("Model", Build.MODEL);
                Log.e("OS Version", String.valueOf(Build.VERSION.SDK_INT));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    Log.d("onAutoFocus", "onAutoFocus working");
                    mCamera.setAutoFocusMoveCallback(new Camera.AutoFocusMoveCallback() {
                        @Override
                        public void onAutoFocusMoving(boolean start, Camera camera) {
                            if (start) {
                               /* imgAutoFocus.setBackgroundResource(R.drawable.auto_focus_frame_invi);
                                lblZoomLev.setVisibility(View.INVISIBLE);*/
                            } else {
                                /*imgAutoFocus.setBackgroundResource(R.drawable.auto_frame_failed);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        imgAutoFocus.setVisibility(View.INVISIBLE);
                                    }
                                }, 2000);*/
                            }
                        }
                    });
                }
            } catch (Exception e) {
                exitOnException(TAG + "Error-:" + e.toString());
            }
            mPreview = new CameraSurfaceView(this, mCamera);
            preview.addView(mPreview);
        } catch (Exception e) {
            exitOnException(TAG + "Cannot open camera:" + e);
        }
    }

    private void exitOnException(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Intent i = new Intent(IssueVideoActivity.this, MainActivity.class);
        i.putExtra("Activity","TO_LOAD");
        startActivity(i);
        finish();
    }

    @SuppressWarnings("deprecation")
    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        Log.i("ABCD2","uuuu"+path.getAbsolutePath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    @SuppressWarnings("deprecation")
    public long getAvailableExternalMemorySize() {
        if (isSDPresent) {
            File path = Environment.getExternalStorageDirectory();
            Log.i("ABCD1",path.getAbsolutePath());
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return 0;
        }
    }

    public long formatSize(long size) {
        size /= 1024;
        return size;
    }





    public Camera.Size getOptimalVideoPictureSize(List<Camera.Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null) {
            return null;
        }
        Camera.Size optimalSize = null;
        // Try to find largest size that matches aspect ratio
        for (Camera.Size size : sizes) {
            Log.i(TAG + "OptimalVideoPictureSize", size.width + "x" + size.height + "AvailablePictureSize");
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (optimalSize == null || size.width > optimalSize.width) {
                optimalSize = size;
            }
        }
        if (optimalSize == null) {
            // can't find match for aspect ratio, so find closest one
            optimalSize = getClosestSize(sizes, targetRatio);
        }
        return optimalSize;
    }

    public Camera.Size getClosestSize(List<Camera.Size> sizes, double targetRatio) {
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(ratio - targetRatio);
            }
        }
        return optimalSize;
    }

    private double getAspectRatio(int width, int height) {
        return ((double) width / height);
    }

    public int getCameraOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(camToOpen, info);
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Log.e("info Orientation", info.orientation + "");
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        } else {
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = 90;
            } else {
                result = 90;
            }
        }
        return result;
    }


    private void setVideoRotation() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                mMediaRecorder.setOrientationHint(imageRotation);
            }
        } catch (Exception e) {
            Log.e(TAG + e, "setVideoRotation+ Exception");
        }
    }

    private int getCamNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            int numberOfCam = Camera.getNumberOfCameras();
            Camera.CameraInfo[] infos = new Camera.CameraInfo[numberOfCam];
            for (int i = 0; i < numberOfCam; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                infos[i] = info;
            }

            for (int i = 0; i < numberOfCam; i++) {
                if (infos[i].facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    return i;
                }
            }
            return -1;
        }

        return -1;
    }


    public class CameraManager {
        private int cameraId;
        private Camera mCamera;
        private HashMap<Integer, String> camcorderProfileNames;
        private List<Camera.Size> availableVideoSizes;
        private List<Camera.Size> supportedVideoSizes;
        private DisplayMetrics metrics;
        private CamcorderProfile bestCamcorderProfile;
        private ProfileSize optimalDisplaySize;
        private double requiredAspectRatio;
        private Camera.Size optimalPreviewSize;
        private boolean is4KAvailable = false;

        public CameraManager(Camera camera) {
            this.cameraId = camToOpen;
            this.mCamera = camera;
            if (mCamera == null) {
                Log.e(TAG, "Camera is null");
            } else {
                //get the size of display.
                Display display = getWindowManager().getDefaultDisplay();
                metrics = new DisplayMetrics();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    display.getRealMetrics(metrics);
                } else {
                    display.getMetrics(metrics);
                }
                Log.i(TAG + "CameraManager", metrics.widthPixels + "x" + metrics.heightPixels + "DisplaySize");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    try {
                        setCamcorderProfileNames();
                        setAvailableProfiles(cameraId);
                    } catch (Exception e) {
                        Log.e(TAG + "CameraManager", "Exception_Point1");
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    supportedVideoSizes = new ArrayList<>();
                    availableVideoSizes = mCamera.getParameters().getSupportedVideoSizes();
                    if (availableVideoSizes == null) {
                        Log.i(TAG + "CameraManager", "getSupportedVideoSizes returns null");
                        /*
                        If the supported video sizes happen to be null, then its possible that the device does not have
                        separate outputs for preview and video.
                        So if getSupportedVideoSizes() returns null, we should use the preview sizes -
                        see http://stackoverflow.com/questions/14263521/android-getsupportedvideosizes-allways-returns-null
                        */
                        availableVideoSizes = mCamera.getParameters().getSupportedPreviewSizes();
                    }

                    /*
                    A lot of newer devices,like LG G4, or Samsung S6 family of devices support 4K video recording,
                    but do not expose the corresponding CamcorderProfile, ie QUALITY_2160P. So we need to avoid using the video
                    sizes above fullHD, ie 1920x1080, since this may lead to unexpected crashes. Hence the video sizes above this are
                    pruned.
                    Also, if 2160P profile is available, then we can acccept video sizes upto the standard4K video size,
                    i.e. 3840x2160, and no more than that.
                    */
                    int maxVideoWidth, maxVideoHeight;
                    if (is4KAvailable) {
                        maxVideoWidth = 3840;
                        maxVideoHeight = 2160;
                    } else {
                        maxVideoWidth = 1920;
                        maxVideoHeight = 1080;
                    }
                    for (Camera.Size size : availableVideoSizes) {
                        Log.i(TAG + "Available Video sizes", size.width + "x" + size.height);
                        if (size.width <= maxVideoWidth && size.height <= maxVideoHeight) {
                            supportedVideoSizes.add(size);
                        }
                    }
                    //Now sort the video sizes in descending order.
                    Collections.sort(supportedVideoSizes, new ListComparator(true));
                    //Log the supported sizes for debugging.
                    for (Camera.Size size : supportedVideoSizes) {
                        Log.i(TAG + "Supported Video sizes", size.width + "x" + size.height);
                    }
                    //Now select the best available valid video size, and get the corresponding profile.
                    Camera.Size bestVideoSize = supportedVideoSizes.get(0);
                    bestCamcorderProfile = CamcorderProfile.get(camToOpen, CamcorderProfile.QUALITY_HIGH);
                    bestCamcorderProfile.videoFrameWidth = bestVideoSize.width;
                    bestCamcorderProfile.videoFrameHeight = bestVideoSize.height;

                    Log.i(TAG + "CameraManager", String.valueOf(bestCamcorderProfile.quality) + "BestProfile quality");
                    Log.i(TAG + "CameraManager", bestCamcorderProfile.videoFrameWidth + "x" + bestCamcorderProfile.videoFrameHeight + "Best camcorder profile");
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    bestCamcorderProfile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH);
                    Log.i(TAG + "CameraManager", bestCamcorderProfile.videoFrameWidth + "x" + bestCamcorderProfile.videoFrameHeight+ "Best camcorder profile");
                }

                if (bestCamcorderProfile != null) {
                    this.requiredAspectRatio = getAspectRatio(bestCamcorderProfile.videoFrameWidth, bestCamcorderProfile.videoFrameHeight);
                    setOptimalDisplaySize();
                    setOptimalPreviewSize(mCamera.getParameters().getSupportedPreviewSizes(), requiredAspectRatio);
                }
            }
        }


        public void setCamcorderProfileNames() {
            this.camcorderProfileNames = new HashMap<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                camcorderProfileNames.put(CamcorderProfile.QUALITY_2160P, "CamcorderProfile.QUALITY_2160P");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                camcorderProfileNames.put(CamcorderProfile.QUALITY_1080P, "CamcorderProfile.QUALITY_1080P");
                camcorderProfileNames.put(CamcorderProfile.QUALITY_720P, "CamcorderProfile.QUALITY_720P");
                camcorderProfileNames.put(CamcorderProfile.QUALITY_480P, "CamcorderProfile.QUALITY_480P");
                camcorderProfileNames.put(CamcorderProfile.QUALITY_CIF, "CamcorderProfile.QUALITY_CIF");
                camcorderProfileNames.put(CamcorderProfile.QUALITY_QCIF, "CamcorderProfile.QUALITY_QCIF");
            }

        }

        public void setAvailableProfiles(int cameraId) {
            CamcorderProfile profile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_HIGH)) {
                    profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH);
                    Log.i(TAG + profile, camcorderProfileNames.get(CamcorderProfile.QUALITY_HIGH));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_2160P)) {
                        profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_2160P);
                        Log.i(TAG + profile, camcorderProfileNames.get(CamcorderProfile.QUALITY_2160P));
                        is4KAvailable = true;
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_1080P)) {
                        profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_1080P);
                        Log.i(TAG + profile, camcorderProfileNames.get(CamcorderProfile.QUALITY_1080P));
                    }
                    if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_720P)) {
                        profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_720P);
                        Log.i(TAG + profile, camcorderProfileNames.get(CamcorderProfile.QUALITY_720P));
                    }
                    if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_480P)) {
                        profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_480P);
                        Log.i(TAG + profile, camcorderProfileNames.get(CamcorderProfile.QUALITY_480P));
                    }
                    if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_CIF)) {
                        profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_CIF);
                        Log.i(TAG + profile, camcorderProfileNames.get(CamcorderProfile.QUALITY_CIF));
                    }
                    if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_QCIF)) {
                        profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_QCIF);
                        Log.i(TAG + profile, camcorderProfileNames.get(CamcorderProfile.QUALITY_QCIF));
                    }
                }

                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_LOW)) {
                    profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_LOW);
                    Log.i(TAG + profile, camcorderProfileNames.get(CamcorderProfile.QUALITY_LOW));
                }
            }

        }

        private void setOptimalDisplaySize() {
            double screenAspectRatio = getAspectRatio(metrics.widthPixels, metrics.heightPixels);
            optimalDisplaySize = new ProfileSize();
            double requiredAspectRatio = getAspectRatio(bestCamcorderProfile.videoFrameHeight, bestCamcorderProfile.videoFrameWidth);
            Log.i(TAG + "setOptimalDisplaySize", String.valueOf(requiredAspectRatio) + "new aspect ratio");
            if (screenAspectRatio == requiredAspectRatio) {
                optimalDisplaySize.x = metrics.widthPixels;
                optimalDisplaySize.y = metrics.heightPixels;
            } else if (screenAspectRatio < requiredAspectRatio) {
                optimalDisplaySize.x = metrics.widthPixels;
                optimalDisplaySize.y = (int) (optimalDisplaySize.x / requiredAspectRatio);
            } else {
                optimalDisplaySize.y = metrics.heightPixels;
                optimalDisplaySize.x = (int) (optimalDisplaySize.y * requiredAspectRatio);
            }
        }

        private void setOptimalPreviewSize(List<Camera.Size> supportedPreviewSizes, double requiredAspectRatio) {
            Log.i(TAG + "setOptimalPreviewSize", String.valueOf(requiredAspectRatio) + "requiredAspectRatio");
            Collections.sort(supportedPreviewSizes, new ListComparator(true));
            double aspectTolerance = Double.MAX_VALUE;
            Camera.Size optimalSize = null;
            double aspectRatio;
            for (Camera.Size size : supportedPreviewSizes) {
                Log.i("setOptimalPreviewSize", size.width + "x" + size.height);
            }
            if (supportedPreviewSizes.size() > 0) {
                for (Camera.Size size : supportedPreviewSizes) {
                    aspectRatio = getAspectRatio(size.width, size.height);
                    if (Math.abs(aspectRatio - requiredAspectRatio) <= 0.05) {
                        optimalSize = size;
                        break;
                    }
                }
                if (optimalSize == null) {
                    for (Camera.Size size : supportedPreviewSizes) {
                        if (Math.abs(getAspectRatio(size.width, size.height) - requiredAspectRatio) < aspectTolerance) {
                            optimalSize = size;
                            aspectTolerance = getAspectRatio(size.width, size.height) - requiredAspectRatio;
                        }
                    }
                }
                optimalPreviewSize = optimalSize;
                Log.i(TAG + "getOptimalPreviewSize", optimalSize.width + "::" + optimalSize.height + "Optimal Preview size");
            } else {
                Log.e("MainActivity", "supported preview sizes are 0");
            }
        }

        public CamcorderProfile getBestCamcorderProfile() {
            return bestCamcorderProfile;
        }

        public Camera.Size getOptimalPreviewSize() {
            return optimalPreviewSize;
        }

        public ProfileSize getOptimalDisplaySize() {
            return optimalDisplaySize;
        }

        private class ListComparator implements Comparator<Camera.Size> {
            private boolean isDescendingOrder;

            public ListComparator(boolean isDescendingOrder) {
                this.isDescendingOrder = isDescendingOrder;
            }

            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.height * lhs.width > rhs.height * rhs.width) {
                    return isDescendingOrder ? -1 : 1;
                } else if (lhs.height * lhs.width == rhs.height * rhs.width) {
                    return 0;
                } else {
                    return isDescendingOrder ? 1 : -1;
                }
            }
        }
    }

    public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder holdMe;
        private Camera theCamera;

        public CameraSurfaceView(Context context, Camera camera) {
            super(context);
            Log.i(TAG + "CameraSurfaceView", "Started" + "Constructor");
            theCamera = camera;
            holdMe = getHolder();
            holdMe.addCallback(this);
            holdMe.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            Log.i(TAG + "onMeasure", "Started", null);
            int widthSpec = MeasureSpec.makeMeasureSpec(cameraManager.getOptimalDisplaySize().x, MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(cameraManager.getOptimalDisplaySize().y, MeasureSpec.EXACTLY);
            super.onMeasure(widthSpec, heightSpec);
            if (mCamera != null) {
                if (!isVideoRecordingStarted) {
                    Camera.Parameters parameters = mCamera.getParameters();
                    int width = cameraManager.getOptimalPreviewSize().width;
                    int height = cameraManager.getOptimalPreviewSize().height;
                    parameters.setPreviewSize(width, height);
                    setParameters(parameters, "onMeasure");
                } else {
                    Log.i(TAG + "onMeasure", "onMeasure called after user clicked record button", null);
                }
            } else {
                Log.i(TAG + "onMeasure", "Camera is null", null);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
            Log.i(TAG + "surfaceChanged", "Started", null);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG + "surfaceCreated", "Started", null);
            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(holder);
                    Camera.Parameters parameters = mCamera.getParameters();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        parameters.setRecordingHint(true);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                        CamcorderProfile profile = cameraManager.getBestCamcorderProfile();
                        Camera.Size pictureSize = getOptimalVideoPictureSize(parameters.getSupportedPictureSizes(),
                                getAspectRatio(profile.videoFrameWidth, profile.videoFrameHeight));
                        Log.i(TAG + "surfaceCreated", pictureSize.width + "x" + pictureSize.height+ "PictureSize");
                        //Some notorious device.
                        if (Build.MODEL.contains("F-02H") || Build.MODEL.contains("F-04G")) {
                            parameters.setPictureSize(1920, 1080);
                        } else {
                            parameters.setPictureSize(pictureSize.width, pictureSize.height);
                        }
                    }
                    setParameters(parameters, "surfaceCreated");
                    mCamera.startPreview();

                }
            } catch (Exception e) {
                exitOnException("Exception:" + e.toString());
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder arg0) {
            Log.i(TAG + "surfaceDestroyed", "Started", null);
            if (theCamera != null) {
                theCamera.stopPreview();
                theCamera.setPreviewCallback(null);
                theCamera.release();
                theCamera = null;
            }
        }
    }

    private class ProfileSize {
        public int x, y;

        public ProfileSize() {
        }

        public ProfileSize(int width, int height) {
            x = width;
            y = height;
        }
    }

}


