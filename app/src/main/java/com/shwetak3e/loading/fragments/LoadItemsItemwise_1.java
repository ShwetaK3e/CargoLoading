package com.shwetak3e.loading.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.shwetak3e.loading.IssueVideoActivity;
import com.shwetak3e.loading.MainActivity;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.adapter.ItemsToBeLoadedAdapter;
import com.shwetak3e.loading.model.ShipmentItem;
import com.shwetak3e.loading.model.Truck;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LoadItemsItemwise_1 extends Fragment {


    private static final String TAG =LoadItemsItemwise_1.class.getSimpleName() ;


    ImageView commodity_img;
    ImageButton inc_load_count;
    ImageButton dec_load_count;

    TextView  commodity_name;
    TextView  booking_desc;
    TextView  commodity_length;
    TextView  commodity_width;
    TextView  commodity_height;
    TextView  commodity_weight;
    TextView  total_count;
    EditText loading_count;
    ImageButton see_more_details;
    LinearLayout other_details;


    EditText  loading_weight;


    TextView drop_loc;
    ImageButton nxt_dest;
    int next_loc=1;

    LinearLayout damage_layout;
    Button go_to_issue;




    LinearLayout play_layout;
    ImageButton btnplay_video;
    Window window;
    boolean isPlayClicked;
    AudioManager audioManager;
    int previousVolume=0;
    VideoView mVideoView=null;
    String videoPath = null;
    ImageView imgPreview;




    LinearLayout enter_booking_id;
    LinearLayout truck_details;


    public static LoadItemsItemwise_1 newInstance() {
        LoadItemsItemwise_1 fragment = new LoadItemsItemwise_1();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.load_items_fragment_itemwise_1, container, false);

        commodity_img=(ImageView)view.findViewById(R.id.commodity_img);
        Glide.with(getActivity()).load(TruckDetails_1.current_item.getImageUri()).into(commodity_img);
        inc_load_count=(ImageButton)view.findViewById(R.id.inc_load_count);
        inc_load_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TruckDetails_1.current_item.setLoadedCount(TruckDetails_1.current_item.getLoadedCount()+1);
                loading_count.setText(TruckDetails_1.current_item.getLoadedCount().toString());
            }
        });

        dec_load_count=(ImageButton)view.findViewById(R.id.dec_load_count);
        dec_load_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TruckDetails_1.current_item.setLoadedCount(TruckDetails_1.current_item.getLoadedCount()-1);
                loading_count.setText(TruckDetails_1.current_item.getLoadedCount().toString());
            }
        });

        commodity_name=(TextView)view.findViewById(R.id.commodity_name);
        commodity_name.setText(TruckDetails_1.current_item.getBookedItem().getCommodityName());
        commodity_length=(TextView)view.findViewById(R.id.commodity_length);
        commodity_length.setText(TruckDetails_1.current_item.getBookedItem().getLength().toString());
        commodity_width=(TextView)view.findViewById(R.id.commodity_width);
        commodity_width.setText(TruckDetails_1.current_item.getBookedItem().getWidth().toString());
        commodity_height=(TextView)view.findViewById(R.id.commodity_height);
        commodity_height.setText(TruckDetails_1.current_item.getBookedItem().getHeight().toString());
        commodity_weight=(TextView)view.findViewById(R.id.commodity_weight);
        commodity_weight.setText(TruckDetails_1.current_item.getBookedItem().getActualWeight().toString());
        total_count=(TextView)view.findViewById(R.id.total_count);
        total_count.setText(TruckDetails_1.current_item.getShippedItemCount().toString());
        booking_desc=(TextView)view.findViewById(R.id.booking_desc);
        booking_desc.setText(TruckDetails_1.current_item.getBookedItem().getDescription().toString());
        loading_count=(EditText) view.findViewById(R.id.loading_count);
        loading_count.setText(TruckDetails_1.current_item.getLoadedCount().toString());


        other_details=(LinearLayout)view.findViewById(R.id.other_details);
        other_details.setVisibility(View.GONE);
        see_more_details=(ImageButton)view.findViewById(R.id.see_more_details);
        see_more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(other_details.getVisibility()==View.GONE){
                    other_details.setVisibility(View.VISIBLE);
                    see_more_details.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_up_arrow));
                }else{
                    other_details.setVisibility(View.GONE);
                    see_more_details.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_down_arrow));
                }
            }
        });


        loading_weight=(EditText) view.findViewById(R.id.loading_weight);
        loading_weight.setText(TruckDetails_1.current_item.getBookedItem().getActualWeight().toString());


        drop_loc=(TextView)view.findViewById(R.id.drop_loc);
        nxt_dest=(ImageButton)view.findViewById(R.id.next_dest);
        nxt_dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(next_loc < AddNewTruck_1.current_truck.getStops().size()) {
                    drop_loc.setText(AddNewTruck_1.current_truck.getStops().get(next_loc++));
                }else {
                    next_loc=1;
                    drop_loc.setText(AddNewTruck_1.current_truck.getStops().get(next_loc++));
                }
            }
        });


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


        damage_layout=(LinearLayout)view.findViewById(R.id.damage_layout);
        damage_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), IssueVideoActivity.class));
            }
        });
        go_to_issue=(Button)view.findViewById(R.id.go_to_issues);
        go_to_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });





        enter_booking_id=(LinearLayout)view.findViewById(R.id.enter_booking_id);
        enter_booking_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.putExtra("Activity","Enter_booking_ID");
                startActivity(i);
            }
        });
        truck_details=(LinearLayout)view.findViewById(R.id.truck_details);
        truck_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.putExtra("Activity","Truck_Details");
                startActivity(i);
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
