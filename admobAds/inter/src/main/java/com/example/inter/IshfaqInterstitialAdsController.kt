package com.example.inter

import android.app.Activity
import com.example.core.AdsController
import com.example.core.AdsLoadingStatusListener
import com.example.core.ad_units.IshfaqInterstitialAd
import com.example.core.ad_units.core.AdUnit
import com.example.core.commons.AdsCommons.logAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

internal class IshfaqInterstitialAdsController(
    private val adKey: String,
    private val adId: String,
) : AdsController {
    private var canRequestAd = true
    private var adEnabled = true
    private var currentInterstitialAd: IshfaqInterstitialAd? = null

    private var listener: AdsLoadingStatusListener? = null

    override fun setAdEnabled(enabled: Boolean) {
        adEnabled = enabled
    }

    override fun loadAd(context: Activity, callback: AdsLoadingStatusListener?) {
        logAds("loadAd function called,enabled=$adEnabled,")
        this.listener = callback
        if (adEnabled.not()) {
            listener?.onAdFailedToLoad("Ad is not enabled", -1)
            return
        }
        if (isAdRequesting()) {
            listener?.onAdFailedToLoad("Ad request is currently disabled", -1)
            return
        }
        if (isAdAvailable()) {
            listener?.onAdLoaded()
            return
        }
        canRequestAd = false
        logAds("Native Ad Requested key=$adKey")
        InterstitialAd.load(
            context,
            adId.ifBlank { "ca-app-pub-3940256099942544/1033173712" },
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interAd: InterstitialAd) {
                    super.onAdLoaded(interAd)

                    // If your app is going to request only one native ad at a time, set the currentNativeAd reference to null.
                    logAds("Native Ad Loaded key=$adKey")
                    currentInterstitialAd?.let {
                        it.destroyAd()
                    }
                    currentInterstitialAd = null
                    canRequestAd = true
                    currentInterstitialAd = AdmobInterstitialAd(interAd)
                    listener?.onAdLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    logAds("Native Ad Failed To Load key=$adKey")
                    canRequestAd = true
                    currentInterstitialAd = null
                    listener?.onAdFailedToLoad(error.message, error.code)
                }
            }
        )
    }

    override fun resetListener(context: Activity) {
        listener = null
    }

    override fun destroyAd(context: Activity) {
        currentInterstitialAd?.destroyAd()
        currentInterstitialAd = null
    }

    override fun getAdKey(): String {
        return adKey
    }

    override fun isAdAvailable(): Boolean {
        return currentInterstitialAd != null
    }

    override fun isAdRequesting(): Boolean {
        return canRequestAd.not()
    }

    override fun isAdAvailableOrRequesting(): Boolean {
        return isAdRequesting() || isAdAvailable()
    }

    override fun getAvailableAd(): AdUnit? {
        return currentInterstitialAd
    }


}