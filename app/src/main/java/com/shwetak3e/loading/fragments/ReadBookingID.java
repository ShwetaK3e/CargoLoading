package com.shwetak3e.loading.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shwetak3e.loading.MainActivity;
import com.shwetak3e.loading.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.shwetak3e.loading.MainActivity.current_truck;
import static com.shwetak3e.loading.MainActivity.express;


public class ReadBookingID extends Fragment {


    private static final String TAG =ReadBookingID.class.getSimpleName() ;
    private static final int FOCUS_STATE_DONE =108 ;
    private static final int FOCUS_STATE_WAITING =107 ;

    ImageButton find_parcel;
    private RelativeLayout overlayView;
    private AudioManager audioManager;
    private int previousVolume;
    private int camToOpen;
    private FrameLayout preview;
    private int iOrientation;
    private int imageRotation;
    private Camera mCamera;
    private boolean autofocusInContinuousMode;
    private int focusState;
    private Runnable resetFocusModeRunnable;
    private  Handler resetFocusModeHandler;
    private boolean isCapturePressed;
    private boolean captureImageOnFocus;
    private boolean isPictureTaken;
    private int maxTextureSize;
    private boolean errorGettingTextureSize;



    private MediaPlayer media;


    private final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback()
    {
        @Override
        public void onShutter()
        {
            media = MediaPlayer.create(getActivity(), R.raw.camera_shutter_click_01);
            media.start();
        }
    };
    private CameraManager cameraManager;
    private CameraPreview cameraPreview;
    private boolean isPreviewReady;

    EditText new_booking_id;
    Dialog dialog;


    public static ReadBookingID newInstance() {
        ReadBookingID fragment = new ReadBookingID();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_booking_id, container, false);
        express=true;

        new_booking_id=(EditText)view.findViewById(R.id.new_booking_id);
        find_parcel=(ImageButton)view.findViewById(R.id.find_parcel);
        find_parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!express) {
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    i.putExtra("Activity", "TO_LOAD");
                    startActivity(i);
                }else{
                    dialog = new Dialog(getActivity());

                    showSuccessDialog(new_booking_id.getText().toString().trim());
                    final Handler handler  = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    };

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            handler.removeCallbacks(runnable);
                        }
                    });

                    handler.postDelayed(runnable, 10000);

                }
            }
        });

        overlayView = (RelativeLayout)view.findViewById(R.id.overlayView);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setSpeakerphoneOn(true);
        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        camToOpen = getCamNumber();
        preview = (FrameLayout)view.findViewById(R.id.camera_preview);
        OrientationEventListener myOrientationEventListener = getOrientationEventListener();
        // To display if orientation detection will work and enable it
        if (myOrientationEventListener.canDetectOrientation())
        {
            Log.e(TAG+"onCreate", "Enabling orientationListener");
            myOrientationEventListener.enable();
        }
        else
        {
            Log.i(TAG+"onCreate", "Failed to enable orientationListener");
            Toast.makeText(getActivity(), "Cannot Detect Orientation", Toast.LENGTH_LONG).show();
        }



        return view;
    }

    private OrientationEventListener getOrientationEventListener()
    {
        return new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL)
        {
            public void onOrientationChanged(int iAngle)
            {

                    if (iAngle != ORIENTATION_UNKNOWN)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
                        {
                            int orientation = (iAngle + 45) / 90 * 90;
                            setImageRotation(iAngle);
                            iOrientation = orientation;
                        }
                        else
                        {
                            imageRotation = 90;
                        }
                    }
                    else
                    {
                        Log.i("onOrientationChanged", "Orientation angle is ORIENTATION_UNKNOWN");
                    }

            }
        };
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void setImageRotation(int iAngle)
    {
        try
        {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            Camera.getCameraInfo(camToOpen, info);
            int orientation = (iAngle + 45) / 90 * 90;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
            {
                imageRotation = (info.orientation - orientation + 360) % 360;
            }
            else
            {
                imageRotation = (info.orientation + orientation) % 360;
            }
            Log.d("setImageRotation", Integer.toString(imageRotation));
        }
        catch (Exception e)
        {
            Log.i(TAG+"setImageRotation", e.toString());
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

    private static float getMegabytesFree()
    {
        final float bytesInMb = 1024.0f * 1024.0f;
        final Runtime rt = Runtime.getRuntime();
        final float bytesUsed = rt.totalMemory();
        final float mbUsed = bytesUsed / bytesInMb;
        return getMegabytesAvailable() - mbUsed;
    }

    /**
     * Gets total heap memory available to the process.
     * the memory size in MB.
     */
    private static float getMegabytesAvailable()
    {
        final float bytesInMb = 1024.0f * 1024.0f;
        final Runtime rt = Runtime.getRuntime();
        final float bytesAvailable = rt.maxMemory();
        return bytesAvailable / bytesInMb;
    }


    private void exitOnException(String error)
    {
        Toast.makeText(getActivity(), "Exception:" + error, Toast.LENGTH_LONG).show();
        Intent i = new Intent(getActivity(), MainActivity.class);

        startActivity(i);
    }


    private void hideSystemUI()
    {
        View decorView = getActivity().getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    private void initImmersiveMode()
    {
        View decorView = getActivity().getWindow().getDecorView();
        hideSystemUI();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                // Note that system bars will only be "visible" if none of the LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                {
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            hideSystemUI();
                        }
                    },1000);
                }
            }
        });
    }


    private void setParameters(Camera.Parameters parameters, String message)
    {
        String setParamTag = "setParameters";
        Log.i(TAG+setParamTag, message);
        if (mCamera != null)
        {
            try
            {
                mCamera.setParameters(parameters);
            }
            catch (Exception e)
            {
                Log.e(TAG+setParamTag, e.getMessage());
            }
        }
        else
        {
            Log.e(TAG+setParamTag, "Camera instance is null");
        }
    }

    private void resetContinuousFocusMode()
    {
        try
        {
            autofocusInContinuousMode = false;
            Camera.Parameters parameters = mCamera.getParameters();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            {
                if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                {
                    mCamera.cancelAutoFocus();
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    setParameters(parameters, "Point11");
                    focusState = FOCUS_STATE_DONE;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void removePendingContinuousFocusReset()
    {
        if (resetFocusModeRunnable != null)
        {
            resetFocusModeHandler.removeCallbacks(resetFocusModeRunnable);
            resetFocusModeRunnable = null;
        }
    }


    /**
     * Prints log message with more context info than the <a href="https://developer.android.com/reference/android/util/Log.html">Android Log utility</a>
     *
     * @param methodName the function name from where the log was printed.
     * @param message    the message of the log
     * @param misc       any miscellaneous tag, to add more context.
     */
    private void logMessage(String methodName, String message, String misc)
    {
        String tag;
        final String tagLog = "CameraSurfaceActivityLog";
        if (misc != null)
        {
            tag = tagLog + ":" + methodName + "-" + misc;
        }
        else
        {
            tag = tagLog + ":" + methodName;
        }
        Log.e(tag, message);
    }

    private void logMessage(String methodName, String message)
    {
        logMessage(methodName, message, null);
    }

    /**
     * Prints the stacktrace of exception with more contextual logs, although does not offer any significant advantage
     * over the printStackTrace() method of the Exception itself.
     *
     * @param e          Exception object.
     * @param methodName the function name from where the log was printed.
     * @param misc       any miscellaneous tag, to add more context.
     */
    private void printStackTrace(Exception e, String methodName, String misc)
    {
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        for (StackTraceElement element : stackTraceElements)
        {
            logMessage(methodName, element.toString(), misc);
        }
    }


    private void captureImage()
    {
        String methodName = "captureImage";
        logMessage(methodName, "started", "Facing:" + "REAR");

            try
            {
                try
                    {
                        isCapturePressed = true;
                        Camera.Parameters parameters;
                        parameters = mCamera.getParameters();  //retrieve the updated the Parameters after setting flash mode.
                        if (parameters.getSupportedFocusModes() != null)
                        {
                            try
                            {
                                if (autofocusInContinuousMode)
                                {
                                    removePendingContinuousFocusReset();
                                    if (focusState == FOCUS_STATE_WAITING)
                                    {
                                        captureImageOnFocus = true;
                                        forceImageCaptureIfRequired();
                                    }
                                    else
                                    {
                                        isPictureTaken = true;
                                        takePicture("Point14");
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                printStackTrace(e, methodName, "Exception_Point1");
                                //Try to click the picture here, to prevent app from getting stuck at this point due to exception.
                                takePicture("Point10");
                            }
                        }
                        else
                        {
                            logMessage(methodName, "Focus modes is null", null);
                            takePicture("Point11");
                        }
                    }
                    catch (Exception e) {
                        logMessage(methodName, e.toString(), "Exception2");
                        Toast.makeText(getActivity(), "Error taking picture.", Toast.LENGTH_SHORT).show();
                    }

            }
            catch (Exception e) {
                logMessage(methodName, e.toString(), "Exception-Could not set the surface preview texture");

            }

    }

    private void forceImageCaptureIfRequired() {
        /*
        There are some devices in which the autoFocusCallback never returns under certain
        conditions, such as after previous autoFocus operations. So in those cases, it is not possible to rely
        on the focus callback for capturing the picture. So we also set a timer, which will capture the picture
        without autoFocus if the callback callback fails to be invoked within a sufficient amount of time.
        */
        try
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (!isPictureTaken) {
                        logMessage("forceImageCaptureIfRequired", "forcing image capture", null);
                        isPictureTaken = true;
                        takePicture("Point13");
                    }
                }
            }, 5000);
        }
        catch (Exception e)
        {
            printStackTrace(e, "forceimageCapture", "Exception_Point0");
        }
    }

    private void setFlashMode()
    {
        String methodName = "setFlashMode";
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> modes = parameters.getSupportedFlashModes();
        if (modes != null)
        {
            logMessage(methodName, "Flash modes not null", null);
            if (modes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else if (modes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            } else if (modes.contains(android.hardware.Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            } else {
                logMessage(methodName, "Flash modes has neither mode Torch, not On", "enableCameraFlash");
            }
            setParameters(parameters, "Point1");
        } else {
            logMessage(methodName, "Flash modes is null", null);
        }
    }

    private void takePicture(String message) {
        try
        {
            removePendingContinuousFocusReset();
            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size maxImageSize = getMaximumPictureResolution();
            parameters.setPictureSize(maxImageSize.width, maxImageSize.height);
            setParameters(parameters, message);
            logMessage("takePicture", "Preview Size final width::height:" + parameters.getPreviewSize().width + ":" + parameters.getPreviewSize().height);
            mCamera.takePicture(shutterCallback, null, new Camera.PictureCallback()
            {
                @Override
                public void onPictureTaken(byte[] data, Camera camera)
                {
                    //onJpegPictureTaken(data, camera);
                }
            });
        }
        catch (Exception e)
        {
            printStackTrace(e, "takePicture", message);
        }
    }


    private Camera.Size getMaximumPictureResolution() {
        setSurfaceTextureDetails();
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        Camera.Size maxSize = null;
        for (Camera.Size size : sizes)
        {
            logMessage("getMaximumPictureResolution", Integer.toString(size.width) + ":" + Integer.toString(size.height), "Picture Size");
        }
        if (sizes.size() > 0)
        {
            for (Camera.Size size : sizes)
            {
                if (maxSize == null)
                {
                    maxSize = size;
                }
                if (size.height * size.width > maxSize.height * maxSize.width)
                {
                    maxSize = size;
                }
            }
            if (maxSize != null)
            {
                logMessage("getMaximumPictureResolution", Integer.toString(maxSize.width) + ":" + Integer.toString(maxSize.height), "Maximum Picture Size");
            }
            else
            {
                logMessage("getMaximumPictureResolution", "maxSize is null", "Maximum Picture Size");
            }
        }
        else
        {
            logMessage("getMaximumPictureResolution", "getMaximumPictureResolution:Supported Picture size list is empty");
        }
        return maxSize;
    }

    private void setSurfaceTextureDetails()
    {
        if (Build.VERSION.SDK_INT >= 17)
        {
            try
            {
                EGLDisplay dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
                int[] vers = new int[2];
                EGL14.eglInitialize(dpy, vers, 0, vers, 1);
                int[] configAttr = {EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER, EGL14.EGL_LEVEL, 0, EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                        EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT, EGL14.EGL_NONE};
                EGLConfig[] configs = new EGLConfig[1];
                int[] numConfig = new int[1];
                EGL14.eglChooseConfig(dpy, configAttr, 0, configs, 0, 1, numConfig, 0);
                boolean configFound = true;
                if (numConfig[0] == 0)
                {
                    //Trouble.Configuration not found.
                    configFound = false;
                }
                if (configFound)
                {
                    EGLConfig config = configs[0];
                    int[] surfAttr = {EGL14.EGL_WIDTH, 64, EGL14.EGL_HEIGHT, 64, EGL14.EGL_NONE};
                    EGLSurface surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);
                    int[] ctxAttrib = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
                    EGLContext ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);
                    EGL14.eglMakeCurrent(dpy, surf, surf, ctx);
                    int[] maxTexSize = new int[1];
                    GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxTexSize, 0);
                    maxTextureSize = maxTexSize[0];
                    logMessage("setSurfaceTextureDetails", "maxPossibleTextureWidth on and after 17:" + Integer.toString(maxTextureSize));
                }
                else
                {
                    maxTextureSize = 0;
                }
            }
            catch (Exception exception)
            {
                errorGettingTextureSize = true;
                logMessage("setSurfaceTextureDetails", "Error getting maximum Texture size:API level 17 and above:" + exception.toString());
            }
        }
        else
        {
            try
            {
                boolean configFound = true;
                javax.microedition.khronos.egl.EGL10 egl = (javax.microedition.khronos.egl.EGL10) javax.microedition.khronos.egl.EGLContext.getEGL();
                javax.microedition.khronos.egl.EGLDisplay dpy = egl.eglGetDisplay(javax.microedition.khronos.egl.EGL10.EGL_DEFAULT_DISPLAY);
                int[] vers = new int[2];
                egl.eglInitialize(dpy, vers);
                int[] configAttr = {javax.microedition.khronos.egl.EGL10.EGL_COLOR_BUFFER_TYPE, javax.microedition.khronos.egl.EGL10.EGL_RGB_BUFFER,
                        javax.microedition.khronos.egl.EGL10.EGL_LEVEL, 0, javax.microedition.khronos.egl.EGL10.EGL_SURFACE_TYPE,
                        javax.microedition.khronos.egl.EGL10.EGL_PBUFFER_BIT, javax.microedition.khronos.egl.EGL10.EGL_NONE};
                javax.microedition.khronos.egl.EGLConfig[] configs = new javax.microedition.khronos.egl.EGLConfig[1];
                int[] numConfig = new int[1];
                egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
                if (numConfig[0] == 0)
                {
                    logMessage("setSurfaceTextureDetails", "EGL config not found API level less than 17");
                    // Trouble! Configuration not found.
                    configFound = false;
                }
                if (configFound)
                {
                    javax.microedition.khronos.egl.EGLConfig config = configs[0];
                    int[] surfAttr = {javax.microedition.khronos.egl.EGL10.EGL_WIDTH, 64, javax.microedition.khronos.egl.EGL10.EGL_HEIGHT, 64,
                            javax.microedition.khronos.egl.EGL10.EGL_NONE};
                    javax.microedition.khronos.egl.EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
                    final int eglContextClientVersion = 0x3098;  // missing in EGL10
                    int[] ctxAttrib = {eglContextClientVersion, 1, javax.microedition.khronos.egl.EGL10.EGL_NONE};
                    javax.microedition.khronos.egl.EGLContext ctx =
                            egl.eglCreateContext(dpy, config, javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT, ctxAttrib);
                    egl.eglMakeCurrent(dpy, surf, surf, ctx);
                    int[] maxTexSize = new int[1];
                    GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxTexSize, 0);
                    maxTextureSize = maxTexSize[0];
                    logMessage("setSurfaceTextureDetails", "maxPossibleTextureWidth before 17:" + Integer.toString(maxTextureSize));
                }
                else
                {
                    maxTextureSize = 0;
                }
            }
            catch (Exception exception)
            {
                errorGettingTextureSize = true;
                logMessage("setSurfaceTextureDetails", "Error getting maximum Texture size:API level below 17" + exception.toString());
            }
            if (maxTextureSize == 0 || errorGettingTextureSize)
            {
                /*
                Just in case we fail to get maximum texture size for any device for any reason,
                this is a safe value of maximum texture size and expected to be valid
                across all devices.
                Please do not change this number. This number is based on observations made
                from a number of devices, and is not a random number.
                */
                maxTextureSize = 2048;
            }
            logMessage("setSurfaceTextureDetails", "maxPossibleTextureSize:" + Integer.toString(maxTextureSize));
        }
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void startCam()
    {
        String methodName = "startCam";
        logMessage(methodName, "startCam() started.", null);
        try
        {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                mCamera = Camera.open(camToOpen);
                if (mCamera != null)
                {
                    mCamera.stopPreview();
                    mCamera.lock();
                    //Set the camera parameters and other specs.
                    cameraManager = new CameraManager(mCamera);
                    Camera.Parameters parameters = cameraManager.getCamera().getParameters();
                    parameters.setPictureSize(cameraManager.getPictureSize().width, cameraManager.getPictureSize().height);
                    parameters.setPreviewSize(cameraManager.getPreviewSize().width, cameraManager.getPreviewSize().height);
                    logMessage(methodName, cameraManager.getPreviewSize().width + "x" + cameraManager.getPreviewSize().height, "Preview Size");
                    parameters.setRotation(0);

                    setParameters(parameters, "SettingBaseConfigurations");


                        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                                supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                        {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        }
                        else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                        {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        }

                        setParameters(parameters, "SettingFocusMode");

                        setFlashMode(); //set the appropriate focus mode.


                    parameters = mCamera.getParameters();
                    mCamera.setDisplayOrientation(getCameraOrientation());
                    setParameters(parameters, "Point0");
                    cameraPreview = new CameraPreview(getActivity(), cameraManager.getCamera(), cameraManager.getRequiredAspectRatio());
                    preview.addView(cameraPreview);
                    LayoutInflater inflater =
                            (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View menuLayout = inflater.inflate(R.layout.scan_direction_layout, preview, true);

                    //preview.addView(scan_direction);
                } else {
                    logMessage(methodName, "Camera instance is null", null);
                }
            }

            if (cameraManager != null && !cameraManager.isZoomSupported()) {
                Toast.makeText(getActivity(), "Zoom is not supported in this mode", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            logMessage(methodName, e.toString(), TAG);
            exitOnException("ERROR");
        }
    }


    private int getCameraOrientation() {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees;
        switch (rotation)
        {
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
            default:
                degrees = 0; //Got no idea what this default is for, but SonarQube asked for it, so. :p
                break;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(camToOpen, info);
        int result=0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

                result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }







      class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        SurfaceHolder mHolder;
        /**
         * The Required aspect ratio.
         */
        double requiredAspectRatio;
        private Camera mCamera;

        /**
         * Instantiates a new Camera preview.
         *
         * @param activity            the current activity
         * @param camera              the camera object
         * @param requiredAspectRatio the required aspect ratio
         */
        public CameraPreview(Activity activity, Camera camera, double requiredAspectRatio)
        {
            super(activity);
            logMessage("CameraPreview", "Camera is null:" + Boolean.toString(camera == null), "Constructor");
            this.mCamera = camera;
            this.requiredAspectRatio = requiredAspectRatio;
            // Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            {
                mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder)
        {
            logMessage("surfaceCreated", "started", null);
            try
            {
                if (mCamera != null)
                {
                    mCamera.setPreviewDisplay(mHolder);
                    logMessage("surfaceCreated", "setPreviewDisplay", null);
                }

            }
            catch (Exception exception)
            {
                isPreviewReady = false;
                logMessage("surfaceCreated", exception.toString(), TAG);
                exitOnException("Exception:" + exception.toString());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
        {
            // Now that the size is known, set up the camera parameters and begin the preview.
            logMessage("surfaceChanged", "Started", null);
            if (mCamera != null)
            {
                try
                {
                    mCamera.startPreview();
                    isPreviewReady = true;
                }
                catch (Exception e)
                {
                    logMessage("surfaceChanged", e.toString(), TAG);
                    isPreviewReady = false;
                    exitOnException("Exception:" + e.toString());
                }
            }
            else
            {
                isPreviewReady = false;
                logMessage("surfaceChanged", "Camera is null");
                exitOnException("Exception: Camera is unavailable. Camera is null.");
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder)
        {
            try
            {
                mCamera = cameraManager.getCamera();
                // Surface will be destroyed when we return, so stop the preview.
                if (mCamera != null)
                {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    isPreviewReady = false;
                }
            }
            catch (Exception e)
            {
                printStackTrace(e, "surfaceDestroyed", TAG);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            Point point = cameraManager.getOptimalSurfaceSize(requiredAspectRatio);
            int displayWidth = point.x;
            int displayHeight = point.y;
            logMessage("onMeasure", displayWidth + "::" + displayHeight, null);
            int widthSpec = MeasureSpec.makeMeasureSpec(displayWidth, MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(displayHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthSpec, heightSpec);
        }
    }

    class CameraManager {
        private Camera mCamera;
        private Camera.Size pictureSize;
        private Camera.Size previewSize;
        private double requiredAspectRatio;
        private boolean isZoomSupported;

        /**
         * Instantiates a new Camera manager.
         * The cameraManager does the job of configuring the camera, most notably setting the preview and picture size.
         *
         * @param camera the camera object.
         */
        public CameraManager(Camera camera)
        {
            setCamera(camera);
            if (getCamera() != null)
            {
                setPictureSize(getMaximumPictureResolution());
                setPreviewSize(getOptimalPreviewSize(getCamera().getParameters().getSupportedPreviewSizes(),
                        getAspectRatio(getPictureSize().width, getPictureSize().height)));
                setRequiredAspectRatio(getAspectRatio(getPictureSize().height, getPictureSize().width));
                Camera.Parameters parameters = mCamera.getParameters();
                try
                {
                    if (parameters.isZoomSupported())
                    {
                        List<Integer> zoomRatios = parameters.getZoomRatios();
                        isZoomSupported = zoomRatios != null && !zoomRatios.isEmpty();
                    }
                    else
                    {
                        isZoomSupported = false;
                    }
                }
                catch (Exception e)
                {
                    logMessage("Cameramanager", e.toString(), "Exception:Constructor");
                    isZoomSupported = true;
                }
            }
        }

        /**
         * Checks whether zoom is supported.
         *
         * @return true if zoom is supported, false otherwise.
         */
        private boolean isZoomSupported()
        {
            return isZoomSupported;
        }

        /**
         * Gets the opened camera.
         *
         * @return the camera
         */
        public Camera getCamera()
        {
            return mCamera;
        }

        /**
         * Sets camera.
         *
         * @param mCamera the m camera
         */
        public void setCamera(Camera mCamera)
        {
            this.mCamera = mCamera;
        }

        /**
         * Getter method for the calculated picture size.
         *
         * @return the selected picture size.
         */
        private Camera.Size getPictureSize()
        {
            return pictureSize;
        }

        /**
         * Setter method for the picture size.
         *
         * @param pictureSize the picture size
         */
        private void setPictureSize(Camera.Size pictureSize)
        {
            this.pictureSize = pictureSize;
        }

        /**
         * Getter method for the calculated preview size.
         *
         * @return the preview size
         */
        private Camera.Size getPreviewSize()
        {
            return previewSize;
        }

        /**
         * Setter method for the calculated preview size.
         *
         * @param previewSize the preview size
         */
        private void setPreviewSize(Camera.Size previewSize)
        {
            this.previewSize = previewSize;
        }

        /**
         * Gets required aspect ratio.
         *
         * @return the required aspect ratio
         */
        private double getRequiredAspectRatio()
        {
            return requiredAspectRatio;
        }

        /**
         * Sets required aspect ratio.
         *
         * @param requiredAspectRatio the required aspect ratio
         */
        private void setRequiredAspectRatio(double requiredAspectRatio)
        {
            this.requiredAspectRatio = requiredAspectRatio;
        }

        /**
         * Calculates the optimal previewSize of the camera, based on camera parameters, and aspect ratio of the picture.
         *
         * @param supportedPreviewSizes the supportedPreviewSizes of the camera.
         * @param requiredAspectRatio   required aspect ratio.
         * @return The Size object encapsulating the dimensions for preview.
         */
        private Camera.Size getOptimalPreviewSize(List<Camera.Size> supportedPreviewSizes, double requiredAspectRatio)
        {
            Collections.sort(supportedPreviewSizes, new ListComparator(false));
            double aspectTolerance = Double.MAX_VALUE;
            Camera.Size optimalSize = null;
            double aspectRatio;
            if (!supportedPreviewSizes.isEmpty())
            {
                for (Camera.Size size : supportedPreviewSizes)
                {
                    logMessage("getOptimalPreviewSize", size.width + "::" + size.height);
                    aspectRatio = getAspectRatio(size.width, size.height);
                    if (Math.abs(aspectRatio - requiredAspectRatio) <= 0.05)
                    {
                        optimalSize = size;
                    }
                }
                if (optimalSize == null)
                {
                    for (Camera.Size size : supportedPreviewSizes)
                    {
                        if (Math.abs(getAspectRatio(size.width, size.height) - requiredAspectRatio) < aspectTolerance)
                        {
                            optimalSize = size;
                            aspectTolerance = getAspectRatio(size.width, size.height) - requiredAspectRatio;
                        }
                    }
                }
                if (optimalSize != null)
                {
                    logMessage("getOptimalPreviewSize", optimalSize.width + "::" + optimalSize.height, "Optimal Preview size");
                }
                else
                {
                    logMessage("getOptimalPreviewSize", "optmalPreviewSize is null", "Optimal Preview size");
                }
            }
            else
            {
                logMessage("getOptimalPreviewSize", "supported preview sizes are 0");
            }
            return optimalSize;
        }

        /**
         * Calculates the optimal size for the surfaceView which displays the preview, based on camera parameters, and aspect ratio of the picture.
         *
         * @param aspectRatio required aspect ratio.
         * @return The Point object encapsulating the dimensions for surfaceView.
         */
        private Point getOptimalSurfaceSize(double aspectRatio)
        {
            Point surfaceSize = new Point();
            DisplayMetrics metrics = new DisplayMetrics();
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                display.getRealMetrics(metrics);
            }
            else
            {
                display.getMetrics(metrics);
            }
            logMessage("getOptimalSurfaceSize", "Display Size:" + metrics.widthPixels + "::" + metrics.heightPixels);
            double screenAspectRatio = getAspectRatio(metrics.widthPixels, metrics.heightPixels);
            if (Math.abs(screenAspectRatio - aspectRatio) < 0.001)
            {
                surfaceSize.x = metrics.widthPixels;
                surfaceSize.y = metrics.heightPixels;
            }
            else if (screenAspectRatio < aspectRatio)
            {
                surfaceSize.x = metrics.widthPixels;
                surfaceSize.y = (int) (surfaceSize.x / aspectRatio);
            }
            else
            {
                surfaceSize.y = metrics.heightPixels;
                surfaceSize.x = (int) (surfaceSize.y * aspectRatio);
            }
            return surfaceSize;
        }

        /**
         * Returns ratio of width and height.
         *
         * @param width  the width dimension.
         * @param height the height dimension
         * @return ratio of width to height.
         */
        private double getAspectRatio(int width, int height)
        {
            return (double) width / height;
        }
    }

    /**
     * The implementation of the SimpleOnScaleGestureListener abstract class.
     * The SimpleOnScaleGestureListener determines whether a touch event is a scale event, or a regular touch event.
     * If it happens to be a scale event, it means that the user is trying to use scale zoom gesture, and the corresponding action
     * is taken for the same.
     */


    private class ListComparator implements Comparator<Camera.Size>
    {
        /**
         * Boolean to determine whether the list is to be sorted in descending order or not.
         */
        boolean isDescendingOrder;

        /**
         * Instantiates the list comparator.
         *
         * @param isDescendingOrder true if list is to be sorted in descending order, false otherwise.
         */
        ListComparator(boolean isDescendingOrder)
        {
            this.isDescendingOrder = isDescendingOrder;
        }

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs)
        {
            if (lhs.height * lhs.width > rhs.height * rhs.width)
            {
                if (isDescendingOrder)
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
            else if (lhs.height * lhs.width == rhs.height * rhs.width)
            {
                return 0;
            }
            else
            {
                if (isDescendingOrder)
                {
                    return 1;
                }
                else
                {
                    return -1;
                }
            }
        }
    }
















    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            super.onResume();
            //initImmersiveMode();
            logMessage("onResume", "onResume started", null);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            startCam();
        }
        catch (Exception e)
        {
            printStackTrace(e, "onResume", TAG);
            exitOnException("Error in Camera onResume: " + e.toString());
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        stopCam();
        if (media != null)
        {
            media.release();
            media = null;
        }
    }



    /**
     * Releases the opened camera,and any other media resources.
     */
    private void stopCam()
    {
        try
        {
            // Because the Camera object is a shared resource, it's important to release it when the activity is paused.
            if (mCamera != null)
            {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                preview.removeAllViews();
                isPreviewReady = false;
                mCamera = null;
            }
        }
        catch (Exception e)
        {
            printStackTrace(e, "stopCam", "Exception");
        }
    }



    public void showSuccessDialog(String booking_id) {


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.booking_success_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        Button find_booking = (Button) dialog.findViewById(R.id.confirm_booking);
        TextView bookingId=(TextView)dialog.findViewById(R.id.booking_id);
        TextView truck_id=(TextView)dialog.findViewById(R.id.truck_id);

        bookingId.setText(booking_id);
        truck_id.setText(current_truck.getId());

        find_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
            }
        });


        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

    }



}
