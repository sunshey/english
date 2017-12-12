package com.yc.english.intelligent.view.activitys

import android.content.Intent
import android.os.Parcelable
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.jakewharton.rxbinding.view.RxView
import com.yc.english.R
import com.yc.english.base.model.BaseEngin
import com.yc.english.base.presenter.BasePresenter
import com.yc.english.base.utils.SimpleCacheUtils
import com.yc.english.base.utils.StatusBarCompat
import com.yc.english.base.view.BaseActivity
import com.yc.english.base.view.IView
import com.yc.english.intelligent.model.domain.QuestionInfoWrapper
import com.yc.english.intelligent.view.adpaters.IntelligentHandInAdapter
import kotlinx.android.synthetic.main.intelligent_avtivity_hand_in.*
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * Created by zhangkai on 2017/11/28.
 */
class IntelligentHandInActivity : BaseActivity<BasePresenter<BaseEngin, IView>>() {

    var adapter: IntelligentHandInAdapter? = null
    var questionInfos: MutableList<QuestionInfoWrapper.QuestionInfo>? = null

    override fun init() {
        mToolbar.mIndexTextView.visibility = View.GONE
        StatusBarCompat.compat(this, mToolbar, mToolbar.mToolbar, mToolbar.mStatubar)
        RxView.clicks(mToolbar.mBackBtn).throttleFirst(200, TimeUnit
                .MILLISECONDS).subscribe {
            finish()
        }

        RxView.clicks(mSubmitBtn).throttleFirst(200, TimeUnit
                .MILLISECONDS).subscribe {
            finish()
            SimpleCacheUtils.writeCache(this, "result${IntelligentQuestionsActivity.getInstance()?.unitId}${IntelligentQuestionsActivity.getInstance()?.type}", JSON
                    .toJSONString(questionInfos))
            SPUtils.getInstance().put("unitInfo-complete-time-${IntelligentQuestionsActivity.getInstance()?.unitId}${IntelligentQuestionsActivity.getInstance()?.type}", mToolbar.mTimeTextView.text.toString())
            val intent = Intent(this@IntelligentHandInActivity, IntelligentResultActivity::class.java)
            intent.putParcelableArrayListExtra("questionInfos", questionInfos as ArrayList<out Parcelable>)
            startActivity(intent)
        }

        mToolbar.mTimeTextView.text = IntelligentQuestionsActivity.getInstance()?.usedTime() ?: ""
        var actIndex = 0
        var frgIndex = 0
        questionInfos = ArrayList<QuestionInfoWrapper.QuestionInfo>()
        for (questionInfo in IntelligentQuestionsActivity.getInstance()?.questionInfos!!) {
            questionInfo.actIndex = actIndex
            questionInfos!!.add(questionInfo)
            if (questionInfo.data != null) {
                for (questionInfo2 in questionInfo.data!!) {
                    questionInfo2.frgIndex = frgIndex++
                    questionInfo2.actIndex = actIndex
                    questionInfos!!.add(questionInfo2)
                }
            }
            actIndex++
            frgIndex = 0
        }
        adapter = IntelligentHandInAdapter(questionInfos!!)
        val gridLayoutManager = GridLayoutManager(this, 5)
        gridLayoutManager.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter!!.getItemViewType(position) == 0) 5 else 1
            }
        })
        mRecyclerView.adapter = adapter
        mRecyclerView.layoutManager = gridLayoutManager

        adapter!!.setOnItemClickListener { adapter, view, position ->
            if (adapter.getItemViewType(position) == 0) return@setOnItemClickListener
            val questionInfo = adapter.data.get(position) as QuestionInfoWrapper.QuestionInfo
            IntelligentQuestionsActivity.getInstance()?.next(questionInfo.actIndex, questionInfo.frgIndex)
            finish()
        }
    }


    override fun getLayoutId() = R.layout.intelligent_avtivity_hand_in

}