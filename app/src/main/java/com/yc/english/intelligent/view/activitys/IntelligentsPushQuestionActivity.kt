package com.yc.english.intelligent.view.activitys

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.jakewharton.rxbinding.view.RxView
import com.kk.securityhttp.net.contains.HttpConfig
import com.yc.english.R
import com.yc.english.base.utils.StatusBarCompat
import com.yc.english.base.view.BaseActivity
import com.yc.english.intelligent.contract.IntelligentPushQuestionContract
import com.yc.english.intelligent.model.domain.UnitInfoWrapper
import com.yc.english.intelligent.presenter.IntelligentPushQuestionPresenter
import com.yc.english.intelligent.view.adpaters.IntelligentPushAdpater
import com.yc.english.main.model.domain.Constant
import com.yc.english.speak.view.activity.QuestionActivity
import kotlinx.android.synthetic.main.intelligent_activity_push_question.*
import java.util.concurrent.TimeUnit

/**
 * Created by zhangkai on 2017/11/30.
 */

class IntelligentsPushQuestionActivity : BaseActivity<IntelligentPushQuestionPresenter>(),
        IntelligentPushQuestionContract.View {


    var reportId = 0
    lateinit var adpater: IntelligentPushAdpater

    override fun init() {
        mPresenter = IntelligentPushQuestionPresenter(this, this)
        StatusBarCompat.light(this)
        StatusBarCompat.compat(this, mToolbarWarpper, mToolbar, R.mipmap.base_actionbar)

        RxView.clicks(mBackBtn).throttleFirst(200, TimeUnit
                .MILLISECONDS).subscribe {
            finish()
        }


        adpater = IntelligentPushAdpater()
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mRecyclerView.adapter = adpater


        reportId = intent.getIntExtra("reportId", 0)
        mPresenter.getPlan(reportId.toString())
    }

    override fun hideStateView() {
        mStateView.hide()
    }

    override fun showNoNet() {
        mStateView.showNoNet(mRecyclerView, HttpConfig.NET_ERROR, {
            mPresenter.getPlan(reportId.toString())
        })
    }

    override fun showNoData() {
        mStateView.showNoData(mRecyclerView)
    }

    override fun showLoading() {
        mStateView.showLoading(mRecyclerView)
    }

    lateinit var infos: MutableList<UnitInfoWrapper.ComleteItemInfo>
    override fun showInfo(comleteInfo: UnitInfoWrapper.ComleteInfo) {
        infos = mutableListOf<UnitInfoWrapper.ComleteItemInfo>()

        if (comleteInfo.vocabulary != -1) {
            comleteInfo.vocabulary = if (comleteInfo.vocabulary == 1) comleteInfo.vocabulary else SPUtils.getInstance()
                    .getInt("finish-reportId${reportId}vocabulary", 0)

            infos.add(UnitInfoWrapper.ComleteItemInfo("vocabulary", "1", comleteInfo.vocabulary))
        }

        if (comleteInfo.oracy != -1) {
            comleteInfo.oracy = if (comleteInfo.oracy == 1) comleteInfo.oracy else SPUtils.getInstance()
                    .getInt("finish-reportId${reportId}oracy", 0)

            infos.add(UnitInfoWrapper.ComleteItemInfo("oracy", "2", comleteInfo.oracy))
        }

        if (comleteInfo.grammar != -1) {
            comleteInfo.grammar = if (comleteInfo.grammar == 1) comleteInfo.grammar else SPUtils.getInstance()
                    .getInt("finish-reportId${reportId}grammar", 0)

            infos.add(UnitInfoWrapper.ComleteItemInfo("grammar", "3", comleteInfo.grammar))
        }

        if (comleteInfo.hearing != -1) {
            comleteInfo.hearing = if (comleteInfo.hearing == 1) comleteInfo.hearing else SPUtils.getInstance()
                    .getInt("finish-reportId${reportId}hearing", 0)

            infos.add(UnitInfoWrapper.ComleteItemInfo("hearing", "4", comleteInfo.hearing))
        }

        if (comleteInfo.read != -1) {

            comleteInfo.read = if (comleteInfo.read == 1) comleteInfo.read else SPUtils.getInstance()
                    .getInt("finish-reportId${reportId}read", 0)


            infos.add(UnitInfoWrapper.ComleteItemInfo("read", "5", comleteInfo.read))
        }

        if (comleteInfo.writing != -1) {

            comleteInfo.writing = if (comleteInfo.writing == 1) comleteInfo.writing else SPUtils.getInstance()
                    .getInt("finish-reportId${reportId}writing", 0)

            infos.add(UnitInfoWrapper.ComleteItemInfo("writing", "6", comleteInfo.writing))
        }
        adpater.setNewData(infos)
        adpater.setOnItemClickListener { adapter, view, position ->
            val comleteInfo = adapter.data.get(position) as UnitInfoWrapper.ComleteItemInfo
            val intent: Intent

            if (comleteInfo.key.equals("oracy")) {
                intent = Intent(this@IntelligentsPushQuestionActivity, QuestionActivity::class.java)
            } else {
                intent = Intent(this@IntelligentsPushQuestionActivity, IntelligentQuestionsActivity::class.java)
            }
            intent.putExtra("reportId", reportId)
            intent.putExtra("type", comleteInfo.key)
            intent.putExtra("isResultIn", comleteInfo.isComplete == 1)
            startActivity(intent)
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag(Constant.RESULT_IN)))
    fun complete(tag: String) {
        for (compeleteInfo in infos) {
            if (tag.equals(compeleteInfo.key)) {
                compeleteInfo.isComplete = 1
                adpater.notifyDataSetChanged()
                break
            }
        }
    }


    override fun getLayoutId() = R.layout.intelligent_activity_push_question

}