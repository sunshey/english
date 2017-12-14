package com.yc.english.intelligent.view.fragments

import android.content.Intent
import android.support.v4.content.ContextCompat
import com.blankj.utilcode.util.SPUtils
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.jakewharton.rxbinding.view.RxView
import com.yc.english.R
import com.yc.english.base.view.BaseFragment
import com.yc.english.intelligent.model.domain.UnitInfoWrapper
import com.yc.english.intelligent.presenter.IntelligentPresenter
import com.yc.english.intelligent.view.activitys.IntelligentQuestionsActivity
import com.yc.english.intelligent.view.activitys.IntelligentReportActivity
import com.yc.english.intelligent.view.wdigets.IntelligentTypeItemView
import com.yc.english.main.hepler.UserInfoHelper
import com.yc.english.main.model.domain.Constant
import com.yc.english.main.view.activitys.LoginActivity
import com.yc.english.speak.view.activity.QuestionActivity
import kotlinx.android.synthetic.main.intelligent_fragment_index.*
import java.util.concurrent.TimeUnit


/**
 * Created by zhangkai on 2017/11/27.
 */

class IntelligentFragment : BaseFragment<IntelligentPresenter>() {
    init {
        isUseInKotlin = true
    }

    var unitInfo: UnitInfoWrapper.UnitInfo? = null

    override fun init() {
        RxView.clicks(mReportBtn).throttleFirst(200, TimeUnit
                .MILLISECONDS).subscribe {
            var intent = Intent(activity, IntelligentReportActivity::class.java)
            intent.putExtra("unitId", unitInfo?.id)
            startActivity(intent)
        }

        if (unitInfo != null) {
            mTitleTextView.text = unitInfo?.name
            initIntelligentTypeEvents(mIntelligentType1, mIntelligentType2, mIntelligentType3, mIntelligentType4,
                    mIntelligentType5, mIntelligentType6)
        }
    }

    private fun setIntelligentTypeEvent(mIntelligentType: IntelligentTypeItemView) {
        mIntelligentType.post {
            RxView.clicks(mIntelligentType.mDoTextView).throttleFirst(200, TimeUnit
                    .MILLISECONDS).subscribe {
                if (!UserInfoHelper.isLogin()) {
                    startActivity(Intent(context, LoginActivity::class.java))
                    return@subscribe
                }

                var type = ""
                when (mIntelligentType.tag) {
                    1 -> type = "vocabulary"
                    2 -> type = "oracy"
                    3 -> type = "grammar"
                    4 -> type = "hearing"
                    5 -> type = "read"
                    6 -> type = "writing"
                }
                var intent: Intent
                if (type == "oracy") {
                    intent = Intent(activity, QuestionActivity::class.java)
                } else {
                    intent = Intent(activity, IntelligentQuestionsActivity::class.java)
                }
                intent.putExtra("unitId", unitInfo?.id)
                intent.putExtra("type", type)
                intent.putExtra("isResultIn", mIntelligentType.complete)
                startActivity(intent)
            }
        }
    }

    private fun checkAllComplete(vararg intelligentTypes: IntelligentTypeItemView) {
        var flag = true
        for (intelligentType in intelligentTypes) {
            if (!intelligentType.complete) {
                flag = false
                break
            }
        }
        if (flag) {
            mReportBtn.isClickable = true
            mReportBtn.text = "查看测评报告"
            mReportBtn.setTextColor(ContextCompat.getColor(context!!, R.color.primary))
        } else {
            mReportBtn.isClickable = false
            mReportBtn.text = "完成所有练习,可查看测评报告"
            mReportBtn.setTextColor(ContextCompat.getColor(context!!, R.color.gray_999))
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag(Constant.RESULT_IN)))
    fun complete(tag: String) {
        initIntelligentTypeEvents(mIntelligentType1, mIntelligentType2, mIntelligentType3, mIntelligentType4,
                mIntelligentType5, mIntelligentType6)
    }


    private fun initIntelligentTypeEvents(vararg intelligentTypes: IntelligentTypeItemView) {
        mIntelligentType1.complete = if (unitInfo?.unit_finish_detail?.vocabulary == 1 || SPUtils.getInstance().getInt
        ("finish-unitId${unitInfo?.id}vocabulary", 0) == 1)
            true else false
        mIntelligentType2.complete = if (unitInfo?.unit_finish_detail?.oracy == 1 || SPUtils.getInstance().getInt
        ("finish-unitId${unitInfo?.id}oracy", 0) == 1)
            true else false
        mIntelligentType3.complete = if (unitInfo?.unit_finish_detail?.grammar == 1 || SPUtils.getInstance().getInt
        ("finish-unitId${unitInfo?.id}grammar", 0) == 1)
            true else false
        mIntelligentType4.complete = if (unitInfo?.unit_finish_detail?.hearing == 1 || SPUtils.getInstance().getInt
        ("finish-unitId${unitInfo?.id}hearing", 0) == 1)
            true else false
        mIntelligentType5.complete = if (unitInfo?.unit_finish_detail?.read == 1 || SPUtils.getInstance().getInt
        ("finish-unitId${unitInfo?.id}read", 0) == 1)
            true else false
        mIntelligentType6.complete = if (unitInfo?.unit_finish_detail?.writing == 1 || SPUtils.getInstance().getInt
        ("finish-unitId${unitInfo?.id}writing", 0) == 1)
            true else false
        checkAllComplete(mIntelligentType1, mIntelligentType2, mIntelligentType3, mIntelligentType4,
                mIntelligentType5, mIntelligentType6)
        var tag = 1
        for (intelligentType in intelligentTypes) {
            intelligentType.tag = tag++
            setIntelligentTypeEvent(intelligentType)
        }

    }


    override fun getLayoutId() = R.layout.intelligent_fragment_index

}