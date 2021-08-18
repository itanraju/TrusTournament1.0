package com.trust.tournamentdemo.activiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.trust.tournamentdemo.R;
import com.trust.tournamentdemo.adapter.RedeemRoomAdapter;
import com.trust.tournamentdemo.adapter.TournamentAdapter;
import com.trust.tournamentdemo.kprogresshud.KProgressHUD;
import com.trust.tournamentdemo.model.RedeemRoomModel;
import com.trust.tournamentdemo.model.TournamentsModel;

import java.util.ArrayList;

public class FreeRedeemCodeActivity extends AppCompatActivity {
    private Activity activity = FreeRedeemCodeActivity.this;
    private TextView importantNotice;
    DatabaseReference databaseReference;
    private AppCompatDialog dialog;
    private LottieAnimationView winnerCup;
    private RecyclerView recyclerView;
    private ImageView back,refreshImg;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<RedeemRoomModel> redeemCodeList = new ArrayList<>();

    //Banner Ads
    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;

    //Interstitial Ads
    private int id;
    public InterstitialAd mInterstitialAd;
    private KProgressHUD hud;

    SharedPreferences sp;
    SharedPreferences.Editor editor2;
    int click=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_redeem_code);
        bindView();
        BannerAds();
        interstitialAd();

        sp = getSharedPreferences("click", Activity.MODE_PRIVATE);
        editor2 = sp.edit();
        click = sp.getInt("click", 0);

        importantNotice.setSelected(true);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.lightBlue));
        }
        dialog = new AppCompatDialog(FreeRedeemCodeActivity.this, R.style.DialogStyleLight);
        dialog.setContentView(R.layout.custom_progress_dialog_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("RedeemRoom");

        fetchData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        refreshImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                id = 4;
                                mInterstitialAd.show();
                            }
                        }
                    }, 2000);
                } else {
                    refreshing();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                                    id = 3;
                                    mInterstitialAd.show();
                                }
                            }
                        }, 2000);
                    } else {
                        refreshing();
                    }
            }
        });

        winnerCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = click + 1;
                editor2.putInt("click", click);
                editor2.commit();
                if (click % 2 == 0) {
                   gotoWinner();
                } else {
                    startActivity(new Intent(FreeRedeemCodeActivity.this,WinnerActivity.class));
                }

            }
        });
    }

    private void gotoWinner() {
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
                        id = 2;
                        mInterstitialAd.show();
                    }
                }
            }, 2000);
        } else {
            startActivity(new Intent(FreeRedeemCodeActivity.this, WinnerActivity.class));
        }
    }

    private void refreshing() {
        swipeRefreshLayout.setRefreshing(true);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(FreeRedeemCodeActivity.this, "Refresh", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    private void BannerAds() {
        try {
            adContainerView = findViewById(R.id.banner_ad_view_container);
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            float f = displayMetrics.density;
            float width = (float) adContainerView.getWidth();
            if (width == 0.0f) {
                width = (float) displayMetrics.widthPixels;
            }
            adSize = AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this, (int) (width / f));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) adContainerView.getLayoutParams();
            layoutParams.height = adSize.getHeightInPixels(this);
            adContainerView.setLayoutParams(layoutParams);
            adContainerView.post(new Runnable() {
                public final void run() {
                    ShowAds();
                }
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void ShowAds() {
        try {
            adView = new AdView(activity);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                }
            });
            adView.setAdUnitId(getString(R.string.Banner_ad_id));
            adContainerView.removeAllViews();
            adContainerView.addView(adView);
            adView.setAdSize(adSize);
            adView.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            e.printStackTrace();
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
                        onBackPressed();
                        break;
                    case 2:
                        startActivity(new Intent(FreeRedeemCodeActivity.this, WinnerActivity.class));
                        break;
                    case 3:
                        refreshing();
                        break;
                    case 4:
                        refreshing();
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

    private void fetchData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                redeemCodeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RedeemRoomModel redeemRoomModel = dataSnapshot.getValue(RedeemRoomModel.class);
                    redeemCodeList.add(redeemRoomModel);
                }
                RedeemRoomAdapter redeemRoomAdapter = new RedeemRoomAdapter(FreeRedeemCodeActivity.this, redeemCodeList);
                recyclerView.setLayoutManager(new LinearLayoutManager(FreeRedeemCodeActivity.this));
                recyclerView.setAdapter(redeemRoomAdapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void bindView() {
        importantNotice = findViewById(R.id.notice);
        winnerCup = findViewById(R.id.winner);
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recycleView);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        refreshImg=findViewById(R.id.refreshImg);
    }

    @Override
    public void onBackPressed() {
        click = click + 1;
        editor2.putInt("click", click);
        editor2.commit();
        if (click % 2 == 0) {
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
        } else {
            super.onBackPressed();
        }
    }
}