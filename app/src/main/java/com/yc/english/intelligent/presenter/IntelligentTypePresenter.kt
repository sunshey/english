package com.yc.english.intelligent.presenter

import android.content.Context
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.kk.securityhttp.net.contains.HttpConfig
import com.yc.english.base.presenter.BasePresenter
import com.yc.english.base.utils.SimpleCacheUtils
import com.yc.english.intelligent.contract.IntelligentTypeContract
import com.yc.english.intelligent.model.domain.UnitInfoWrapper
import com.yc.english.intelligent.model.domain.VGInfoWarpper
import com.yc.english.intelligent.model.engin.IntelligentTypeEngin
import com.yc.english.intelligent.view.activitys.IntelligentVGSelectPopupWindow
import com.yc.english.main.model.domain.Constant
import java.util.*

/**
 * Created by zhangkai on 2017/11/24.
 */
open class IntelligentTypePresenter : BasePresenter<IntelligentTypeEngin,
        IntelligentTypeContract.View> {
    constructor(context: Context?, v: IntelligentTypeContract.View?) : super(context, v) {
        mEngin = IntelligentTypeEngin(context)
        RxBus.get().register(this)
    }

    override fun loadData(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (!forceUpdate) return

        if (!SPUtils.getInstance().getString("period", "").isEmpty()) {
            getUnit("loadData")
        }
    }

    @Subscribe(thread = EventThread.NEW_THREAD, tags = arrayOf(Tag(Constant.GET_UNIT)))
    fun getUnit(tag: String) {
        var versionInfo = JSON.parseObject(SPUtils.getInstance().getString(IntelligentVGSelectPopupWindow
                .DEFAULT_VERSION_KEY, ""), VGInfoWarpper.VGInfo::class.java)
        var gradeInfo = JSON.parseObject(SPUtils.getInstance().getString(IntelligentVGSelectPopupWindow
                .DEFAULT_GRADE_KEY, ""), VGInfoWarpper.VGInfo::class.java)
        var title = ""
        var versionId = 22
        if (versionInfo != null) {
            title = versionInfo.name ?: versionInfo?.title ?: "人教版"
            versionId = versionInfo.versionId ?: 22
        } else {
            title = "人教版"
        }

        if (gradeInfo == null) {
            gradeInfo = VGInfoWarpper.VGInfo()
            var grade = SPUtils.getInstance().getInt("grade", 0)
            gradeInfo.id = -1
            gradeInfo.grade = grade
            val name = title     //替换字符的奇技淫巧 1
            when (grade) {
                1 -> title += "一年级"
                2 -> title += "二年级"
                3 -> title += "三年级"
                4 -> title += "四年级"
                5 -> title += "五年级"
                6 -> title += "六年级"
                7 -> title += "七年级"
                8 -> title += "八年级"
                9 -> title += "九年级"
            }
            when (Calendar.getInstance().get(Calendar.MONTH)) {
                in 3..6 -> {
                    gradeInfo.partType = 0
                    title += "上"
                }
                in 1..2, in 7..12 -> {
                    gradeInfo.partType = 1
                    title += "下"
                }
            }
            gradeInfo.title = title.replace(name, "") //替换字符的奇技淫巧 2
        } else {
            title += gradeInfo.title ?: ""
        }
        mView.showTitle(title)
        SPUtils.getInstance().put(IntelligentVGSelectPopupWindow.DEFAULT_GRADE_KEY, JSON.toJSONString(gradeInfo))
        gradeInfo.versionId = versionId

        SimpleCacheUtils.readCache(mContext, "getUnit", object : SimpleCacheUtils.CacheRunnable() {
            override fun run() {
                val list = Gson().fromJson<List<UnitInfoWrapper.UnitInfo>>(json, object : TypeReference<List<UnitInfoWrapper.UnitInfo>>() {}.type)
                if (list != null && list.size > 0) {
                    showInfo(list)
                }
            }
        })
        val subriction = mEngin.getUnit(gradeInfo).subscribe({
            val code = it?.code ?: -1
            if (code == HttpConfig.STATUS_OK) {

                if (it?.data?.list != null) {
                    SimpleCacheUtils.writeCache(mContext, "getUnit", JSON.toJSONString(it.data?.list ?: ""))
                    showInfo(it?.data?.list!!)
                }
            }
        }, {})
        mSubscriptions.add(subriction)
    }

    private fun showInfo(list: List<UnitInfoWrapper.UnitInfo>) {
        val titles = arrayOfNulls<String>(list.size)
        val types = arrayOfNulls<UnitInfoWrapper.UnitInfo>(list.size)
        var i = 0
        for (unitInfo in list) {
            titles[i] = "Unit ${i + 1}"
            types[i] = unitInfo
            i++
        }
        mView.showInfo(titles, types)
    }

    @Subscribe(thread = EventThread.NEW_THREAD, tags = arrayOf(Tag(Constant.GET_VERSION)))
    fun getVersionInfo(tag: String) {
        mEngin.getVersion().subscribe({
            val code = it?.code ?: -1
            if (code == HttpConfig.STATUS_OK && it.data?.list != null) {
                SPUtils.getInstance().put(IntelligentVGSelectPopupWindow.DEFAULT_VERSION_KEY, JSON.toJSONString(it.data?.list?.get(0)!!))
                getUnit("after getVersionInfo")
            }
        }, {})
    }


}