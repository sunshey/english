package com.yc.english.read.view.activitys;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.jakewharton.rxbinding.view.RxView;
import com.umeng.analytics.MobclickAgent;
import com.yc.english.R;
import com.yc.english.base.helper.TipsHelper;
import com.yc.english.base.utils.StatusBarCompat;
import com.yc.english.base.utils.WakeLockUtils;
import com.yc.english.base.view.FullScreenActivity;
import com.yc.english.base.view.StateView;
import com.yc.english.main.hepler.UserInfoHelper;
import com.yc.english.main.model.domain.Constant;
import com.yc.english.main.model.domain.UserInfo;
import com.yc.english.read.common.SpeechUtils;
import com.yc.english.read.contract.CoursePlayContract;
import com.yc.english.read.model.domain.EnglishCourseInfo;
import com.yc.english.read.model.domain.EnglishCourseInfoList;
import com.yc.english.read.model.domain.UnitInfo;
import com.yc.english.read.presenter.CoursePlayPresenter;
import com.yc.english.read.view.adapter.ReadCourseItemClickAdapter;
import com.yc.english.read.view.wdigets.PopupWindowFactory;
import com.yc.english.speak.utils.IatSettings;
import com.yc.english.speak.utils.VoiceJsonParser;
import com.yc.english.vip.model.bean.GoodsType;
import com.yc.english.vip.utils.VipDialogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class CoursePlayActivity extends FullScreenActivity<CoursePlayPresenter> implements CoursePlayContract.View {

    @BindView(R.id.sv_loading)
    StateView mStateView;

    @BindView(R.id.layout_content)
    FrameLayout mLayoutContext;

    @BindView(R.id.layout_course_play)
    LinearLayout mCoursePlayLayout;

    @BindView(R.id.rv_course_list)
    RecyclerView mCourseRecyclerView;

    @BindView(R.id.iv_course_play)
    ImageView mCoursePlayImageView;

    @BindView(R.id.layout_language_change)
    LinearLayout mLanguageChangeLayout;

    @BindView(R.id.tv_language)
    TextView mLanguageTextView;

    @BindView(R.id.iv_next_unit)
    ImageView mNextUnitImageView;

    @BindView(R.id.iv_language_change)
    ImageView mLanguageChangeImageView;

    ReadCourseItemClickAdapter mItemAdapter;

    private int playPosition = -1;

    LinearLayoutManager linearLayoutManager;

    private boolean isCountinue = false;

    private int languageType = 1; //1:中英,2:英,3:中

    private PublishSubject mTsSubject;

    private String unitId;

    private String unitTitle;

    private int currentPage = 1;

    private boolean isNext = false;

    @BindView(R.id.toolbarWarpper)
    FrameLayout mToolbarWarpper;

    private int position;

    private List<UnitInfo> unitInfoList;

    private boolean isRead;

    // 语音听写对象
    private SpeechRecognizer mIat;

    /**
     * 函数调用返回值
     */
    private int ret = 0;

    /**
     * 用HashMap存储听写结果
     */
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    // 语音合成对象
    private SpeechSynthesizer mTts;

    private SharedPreferences mSharedPreferences;
    /**
     * 引擎类型
     */
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private boolean mTranslateEnable = false;

    private MediaPlayer mPlayer;

    private File audioFile;

    private String audioFilePath;

    private String voiceText;

    private int lastPosition = 0;

    // 语音听写UI
    private RecognizerDialog mIatDialog;

    private ImageView mTapeImageView;

    private PopupWindowFactory mTapePop;
    private UserInfo userInfo;

    @Override
    public int getLayoutId() {
        return R.layout.read_activity_course_play;
    }

    @Override
    public void init() {

        StatusBarCompat.compat(this, mToolbarWarpper, mToolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            position = bundle.getInt("position");
            unitInfoList = bundle.getParcelableArrayList("unitInfoList");
            if (unitInfoList != null) {
                UnitInfo unitInfo = unitInfoList.get(position);
                unitId = unitInfo.getId();
                unitTitle = unitInfo.getName();
            }
        }

        WakeLockUtils.acquireWakeLock(this);
        mTts = SpeechUtils.getTts(this);
        mPresenter = new CoursePlayPresenter(this, this);

        mToolbar.setTitle(unitTitle != null ? unitTitle : "");
        mToolbar.showNavigationIcon();
        mToolbar.setTitleColor(ContextCompat.getColor(this, R.color.black_333));

        linearLayoutManager = new LinearLayoutManager(this);
        mCourseRecyclerView.setLayoutManager(linearLayoutManager);

        mItemAdapter = new ReadCourseItemClickAdapter(this, null);
        mCourseRecyclerView.setAdapter(mItemAdapter);

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(CoursePlayActivity.this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(CoursePlayActivity.this, mInitListener);

        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);

        setParam();

        mTsSubject = PublishSubject.create();
        mTsSubject.delay(800, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer position) {
                if (playPosition < mItemAdapter.getData().size()) {
                    enableState(playPosition);
                    startSynthesizer(position);
                } else {
                    isCountinue = false;
                    disableState();
                }
            }
        });
        userInfo = UserInfoHelper.getUserInfo();
        //下一单元
        RxView.clicks(mNextUnitImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                if (unitInfoList != null && position + 1 < unitInfoList.size()) {
                    UnitInfo unitInfo = unitInfoList.get(position + 1);
                    position++;
                    //1是免费，2是收费
                    if (unitInfo.getFree() == 1) {
                        isRead = true;
                    } else {
                        if (userInfo != null) {
                            int isVip = userInfo.getIsVip();
                            isRead = isVip == 1;
                        } else {
                            UserInfoHelper.isGotoLogin(CoursePlayActivity.this);
                            MobclickAgent.onEvent(CoursePlayActivity.this, "textbook_read", "教材点读");
                            return;
                        }
                    }
                    if (!isRead) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(GoodsType.GOODS_KEY, GoodsType.TYPE_GENERAL_VIP);
                        VipDialogHelper.showVipDialog(getSupportFragmentManager(), "", bundle);
                        return;
                    }

                    isNext = true;
                    resetPlayState();
                    mToolbar.setTitle(unitInfo.getName());
                    unitId = unitInfo.getId();
                    currentPage = 1;
                    mPresenter.getCourseListByUnitId(currentPage, 0, unitId);


                } else {
                    TipsHelper.tips(CoursePlayActivity.this, "已经是最后一个单元");
                }

            }
        });

        //播放
        RxView.clicks(mCoursePlayImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (playPosition == -1) {
                    playPosition = 0;
                }
                isCountinue = !isCountinue;
                if (isCountinue) {
                    enableState(playPosition);
                    startSynthesizer(playPosition);
                } else {
                    disableState();
                }
            }
        });

        //语言切换
        RxView.clicks(mLanguageChangeImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                languageType++;

                if (languageType > 3) {
                    languageType = 1;
                }
                if (playPosition > -1 && playPosition < mItemAdapter.getData().size()) {
                    mItemAdapter.getData().get(playPosition).setPlay(true);
                }
                switch (languageType) {
                    case 1:
                        mLanguageTextView.setText(getString(R.string.read_course_language_blend_text));
                        break;
                    case 2:
                        mLanguageTextView.setText(getString(R.string.read_course_language_en_text));
                        break;
                    case 3:
                        mLanguageTextView.setText(getString(R.string.read_course_language_cn_text));
                        break;
                    default:
                        break;
                }

                mItemAdapter.setLanguageType(languageType);
                mItemAdapter.notifyDataSetChanged();
            }
        });

        mItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, final View view, int position) {
                /*if (position == playPosition) {
                    return;
                }*/
                isCountinue = false;
                playPosition = position;
                itemChoose(playPosition);
                //startSynthesizer(playPosition);
            }
        });

        //点击跟读按钮后操作
        mItemAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                playPosition = position;
                if (view.getId() == R.id.iv_tape) {

                    lastPosition = position;
                    isCountinue = false;
                    disableState();

                    //弹出录音界面
                    //mIatDialog.setListener(mRecognizerDialogListener);
                    //mIatDialog.show();

                    /*View currentView = linearLayoutManager.findViewByPosition(playPosition);
                    if (currentView != null) {
                        ((ImageView) currentView.findViewById(R.id.iv_tape)).setImageResource(R.drawable.record_microphone);
                    }*/

                    final View tapeView = View.inflate(CoursePlayActivity.this, R.layout.layout_microphone, null);
                    mTapePop = new PopupWindowFactory(CoursePlayActivity.this, tapeView);

                    //PopupWindow布局文件里面的控件
                    mTapeImageView = (ImageView) tapeView.findViewById(R.id.iv_recording_icon);
                    mTapePop.showAtLocation(mLayoutContext, Gravity.CENTER, 0, 0);

                    ret = mIat.startListening(mRecognizerListener);
                    if (ret != ErrorCode.SUCCESS) {
                        ToastUtils.showLong("听写失败,错误码：" + ret);
                    } else {
                        //ToastUtils.showLong("开始");
                    }
                    return false;
                }

                if (view.getId() == R.id.iv_play && mTts != null) {
                    if (mTts.isSpeaking()) {
                        mTts.stopSpeaking();
                        disableState();
                    } else {
                        isCountinue = false;
                        enableState(playPosition);
                        startSynthesizer(playPosition);
                    }
                    return false;
                }

                if (view.getId() == R.id.iv_play_tape && mItemAdapter.getData().get(lastPosition).isShow()) {
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        stopPlayTape();
                        View currentView = linearLayoutManager.findViewByPosition(position);
                        if (currentView != null) {
                            Glide.with(CoursePlayActivity.this).load(R.mipmap.item_tape_play_normal_icon).into((ImageView) currentView.findViewById(R.id.iv_play_tape));
                        }
                    } else {
                        playTape(position);
                    }
                } else {
                    ToastUtils.showLong("请先录音评测后再回放");
                }

                return false;
            }
        });

        mCourseRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(recyclerView)) {
                    currentPage++;
                    mPresenter.getCourseListByUnitId(currentPage, 0, unitId);
                }
            }
        });

        mPresenter.getCourseListByUnitId(currentPage, 0, unitId);

        if (com.yc.english.base.utils.SpeechUtils.getAppids() == null || com.yc.english.base.utils.SpeechUtils.getAppids().size() <= 0) {
            com.yc.english.base.utils.SpeechUtils.setAppids(this);
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            LogUtils.e("SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                ToastUtils.showLong("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 听写UI监听器
     */
    public RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            LogUtils.i("RecognizerDialogListener result--->" + results + "---" + isLast);
            if (!mTranslateEnable) {
                printResult(results);
            }
        }

        /**
         * 识别回调错误.
         */
        @Override
        public void onError(SpeechError error) {
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                ToastUtils.showLong(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
                ToastUtils.showLong(error.getPlainDescription(true));
            }
        }

    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            //ToastUtils.showLong("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            if (mTapePop != null && mTapePop.getPopupWindow().isShowing()) {
                mTapePop.dismiss();
            }
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                ToastUtils.showLong(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
                ToastUtils.showLong("听写识别错误，请重试");
                if (mIat != null) {
                    mIat.stopListening();
                }
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //ToastUtils.showLong("结束说话");
            if (mTapePop != null && mTapePop.getPopupWindow().isShowing()) {
                mTapePop.dismiss();
            }
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            LogUtils.e("results string" + results.getResultString());

            if (mTapePop != null && mTapePop.getPopupWindow().isShowing()) {
                mTapePop.dismiss();
            }

            if (mTranslateEnable) {
                //printTransResult(results);
            } else {
                printResult(results);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //ToastUtils.showLong("当前正在说话，音量大小：" + volume);
            LogUtils.e("音量大小--->" + volume);
            mTapeImageView.getDrawable().setLevel((int) (3000 + 6000 * volume * 18 / 100));
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };


    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    private void resetPlayState() {
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
        playPosition = -1;
        isCountinue = false;
        disableState();
    }

    @Override
    public void hideStateView() {
        mStateView.hide();
    }

    @Override
    public void showNoNet() {
        mStateView.showNoNet(mCourseRecyclerView, "网络不给力", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getCourseListByUnitId(currentPage, 0, unitId);
            }
        });
    }

    @Override
    public void showNoData() {
        mStateView.showNoData(mCourseRecyclerView);
    }

    @Override
    public void showLoading() {
        mStateView.showLoading(mCourseRecyclerView, 2);
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        this.mTranslateEnable = mSharedPreferences.getBoolean(this.getString(R.string.pref_key_translate), false);
        if (mTranslateEnable) {
            LogUtils.e("translate enable");
            mIat.setParameter(SpeechConstant.ASR_SCH, "1");
            mIat.setParameter(SpeechConstant.ADD_CAP, "translate");
            mIat.setParameter(SpeechConstant.TRS_SRC, "its");
        }

        String lag = mSharedPreferences.getString("iat_language_preference",
                "en_us");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "en");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "cn");
            }
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
            }
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        audioFilePath = Environment.getExternalStorageDirectory() + "/msc/iat.wav";
        audioFile = new File(audioFilePath);
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, audioFilePath);
    }

    private void printResult(RecognizerResult results) {
        String text = VoiceJsonParser.parseIatResult(results.getResultString());

        if (StringUtils.isEmpty(text)) {
            return;
        }
        LogUtils.e("the text--->" + text);

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        voiceText = resultBuffer.toString();
        if (!StringUtils.isEmpty(voiceText)) {
            LogUtils.e("result text --->" + voiceText);
        }

        LogUtils.i("first --->" + mItemAdapter.getData().get(lastPosition).getSubTitle());

        mItemAdapter.getData().get(lastPosition).setShow(true);

        if (compareResult(mItemAdapter.getData().get(lastPosition).getSubTitle(), voiceText)) {
            LogUtils.i("result --->" + true);
            mItemAdapter.getData().get(lastPosition).setSpeakResult(true);
        } else {
            LogUtils.i("result --->" + false);
            mItemAdapter.getData().get(lastPosition).setSpeakResult(false);
        }

        mItemAdapter.notifyDataSetChanged();

        if (mIat != null) {
            mIat.stopListening();
        }
    }

    /**
     * 将录入的语音与源语音进行对比
     *
     * @param sourceSen
     * @param speakSen
     * @return
     */
    public boolean compareResult(String sourceSen, String speakSen) {

        try {
            if (StringUtils.isEmpty(sourceSen) || StringUtils.isEmpty(speakSen)) {
                return false;
            }

            String regEx = " |、|，|。|；|？|！|,|\\.|;|\\?|!|]|:|：|\"|-";
            Pattern p = Pattern.compile(regEx);

            //按照句子结束符分割句子
            String[] words = p.split(sourceSen);
            List<String> sourceList = new ArrayList<>();
            for (int i = 0; i < words.length; i++) {
                if (!StringUtils.isTrimEmpty(words[i])) {
                    sourceList.add(words[i]);
                }
            }

            List<String> speakList = new ArrayList<>();
            String[] speakWords = p.split(speakSen);
            for (int m = 0; m < speakWords.length; m++) {
                if (!StringUtils.isTrimEmpty(speakWords[m])) {
                    speakList.add(speakWords[m]);
                }
            }

            int matchCount = 0;
            float percent = 0;
            for (String str : sourceList) {
                if (speakList.contains(str)) {
                    matchCount++;
                }
            }

            if (matchCount > 0 && sourceList.size() > 0) {
                percent = (float) matchCount / (float) sourceList.size() * 100;
            } else {
                return false;
            }

            mItemAdapter.getData().get(lastPosition).setPercent(percent + "");

            if (percent >= 60) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 语音合成播放
     *
     * @param postion
     */
    public void startSynthesizer(int postion) {
        if (postion < 0 || postion >= mItemAdapter.getData().size()) {
            return;
        }
        mTts = SpeechUtils.getTts(this);
        String text = mItemAdapter.getData().get(postion).getSubTitle();
        int code = mTts.startSpeaking(text, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                TipsHelper.tips(CoursePlayActivity.this, "语音合成失败");
            } else {
                TipsHelper.tips(CoursePlayActivity.this, "语音合成失败");
                mTts.stopSpeaking();
            }
        }
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
            //暂停播放
        }

        @Override
        public void onSpeakResumed() {
            //继续播放
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                speekContinue(isCountinue ? ++playPosition : playPosition);
            } else if (error != null) {
                if (error.getErrorDescription().contains("权")) {
                    com.yc.english.base.utils.SpeechUtils.resetAppid(CoursePlayActivity.this);
                    return;
                }
                speekContinue(playPosition);
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
        }
    };

    public void speekContinue(int index) {
        if (isCountinue) {
            mTsSubject.onNext(index);
        } else {
            disableState();
        }
    }

    public void resetPlay() {
        for (EnglishCourseInfo englishCourseInfo : mItemAdapter.getData()) {
            englishCourseInfo.setPlay(false);
            englishCourseInfo.setShow(false);
        }
    }

    public void itemChoose(int postion) {
        if (postion < 0 || postion >= mItemAdapter.getData().size()) {
            return;
        }
        if (playPosition > 2) {
            linearLayoutManager.scrollToPositionWithOffset(playPosition - 2, 0);
        }
        if (mTts != null) {
            mTts.stopSpeaking();
        }
        resetPlay();
        mCoursePlayImageView.setBackgroundResource(R.drawable.read_playing_course_btn_selector);
        mItemAdapter.setLastPosition(playPosition);
        mItemAdapter.notifyDataSetChanged();
    }

    public void enableState(int postion) {
        if (postion < 0 || postion >= mItemAdapter.getData().size()) {
            return;
        }
        if (playPosition > 2) {
            linearLayoutManager.scrollToPositionWithOffset(playPosition - 2, 0);
        }
        if (mTts != null) {
            mTts.stopSpeaking();
        }
        resetPlay();
        mCoursePlayImageView.setBackgroundResource(R.drawable.read_playing_course_btn_selector);
        mItemAdapter.getData().get(postion).setPlay(true);
        mItemAdapter.setLastPosition(playPosition);
        mItemAdapter.notifyDataSetChanged();
    }

    public void disableState() {
        if (!isCountinue) {
            mCoursePlayImageView.setBackgroundResource(R.drawable.read_play_course_btn_selector);
        }
        mTts.stopSpeaking();
        resetPlay();
        mItemAdapter.setLastPosition(playPosition);
        mItemAdapter.notifyDataSetChanged();
    }

    /**
     * 播放录音
     */
    public void playTape(final int position) {
        try {
            if (audioFile != null && audioFile.exists()) {
                View currentView = linearLayoutManager.findViewByPosition(position);
                if (currentView != null) {
                    Glide.with(CoursePlayActivity.this).load(R.mipmap.item_read_press_icon).into((ImageView) currentView.findViewById(R.id.iv_play_tape));
                }

                mPlayer = new MediaPlayer();
                //设置要播放的文件
                mPlayer.setDataSource(audioFile.getAbsolutePath());
                mPlayer.prepare();
                //播放
                mPlayer.start();
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        disableState();
                        stopPlayTape();

                        View currentView = linearLayoutManager.findViewByPosition(position);
                        if (currentView != null) {
                            Glide.with(CoursePlayActivity.this).load(R.mipmap.item_tape_play_normal_icon).into((ImageView) currentView.findViewById(R.id.iv_play_tape));
                        }
                    }
                });

            }
        } catch (Exception e) {
            LogUtils.e("prepare() failed");
        }
    }

    /**
     * 停止播放录音
     */
    public void stopPlayTape() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mTts) {
            mTts.stopSpeaking();
            mTts.destroy();
            mTts = null;
        }
        WakeLockUtils.releaseWakeLock();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }


    @Override
    public void showCourseListData(EnglishCourseInfoList englishCourseInfoList) {
        if (englishCourseInfoList != null) {
            if (isNext) {
                mItemAdapter.setNewData(englishCourseInfoList.list);
                isNext = false;
            } else {
                mItemAdapter.addData(englishCourseInfoList.list);
            }
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.COMMUNITY_ACTIVITY_REFRESH)
            }
    )
    public void getInfo(String loginInfo) {
        userInfo = UserInfoHelper.getUserInfo();
    }

}
