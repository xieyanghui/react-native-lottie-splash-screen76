package org.devio.rn.splashscreen

import android.animation.Animator
import android.app.Activity
import android.app.Dialog
import android.os.Build
import com.airbnb.lottie.LottieAnimationView
import java.lang.ref.WeakReference

/**
 * SplashScreen
 * 启动屏
 * from：http://www.devio.org
 * Author:CrazyCodeBoy
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */
object SplashScreen {
    private var mSplashDialog: Dialog? = null
    private var mActivity: WeakReference<Activity>? = null
    private var isAnimationFinished = false
    private var waiting = false

    /**
     * 打开启动屏
     */
    @JvmStatic
    fun show(activity: Activity?, themeResId: Int = R.style.SplashScreen_SplashTheme, lottieId: Int, fullScreen: Boolean) {
        if (activity == null) return
        mActivity = WeakReference(activity)
        activity.runOnUiThread {
            if (!activity.isFinishing) {
                mSplashDialog = Dialog(activity, themeResId)
                mSplashDialog?.setContentView(R.layout.launch_screen)
                mSplashDialog?.setCancelable(false)
                val lottie = mSplashDialog?.findViewById<LottieAnimationView>(lottieId)

                lottie?.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        println("SplashScreen is started")
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        setAnimationFinished(true)
                    }

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                })
                if (fullScreen) {
                   setActivityAndroidP(this)
                }
                if (mSplashDialog?.isShowing == false) {
                    mSplashDialog?.show()
                }
            }
        }
    }

    @JvmStatic
    fun setAnimationFinished(flag: Boolean) {
        if (mActivity == null) return

        isAnimationFinished = flag

        val _activity = mActivity?.get() ?: return

        _activity.runOnUiThread {
            if (mSplashDialog != null && mSplashDialog?.isShowing == true) {
                var isDestroyed = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    isDestroyed = _activity.isDestroyed
                }

                if (!_activity.isFinishing && !isDestroyed && waiting) {
                    mSplashDialog?.dismiss()
                    mSplashDialog = null
                }
            }
        }
    }

    fun hide(activity: Activity?) {
        var _activity = activity
        if (_activity == null) {
            _activity = mActivity?.get()
        }

        if (_activity == null) return

        waiting = true

        _activity.runOnUiThread {
            if (mSplashDialog != null && mSplashDialog?.isShowing == true) {
                var isDestroyed = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    isDestroyed = _activity.isDestroyed
                }

                if (!_activity.isFinishing && !isDestroyed && isAnimationFinished) {
                    mSplashDialog?.dismiss()
                    mSplashDialog = null
                }
            }
        }
    }
    @JvmStatic
    fun show(activity: Activity?, lottieId: Int, fullScreen: Boolean) {
        val resourceId = if (fullScreen) R.style.SplashScreen_Fullscreen else R.style.SplashScreen_SplashTheme
        show(activity, resourceId, lottieId, fullScreen)
    }

    private fun setActivityAndroidP(dialog: Dialog?) {
        // 设置全屏展示
        if (Build.VERSION.SDK_INT >= 28) {
            dialog?.window?.let { window ->
                window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) // 全屏显示
                val lp = window.attributes
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                window.attributes = lp
            }
        }
    }
}
