package com.yc.english.read.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yc.english.R;
import com.yc.english.read.model.domain.BookInfo;

import java.util.List;


public class ReadBookItemClickAdapter extends BaseMultiItemQuickAdapter<BookInfo, BaseViewHolder> {

    private Context mContext;

    private boolean isEdit = false;

    public ReadBookItemClickAdapter(Context mContext, List<BookInfo> data) {
        super(data);
        this.mContext = mContext;
        addItemType(BookInfo.CLICK_ITEM_VIEW, R.layout.read_book_item);
    }

    public boolean getEditState() {
        return this.isEdit;
    }

    public void setEditState(boolean editState) {
        this.isEdit = editState;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final BookInfo item) {

        helper.addOnClickListener(R.id.iv_delete_book);

        ImageView mBookCover = (ImageView) helper.getView(R.id.iv_book_cover);
        ImageView mDeleteBook = (ImageView) helper.getView(R.id.iv_delete_book);

        if (helper.getAdapterPosition() > 0) {
            mBookCover.setImageResource(R.mipmap.read_book_test);
        } else {
            mBookCover.setImageResource(R.mipmap.read_book_add);
        }

        if (this.isEdit) {
            mDeleteBook.setVisibility(View.VISIBLE);
        } else {
            mDeleteBook.setVisibility(View.INVISIBLE);
        }
    }

}