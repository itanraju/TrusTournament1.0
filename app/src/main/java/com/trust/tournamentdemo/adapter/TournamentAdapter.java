package com.trust.tournamentdemo.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.icu.text.NumberFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.trust.tournamentdemo.R;
import com.trust.tournamentdemo.Utils.ViewDialog;
import com.trust.tournamentdemo.activiy.MainActivity;
import com.trust.tournamentdemo.activiy.TournamentDetailActivity;
import com.trust.tournamentdemo.kprogresshud.KProgressHUD;
import com.trust.tournamentdemo.model.TournamentsModel;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.ViewHolder> {
    Context context;
    private ArrayList<TournamentsModel> tournamentDataArrayList;
    //Interstitial Ads
    private int id;
    public InterstitialAd mInterstitialAd;
    private KProgressHUD hud;

    SharedPreferences sp;
    SharedPreferences.Editor editor2;
    int click = 0;

    public TournamentAdapter(Context context, ArrayList<TournamentsModel> tournamentDataArrayList) {
        this.context = context;
        this.tournamentDataArrayList = tournamentDataArrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, joinedOrNotImage;
        LinearLayout join, idPass;
        TextView tournamentNo, tournamentName, map, mode, type, time, date, period, joinedPlayer, playerJoinedorNot;
        ProgressBar progressBar;
        RelativeLayout tournamentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            join = itemView.findViewById(R.id.join);
            idPass = itemView.findViewById(R.id.idPass);
            tournamentNo = itemView.findViewById(R.id.tournamentNo);
            tournamentName = itemView.findViewById(R.id.tournamentName);
            map = itemView.findViewById(R.id.map);
            type = itemView.findViewById(R.id.type);
            mode = itemView.findViewById(R.id.mode);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            period = itemView.findViewById(R.id.period);
            progressBar = itemView.findViewById(R.id.progressBar);
            joinedPlayer = itemView.findViewById(R.id.joinedPlayer);
            playerJoinedorNot = itemView.findViewById(R.id.playerJoinedorNot);
            joinedOrNotImage = itemView.findViewById(R.id.joinedOrNotImage);
            tournamentLayout = itemView.findViewById(R.id.tournamentLayout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.tournament_layout, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TournamentsModel tournamentsModel = tournamentDataArrayList.get(position);

        sp = context.getSharedPreferences("click", Activity.MODE_PRIVATE);
        editor2 = sp.edit();
        click = sp.getInt("click", 0);

        if (tournamentsModel.getVisibility().equals("no")) {
            holder.tournamentLayout.getLayoutParams().height = 0;
            holder.tournamentLayout.getLayoutParams().width = 0;
        } else {
            holder.tournamentLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.tournamentLayout.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        holder.tournamentNo.setText(tournamentsModel.getTournamentNo());
        holder.tournamentName.setText(tournamentsModel.getTournamentName());
        holder.map.setText(tournamentsModel.getMap());
        holder.mode.setText(tournamentsModel.getMode());
        holder.type.setText(tournamentsModel.getType());
        holder.time.setText(tournamentsModel.getTime());
        holder.period.setText(tournamentsModel.getTimeCounter());
        holder.date.setText(tournamentsModel.getDate());

        RequestOptions reqOpt = RequestOptions
                .fitCenterTransform()
                .transform(new RoundedCorners(5))
                .diskCacheStrategy(DiskCacheStrategy.ALL) // It will cache your image after loaded for first time
                .override(holder.image.getWidth(), holder.image.getHeight());

        Glide.with(context) //1
                .load(tournamentsModel.getImage())
                .placeholder(R.drawable.loading)
                .skipMemoryCache(true) //2
                .diskCacheStrategy(DiskCacheStrategy.ALL) //3
                .apply(reqOpt)
                .into(holder.image);


        final Boolean[] playerJoined = {true};
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.awmsound);

        ArrayList<String> arrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registrations").child(tournamentsModel.getTournamentNo());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    arrayList.add(dataSnapshot.getKey());
                    if (MainActivity.androidId.equals(dataSnapshot.getKey().toString())) {
                        playerJoined[0] = false;
                    }
                }

                int joinPercentage = (int) ((snapshot.getChildrenCount() * 100) / Integer.parseInt(tournamentsModel.getJoinedPlayers()));
                holder.joinedPlayer.setText(joinPercentage + " % Joined");
                holder.progressBar.setProgress(joinPercentage);

                if (playerJoined[0]) {
                    holder.playerJoinedorNot.setText("Please Join The Tournament");
                    Drawable placeholder = holder.joinedOrNotImage.getContext().getResources().getDrawable(R.drawable.notjoined);
                    holder.joinedOrNotImage.setImageDrawable(placeholder);
                } else {
                    holder.playerJoinedorNot.setText("You have Joined This Tournament");
                    Drawable placeholder = holder.joinedOrNotImage.getContext().getResources().getDrawable(R.drawable.joined);
                    holder.joinedOrNotImage.setImageDrawable(placeholder);
                }
                mInterstitialAd = new InterstitialAd(context);
                mInterstitialAd.setAdUnitId(context.getString(R.string.InterstitialAd_id));
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        switch (id) {
                            case 1:
                                String joined;
                                if (playerJoined[0]) {
                                    joined = "no";
                                } else {
                                    joined = "yes";
                                }
                                Gson gson = new Gson();
                                String data = gson.toJson(tournamentsModel);
                                Intent intent = new Intent(context, TournamentDetailActivity.class);
                                intent.putExtra("data", data);
                                intent.putExtra("playerJoined", joined);
                                context.startActivity(intent);
                                break;

                            case 2:
                                final MediaPlayer mediaPlayer2 = MediaPlayer.create(context, R.raw.joto);
                                mediaPlayer2.start();
                                ((MainActivity) context).idPassClick(new Boolean[]{playerJoined[0]}, tournamentsModel);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                click = click + 1;
                editor2.putInt("click", click);
                editor2.commit();
                if (click % 2 == 0) {

                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        try {
                            hud = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("Showing Ads").setDetailsLabel("Please Wait...");
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
                        String joined;
                        if (playerJoined[0]) {
                            joined = "no";
                        } else {
                            joined = "yes";
                        }
                        Gson gson = new Gson();
                        String data = gson.toJson(tournamentsModel);
                        Intent intent = new Intent(context, TournamentDetailActivity.class);
                        intent.putExtra("data", data);
                        intent.putExtra("playerJoined", joined);
                        context.startActivity(intent);
                    }

                } else {
                    String joined;
                    if (playerJoined[0]) {
                        joined = "no";
                    } else {
                        joined = "yes";
                    }
                    mediaPlayer.start();
                    Gson gson = new Gson();
                    String data = gson.toJson(tournamentsModel);
                    Intent intent = new Intent(context, TournamentDetailActivity.class);
                    intent.putExtra("data", data);
                    intent.putExtra("playerJoined", joined);
                    context.startActivity(intent);
                }
            }
        });

        holder.idPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    try {
                        hud = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("Showing Ads").setDetailsLabel("Please Wait...");
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
                    final MediaPlayer mediaPlayer2 = MediaPlayer.create(context, R.raw.joto);
                    mediaPlayer2.start();
                    ((MainActivity) context).idPassClick(new Boolean[]{playerJoined[0]}, tournamentsModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tournamentDataArrayList.size();
    }
}
