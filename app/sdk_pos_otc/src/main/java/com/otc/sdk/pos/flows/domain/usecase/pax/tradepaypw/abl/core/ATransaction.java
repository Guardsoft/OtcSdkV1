package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.abl.core;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态机
 *
 * @author Steven.W
 */
public abstract class ATransaction {
    private static final String TAG = ATransaction.class.getSimpleName();
    /**
     * state和action的绑定关系表
     */
    private Map<String, AAction> actionMap;

    /**
     * 交易结束监听器
     *
     * @author Steven.W
     */
    public interface TransEndListener {
        public void onEnd(ActionResult result);
    }

    /**
     * 单个state绑定action
     *
     * @param state
     * @param action
     */
    protected void bind(String state, AAction action) {
        if (actionMap == null) {
            actionMap = new HashMap<String, AAction>();
        }
        actionMap.put(state, action);
    }

    /**
     * 执行state状态绑定的action
     *
     * @param state
     */
    public void gotoState(String state) {
        AAction action = actionMap.get(state);
        if (action != null) {
            action.execute();
        } else {
            Log.e(TAG, "无效State:" + state);
        }
    }

    /**
     * 执行交易
     */
    public void execute() {
        bindStateOnAction();
    }

    /**
     * state绑定action抽象方法，在此实现中调用{@link #bind(String, AAction)}方法， 并在最后调用{@link #gotoState(String)}方法，执行第一个state
     */
    protected abstract void bindStateOnAction();

}
