package com.example.pfeproject.fragments.video;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.pfeproject.R;
import com.example.pfeproject.model.TotalPoint;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoPlayerFragment extends DialogFragment {
    private static final String TAG = "TagVideoFrag";
    private static final String VIDEO_SAMPLE = "http://techslides.com/demos/sample-videos/small.mp4";
    private static String pubId, videoSample, entrId;

    @BindView(R.id.myProgBar)
    AVLoadingIndicatorView myProgBar;
    @BindView(R.id.videoView)
    VideoView videoView;

    private Uri uri;
    private Dialog dialog;
    private String replacedVideoLink = "";
    private int currentPosition = 0;

    public VideoPlayerFragment() {
    }

    public static VideoPlayerFragment display(FragmentManager fragmentManager, Bundle bundle) {
        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
        videoPlayerFragment.setCancelable(false);
        videoPlayerFragment.show(fragmentManager, TAG);
        pubId = bundle.getString("pubId");
        videoSample = bundle.getString("pubVideoLink");
        entrId = bundle.getString("entrId");
        return videoPlayerFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pubVideoLink", videoSample);
        outState.putString("pubId", pubId);
        outState.putString("entrId", entrId);
        outState.putInt("PlayBackTime", videoView.getCurrentPosition());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            videoSample = savedInstanceState.getString("pubVideoLink");
            pubId = savedInstanceState.getString("pubId");
            entrId = savedInstanceState.getString("entrId");
            currentPosition = savedInstanceState.getInt("PlayBackTime",0);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
        initializePlayer();
    }

    private void ReleasePlayer() {
        videoView.stopPlayback();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        ReleasePlayer();
    }

    private void initializePlayer() {
        myProgBar.setVisibility(VideoView.VISIBLE);
        if (videoSample != null)
            replacedVideoLink = videoSample.replace(ApiUrl.hostnameHost, ApiUrl.hostname);

        Uri videoUri = getMedia(replacedVideoLink.isEmpty() ? VIDEO_SAMPLE : replacedVideoLink);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        myProgBar.setVisibility(VideoView.INVISIBLE);
                        Log.d(TAG, "onPrepared: current position ... "+currentPosition);
//                        // Restore saved position, if available.
                        if (currentPosition > 0) {
                            videoView.seekTo(currentPosition);
                        } else {
                            // Skipping to 1 shows the first frame of the video.
                            videoView.seekTo(1);
                        }

                        // Start playing!
                        videoView.start();
                    }
                });

        videoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        videoView.seekTo(0);
                        initPointUserPublicity();

                    }
                });
    }

    private void initPointUserPublicity() {
        SessionManager manager = new SessionManager(getActivity());
//        Log.d(TAG, "initPointUserPublicity: pubId " + pubId + " ... userId : " + manager.getUserId());
        if (!pubId.isEmpty()) {
            Ion.with(getActivity())
                    .load(ApiUrl.POST, ApiUrl.apiAffectPointUserPublicity + manager.getUserId() + "/publicity/" + pubId)
                    .setHeader(Types.ContentType, Types.ContentTypeValue)
                    .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                    .setJsonObjectBody(new JsonObject())
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e != null) {
                                Toast.makeText(getActivity(), "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onCompleted: initPointUserPublicity error = " + e.getMessage());
                            } else {
                                int code = result.getHeaders().code();
                                if (code == 200)
                                {
//                                    Log.d(TAG, "onCompleted: result size "+result);
                                    if(result.getResult().has("earned"))
                                    {
                                        String earned = result.getResult().get("publicity").getAsJsonObject().get("pointToEarn").getAsString();
                                        Toast.makeText(getActivity(), ""+getString(R.string.toast_earn_point)+" "+earned+" points ", Toast.LENGTH_SHORT).show();

                                        JsonArray jsonArrayPoint = result.getResult().get("client").getAsJsonObject().get("totalpointsPerEntreprise").getAsJsonArray();

                                        Gson gson = new Gson();
                                        TotalPoint[] points = gson.fromJson(jsonArrayPoint.toString(),TotalPoint[].class);
                                        ArrayList<TotalPoint> pointArrayList = new ArrayList<>();
                                        Collections.addAll(pointArrayList,points);

                                        manager.saveOrUpdateUserPointList(pointArrayList);

                                    }else
                                        Log.e(TAG, "onCompleted: ! result.has(earned) " );
                                }else {
                                    Log.e(TAG, "onCompleted: code : " + code);
                                    Toast.makeText(getActivity(), ""+getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                                }

                            }
                            if (dialog != null)
                                dialog.dismiss();
                            else
                                Log.e(TAG, "onCompletion: dialog == null ");
                        }
                    });


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ....................................");
    }

    private Uri getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
//            Log.d("TAG", "getMedia: .....valid ");
            // Media name is an external URL.
            return Uri.parse(mediaName);
        } else {
            // you can also put a video file in raw package and get file from there as shown below
            return Uri.parse("android.resource://" + getActivity().getPackageName() +
                    "/raw/video.mp4");//+ mediaName
        }
    }


}