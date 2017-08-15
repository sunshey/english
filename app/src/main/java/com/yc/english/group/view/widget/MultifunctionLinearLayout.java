package com.yc.english.group.view.widget;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.yc.english.R;
import com.yc.english.group.constant.GroupConstant;
import com.yc.english.group.model.bean.Voice;
import com.yc.english.group.view.adapter.GroupFileAdapter;
import com.yc.english.group.view.adapter.GroupPictureAdapter;
import com.yc.english.group.view.adapter.GroupTaskSolePicAdapter;
import com.yc.english.group.view.adapter.GroupVoiceAdapter;

import java.util.List;

import butterknife.BindView;
import io.rong.imkit.model.FileInfo;


/**
 * Created by wanglin  on 2017/8/9 11:20.
 */

public class MultifunctionLinearLayout extends LinearLayout {

    private Context mContext;
    private TextView textView;
    private View pictureView;
    private View voiceView;
    private View wordView;
    private View synthesizeView;

    private int currentType = -1;

    private LayoutInflater inflater;
    private GroupVoiceAdapter groupVoiceAdapter;
    private GroupFileAdapter groupFileAdapter;
    private GroupPictureAdapter groupPictureAdapter;

    private List<FileInfo> fileInfos;
    private List<String> uriList;
    private List<Voice> voices;
    private String text;

    public MultifunctionLinearLayout(Context context) {
        this(context, null);
    }

    public MultifunctionLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultifunctionLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        inflater = LayoutInflater.from(mContext);

    }

    public void showTextView() {
        currentType = GroupConstant.TASK_TYPE_CHARACTER;
        if (null == textView) {
            textView = new TextView(mContext);
            textView.setText(getText());
            textView.setTextColor(mContext.getResources().getColor(R.color.black_333333));
            textView.setTextSize(14);
            MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
            layoutParams.leftMargin = 15;
            layoutParams.topMargin = 15;
            layoutParams.rightMargin = 15;
            layoutParams.bottomMargin = 15;
            textView.setLayoutParams(layoutParams);
            addView(textView);
        }
    }


    public void showPictureView() {
        currentType = GroupConstant.TASK_TYPE_PICTURE;
        if (null == pictureView) {
            pictureView = inflater.inflate(R.layout.group_publish_task_detail_picture, null);
            RecyclerView recyclerView = (RecyclerView) pictureView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            groupPictureAdapter = new GroupPictureAdapter(mContext, false, null);
            recyclerView.setAdapter(groupPictureAdapter);


            addView(pictureView);
        }

    }

    public void showVoiceView() {
        currentType = GroupConstant.TASK_TYPE_VOICE;
        if (null == voiceView) {
            voiceView = inflater.inflate(R.layout.group_publish_task_detail_picture, null);
            RecyclerView recyclerView = (RecyclerView) voiceView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            groupVoiceAdapter = new GroupVoiceAdapter(mContext, false, null);
            recyclerView.setAdapter(groupVoiceAdapter);
            addView(voiceView);
        }
    }

    public void showWordView() {
        currentType = GroupConstant.TASK_TYPE_WORD;
        if (null == wordView) {
            wordView = inflater.inflate(R.layout.group_publish_task_detail_picture, null);
            RecyclerView recyclerView = (RecyclerView) wordView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            groupFileAdapter = new GroupFileAdapter(mContext, false, null);

            recyclerView.setAdapter(groupFileAdapter);

            addView(wordView);

        }
    }

    public void showSynthesizeView() {
        currentType = GroupConstant.TASK_TYPE_SYNTHESIZE;
        if (null == synthesizeView) {
            synthesizeView = inflater.inflate(R.layout.group_publish_task_detail_synthesis, null);
            TextView textView = (TextView) synthesizeView.findViewById(R.id.m_et_issue_task);
            RecyclerView pictureRecycleView = (RecyclerView) synthesizeView.findViewById(R.id.recyclerView_picture);
            RecyclerView voiceRecycleView = (RecyclerView) synthesizeView.findViewById(R.id.voice_recyclerView);
            RecyclerView fileRecycleView = (RecyclerView) synthesizeView.findViewById(R.id.file_recyclerView);

            groupPictureAdapter = new GroupPictureAdapter(mContext, false, null);
            pictureRecycleView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            pictureRecycleView.setAdapter(groupPictureAdapter);

            groupVoiceAdapter = new GroupVoiceAdapter(mContext, false, null);
            voiceRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
            voiceRecycleView.setAdapter(groupVoiceAdapter);


            groupFileAdapter = new GroupFileAdapter(mContext, false, null);
            fileRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
            fileRecycleView.setAdapter(groupFileAdapter);

            textView.setText(getText());
            addView(synthesizeView);
        }

    }

    public List<FileInfo> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(List<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
        groupFileAdapter.setData(fileInfos);
    }

    public List<String> getUriList() {
        return uriList;
    }

    public void setUriList(List<String> uriList) {
        this.uriList = uriList;
        if (groupPictureAdapter != null) {
            groupPictureAdapter.setData(uriList);
        }

    }

    public List<Voice> getVoices() {
        return voices;
    }

    public void setVoices(List<Voice> voices) {
        this.voices = voices;
        groupVoiceAdapter.setData(voices);
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}