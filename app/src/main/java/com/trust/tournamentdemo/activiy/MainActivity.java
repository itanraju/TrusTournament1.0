package com.trust.tournamentdemo.activiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.trust.tournamentdemo.R;
import com.trust.tournamentdemo.Utils.ViewDialog;
import com.trust.tournamentdemo.adapter.TournamentAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trust.tournamentdemo.kprogresshud.KProgressHUD;
import com.trust.tournamentdemo.model.TournamentsModel;
import com.trust.tournamentdemo.pref.EPreferences;

import java.util.ArrayList;

import static com.trust.tournamentdemo.nativeAds.NativeAdMethod.populateUnifiedNativeAdView;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout freeRedeemRoom;
    private LottieAnimationView winner, youtube,instagram;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView optionMenu;

    public static ArrayList<TournamentsModel> tournamentDataArrayList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private LinearLayout noInternentLayout;
    public static String androidId;
    private String android_id;

    SharedPreferences sp;
    SharedPreferences.Editor editor2;
    int click = 0;

    //Ads
    private int id;
    private Activity activity = MainActivity.this;
    public InterstitialAd mInterstitialAd;
    private KProgressHUD hud;

    //Native Ads
    private EPreferences ePreferences;
    private UnifiedNativeAd nativeAd;

    private AppCompatDialog dialog;

    @Override
    public void onBackPressed() {
        if (this.ePreferences.getBoolean("dialog_pref", false)) {
            ExitDialog();
        } else {
            RateDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callInupdate();
        sp = getSharedPreferences("click", Activity.MODE_PRIVATE);
        editor2 = sp.edit();
        click = sp.getInt("click", 0);
        ePreferences = EPreferences.getInstance((Context) this);
        FirebaseMessaging.getInstance().subscribeToTopic("notification");
        android_id = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Intent intentBackgroundService = new Intent(this, FirebasePushNotificationClass.class);
        startService(intentBackgroundService);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.lightBlue));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Tournaments");
        sharedPreferences = getSharedPreferences("RegistrationData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        androidId = sharedPreferences.getString("androidId", android_id);

        bindView();
        interstitialAd();

        if (!isNetworkConnected()) {
            recyclerView.setVisibility(View.GONE);
            freeRedeemRoom.setVisibility(View.GONE);
            noInternentLayout.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Check your internet connection !", Toast.LENGTH_LONG).show();
        }

        dialog = new AppCompatDialog(MainActivity.this, R.style.DialogStyleLight);
        dialog.setContentView(R.layout.custom_progress_dialog_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        winner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = click + 1;
                editor2.putInt("click", click);
                editor2.commit();
                if (click % 2 == 0) {
                    Log.e("click", click + "  click divided by 2");
                    gotoWinner();
                } else {
                    Log.e("click", click + "  click not divided by 2");
                    Intent intent = new Intent(MainActivity.this, WinnerActivity.class);
                    startActivity(intent);
                }
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/trustournament.official/");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.instagram.com/trustournament.official/")));
                }
            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                String url = "https://www.youtube.com/channel/UC22tWXQw8PNGXC6xPi3HsNQ";
                try {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("com.google.android.youtube");
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }
        });
        freeRedeemRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = click + 1;
                editor2.putInt("click", click);
                editor2.commit();
                System.out.println(click);
                if (click % 2 == 0) {
                    gotoRedeemCode();
                } else {
                    startActivity(new Intent(MainActivity.this, FreeRedeemCodeActivity.class));
                }
            }
        });
        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, optionMenu);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.privacyAndPolicy) {
                            Intent viewIntent =
                                    new Intent("android.intent.action.VIEW",
                                            Uri.parse("https://trustournament.flycricket.io/privacy.html"));
                            startActivity(viewIntent);
                        }
                        if (item.getItemId() == R.id.termsAndCondition) {
                            Intent viewIntent =
                                    new Intent("android.intent.action.VIEW",
                                            Uri.parse("https://trustournament.flycricket.io/terms.html"));
                            startActivity(viewIntent);
                        }
                        if (item.getItemId() == R.id.rateUse) {
                            final String appPackageName = getPackageName(); // package name of the app
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                        if (item.getItemId() == R.id.shareApp) {
                            try {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "TrusTournament");
                                String shareMessage= "Download this app for playing free tournaments and get many diamonds and redeem code for free\n\n";
                                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.trust.tournament";
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                                startActivity(Intent.createChooser(shareIntent, "choose one"));
                            } catch(Exception e) {
                                //e.toString();
                            }
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void gotoRedeemCode() {
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
            startActivity(new Intent(MainActivity.this, FreeRedeemCodeActivity.class));
        }
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
                        id = 1;
                        mInterstitialAd.show();
                    }
                }
            }, 2000);
        } else {
            Intent intent = new Intent(MainActivity.this, WinnerActivity.class);
            startActivity(intent);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void RateDialog() {
        final boolean[] isRate = {false, false};
        final Dialog dialog = new Dialog(MainActivity.this);
        final ImageView ivStar1, ivStar2, ivStar3, ivStar4, ivStar5;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.PauseDialogAnimation1);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCanceledOnTouchOutside(false);

        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.NativeAdvanceAd_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout = dialog.findViewById(R.id.fl_adplaceholder);
                dialog.findViewById(R.id.tvLoadAds).setVisibility(View.GONE);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.ad_unified_small, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });
        VideoOptions videoOptions = new VideoOptions.Builder().build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

        ivStar1 = dialog.findViewById(R.id.ivStar1);
        ivStar2 = dialog.findViewById(R.id.ivStar2);
        ivStar3 = dialog.findViewById(R.id.ivStar3);
        ivStar4 = dialog.findViewById(R.id.ivStar4);
        ivStar5 = dialog.findViewById(R.id.ivStar5);
        ivStar1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_empty);
                ivStar3.setImageResource(R.drawable.star_empty);
                ivStar4.setImageResource(R.drawable.star_empty);
                ivStar5.setImageResource(R.drawable.star_empty);
                isRate[0] = false;
                isRate[1] = true;
                return false;
            }
        });
        ivStar2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_fill);
                ivStar3.setImageResource(R.drawable.star_empty);
                ivStar4.setImageResource(R.drawable.star_empty);
                ivStar5.setImageResource(R.drawable.star_empty);
                isRate[0] = false;
                isRate[1] = true;
                return false;
            }
        });
        ivStar3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_fill);
                ivStar3.setImageResource(R.drawable.star_fill);
                ivStar4.setImageResource(R.drawable.star_empty);
                ivStar5.setImageResource(R.drawable.star_empty);
                isRate[0] = false;
                isRate[1] = true;
                return false;
            }
        });
        ivStar4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_fill);
                ivStar3.setImageResource(R.drawable.star_fill);
                ivStar4.setImageResource(R.drawable.star_fill);
                ivStar5.setImageResource(R.drawable.star_empty);
                isRate[0] = true;
                isRate[1] = true;
                return false;
            }
        });
        ivStar5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_fill);
                ivStar3.setImageResource(R.drawable.star_fill);
                ivStar4.setImageResource(R.drawable.star_fill);
                ivStar5.setImageResource(R.drawable.star_fill);
                isRate[0] = true;
                isRate[1] = true;
                return false;
            }
        });
        dialog.findViewById(R.id.btnLater).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finishAffinity();
            }
        });
        dialog.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRate[1]) {
                    ePreferences.putBoolean("pref_key_rate", true);
                    dialog.dismiss();
                    if (isRate[0]) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.trust.tournament")));
                        } catch (ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.trust.tournament")));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
                    }
                    finishAffinity();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Your Review Star", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void ExitDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.PauseDialogAnimation1);
        dialog.setContentView(R.layout.dialog_layout_exit);
        dialog.setCanceledOnTouchOutside(false);
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.NativeAdvanceAd_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout = dialog.findViewById(R.id.fl_adplaceholder);
                dialog.findViewById(R.id.tvLoadAds).setVisibility(View.GONE);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.ad_unified_small, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });
        VideoOptions videoOptions = new VideoOptions.Builder().build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
        dialog.findViewById(R.id.btnLater).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        dialog.show();
    }

    public void idPassClick(Boolean[] playerJoined, TournamentsModel tournamentsModel) {

        if (playerJoined[0]) {
            ViewDialog alert = new ViewDialog();
            alert.showDialogForNotRegistration(activity, tournamentsModel.getTournamentNo(), tournamentsModel.getTournamentName());
        } else {
            if (tournamentsModel.getIsAvailableIdPass().equals("yes")) {
                ViewDialog alert = new ViewDialog();
                alert.showDialogForRoomIdPassword(activity, tournamentsModel.getTournamentNo(), tournamentsModel.getTournamentName(), tournamentsModel.getRoomId(), tournamentsModel.getRoomPassword());
            } else {
                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, tournamentsModel.getTournamentNo(), tournamentsModel.getTournamentName(), tournamentsModel.getIdPassTime(),tournamentsModel.getDate());
            }
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
                        startActivity(new Intent(MainActivity.this, WinnerActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, FreeRedeemCodeActivity.class));
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

    private void fetchTournamentData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tournamentDataArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TournamentsModel tournamentsModel = dataSnapshot.getValue(TournamentsModel.class);
                    tournamentDataArrayList.add(tournamentsModel);
                }
                TournamentAdapter tournamentAdapter = new TournamentAdapter(MainActivity.this, tournamentDataArrayList);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(tournamentAdapter);

                recyclerView.setVisibility(View.VISIBLE);
                freeRedeemRoom.setVisibility(View.VISIBLE);
                noInternentLayout.setVisibility(View.GONE);

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        callInupdate();
    }

    private final int UPDATE_REQUEST_CODE = 1612;

    @Override
    protected void onStart() {
        super.onStart();
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                fetchTournamentData();
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }

    private void callInupdate() {

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {

                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE
                            , MainActivity.this, UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException exception) {
                    Log.d("TAG", "callInupdate: " + exception.getMessage());
                }
                // Request the update.
            }
        });
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void bindView() {
        winner = findViewById(R.id.winner);
        freeRedeemRoom = findViewById(R.id.freeRedeemRoom);
        optionMenu = findViewById(R.id.optionMenu);
        recyclerView = findViewById(R.id.recycleView);
        noInternentLayout = findViewById(R.id.noInternentLayout);
        winner = findViewById(R.id.winner);
        youtube = findViewById(R.id.youtube);
        instagram=findViewById(R.id.instagram);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        if (requestCode == UPDATE_REQUEST_CODE) {
            Toast.makeText(this, "Downloading start", Toast.LENGTH_LONG).show();
            if (resultCode != RESULT_OK) {
                Log.d("TAG", "onActivityResult: Update flow failed" + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }

        }
    }
}