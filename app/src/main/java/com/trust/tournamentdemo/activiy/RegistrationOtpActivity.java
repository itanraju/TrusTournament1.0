package com.trust.tournamentdemo.activiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.trust.tournamentdemo.R;
import com.google.firebase.FirebaseApp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.gson.Gson;
import com.trust.tournamentdemo.Utils.ViewDialog;
import com.trust.tournamentdemo.kprogresshud.KProgressHUD;
import com.trust.tournamentdemo.model.RegistrationModel;
import com.trust.tournamentdemo.model.TournamentsModel;

public class RegistrationOtpActivity extends AppCompatActivity {
    ImageView back, home;
    TextView tournamentNo;
    EditText whatsappNo, userName, uid;
    LinearLayout registerTournament;
    TournamentsModel tournamentsModel = new TournamentsModel();
    DatabaseReference databaseReference;
    private String android_id;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    ViewDialog alert = new ViewDialog();
    private AppCompatDialog dialog;
    private String sWhatsappNo, sUserName, sUid;

    //InterstitialAds
    private int id;
    public InterstitialAd mInterstitialAd;
    private KProgressHUD hud;
    private Activity activity = RegistrationOtpActivity.this;

    //RewardsAds
    private RewardedAd rewardedAd;

    SharedPreferences sp;
    SharedPreferences.Editor editor2;
    int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_otp);
        sharedPreferences = getSharedPreferences("RegistrationData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        bindView();
        interstitialAd();
        rewardsAds();

        sp = getSharedPreferences("click", Activity.MODE_PRIVATE);
        editor2 = sp.edit();
        click = sp.getInt("click", 0);

        setDataFromSharedPreference();
        Gson gson = new Gson();
        String data = getIntent().getStringExtra("data");
        tournamentsModel = gson.fromJson(data, TournamentsModel.class);
        tournamentNo.setText("#" + tournamentsModel.getTournamentNo() + " Tournament");
        android_id = Settings.Secure.getString(RegistrationOtpActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("TGA","Reg Detail : "+tournamentsModel.getTournamentNo());
        if (!isNetworkConnected()) {
            alert.showDialogForNoInternet(RegistrationOtpActivity.this, tournamentsModel.getTournamentNo(), tournamentsModel.getTournamentName());
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Registrations").child(tournamentsModel.getTournamentNo());
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.lightBlue));
        }
        FirebaseApp.initializeApp(RegistrationOtpActivity.this);

        registerTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer mediaPlayer = MediaPlayer.create(RegistrationOtpActivity.this, R.raw.awmsound);
                mediaPlayer.start();
                sWhatsappNo = whatsappNo.getText().toString();
                sUserName = userName.getText().toString();
                sUid = uid.getText().toString();

                if (TextUtils.isEmpty(sWhatsappNo)) {
                    whatsappNo.setError("Please enter whatsapp number !");
                    whatsappNo.requestFocus();
                } else if (TextUtils.isEmpty(sUserName)) {
                    userName.setError("Please enter Username !");
                    userName.requestFocus();
                } else if (TextUtils.isEmpty(sUid)) {
                    uid.setError("Please Enter ff UID !");
                    uid.requestFocus();
                } else {
                    editor.putString("whatsappNumber", sWhatsappNo);
                    editor.putString("userName", sUserName);
                    editor.putString("uid", sUid);
                    editor.putString("androidId", android_id);
                    editor.commit();
                    if (sWhatsappNo.length() != 10) {
                        whatsappNo.setError("Please enter correct number !");
                        whatsappNo.requestFocus();
                    } else {
                        hud = KProgressHUD.create(activity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("Showing Ads").setDetailsLabel("Please Wait...");
                        hud.show();
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

                                if (rewardedAd.isLoaded()) {
                                    Activity activityContext = RegistrationOtpActivity.this;
                                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                                        @Override
                                        public void onRewardedAdOpened() {
                                            // Ad opened.
                                        }

                                        @Override
                                        public void onRewardedAdClosed() {
                                            RegistrationOtpActivity.this.rewardedAd = createAndLoadRewardedAd();
                                        }

                                        @Override
                                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                                            dialog = new AppCompatDialog(RegistrationOtpActivity.this, R.style.DialogStyleLight);
                                            dialog.setContentView(R.layout.custom_progress_dialog_layout);
                                            dialog.setCancelable(true);
                                            dialog.setCanceledOnTouchOutside(true);
                                            dialog.show();
                                            registering();
                                        }

                                        @Override
                                        public void onRewardedAdFailedToShow(AdError adError) {
                                            // Ad failed to display.
                                        }
                                    };
                                    rewardedAd.show(activityContext, adCallback);
                                } else {
                                    dialog = new AppCompatDialog(RegistrationOtpActivity.this, R.style.DialogStyleLight);
                                    dialog.setContentView(R.layout.custom_progress_dialog_layout);
                                    dialog.setCancelable(true);
                                    dialog.setCanceledOnTouchOutside(true);
                                    dialog.show();
                                    registering();
                                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                                }

                            }
                        }, 2000);

                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    id = 2;
                                    mInterstitialAd.show();
                                }
                            }
                        }, 2000);
                    } else {
                        startActivity(new Intent(RegistrationOtpActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(RegistrationOtpActivity.this, MainActivity.class));
                    finish();
                }

            }
        });
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
    }

    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                dialog = new AppCompatDialog(RegistrationOtpActivity.this, R.style.DialogStyleLight);
                dialog.setContentView(R.layout.custom_progress_dialog_layout);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                registering();
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    private void rewardsAds() {
        rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    private void registering() {

        alert.showDialogForProgressTwoSecond(RegistrationOtpActivity.this);
        RegistrationModel registrationModel = new RegistrationModel();
        registrationModel.setWhatsappNmber(sWhatsappNo);
        registrationModel.setUserName(sUserName);
        registrationModel.setUid(sUid);
        registrationModel.setAndroidId(android_id);
        databaseReference.child(android_id).setValue(registrationModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showNotification(RegistrationOtpActivity.this,"Congratulations !","Your registration is succesfull for tournament "+tournamentsModel.getTournamentNo()+", Room ID and PASSWORD will be available on "+tournamentsModel.getDate()+" at "+tournamentsModel.getIdPassTime(),getIntent(),101);
                alert.showDialogForRegistration(RegistrationOtpActivity.this, tournamentsModel.getTournamentNo(), tournamentsModel.getTournamentName());
                dialog.dismiss();
            }
        });
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
                        startActivity(new Intent(RegistrationOtpActivity.this, MainActivity.class));
                        finish();
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


    private void setDataFromSharedPreference() {
        String sWhastappNo = sharedPreferences.getString("whatsappNumber", "");
        String sUserName = sharedPreferences.getString("userName", "");
        String sUid = sharedPreferences.getString("uid", "");

        whatsappNo.setText(sWhastappNo);
        userName.setText(sUserName);
        uid.setText(sUid);

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void bindView() {
        back = findViewById(R.id.back);
        home = findViewById(R.id.home);
        tournamentNo = findViewById(R.id.tournamentNo);
        registerTournament = findViewById(R.id.registerTournament);
        whatsappNo = findViewById(R.id.whatsappNumber);
        userName = findViewById(R.id.userName);
        uid = findViewById(R.id.uid);
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
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }
}