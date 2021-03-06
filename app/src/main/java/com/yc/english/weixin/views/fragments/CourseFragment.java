package com.yc.english.weixin.views.fragments;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.yc.english.R;
import com.yc.english.base.view.BaseFragment;
import com.yc.english.base.view.StateView;
import com.yc.english.main.model.domain.Constant;
import com.yc.english.weixin.contract.WeiKeContract;
import com.yc.english.weixin.model.domain.WeiKeCategory;
import com.yc.english.weixin.model.domain.WeiKeCategoryWrapper;
import com.yc.english.weixin.model.domain.WeiKeInfo;
import com.yc.english.weixin.presenter.WeiKePresenter;
import com.yc.english.weixin.views.activitys.WeikeUnitActivity;
import com.yc.english.weixin.views.adapters.WeiKeCategoryItemAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by zhangkai on 2017/8/30.
 * 微课分类页
 */

public class CourseFragment extends BaseFragment<WeiKePresenter> implements WeiKeContract.View {

    @BindView(R.id.rv_course)
    RecyclerView mCourseRecyclerView;

    @BindView(R.id.sv_loading)
    StateView mLoadingStateView;

    private int page = 1;

    private int pageSize = 20;

    private WeiKeCategoryItemAdapter mWeiKeCategoryItemAdapter;

    @BindView(R.id.refresh)
    SwipeRefreshLayout mRefreshSwipeRefreshLayout;
    private TextView mTvHeaderView;
    private String type;


    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.GRADE_REFRESH)
            }
    )
    public void refresh(String tag) {
        page = 1;
        mPresenter.getWeikeCategoryList(type, page + "");
    }

    @Override
    public void init() {
        if (getArguments() != null) {
            type = getArguments().getString("type");
        }

        mPresenter = new WeiKePresenter(getActivity(), this);
        mCourseRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mWeiKeCategoryItemAdapter = new WeiKeCategoryItemAdapter(null);
        mCourseRecyclerView.setAdapter(mWeiKeCategoryItemAdapter);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.course_header_view, null);
        mTvHeaderView = (TextView) headerView.findViewById(R.id.tv_header_view);
        mWeiKeCategoryItemAdapter.addHeaderView(headerView);
        mWeiKeCategoryItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), WeikeUnitActivity.class);

                intent.putExtra("pid", mWeiKeCategoryItemAdapter.getData().get(position).getId());
                intent.putExtra("type", mWeiKeCategoryItemAdapter.getData().get(position).getTypeId());
                startActivity(intent);
            }
        });

        mWeiKeCategoryItemAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.getWeikeCategoryList(type, page + "");
            }
        }, mCourseRecyclerView);

        mPresenter.getWeikeCategoryList(type, page + "");
        mRefreshSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.primaryDark), ContextCompat.getColor(getActivity(), R.color.primaryDark));
        mRefreshSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh("mRefreshSwipeRefreshLayout");
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.weixin_fragment_course;
    }

    @Override
    public void hideStateView() {
        mLoadingStateView.hide();
    }

    @Override
    public void showNoNet() {
        mLoadingStateView.showNoNet(mCourseRecyclerView, "网络不给力", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getWeikeCategoryList(type, page + "");
            }
        });
    }

    @Override
    public void showNoData() {
        mLoadingStateView.showNoData(mCourseRecyclerView);
    }

    @Override
    public void showLoading() {
        if (!mRefreshSwipeRefreshLayout.isRefreshing() || (mWeiKeCategoryItemAdapter.getData() == null || mWeiKeCategoryItemAdapter.getData()
                .size() == 0))
            mLoadingStateView.showLoading(mCourseRecyclerView);
    }

    @Override
    public void showWeikeCategoryList(WeiKeCategoryWrapper weiKeCategoryWrapper) {
        List<WeiKeCategory> list = weiKeCategoryWrapper.getList();
        int count = weiKeCategoryWrapper.getCount();
        if (page == 1) {
            mWeiKeCategoryItemAdapter.setNewData(list);
        } else {
            mWeiKeCategoryItemAdapter.addData(list);
        }
        if (list.size() == pageSize) {
            page++;
            mWeiKeCategoryItemAdapter.loadMoreComplete();
        } else {
            mWeiKeCategoryItemAdapter.loadMoreEnd();
        }
        mRefreshSwipeRefreshLayout.setRefreshing(false);
        mTvHeaderView.setText(String.format(getString(R.string.update_weike), count + ""));

    }

    @Override
    public void showWeiKeInfoList(List<WeiKeInfo> list) {

    }

    @Override
    public void fail() {
        mWeiKeCategoryItemAdapter.loadMoreFail();
    }


    @Override
    public void end() {
        mWeiKeCategoryItemAdapter.loadMoreEnd();
    }
}
