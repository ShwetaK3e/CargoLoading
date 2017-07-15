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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class IssueList extends Fragment {


    private static final String TAG =IssueList.class.getSimpleName() ;

    ImageButton prev_shipment;
    ImageButton next_shipment;
    TextView shipmentID_txtview;

    FrameLayout content;

    RelativeLayout select_issue_type;
    TextView damage_count,missing_count,weight_change_count;
    LinearLayout damage,missing,weight;
    ImageView damagebtn,missingbtn,weightbtn;

    RelativeLayout missing_layout;
    EditText missing_layout_count;
    int missing_no=0;

    RecyclerView issue_list;
    IssuesAdapter issuesAdapter;
    List<Issues> currentIssueList=new LinkedList<>();
    List<Issues> damageList=new LinkedList<>();
    List<Issues> weightIssueList=new LinkedList<>();

    FloatingActionButton add_issue;


    String IssueType="NO_TYPE";





    private String shipmentID;
    private  String from;
    private boolean skip;
    OnBackPressedListener onBackPressedListener;




    public static IssueList newInstance( OnBackPressedListener onBackPressedListener ) {
        IssueList fragment = new IssueList();
        fragment.onBackPressedListener=onBackPressedListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_list_issues, container, false);

        shipmentID=getActivity().getIntent().getStringExtra("Shipment_ID");
        from=getActivity().getIntent().getStringExtra("From");
        skip=getActivity().getIntent().getBooleanExtra("SKIP",false);


        shipmentID_txtview=(TextView)view.findViewById(R.id.shipment_id);
        shipmentID_txtview.setText(shipmentID);

        prev_shipment=(ImageButton) view.findViewById(R.id.prev_shipment);
        if("item".equalsIgnoreCase(from)){
            prev_shipment.setVisibility(INVISIBLE);
            prev_shipment.setEnabled(false);
        }
        prev_shipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAnotherShipment(false);
                IssueType="NO_TYPE";
                add_issue.setVisibility(INVISIBLE);
                shipmentID_txtview.setText(shipmentID);
                issue_list.setVisibility(INVISIBLE);
                missing_layout.setVisibility(INVISIBLE);
                select_issue_type.setVisibility(VISIBLE);
                setCountsonSelectIssue();
            }
        });

        next_shipment=(ImageButton)view.findViewById(R.id.next_shipment);
        if("item".equalsIgnoreCase(from)){
            next_shipment.setVisibility(INVISIBLE);
            next_shipment.setEnabled(false);
        }
        next_shipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAnotherShipment(true);
                shipmentID_txtview.setText(shipmentID);
                IssueType="NO_TYPE";
                add_issue.setVisibility(INVISIBLE);
                issue_list.setVisibility(INVISIBLE);
                missing_layout.setVisibility(INVISIBLE);
                select_issue_type.setVisibility(VISIBLE);
                setCountsonSelectIssue();
            }
        }
                );




        content=(FrameLayout)view.findViewById(R.id.content);

        //select issue Layout


        select_issue_type=(RelativeLayout)view.findViewById(R.id.select_issue_type);
        damage=(LinearLayout) view.findViewById(R.id.damage_layout);
        damagebtn=(ImageView)view.findViewById(R.id.damage);
        damagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IssueType="damage";
                currentIssueList=damageList;
                if("item".equalsIgnoreCase(from)) {
                    add_issue.setVisibility(VISIBLE);
                }
                select_issue_type.setVisibility(INVISIBLE);
                missing_layout.setVisibility(INVISIBLE);
                issue_list.setVisibility(VISIBLE);
                issue_list.setAdapter(new IssuesAdapter(getActivity(), damageList, new IssuesAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int pos) {
                        openIssue(damageList.get(pos));
                    }
                }));
            }
        });
        damage_count=(TextView)view.findViewById(R.id.damage_issue_nos);
        damage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IssueType="damage";
                currentIssueList=damageList;
                if("item".equalsIgnoreCase(from)) {
                    add_issue.setVisibility(VISIBLE);
                }
                select_issue_type.setVisibility(INVISIBLE);
                missing_layout.setVisibility(INVISIBLE);
                issue_list.setVisibility(VISIBLE);
                issue_list.setAdapter(new IssuesAdapter(getActivity(), damageList, new IssuesAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int pos) {
                        openIssue(damageList.get(pos));
                    }
                }));
            }
        });
        missing=(LinearLayout) view.findViewById(R.id.missing_layout);
        missingbtn=(ImageView)view.findViewById(R.id.missing);
        missing_count=(TextView)view.findViewById(R.id.missing_issue_nos);
        missingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueType="missing";
                if("item".equalsIgnoreCase(from)) {
                    add_issue.setVisibility(VISIBLE);
                }
                select_issue_type.setVisibility(INVISIBLE);
                missing_layout.setVisibility(View.VISIBLE);
                missing_layout_count.setText(String.valueOf(missing_no));
                issue_list.setVisibility(INVISIBLE);
            }
        });
        missing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueType="missing";
                if("item".equalsIgnoreCase(from)) {
                    add_issue.setVisibility(VISIBLE);
                }

                select_issue_type.setVisibility(INVISIBLE);
                missing_layout.setVisibility(View.VISIBLE);
                missing_layout_count.setText(String.valueOf(missing_no));
                issue_list.setVisibility(INVISIBLE);

            }
        });


        weight=(LinearLayout) view.findViewById(R.id.weight_layout);
        weightbtn=(ImageView)view.findViewById(R.id.weight);
        weight_change_count=(TextView)view.findViewById(R.id.weight_issue_nos);
        weightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueType="weight";
                currentIssueList=weightIssueList;
                if("item".equalsIgnoreCase(from)) {
                    add_issue.setVisibility(VISIBLE);
                }
                select_issue_type.setVisibility(INVISIBLE);
                missing_layout.setVisibility(INVISIBLE);
                issue_list.setVisibility(VISIBLE);
                issue_list.setAdapter(new IssuesAdapter(getActivity(),weightIssueList, new IssuesAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int pos) {
                        openIssue(weightIssueList.get(pos));
                    }
                }));
            }
        });
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueType="weight";
                currentIssueList=weightIssueList;
                if("item".equalsIgnoreCase(from)) {
                    add_issue.setVisibility(VISIBLE);
                }
                select_issue_type.setVisibility(INVISIBLE);
                missing_layout.setVisibility(INVISIBLE);
                issue_list.setVisibility(VISIBLE);
                issue_list.setAdapter(new IssuesAdapter(getActivity(),weightIssueList, new IssuesAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int pos) {
                        openIssue(weightIssueList.get(pos));
                    }
                }));
            }
        });



        setCountsonSelectIssue();


        //missing
        missing_layout=(RelativeLayout) view.findViewById(R.id.missing_category_layout);
        missing_layout.setVisibility(INVISIBLE);
        missing_layout_count=(EditText)view.findViewById(R.id.missing_count);
        if(!"item".equalsIgnoreCase(from)){
            missing_layout_count.setEnabled(false);
        }
        missing_layout_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<ShipmentItem> items = AddNewTruck_1.current_truck.getShipmentItems();
                items.remove(TruckDetails_1.current_item);

                int newCount;
                if(s.length()!=0) {
                    newCount=Integer.parseInt(s.toString());
                    if(newCount<0 || newCount +TruckDetails_1.current_item.getLoadedCount()> TruckDetails_1.current_item.getShippedItemCount()){
                        missing_no=TruckDetails_1.current_item.getMissing_count();
                        missing_layout_count.setText(String.valueOf(missing_no));
                        missing_layout_count.setSelection(missing_layout_count.getText().toString().length());
                        Toast.makeText(getActivity(), "Put in Appropriate Count", Toast.LENGTH_SHORT).show();
                    }else {
                        missing_layout_count.setSelection(missing_layout_count.getText().toString().length());
                        missing_no=newCount;
                        TruckDetails_1.current_item.setMissing_count(newCount);
                        if(TruckDetails_1.current_item.getLoadedCount()+TruckDetails_1.current_item.getMissing_count()==TruckDetails_1.current_item.getShippedItemCount()){
                            add_issue.setEnabled(false);
                            add_issue.setBackgroundResource(R.color.white);
                        }else if(newCount+TruckDetails_1.current_item.getMissing_count()<TruckDetails_1.current_item.getShippedItemCount()) {
                            add_issue.setEnabled(true);
                            add_issue.setBackgroundResource(R.color.colorPrimary);
                        }

                        items.add(TruckDetails_1.current_item);
                        AddNewTruck_1.current_truck.setShipmentItems((LinkedList) items);
                    }

                }

            }
        });



        //List for damage and weight
        issue_list = (RecyclerView) view.findViewById(R.id.issue_list);
        issue_list.setVisibility(INVISIBLE);
        issue_list.setLayoutManager(new GridLayoutManager(getActivity(),1));


        //floating button
        add_issue=(FloatingActionButton)view.findViewById(R.id.add_issue);
        add_issue.setVisibility(INVISIBLE);
        add_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"missing".equalsIgnoreCase(IssueType)) {
                    Intent intent = new Intent(getActivity(), IssueVideoActivity.class);
                    intent.putExtra("Shipment_ID", shipmentID);
                    intent.putExtra("IssueType", IssueType);
                    startActivity(intent);
                }else{
                    missing_no ++;
                    missing_layout_count.setText(String.valueOf(missing_no));
                    List<ShipmentItem> items=AddNewTruck_1.current_truck.getShipmentItems();
                    items.remove(TruckDetails_1.current_item);
                    TruckDetails_1.current_item.setMissing_count(missing_no);
                    items.add(TruckDetails_1.current_item);
                    AddNewTruck_1.current_truck.setShipmentItems((LinkedList)items);
                }

            }
        });


        if(currentIssueList!=null && currentIssueList.size()!=0) {

            issue_list.setVisibility(VISIBLE);
            issuesAdapter = new IssuesAdapter(getActivity(), currentIssueList, new IssuesAdapter.OnItemClickListener() {
                @Override
                public void onClick(int pos) {
                    openIssue(currentIssueList.get(pos));
                }
            });
            issue_list.setAdapter(issuesAdapter);
        }else{
            issue_list.setVisibility(GONE);
        }


       /* add_issue=(LinearLayout)view.findViewById(R.id.add_issue);
        add_issue.setVisibility(INVISIBLE);
        add_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"missing".equalsIgnoreCase(IssueType)) {
                    Intent intent = new Intent(getActivity(), IssueVideoActivity.class);
                    intent.putExtra("Shipment_ID", shipmentID);
                    intent.putExtra("IssueType", IssueType);
                    startActivity(intent);
                }else{
                     missing_no ++;
                     missing_layout_count.setText(String.valueOf(missing_no));
                     List<ShipmentItem> items=AddNewTruck_1.current_truck.getShipmentItems();
                     items.remove(TruckDetails_1.current_item);
                     TruckDetails_1.current_item.setMissing_count(missing_no);
                     items.add(TruckDetails_1.current_item);
                     AddNewTruck_1.current_truck.setShipmentItems((LinkedList)items);


                }
            }
        });*/

       if(skip) {

           if (select_issue_type.getVisibility() == VISIBLE) {
               Intent i = new Intent(getActivity(), MainActivity.class);
               i.putExtra("Activity", "TRUCK_DETAILS_1");
               startActivity(i);
           }else{
               missing_layout.setVisibility(INVISIBLE);
               issue_list.setVisibility(INVISIBLE);
           }

       }

        return view;
    }










    void  getAnotherShipment(boolean next){
        String prev_ID=null;
        String ID=null;
        int pos=0;
        List<ShipmentItem>shipmentItems=AddNewTruck_1.current_truck.getLoadedItems();
        for(ShipmentItem  item : shipmentItems){
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

    void  setCountsonSelectIssue(){

        List<ShipmentItem> items=AddNewTruck_1.current_truck.getShipmentItems();
        for(ShipmentItem item:items){
            if(shipmentID.equalsIgnoreCase(item.getId())){
                damageList=item.getDamaged_list();
                weightIssueList=item.getWeight_list();
                missing_no=item.getMissing_count();
                damage_count.setText(String.valueOf(damageList.size()));
                weight_change_count.setText(String.valueOf(weightIssueList.size()));
                missing_count.setText(String.valueOf(missing_no));

                break;
            }
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
                    holder.play_layout.setVisibility(View.VISIBLE);
                    isPlayClicked=false;
                }
            });
        }else if(issue.getIssueDescriptionType()==1){
            holder.issue_video_preview.setVisibility(GONE);
            holder.issue_image.setVisibility(VISIBLE);
            Glide.with(getContext()).load(issue.getUri()).into(holder.issue_image);
        }else if(issue.getIssueDescriptionType()==2){
            holder.issue_details.setVisibility(GONE);
            holder.issue_video_preview.setVisibility(GONE);
            holder.issue_image.setVisibility(GONE);
        }

        if(issue.getIssueType()==0){
            holder.short_desc.setText("DAMAGE");
        }else if(issue.getIssueType()==1){
            holder.short_desc.setText("MISSING");
        }else if(issue.getIssueType()==2){
            holder.short_desc.setText("WEIGHT CHANGE" + " ( "+TruckDetails_1.current_item.getBookedItem().getActualWeight()+"kgs )");
        }
        if(issue.getIssueDescription()!=null){
            holder.desc.setText(issue.getIssueDescription());
        }

        holder.play_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.play_layout.setVisibility(INVISIBLE);
                playVideo(holder.issue_video, issue.getUri());
            }
        });
        holder.play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.play_layout.setVisibility(INVISIBLE);
                playVideo(holder.issue_video, issue.getUri());
            }
        });

        holder.issue_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
                holder.play_layout.setVisibility(View.VISIBLE);
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
        LinearLayout play_layout;
        ImageButton play_video;

        ImageView issue_image;

        TextView short_desc;
        TextView desc;

        IssueDetailsHolder(Dialog dialog){
            cancel=(ImageButton)dialog.findViewById(R.id.cancel);

            issue_details=(FrameLayout)dialog.findViewById(R.id.issue_content);
            issue_video_preview=(RelativeLayout)dialog.findViewById(R.id.issue_video_preview);
            issue_video=(VideoView)dialog.findViewById(R.id.issue_video);
            play_layout=(LinearLayout) dialog.findViewById(R.id.play_layout);
            play_video=(ImageButton) dialog.findViewById(R.id.play_video);
            issue_image=(ImageView)dialog.findViewById(R.id.issue_image);

            short_desc=(TextView)dialog.findViewById(R.id.short_desc);
            desc=(TextView)dialog.findViewById(R.id.desc);
        }
    }

    public interface OnBackPressedListener{
        void onBack();
    }




}
