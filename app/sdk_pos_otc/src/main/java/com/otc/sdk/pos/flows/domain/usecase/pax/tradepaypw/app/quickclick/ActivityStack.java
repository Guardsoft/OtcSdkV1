package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.app.quickclick;

import android.app.Activity;
import android.util.Log;

import java.util.NoSuchElementException;
import java.util.Stack;

public class ActivityStack {
    private static final String TAG = ActivityStack.class.getSimpleName();

    private static Stack<Activity> activityStack;
    private static ActivityStack instance;

    private ActivityStack() {

    }

    public static ActivityStack getInstance() {
        if (instance == null)
            instance = new ActivityStack();

        return instance;
    }

    public void pop() {
        try {
            Activity activity = activityStack.lastElement();
            if (activity != null) {
                activityStack.remove(activity);
                activity.finish();
                //activity = null;
            }
        } catch (Exception e) {
            Log.e("pop", e.getMessage());
            //e.printStackTrace();
        }
    }

    /**
     * 从栈的后面开始删除，知道删除自身界面为止
     *
     * @param activity
     */
    public void popTo(Activity activity) {
        if (activity != null) {
            Boolean temp = true;
            while (temp) {
                Activity lastcurrent = top();
                if (activity == top()) {
                    return;
                }
                activityStack.remove(lastcurrent);
                lastcurrent.finish();
            }
        }
    }

    public Activity top() {
        try {
            if (activityStack.size() < 1) {
                return null;
            }
            Activity activity = activityStack.lastElement();
            return activity;
        } catch (NoSuchElementException ex) {
            Log.e("top", ex.getMessage());
            //ex.printStackTrace();
        } catch (Exception e) {
            Log.e("pop", e.getMessage());
            //e.printStackTrace();
        }
        return null;
    }

    public void push(Activity activity) {
        if (activityStack == null)
            activityStack = new Stack<Activity>();

        activityStack.add(activity);
    }

    /**
     * 除站底外，其他pop掉
     */
    public void popAllButBottom() {
        while (true) {
            Activity topActivity = top();
            if (topActivity == null || topActivity == activityStack.firstElement()) {
                break;
            }
            activityStack.remove(topActivity);
            topActivity.finish();
        }

    }

    /**
     * 结束所有栈中的activity
     */
    public void popAll() {
        if (activityStack == null) {
            return;
        }
        while (true) {
            Activity activity = top();
            if (activity == null) {
                break;
            }
            //AppLog.i(TAG, activity.toString());
            activityStack.remove(activity);
            activity.finish();
        }
    }

    public Activity bottom() {
        return activityStack.firstElement();
    }

    public void removeTop() {
        Activity topActivity = top();
        if (topActivity == null || topActivity == activityStack.firstElement()) {
            return;
        }
        activityStack.remove(topActivity);
    }

}
