
package com.shwetak3e.loading;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.design.widget.TextInputEditText;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shwetak3e.loading.fragments.TruckDetails_1;
import com.shwetak3e.loading.model.Issues;
import com.shwetak3e.loading.model.ShipmentItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class IssueVideoActivity extends AppCompatActivity {

    private static final int REAR_CAM_ID = 0;
    private static final int MEDIA_TYPE_PHOTO=1;
    private static final int MEDIA_TYPE_VIDEO = 2;


    AudioManager audioManager = null;
    int imageRotation;
    private int previousVolume = 0;
    private Boolean isSDPresent = true;
    private int camToOpen;
    private long startTime = 60000;
    private boolean isFlashOn=false;
    File outputFile=null;




    public static String TAG = IssueVideoActivity.class.getSimpleName();


    private FrameLayout preview;
    private int preview_height=0;
    private  RelativeLayout preview_layout;
    private Camera mCamera;
    private CameraSurfaceView mPreview;
    private CameraManager cameraManager;

    private ImageButton issue_videocaptureButton;
    private MediaRecorder mMediaRecorder;
    private boolean isVideoRecordingStarted = false;


    private ImageButton issue_photocaptureButton;
    private boolean isPhotoCapturingStarted=false;
    private Camera.PictureCallback mPictureCallback=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
             outputFile=getOutputMediaFile(MEDIA_TYPE_PHOTO);
             if(outputFile!=null){
                 try {
                     FileOutputStream fos=new FileOutputStream(outputFile);
                     fos.write(data);
                     fos.close();
                 } catch (FileNotFoundException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }else{

                 return;
             }
        }
    };

    private Camera.ShutterCallback shutterCallback=new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            MediaPlayer media=MediaPlayer.create(IssueVideoActivity.this, R.raw.camera_shutter_click_01);
            media.start();
        }
    };


    private EditText damage_desc;
    private ImageButton save_damage_record;
    private String shipment_ID;

    private  Issues issue=null;


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


        shipment_ID=getIntent().getStringExtra("Shipment_ID");

        showIssueTypeDialog();

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




        preview_layout=(RelativeLayout)findViewById(R.id.preview_layout);
        preview = (FrameLayout) findViewById(R.id.camera_preview);


        issue_videocaptureButton = (ImageButton) findViewById(R.id.button_capture_issue_video);
        issue_videocaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
            }
        });

        issue_photocaptureButton = (ImageButton) findViewById(R.id.button_capture_issue_pic);
        issue_photocaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });




        damage_desc=(EditText)findViewById(R.id.damage_desc);
        save_damage_record=(ImageButton)findViewById(R.id.store_damage_record);
        save_damage_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String damagedesc=damage_desc.getText().toString().trim();
                Issues issue=new Issues();
                List<Issues> issuesList=TruckDetails_1.current_item.getIssues();
                if(issuesList==null){
                    issuesList=new LinkedList<>();
                }else{
                    issue=issuesList.get(issuesList.size()-1);
                    issuesList.remove(issue);
                }

                issue.setIssueType(0);
                if (outputFile == null) {
                    if(damagedesc.length()==0){
                        Toast.makeText(IssueVideoActivity.this,"Please Describe the Issue", Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        issue.setUri("");
                        issue.setIssueDescriptionType(2);  //only text
                        issue.setIssueDescription(damagedesc);
                    }
                }else{
                    issue.setUri(outputFile.getAbsolutePath());
                    if(damagedesc.length()!=0)issue.setIssueDescription(damagedesc);
                    if(outputFile.getName().endsWith(".mp4")){
                        issue.setIssueDescriptionType(0);
                    }else if(outputFile.getName().endsWith(".jpeg")){
                        issue.setIssueDescriptionType(1);
                    }
                }
                issuesList.add(issue);
                TruckDetails_1.current_item.setIssues(issuesList);
                Intent i = new Intent(IssueVideoActivity.this, MainActivity.class);
                i.putExtra("Activity","LOAD_THIS_ITEM");
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }





    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            preview_height=preview_layout.getHeight();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        releaseMediaRecorder();
        stopCam();
    }



    //setting up of Camera
    private void setParameters(Camera.Parameters parameters, String miscellaneous) {
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            Log.e(TAG + "SetParameters", miscellaneous);
        }
    }


    private void setAudioManagerForClickSound(){
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
    }

    void addOverlay(){
        LayoutInflater inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View view= inflater.inflate(R.layout.record_issue_overlay_layout,preview,true);
        final OverlayHolder holder=new OverlayHolder(view);
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            holder.flash_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFlashOn){
                        switchOnFlash();
                        holder.flash_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));
                        isFlashOn=true;
                }else{
                        isFlashOn=false;
                        switchOffFlash();
                        holder.flash_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                    }
            }});
        }else{
            holder.flash_button.setVisibility(GONE);
        }

        holder.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(IssueVideoActivity.this, MainActivity.class);
                i.putExtra("Activity","TRUCK_DETAILS_1");
                startActivity(i);
            }
        });


        view.setTag(holder);

    }


    private void switchOnFlash(){

        Camera.Parameters flashCamParams = mCamera.getParameters();
        try {
            List<String> modes = flashCamParams.getSupportedFlashModes();
            if (modes != null) {
                if (modes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    flashCamParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else if (modes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                    flashCamParams.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                }
                setParameters(flashCamParams, "flash_Param");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void switchOffFlash(){
        Camera.Parameters flashCamParams=mCamera.getParameters();
        try{
            flashCamParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            setParameters(flashCamParams,"Flash_off");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        File issue_file;
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
                issue_file = new File(mediaStorageDir.getPath() + File.separator + findnoOfIssues()+ "_ISSUE_VIDEO.mp4");
                Log.i(TAG + "MediaFile path", issue_file.getAbsolutePath());
            } else if(type==MEDIA_TYPE_PHOTO){
                issue_file=new File(mediaStorageDir.getPath()+File.separator+findnoOfIssues()+"_ISSUE_PHOTO.jpeg");
                Log.i(TAG + "MediaFile path", issue_file.getAbsolutePath());
            }else{
                return null;
            }
            return issue_file;
        } else {
            Toast.makeText(IssueVideoActivity.this, "Insufficient Storage....", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //used for naming the files
    private int findnoOfIssues(){
        List<Issues> issuesList=TruckDetails_1.current_item.getIssues();
        if(issuesList==null){
            return 0;
        }else{
            return issuesList.size();
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


    boolean isMemoryAvailable(long requiredMemory) {
        if (getAvailableExternalMemorySize() > requiredMemory)
            return true;
        if (getAvailableInternalMemorySize() > requiredMemory)
            return true;
        return false;
    }

    private void startingCamera(){
        try {
            issue_videocaptureButton.setEnabled(true);
            issue_photocaptureButton.setEnabled(true);

            isVideoRecordingStarted = false;
            isPhotoCapturingStarted=false;


            isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            isSDPresent = isSDPresent && !(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY));
            if (!isSDPresent) {
                AlertDialog.Builder builder = new AlertDialog.Builder(IssueVideoActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("SD card is not mounted.");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
            Log.e(TAG,"Error in recording video");
        }
    }

    private void startCam() {
        setAudioManagerForClickSound();
        camToOpen = getCamNumber();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                mCamera = Camera.open(camToOpen);
                mCamera.stopPreview();
                mCamera.lock();
                mCamera.setDisplayOrientation(getCameraOrientation());
                cameraManager = new CameraManager(mCamera);
            }

            mPreview = new CameraSurfaceView(this, mCamera);
            preview.addView(mPreview);
            addOverlay();
        } catch (Exception e) {
            Log.e(TAG + "Cannot open camera:" ,e.getMessage());
        }
    }






    /*
    * Recording Issue Video
    * */

    private void recordVideo() {
        if(!isVideoRecordingStarted) {
            isVideoRecordingStarted = true;


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
                                issue_photocaptureButton.setEnabled(true);

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
                    Log.e(TAG,"Error starting Camera Video Test.");
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

        }
    }

    private void playVideoRecordingStartedSound() {
        try {
            MediaPlayer media = MediaPlayer.create(this, R.raw.beep_tone);
            media.start();
        } catch (Exception e) {
            Log.e(TAG + "RecordingStartedSound", "Exception");
        }
    }

    private boolean prepareVideoRecorder() {
        issue_photocaptureButton.setEnabled(false);
        try {
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            } else {
                Log.i(TAG + "Alpha", "MediaRecoder is Not Null");
            }
            Camera.Parameters focusParams= mCamera.getParameters();
            List<String> focusModes=focusParams.getSupportedFocusModes();
            try {

                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    focusParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                setParameters(focusParams, "Focus_params");
            }catch (Exception e){
                Toast.makeText(this, "AutoFocus Not Working !!", Toast.LENGTH_SHORT).show();
                return false;
            }

            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            if (Build.VERSION.SDK_INT >= 8) {
                mMediaRecorder.setProfile(cameraManager.getBestCamcorderProfile());
            }

            //  Set output file
            outputFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
            if (null != outputFile) {
                mMediaRecorder.setOutputFile(outputFile.toString());
                Intent mediaScanIntent1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(outputFile);
                mediaScanIntent1.setData(contentUri);
                sendBroadcast(mediaScanIntent1);
            } else {
                Toast.makeText(IssueVideoActivity.this, "Insufficient Storage...", Toast.LENGTH_SHORT).show();

            }
            mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
            setRotation();
            try {
                mMediaRecorder.setMaxDuration((int) startTime);
                mMediaRecorder.setMaxFileSize(32 * 1024 * 1024);
                mMediaRecorder.prepare();
            } catch (IllegalStateException e) {
                Log.e("Alpha", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
                Log.e(TAG + "prepareMediaRecorder", "IllegalStateException");
                releaseMediaRecorder();
                Log.e(TAG + "Error:" ,e.toString());
                return false;
            } catch (IOException e) {
                Log.e("Alpha", "IOException preparing MediaRecorder: " + e.getMessage());
                Log.e(TAG + "prepareMediaRecorder", "IOException");
                releaseMediaRecorder();
                Log.e(TAG + "Error :" , e.toString());
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG + "prepareMediaRecorder", "Exception");
            releaseMediaRecorder();
            Log.e(TAG + "Error-:" , e.toString());
            return false;
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


    /*
    Taking Issue Image
     */

    private void captureImage(){
        Camera.Parameters focusParams=mCamera.getParameters();
        try {
            List<String> focusModes = focusParams.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                focusParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            setParameters(focusParams, "Focus_Param_image");
        }catch (Exception e){
            Log.e(TAG, "AutoFocus Not Working");
        }try {
            mCamera.takePicture(shutterCallback, null, mPictureCallback);
        }catch(Exception e){
            Toast.makeText(this, "Try Again !!", Toast.LENGTH_SHORT).show();
        }
    };









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
                result = (info.orientation - degrees + 360) % 360;
        } else {

                result = 90;

        }
        return result;
    }

    private void setRotation() {
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
                        Log.e(TAG + "CameraManager", "Exception_Point1  "+e.getMessage());
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
            optimalDisplaySize.x = metrics.widthPixels;
            optimalDisplaySize.y = preview_height;
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
            int heightSpec = MeasureSpec.makeMeasureSpec(preview_height, MeasureSpec.EXACTLY);
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
                        parameters.setPictureSize(pictureSize.width, pictureSize.height);

                    }
                    setParameters(parameters, "surfaceCreated");
                    mCamera.startPreview();

                }
            } catch (Exception e) {
                Log.e("Exception:" , e.toString());
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

    private static class OverlayHolder{
        ImageButton flash_button;
        Button skip;

        OverlayHolder(View view) {
            flash_button=(ImageButton)view.findViewById(R.id.flash_button);
            skip=(Button)view.findViewById(R.id.skip);
        }

    }

    Dialog issueTypedialog;
    int dialog_face=0;
    boolean suggstn_1_visible=false;
    boolean suggstn_2_visible=false;
    boolean suggstn_3_visible=false;
    String issue_selected;
    void showIssueTypeDialog(){

        issueTypedialog=new Dialog(this, R.style.MyDialogTheme);
        issueTypedialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        issueTypedialog.getWindow().setContentView(R.layout.issue_type_dialog);
        issueTypedialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;



        final IssueTypeDialogHolder holder=new IssueTypeDialogHolder(issueTypedialog);

        holder.issue_type_1_suggstn.setVisibility(GONE);
        holder.issue_type_2_suggstn.setVisibility(GONE);
        holder.issue_type_3_suggstn.setVisibility(GONE);
        holder.buttons_layout.setVisibility(GONE);
        holder.add_details.setVisibility(GONE);
        holder.done.setEnabled(false);

        holder.go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog_face==0){
                    Intent intent=new Intent(IssueVideoActivity.this,MainActivity.class);
                    intent.putExtra("Activity","TRUCK_DETAILS_1");
                    startActivity(intent);
                }else if(dialog_face==1){
                    dialog_face=0;
                    holder.title.setText("Select Issue Type");
                    holder.buttons_layout.setVisibility(GONE);
                    holder.issue_type_1_layout.setVisibility(VISIBLE);
                    holder.issue_type_2_layout.setVisibility(VISIBLE);
                    holder.issue_type_3_layout.setVisibility(VISIBLE);
                    holder.add_details.setVisibility(GONE);
                }
            }
        });

        holder.issue_type_1_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_face=1;
                issue_selected="damage";
                holder.issue_type_2_layout.setVisibility(GONE);
                holder.issue_type_3_layout.setVisibility(GONE);
                holder.title.setText("Change Issue Type");
                holder.buttons_layout.setVisibility(VISIBLE);
                holder.add_details.setVisibility(VISIBLE);
                holder.separator_1.setVisibility(GONE);
                holder.separator_2.setVisibility(GONE);
                holder.add_details.setHint("Damage Count");
            }
        });

        holder.issue_type_2_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_face=1;
                issue_selected="missing";
                holder.issue_type_1_layout.setVisibility(GONE);
                holder.issue_type_3_layout.setVisibility(GONE);
                holder.title.setText("Change Issue Type");
                holder.buttons_layout.setVisibility(VISIBLE);
                holder.add_details.setVisibility(VISIBLE);
                holder.separator_1.setVisibility(GONE);
                holder.separator_2.setVisibility(GONE);
                holder.add_details.setHint("Missing Count");
            }
        });

        holder.issue_type_3_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_face=1;
                issue_selected="weight_loss";
                holder.issue_type_1_layout.setVisibility(GONE);
                holder.issue_type_2_layout.setVisibility(GONE);
                holder.title.setText("Change Issue Type");
                holder.buttons_layout.setVisibility(VISIBLE);
                holder.add_details.setVisibility(VISIBLE);
                holder.separator_1.setVisibility(GONE);
                holder.separator_2.setVisibility(GONE);
                holder.add_details.setHint("Loading Weight (in kgs)");
            }
        });


        holder.issue_type_1_see_more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suggstn_1_visible){
                    suggstn_1_visible=false;
                    holder.issue_type_1_suggstn.setVisibility(GONE);
                    holder.issue_type_1_see_more_details.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_arrow_1));
                }else{
                    suggstn_1_visible=true;
                    holder.issue_type_1_suggstn.setVisibility(VISIBLE);
                    holder.issue_type_1_see_more_details.setImageDrawable(getResources().getDrawable(R.drawable.ic_up_arrow));
                }
            }
        });

        holder.issue_type_2_see_more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suggstn_2_visible){
                    suggstn_2_visible=false;
                    holder.issue_type_2_suggstn.setVisibility(GONE);
                    holder.issue_type_2_see_more_details.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_arrow_1));
                }else{
                    suggstn_2_visible=true;
                    holder.issue_type_2_suggstn.setVisibility(VISIBLE);
                    holder.issue_type_2_see_more_details.setImageDrawable(getResources().getDrawable(R.drawable.ic_up_arrow));
                }
            }
        });


        holder.issue_type_3_see_more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suggstn_3_visible){
                    suggstn_3_visible=false;
                    holder.issue_type_3_suggstn.setVisibility(GONE);
                    holder.issue_type_3_see_more_details.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_arrow_1));
                }else{
                    suggstn_3_visible=true;
                    holder.issue_type_3_suggstn.setVisibility(VISIBLE);
                    holder.issue_type_3_see_more_details.setImageDrawable(getResources().getDrawable(R.drawable.ic_up_arrow));
                }
            }
        });


        holder.add_more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issue=new Issues();
                if("damage".equals(issue_selected)) {
                    issue.setIssueType(0);
                    issue.setIssueDescriptionShort("Damage Count : "+ holder.add_details.getText().toString());
                }else if("missing".equals(issue_selected)){
                    issue.setIssueType(1);
                    issue.setIssueDescriptionShort("Missing Count : "+ holder.add_details.getText().toString());
                }else if("weight_loss".equals(issue_selected)){
                    issue.setIssueType(2);
                    issue.setIssueDescriptionShort("Loading Weight : "+ holder.add_details.getText().toString()+" kg");
                }
                saveIssueDetails();
                issueTypedialog.dismiss();
                startingCamera();
            }
        });

        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issue=new Issues();
                if("damage".equals(issue_selected)) {
                    issue.setIssueType(0);
                    issue.setIssueDescriptionShort("Damage Count : "+ holder.add_details.getText().toString());
                }else if("missing".equals(issue_selected)){
                    issue.setIssueType(1);
                    issue.setIssueDescriptionShort("Missing Count : "+ holder.add_details.getText().toString());
                }else if("weight_loss".equals(issue_selected)){
                    issue.setIssueType(2);
                    issue.setIssueDescriptionShort("Loading Weight : "+ holder.add_details.getText().toString());
                }
                saveIssueDetails();
                issue=null;
                Intent intent=new Intent(IssueVideoActivity.this,MainActivity.class);
                intent.putExtra("Activity","TRUCK_DETAILS_1");
                startActivity(intent);
            }
        });

        issueTypedialog.show();

    }

    void saveIssueDetails() {
        if(issue!=null) {
            LinkedList<Issues> issues = new LinkedList<>();
            if (MainActivity.issueList != null && MainActivity.issueList.containsKey(shipment_ID)) {
                issues = (LinkedList) MainActivity.issueList.get(shipment_ID);
                MainActivity.issueList.remove(shipment_ID);
            }
            issues.add(issue);

            MainActivity.issueList.put(shipment_ID, issues);
            ShipmentItem item = MainActivity.shipments_1.get(shipment_ID);
            item.setIssues(issues);
        }else{
            Toast.makeText(this, "No Issue Recorded", Toast.LENGTH_SHORT).show();
        }

    }



    class IssueTypeDialogHolder{
        ImageButton go_back;
        TextView title;

        LinearLayout issue_type_1_layout;
        ImageView issue_type_1_img;
        TextView issue_type_1_name;
        ImageButton issue_type_1_see_more_details;
        TextView issue_type_1_suggstn;

        View separator_1;

        LinearLayout issue_type_2_layout;
        ImageView issue_type_2_img;
        TextView issue_type_2_name;
        ImageButton issue_type_2_see_more_details;
        TextView issue_type_2_suggstn;

        View separator_2;

        LinearLayout issue_type_3_layout;
        ImageView issue_type_3_img;
        TextView issue_type_3_name;
        ImageButton issue_type_3_see_more_details;
        TextView issue_type_3_suggstn;

        TextInputEditText add_details;

        LinearLayout buttons_layout;
        Button add_more_details;
        Button done;

        IssueTypeDialogHolder(Dialog dialog){

            go_back=(ImageButton)dialog.findViewById(R.id.go_back);
            title=(TextView)dialog.findViewById(R.id.title);

            issue_type_1_layout=(LinearLayout)dialog.findViewById(R.id.issue_type_1_layout);
            issue_type_1_img=(ImageView)dialog.findViewById(R.id.issue_type_1_img);
            issue_type_1_name=(TextView)dialog.findViewById(R.id.issue_type_1_name);
            issue_type_1_see_more_details=(ImageButton)dialog.findViewById(R.id.issue_type_1_see_more);
            issue_type_1_suggstn=(TextView)dialog.findViewById(R.id.issue_type_1_suggstn);

            separator_1=dialog.findViewById(R.id.separator_1);

            issue_type_2_layout=(LinearLayout)dialog.findViewById(R.id.issue_type_2_layout);
            issue_type_2_img=(ImageView)dialog.findViewById(R.id.issue_type_2_img);
            issue_type_2_name=(TextView)dialog.findViewById(R.id.issue_type_2_name);
            issue_type_2_see_more_details=(ImageButton)dialog.findViewById(R.id.issue_type_2_see_more);
            issue_type_2_suggstn=(TextView)dialog.findViewById(R.id.issue_type_2_suggstn);

            separator_2=dialog.findViewById(R.id.separator_2);

            issue_type_3_layout=(LinearLayout)dialog.findViewById(R.id.issue_type_3_layout);
            issue_type_3_img=(ImageView)dialog.findViewById(R.id.issue_type_3_img);
            issue_type_3_name=(TextView)dialog.findViewById(R.id.issue_type_3_name);
            issue_type_3_see_more_details=(ImageButton)dialog.findViewById(R.id.issue_type_3_see_more);
            issue_type_3_suggstn=(TextView)dialog.findViewById(R.id.issue_type_3_suggstn);

            add_details=(TextInputEditText)dialog.findViewById(R.id.issue_details);
            add_details.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    done.setTextColor(getResources().getColor(R.color.colorPrimary));
                    done.setEnabled(true);
                }
            });

            buttons_layout=(LinearLayout)dialog.findViewById(R.id.button_layout);
            add_more_details=(Button)dialog.findViewById(R.id.button_1);
            done=(Button)dialog.findViewById(R.id.button_2);



        }

    }





}


