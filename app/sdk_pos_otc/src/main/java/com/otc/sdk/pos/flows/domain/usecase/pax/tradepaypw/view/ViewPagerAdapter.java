package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.view;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

/**
 * Created by chenld on 2017/3/14.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> lists;

    public ViewPagerAdapter(List<View> data) {
        lists = data;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        // return false;
        return arg0 == (arg1);
    }

    public Object instantiateItem(View view, int position) {
        try {
            // 解决View只能滑动两屏的方法
            ViewGroup parent = (ViewGroup) lists.get(position).getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            // container.addView(v);
            ((ViewPager) view).addView(lists.get(position), 0);
        } catch (Exception e) {
            Log.e("inst", e.getMessage());
            //e.printStackTrace();
        }

        return lists.get(position);
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        try {
            ((ViewPager) arg0).removeView(lists.get(arg1));
        } catch (Exception e) {
            Log.e("dest", e.getMessage());
            //e.printStackTrace();
        }
    }
}
