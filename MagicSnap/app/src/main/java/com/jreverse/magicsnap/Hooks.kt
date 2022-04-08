package com.jreverse.magicsnap

import android.content.Context
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlin.jvm.Throws

class Hooks : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam != null) {
            if(!lpparam.packageName.equals("com.snapchat.android"))
                return
        };

        XposedBridge.log("[MagicSnap] Hooking Snapchat")

        if (lpparam != null) {
            findAndHookMethod(
                "android.app.Application",
                lpparam.classLoader,
                "attach",
                Context::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        val snapContext = param?.args?.get(0) as Context
                        Toast.makeText(snapContext, "MagicSnap Initialized", Toast.LENGTH_SHORT).show()
                        XposedBridge.log("[MagicSnap] Hooking ScreenShot Class")
                        //ScreenShot Bypass
                        super.afterHookedMethod(param)
                        if (lpparam != null) {
                            findAndHookMethod(
                                "WId",
                                lpparam.classLoader,
                                "a",
                                "WId",
                                XC_MethodReplacement.DO_NOTHING
                            )
                        }
                    }
                }
            )
        }
    }
}