package com.yc.english.intelligent.view.adpaters

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yc.english.R
import com.yc.english.base.helper.GlideHelper
import com.yc.english.intelligent.model.domain.QuestionInfoWrapper

/**
 * Created by zhangkai on 2017/12/4.
 */
class IntelligentOptionsAdapter(val questionInfo: QuestionInfoWrapper.QuestionInfo?, val isResult: Boolean) :
        BaseQuickAdapter<String,
                BaseViewHolder>(R.layout
                .intelligent_item_options) {

    override fun convert(helper: BaseViewHolder?, item: String?) {
        if ((questionInfo?.options?.type ?: "text") == "text") {
            helper?.getView<TextView>(R.id.tv_answer_text)?.visibility = View.VISIBLE
            helper?.getView<ImageView>(R.id.iv_answer_image)?.visibility = View.GONE
            helper?.setText(R.id.tv_answer_text, item)
        } else {
            helper?.getView<TextView>(R.id.tv_answer_text)?.visibility = View.GONE
            helper?.getView<ImageView>(R.id.iv_answer_image)?.visibility = View.VISIBLE
            GlideHelper.imageView(mContext, helper?.getView<ImageView>(R.id.iv_answer_image), item, R.mipmap.default_book)
        }

        if (TextUtils.isEmpty(questionInfo?.userAnswer)) {
            questionInfo?.userAnswer = SPUtils.getInstance().getString("userAnswer${questionInfo?.id}", "")
        }

        val cAnswer = (65 + helper?.adapterPosition!!).toChar().toString()
        helper?.setText(R.id.tv_answer, cAnswer)


        if (!isResult) {
            helper?.getView<TextView>(R.id.tv_answer).isSelected = (questionInfo?.userAnswer == cAnswer)
        } else {
            if(questionInfo?.userAnswer == cAnswer){
                if (questionInfo?.userAnswer != "" && questionInfo?.userAnswer == questionInfo?.answer) {
                    helper?.getView<TextView>(R.id.tv_answer).setTextColor(Color.WHITE)
                    helper?.setBackgroundRes(R.id.tv_answer, R.drawable.intelligent_answer_right)
                } else {
                    helper?.getView<TextView>(R.id.tv_answer).setTextColor(Color.WHITE)
                    helper?.setBackgroundRes(R.id.tv_answer, R.drawable.intelligent_answer_error)
                }
            } else {
                helper?.getView<TextView>(R.id.tv_answer).isSelected = false
            }
        }
    }
}