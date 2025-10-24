package com.huazi.gtads_honor

import android.app.Activity
import android.content.Context
import com.hihonor.adsdk.base.HnAds
import com.hihonor.adsdk.base.init.HnAdConfig
import com.huazi.gtads_honor.rewardvideoad.RewardVideoAd
import com.huazi.gtads_huawei.interstitialad.InterstitialAd
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** GtadsHuaweiPlugin */
class GtadsHonor : FlutterPlugin, MethodCallHandler, ActivityAware {
    private var applicationContext: Context? = null
    private var mActivity: Activity? = null

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.applicationContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "gtads_honor")
        channel.setMethodCallHandler(this)
        FlutterHuaweiAdEventPlugin().onAttachedToEngine(flutterPluginBinding)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        mActivity = binding.activity
//        Log.e("GtadsHuaweiPlugin->","onAttachedToActivity")
//        FlutterTencentAdViewPlugin.registerWith(mFlutterPluginBinding!!,mActivity!!)
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        mActivity = binding.activity
//        Log.e("GtadsHuaweiPlugin->","onReattachedToActivityForConfigChanges")
    }

    override fun onDetachedFromActivityForConfigChanges() {
        mActivity = null
//        Log.e("GtadsHuaweiPlugin->","onDetachedFromActivityForConfigChanges")
    }

    override fun onDetachedFromActivity() {
        mActivity = null
//        Log.e("GtadsHuaweiPlugin->","onDetachedFromActivity")
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "init" -> {
                val debug = call.argument<Boolean>("debug")
                val appId = call.argument<String>("appId")
                val appKey = call.argument<String>("appKey")

                // 构造广告配置
                val config = HnAdConfig.Builder()
                    // 设置您的媒体id，媒体id是您在荣耀广告平台注册的媒体id
                    .setAppId(appId)
                    // 设置您的appKey，appKey是您在荣耀广告平台注册的媒体id对应的密钥:
                    .setAppKey(appKey)
                    .setDebug(debug!!)
                    .useTestTools(appId == "1640545857217757184")
                    .setRewardListener {
                        // 获取激励动作类型
                        val action: Int = it.getInt("reward_action")
                        print("setRewardListener:${action}")
                    }
                    .build()

                // 调用初始化接口 context 与 config 不能为null，否则将会抛出异常
                HnAds.get().init(applicationContext, config)
                result.success(true)
            }

            "loadInterstitialAD" -> {
                InterstitialAd.init(mActivity!!, call.argument("androidId")!!)
                result.success(true)
            }

            "showInterstitialAD" -> {
                InterstitialAd.showAd()
                result.success(true)
            }

            "loadRewardVideoAd" -> {
                RewardVideoAd.init(mActivity!!, call.arguments as Map<*, *>)
                result.success(true)
            }

            "showRewardVideoAd" -> {
                RewardVideoAd.showAd()
                result.success(true)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
