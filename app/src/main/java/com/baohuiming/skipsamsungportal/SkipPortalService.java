package com.baohuiming.skipsamsungportal;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
//import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.RequiresApi;
import java.util.List;

public class SkipPortalService extends AccessibilityService {

    //private static final String TAG = "SkipPortalService";
    private static final String buttonText = "直接使用此网络";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //此处写事件的处理，常用eventType和className来做判断。
        int eventType = event.getEventType();
        String className = event.getClassName().toString();
        //Log.d(TAG, "当前事件类型：" + eventType);
        //Log.d(TAG, "当前执行类名：" + className);
        //根据不同的eventType和className，来进行具体的事件处理
        //com.android.settings:id/summary
        //com.android.settings.Settings$WifiSettingsActivity
        //com.android.captiveportallogin.CaptivePortalLoginActivity
        String status = "Null";
        switch (className) {
            case "com.android.settings.Settings$WifiSettingsActivity":
                //Log.d(TAG, "打开wifi选择界面");
                status = "Select";
                break;
            case "com.android.captiveportallogin.CaptivePortalLoginActivity":
                //Log.d(TAG, "打开wifi认证界面");
                status = "Login";
                break;
        }
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            switch (status) {
                case "Null":
                case "Select":
                    clickConnectedWifi(root);
                case "Login":
                    clickMore(root);
                    break;
            }
            clickDirect(root);
        }
    }

    public void clickConnectedWifi(AccessibilityNodeInfo root) {
        // 点击“登录网络”按钮
        List<AccessibilityNodeInfo> connected_list = root.
                findAccessibilityNodeInfosByViewId("com.android.settings:id/connected_list");
        //Log.d(TAG, "已连接的WiFi数量:" + connected_list.size());
        if (connected_list.size() != 0) {
            AccessibilityNodeInfo viewGroupNode = connected_list.get(0);
            if (viewGroupNode.getChildCount() != 0) {
                AccessibilityNodeInfo clickableLinearLayoutNode = viewGroupNode.getChild(0);
                List<AccessibilityNodeInfo> titleNodes = root.findAccessibilityNodeInfosByViewId("com.android.settings:id/title");
                if (titleNodes.size() != 0) {
                    AccessibilityNodeInfo titleNode = titleNodes.get(0);
                    if (titleNode.getText().toString().contains(".wlan.bjtu")) {
                        clickableLinearLayoutNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }
    }

    public void clickMore(AccessibilityNodeInfo root) {
        // 点击“更多选项”按钮
        List<AccessibilityNodeInfo> bars = root.
                findAccessibilityNodeInfosByViewId("android:id/action_bar");
        if (bars.size() != 0) {
            AccessibilityNodeInfo bar = bars.get(0);
            if (bar.getChildCount() > 2) {
                //Log.d(TAG, String.valueOf(bar.getChildCount()));
                AccessibilityNodeInfo btn = bar.getChild(2);
                btn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    public void clickDirect(AccessibilityNodeInfo root) {
        // 点击“直接使用此网络”按钮
        List<AccessibilityNodeInfo> texts = root.
                findAccessibilityNodeInfosByViewId("android:id/title");
        if (texts.size() > 1) {
            if (texts.get(1).getText().equals(buttonText)) {
                //Log.d(TAG,"找到了！");
                texts.get(1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                // 退出程序
                exit();
            }
        }
    }

    public void exit() {
        // 退出程序
        //android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    public void onInterrupt() {

    }

}
