package com.trust.tournamentdemo.activiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.trust.tournamentdemo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trust.tournamentdemo.adapter.TournamentAdapter;
import com.trust.tournamentdemo.adapter.WinnerAdapter;
import com.trust.tournamentdemo.kprogresshud.KProgressHUD;
import com.trust.tournamentdemo.model.TournamentsModel;
import com.trust.tournamentdemo.model.WinnerModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class WinnerActivity extends AppCompatActivity {
    private RecyclerView winnerRecycleView;
    private TextView tournamentName;
    private KonfettiView konfettiView;
    private AppCompatDialog dialog;
    private ImageView back,home;
    private DatabaseReference databaseReference;
    private ArrayList<WinnerModel> winnerModelArrayList=new ArrayList<>();

    //InterstitialAds
    private int id;
    public InterstitialAd mInterstitialAd;
    private KProgressHUD hud;
    private Activity activity=WinnerActivity.this;

    SharedPreferences sp;
    SharedPreferences.Editor editor2;
    int click=0;

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
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        interstitialAd();
        databaseReference=FirebaseDatabase.getInstance().getReference("Winners");

        dialog = new AppCompatDialog(WinnerActivity.this, R.style.DialogStyleLight);
        dialog.setContentView(R.layout.custom_progress_dialog_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

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
        fetchData();

        if(!isNetworkConnected())
        {
            Toast.makeText(this, "Check your internet connection !", Toast.LENGTH_LONG).show();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                winnerModelArrayList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    WinnerModel winnerModel=dataSnapshot.getValue(WinnerModel.class);
                    winnerModelArrayList.add(winnerModel);
                }

                WinnerAdapter winnerAdapter=new WinnerAdapter(WinnerActivity.this,winnerModelArrayList);
                winnerRecycleView.setLayoutManager(new LinearLayoutManager(WinnerActivity.this));
                winnerRecycleView.setAdapter(winnerAdapter);

                dialog.dismiss();

                konfettiView.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 4f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(50000L)
                        .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                        .addSizes(new Size(10, 4f))
                        .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                        .streamFor(350, 100000L);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void bindView() {
        tournamentName=findViewById(R.id.tournamentName);
        konfettiView=findViewById(R.id.viewKonfetti);
        back=findViewById(R.id.back);
        home=findViewById(R.id.home);
        winnerRecycleView=findViewById(R.id.winnerRecycleView);
    }


}