package com.yc.english.speak.view.wdigets;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yc.english.R;

/**
 * Created by wanglin  on 2017/12/6 14:23.
 */

public class DownloadProgressDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View contentView = getActivity().getLayoutInflater().inflate(R.layout.base_speak_download_view, null);
        TextView mTvMessage = contentView.findViewById(R.id.tv_message);
        ProgressBar pb = contentView.findViewById(R.id.progressBar);
        for (int i = 0; i < 100; i++) {
            pb.setProgress(i);
        }
        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("message"))) {
            mTvMessage.setVisibility(View.VISIBLE);
            mTvMessage.setText(getArguments().getString("message"));
        } else mTvMessage.setVisibility(View.GONE);
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//设置Dialog没有标题。需在setContentView之前设置，在之后设置会报错
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//设置Dialog背景透明效果
        dialog.setCancelable(false);
        dialog.setContentView(contentView);

        return dialog;
    }
}
