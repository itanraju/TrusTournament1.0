package com.trust.tournamentdemo.activiy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.trust.tournamentdemo.R;
import com.google.gson.Gson;
import com.trust.tournamentdemo.kprogresshud.KProgressHUD;
import com.trust.tournamentdemo.model.TournamentsModel;

public class TournamentDetailActivity extends AppCompatActivity {
    private com.google.android.material.button.MaterialButton registrationTab;
    TextView winner,r1,r2,r3,r4,r5,r6,r7,r8,r9,r10;
    ImageView image;
    private String playerJoined;
    TournamentsModel tournamentsModel=new TournamentsModel();

    //InterstitialAds
    private int id;
    public InterstitialAd mInterstitialAd;
    private KProgressHUD hud;
    private Activity activity=TournamentDetailActivity.this;

    SharedPreferences sp;
    SharedPreferences.Editor editor2;
    int click=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_detail);
        sp = getSharedPreferences("click", Activity.MODE_PRIVATE);
        editor2 = sp.edit();
        click = sp.getInt("click", 0);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window =this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.lightBlue));
        }
        bindView();
        interstitialAd();
        if(!isNetworkConnected())
        {
            Toast.makeText(this, "Check your internet connection !", Toast.LENGTH_LONG).show();
        }


        Gson gson = new Gson();
        String data = getIntent().getStringExtra("data");
        playerJoined=getIntent().getStringExtra("playerJoined");
        tournamentsModel = gson.fromJson(data, TournamentsModel.class);
        setData();

        registrationTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer mediaPlayer = MediaPlayer.create(TournamentDetailActivity.this, R.raw.joto);
                mediaPlayer.start();
                click = click + 1;
                editor2.putInt("click", click);
                editor2.commit();
                if (click % 2 == 0) {
                    goToRegistration();
                } else {
                    if (mInterstitialAd!=null&&mInterstitialAd.isLoaded()){
                        try {
                            hud = KProgressHUD.create(activity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("Showing Ads").setDetailsLabel("Please Wait...");
                            hud.show();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e2) {
                            e2.printStackTrace();
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    hud.dismiss();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();

                                } catch (NullPointerException e2) {
                                    e2.printStackTrace();
                                } catch (Exception e3) {
                                    e3.printStackTrace();
                                }
                                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                                    id = 1;
                                    mInterstitialAd.show();
                                }
                            }
                        }, 2000);
                    }
                    else {
                        goToRegistration();
                    }
                }
            }
        });
    }

    private void goToRegistration() {
        if(playerJoined.equals("yes"))
        {
            Toast.makeText(TournamentDetailActivity.this, "You are already joined this tournament !", Toast.LENGTH_LONG).show();
        }
        else
        {
            Gson gson=new Gson();
            String data=gson.toJson(tournamentsModel);
            Intent intent=new Intent(TournamentDetailActivity.this,RegistrationOtpActivity.class);
            intent.putExtra("data",data);
            startActivity(intent);
        }
    }

    private void interstitialAd() {
        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.InterstitialAd_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestInterstitial();
                switch (id) {
                    case 1:
                        goToRegistration();
                        break;
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

            }
        });
    }

    private void requestInterstitial() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void setData() {
        Glide.with(TournamentDetailActivity.this) //1
                .load(tournamentsModel.getImage())
                .placeholder(R.drawable.loading)
                .skipMemoryCache(true) //2
                .diskCacheStrategy(DiskCacheStrategy.DATA) //3
                .into(image);
        winner.setText(tournamentsModel.getWinner());
        r1.setText(tournamentsModel.getR1());
        r2.setText(tournamentsModel.getR2());
        r3.setText(tournamentsModel.getR3());
        r4.setText(tournamentsModel.getR4());
        r5.setText(tournamentsModel.getR5());
        r6.setText(tournamentsModel.getR6());
        r7.setText(tournamentsModel.getR7());
        r8.setText(tournamentsModel.getR8());
        r9.setText(tournamentsModel.getR9());
        r10.setText(tournamentsModel.getR10());

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void bindView() {
        registrationTab=findViewById(R.id.registrationTab);
        image=findViewById(R.id.image);
        winner=findViewById(R.id.winner);
        r1=findViewById(R.id.r1);
        r2=findViewById(R.id.r2);
        r3=findViewById(R.id.r3);
        r4=findViewById(R.id.r4);
        r5=findViewById(R.id.r5);
        r6=findViewById(R.id.r6);
        r7=findViewById(R.id.r7);
        r8=findViewById(R.id.r8);
        r9=findViewById(R.id.r9);
        r10=findViewById(R.id.r10);
    }

    @Override
    public void onBackPressed() {
        click = click + 1;
        editor2.putInt("click", click);
        editor2.commit();
        if (click % 2 == 0) {
            super.onBackPressed();
        } else {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                try {
                    hud = KProgressHUD.create(activity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("Showing Ads").setDetailsLabel("Please Wait...");
                    hud.show();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NullPointerException e2) {
                    e2.printStackTrace();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            hud.dismiss();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();

                        } catch (NullPointerException e2) {
                            e2.printStackTrace();
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                            id = 1;
                            mInterstitialAd.show();
                        }
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }
    }
}