package com.yc.english.news.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;


import com.chad.library.adapter.base.BaseViewHolder;
import com.yc.english.R;
import com.yc.english.base.helper.GlideHelper;
import com.yc.english.news.view.activity.NewsDetailActivity;
import com.yc.english.weixin.model.domain.CourseInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by wanglin  on 2017/9/6 09:55.
 */

public class NewsDetailAdapter extends BaseQuickAdapter<CourseInfo, BaseViewHolder> {
    public NewsDetailAdapter(List<CourseInfo> mList) {
        super(R.layout.index_aritle_item, mList);
    }


    @Override
    protected void convert(BaseViewHolder helper, final CourseInfo item) {


        int position = helper.getAdapterPosition();
        long addTime = Long.parseLong(item.getAdd_time()) * 1000;
        helper.setText(R.id.tv_time, TimeUtils.millis2String(addTime, new SimpleDateFormat("yyyy-MM-dd " +
                "HH:mm:ss",
                Locale.getDefault())));
        helper.setText(R.id.tv_title, item.getTitle());
        GlideHelper.imageView(mContext, (ImageView) helper.getView(R.id.iv_icon), item.getImg(), 0);
        helper.setVisible(R.id.iv_microclass_type, false);

        if (getData().size() - 1 == position) {
            helper.setVisible(R.id.line, false);
        }

        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                intent.putExtra("info", getData().get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (mContext instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) mContext;
                    activity.finish();
                }
            }
        });
    }
}
