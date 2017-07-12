package com.shwetak3e.loading.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.shwetak3e.loading.IssueVideoActivity;
import com.shwetak3e.loading.MainActivity;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.adapter.IssuesAdapter;
import com.shwetak3e.loading.adapter.TruckAdapter;
import com.shwetak3e.loading.model.Issues;
import com.shwetak3e.loading.model.ShipmentItem;

import org.w3c.dom.Text;

import java.io.File;
import java.security.IdentityScope;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class IssueList extends Fragment {


    private static final String TAG =IssueList.class.getSimpleName() ;

    ImageButton prev_shipment;
    ImageButton next_shipment;
    TextView shipmentID_txtview;

    FrameLayout content;
    TextView no_issues;
    RecyclerView issue_list;
    IssuesAdapter issuesAdapter;
    List<Issues> currentIssueList=new LinkedList<>();



    LinearLayout add_issue;

    private String shipmentID;



    public static IssueList newInstance( ) {
        IssueList fragment = new IssueList();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_list_issues, container, false);

        shipmentID=getActivity().getIntent().getStringExtra("Shipment_ID");
        Log.i("ABCDS",shipmentID+"yritro");
        shipmentID_txtview=(TextView)view.findViewById(R.id.shipment_id);
        shipmentID_txtview.setText(shipmentID);
        prev_shipment=(ImageButton) view.findViewById(R.id.prev_shipment);
        prev_shipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAnotherShipment(false);
                shipmentID_txtview.setText(shipmentID);
                currentIssueList=MainActivity.issueList.get(shipmentID);
                if(currentIssueList!=null) {
                    no_issues.setVisibility(GONE);
                    issue_list.setVisibility(VISIBLE);
                    if(issuesAdapter!=null) {
                        issuesAdapter = new IssuesAdapter(getActivity(), currentIssueList, new IssuesAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int pos) {
                                openIssue(currentIssueList.get(pos));
                            }
                        });
                        issue_list.setAdapter(issuesAdapter);
                    }else{
                        issuesAdapter = new IssuesAdapter(getActivity(), currentIssueList, new IssuesAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int pos) {
                                openIssue(currentIssueList.get(pos));
                            }
                        });
                        issue_list.setAdapter(issuesAdapter);
                    }
                }else{
                    no_issues.setVisibility(VISIBLE);
                    issue_list.setVisibility(GONE);
                }
            }
        });

        next_shipment=(ImageButton)view.findViewById(R.id.next_shipment);
        next_shipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAnotherShipment(true);
                shipmentID_txtview.setText(shipmentID);
                currentIssueList=MainActivity.issueList.get(shipmentID);
                if(currentIssueList!=null) {
                    no_issues.setVisibility(GONE);
                    issue_list.setVisibility(VISIBLE);
                    if(issuesAdapter!=null) {
                        issuesAdapter = new IssuesAdapter(getActivity(), currentIssueList, new IssuesAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int pos) {
                                openIssue(currentIssueList.get(pos));
                            }
                        });
                        issue_list.setAdapter(issuesAdapter);
                    }else{
                        issuesAdapter = new IssuesAdapter(getActivity(), currentIssueList, new IssuesAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int pos) {
                                openIssue(currentIssueList.get(pos));
                            }
                        });
                        issue_list.setAdapter(issuesAdapter);
                    }
                }else{
                    no_issues.setVisibility(VISIBLE);
                    issue_list.setVisibility(GONE);
                }
            }
        });


        content=(FrameLayout)view.findViewById(R.id.content);
        issue_list = (RecyclerView) view.findViewById(R.id.issue_list);
        issue_list.setLayoutManager(new GridLayoutManager(getActivity(),1));

        no_issues=(TextView)view.findViewById(R.id.no_issue_text);
        currentIssueList=MainActivity.issueList.get(shipmentID);
        if(currentIssueList!=null) {
            no_issues.setVisibility(GONE);
            issue_list.setVisibility(VISIBLE);
            issuesAdapter = new IssuesAdapter(getActivity(), currentIssueList, new IssuesAdapter.OnItemClickListener() {
                @Override
                public void onClick(int pos) {
                    openIssue(currentIssueList.get(pos));
                }
            });
            issue_list.setAdapter(issuesAdapter);
        }else{
            no_issues.setVisibility(VISIBLE);
            issue_list.setVisibility(GONE);
        }


        add_issue=(LinearLayout)view.findViewById(R.id.add_issue);
        add_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), IssueVideoActivity.class);
                intent.putExtra("Shipment_ID",shipmentID);
                startActivity(intent);
            }
        });

        return view;
    }


    void  getAnotherShipment(boolean next){
        String prev_ID=null;
        String ID=null;
        int pos=0;
        List<ShipmentItem>shipmentItems=AddNewTruck_1.current_truck.getShipmentItems();
        for(ShipmentItem  item : shipmentItems){
            Log.i("ABCDE", item.getId());
            if(item.getId().equalsIgnoreCase(shipmentID)){
                if(!next){
                    pos=shipmentItems.indexOf(item);
                    if(pos!=0) {
                        shipmentID = prev_ID;
                    }else{
                        shipmentID=shipmentItems.get(shipmentItems.size()-1).getId();
                    }
                }else{
                    pos=shipmentItems.indexOf(item);
                    if(pos<shipmentItems.size()-1){
                        shipmentID=shipmentItems.get(pos+1).getId();
                    }else{
                        shipmentID=shipmentItems.get(0).getId();
                    }
                }
                break;
            }
            prev_ID=item.getId();
        }

    }

    Dialog issue_details;
    boolean isPlayClicked=false;
    AudioManager audioManager;
    int previousVolume=0;
    void openIssue(final Issues issue){

        issue_details=new Dialog(getContext(), R.style.MyDialogTheme);
        issue_details.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        issue_details.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        issue_details.setContentView(R.layout.issue_details_dialog);

        final IssueDetailsHolder holder=new IssueDetailsHolder(issue_details);

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issue_details.dismiss();
            }
        });

        if(issue.getIssueDescriptionType()==0){
            holder.issue_video_preview.setVisibility(VISIBLE);
            holder.issue_image.setVisibility(GONE);
            setupVideoPlayer(holder.issue_video,issue.getUri());
            holder.issue_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
                    holder.play_video.setVisibility(View.VISIBLE);
                    isPlayClicked=false;
                }
            });
        }else if(issue.getIssueDescriptionType()==1){
            holder.issue_video_preview.setVisibility(GONE);
            holder.issue_image.setVisibility(VISIBLE);
            Glide.with(getContext()).load(issue.getUri()).into(holder.issue_image);
        }else if(issue.getIssueDescriptionType()==2){
            holder.issue_video_preview.setVisibility(GONE);
            holder.issue_image.setVisibility(GONE);
        }

        if(issue.getIssueDescriptionShort()!=null){
            holder.short_desc.setText(issue.getIssueDescriptionShort());
        }
        if(issue.getIssueDescription()!=null){
            holder.desc.setText(issue.getIssueDescription());
        }

        holder.play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.play_video.setVisibility(View.INVISIBLE);
                playVideo(holder.issue_video, issue.getUri());
            }
        });

        holder.issue_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
                holder.play_video.setVisibility(View.VISIBLE);
                isPlayClicked=false;
            }
        });




        issue_details.show();
    }


    private void updateBrightness() {
        WindowManager.LayoutParams layoutpars = issue_details.getWindow().getAttributes();
        layoutpars.screenBrightness = 1f;
        issue_details.getWindow().setAttributes(layoutpars);
    }


    private void playVideo(final VideoView mVideoView, String videoPath) {
        isPlayClicked = true;
        updateBrightness();
        mVideoView.setVideoPath(videoPath);
        audioManager=(AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
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


    }

    private void setupVideoPlayer(VideoView mVideoView,String videoPath) {
        isPlayClicked = false;
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

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




    class IssueDetailsHolder{

        ImageButton cancel;

        FrameLayout issue_details;

        RelativeLayout issue_video_preview;
        VideoView issue_video;
        LinearLayout play_video;
        ImageView issue_image;

        TextView short_desc;
        TextView desc;

        IssueDetailsHolder(Dialog dialog){
            cancel=(ImageButton)dialog.findViewById(R.id.cancel);

            issue_details=(FrameLayout)dialog.findViewById(R.id.issue_content);
            issue_video_preview=(RelativeLayout)dialog.findViewById(R.id.issue_video_preview);
            issue_video=(VideoView)dialog.findViewById(R.id.issue_video);
            play_video=(LinearLayout) dialog.findViewById(R.id.play_layout);
            issue_image=(ImageView)dialog.findViewById(R.id.issue_image);

            short_desc=(TextView)dialog.findViewById(R.id.short_desc);
            desc=(TextView)dialog.findViewById(R.id.desc);
        }
    }



}
