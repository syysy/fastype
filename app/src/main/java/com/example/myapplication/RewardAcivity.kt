package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.RewardadBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardAcivity () : AppCompatActivity() {

    private lateinit var binding: RewardadBinding
    private var mRewardedAd: RewardedAd? = null
    private final var TAG = "RewardActivity"
    var loaded : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RewardadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        var adRequest = AdRequest.Builder().build()

        // faire un callback pour que la RewardActivity se lance seulement quand l'ad est chargée

        RewardedAd.load(this,"ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {

            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError?.toString()?.let { Log.d(TAG, it) }
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Toast.makeText(this@RewardAcivity, "Ad was loaded.", Toast.LENGTH_SHORT).show()
                mRewardedAd = rewardedAd
                loaded = true
                mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Toast.makeText(this@RewardAcivity, "Ad was clicked.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Toast.makeText(this@RewardAcivity, "Ad was dismissed.", Toast.LENGTH_SHORT).show()
                        mRewardedAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        // Called when ad fails to show.
                        Toast.makeText(this@RewardAcivity, "Ad failed to show.", Toast.LENGTH_SHORT).show()
                        mRewardedAd = null
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Toast.makeText(this@RewardAcivity, "Ad impression.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Toast.makeText(this@RewardAcivity, "Ad showed fullscreen content.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })


        binding.buttonLoadAd.setOnClickListener {
            if (mRewardedAd != null) {
                val activityContext = this
                mRewardedAd?.show(this) { rewardItem ->
                    // Handle the reward.
                    Toast.makeText(this, "The user earned the reward.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mRewardedAd != null) {
            mRewardedAd?.show(this) {
                // Reward the user.
                Toast.makeText(this, "The user earned the reward.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show()
        }

    }

}