package com.shwetak3e.loading.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.shwetak3e.loading.IssueVideoActivity;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.adapter.ItemsToBeLoadedAdapter;
import com.shwetak3e.loading.model.ShipmentItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LoadItems_itemwise extends Fragment {


    private static final String TAG =LoadItems_itemwise.class.getSimpleName() ;
    RecyclerView item_to_be_loaded_list;
    public static List<ShipmentItem> booked_shipmentItems=new ArrayList<>();
    public static List<Integer> keyList=new ArrayList<>();
    ItemsToBeLoadedAdapter adapter;



    LinearLayout play_layout;
    ImageButton btnplay_video;
    Window window;
    boolean isPlayClicked;
    AudioManager audioManager;
    int previousVolume=0;
    VideoView mVideoView=null;
    String videoPath = null;
    ImageView imgPreview;
    ImageButton record_issue_video;


    public static LoadItems_itemwise newInstance() {
        LoadItems_itemwise fragment = new LoadItems_itemwise();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.load_items_fragment_itemwise, container, false);
        imgPreview = (ImageView) view.findViewById(R.id.imgPreview);
        play_layout = (LinearLayout) view.findViewById(R.id.play_layout);
        mVideoView = (VideoView) view.findViewById(R.id.videoView);
        btnplay_video = (ImageButton) view.findViewById(R.id.btnplay);
        btnplay_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 playVideo();
            }
        });
        record_issue_video=(ImageButton)view.findViewById(R.id.take_issue_video);
        record_issue_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), IssueVideoActivity.class));
            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                setupVideoPlayer();
            }
        });

        return view;
    }



    private void updateBrightness() {
        WindowManager.LayoutParams layoutpars = window.getAttributes();
        layoutpars.screenBrightness = 1f;
        window.setAttributes(layoutpars);
    }


    private void playVideo() {
        isPlayClicked = true;
        updateBrightness();
        play_layout.setVisibility(View.GONE);
        mVideoView.setVideoPath(videoPath);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(true);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
                if (isPlayClicked)
                {
                    mVideoView.start();
                }
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
                play_layout.setVisibility(View.VISIBLE);
                isPlayClicked=false;
            }
        });
    }

    private void setupVideoPlayer()
    {
        window = getActivity().getWindow();
        isPlayClicked = false;
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        videoPath=getActivity().getApplicationContext().getExternalFilesDir(null)+ File.separator+"Issues"+File.separator+"ISSUE_VIDEO.mp4";

        mVideoView.setVisibility(View.VISIBLE);
        mVideoView.setVideoURI(Uri.parse(videoPath));
        mVideoView.setMediaController(new MediaController(getActivity()));
        mVideoView.seekTo(1000);


        if (videoPath == null || videoPath.trim().equals("")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Warning.").setMessage("Some thing wrong with camera.");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (audioManager != null) {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
                        }
                    }

            });

            alertDialogBuilder.setCancelable(true).setIcon(android.R.drawable.ic_dialog_alert);
            AlertDialog alert = alertDialogBuilder.show();
            Button posBtn = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            posBtn.setTextColor(Color.TRANSPARENT);
            posBtn.setTextSize(12);

        } else {
            final File corruptFile = new File(videoPath);
            long thresholdFileSize = 5 * 1000L;
            if (corruptFile.length() < thresholdFileSize) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Warning").setMessage("Some thing wrong with camera.");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (audioManager != null) {
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
                            }
                    }
                });

                alertDialogBuilder.setCancelable(true).setIcon(android.R.drawable.ic_dialog_alert);
                AlertDialog alert = alertDialogBuilder.show();
                Button posBtn = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                posBtn.setTextColor(Color.TRANSPARENT);
                posBtn.setTextSize(12);
            } else {
               // play_layout.setVisibility(View.VISIBLE);
            }
        }
    }



    @Override
    public void onPause()
    {
        super.onPause();

        try
        {
            if ( mVideoView != null)
            {
                mVideoView.pause();
                mVideoView.stopPlayback();
                mVideoView.clearFocus();
                mVideoView.setVisibility(View.INVISIBLE);


            }
        }
        catch (Exception e)
        {
            Log.e(TAG+"VideoView"+ "onPause", e.getMessage());
        }
    }





    @Override
    public void onStop() {
        super.onStop();
    }

}
