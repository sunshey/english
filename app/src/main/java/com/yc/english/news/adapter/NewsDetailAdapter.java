package com.yc.english.news.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.comm_recyclviewadapter.BaseAdapter;
import com.example.comm_recyclviewadapter.BaseViewHolder;
import com.yc.english.R;
import com.yc.english.news.bean.NewsInfo;
import com.yc.english.news.view.activity.NewsDetailActivity;

import java.util.List;

/**
 * Created by wanglin  on 2017/9/6 09:55.
 */

public class NewsDetailAdapter extends BaseAdapter<NewsInfo> {
    public NewsDetailAdapter(Context context, List<NewsInfo> mList) {
        super(context, mList);
    }

    @Override
    protected void convert(BaseViewHolder holder, int position) {
        final NewsInfo newsInfo = mList.get(position);
        holder.setText(R.id.mTextViewTitle, newsInfo.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                intent.putExtra("newsInfo", newsInfo);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (mContext instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) mContext;
                    activity.finish();
                }
            }
        });
    }

    @Override
    public int getLayoutID(int viewType) {
        return R.layout.news_detail_item;
    }
}