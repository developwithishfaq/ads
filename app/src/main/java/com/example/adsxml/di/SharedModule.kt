package com.example.adsxml.di

import com.example.banner_ads.BannerAdsManager
import com.example.core.IshfaqAdsSdk
import com.example.inter.InterstitialAdsManager
import com.example.native_ads.NativeAdsManager
import org.koin.dsl.module

val SharedModule = module {
    single {
        NativeAdsManager()
    }
    single {
        IshfaqAdsSdk()
    }
    single {
        InterstitialAdsManager()
    }
    single {
        BannerAdsManager()
    }
    single {
        IshfaqAdsSdk()
    }
}