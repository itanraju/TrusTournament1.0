package com.trust.tournament.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.trust.tournament.R;
import com.trust.tournament.kprogresshud.KProgressHUD;
import com.trust.tournament.model.RedeemRoomModel;

import java.util.ArrayList;

public class RedeemRoomAdapter extends RecyclerView.Adapter<RedeemRoomAdapter.ViewHolder> {
    Context context;
    ArrayList<RedeemRoomModel> redeemCodeList;

    SharedPreferences sp;
    SharedPreferences.Editor editor2;
    int click=0;

    //Interstitial Ads
    private int id;
    public InterstitialAd mInterstitialAd;
    private KProgressHUD hud;

    public RedeemRoomAdapter(Context context, ArrayList<RedeemRoomModel> redeemCodeList) {
        this.context = context;
        this.redeemCodeList = redeemCodeList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView redeemCode;
        ImageView copy;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            redeemCode=itemView.findViewById(R.id.redeemCode);
            copy=itemView.findViewById(R.id.copy);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.redeemroom_layout, parent, false);
        return new RedeemRoomAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RedeemRoomModel redeemRoomModel=redeemCodeList.get(position);

        sp = context.getSharedPreferences("click", Activity.MODE_PRIVATE);
        editor2 = sp.edit();
        click = sp.getInt("click", 0);

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.InterstitialAd_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                switch (id) {
                    case 1:
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copied",redeemRoomModel.getCode());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, redeemRoomModel.getCode()+" Copied", Toast.LENGTH_SHORT).show();
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

        holder.redeemCode.setText(redeemRoomModel.getCode());
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = click + 1;
                editor2.putInt("click", click);
                editor2.commit();
                if (click % 2 == 0) {
                    if (mInterstitialAd!=null&&mInterstitialAd.isLoaded()){
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
                    }
                    else {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copied",redeemRoomModel.getCode());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, redeemRoomModel.getCode()+" Copied", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied",redeemRoomModel.getCode());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, redeemRoomModel.getCode()+" Copied", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return redeemCodeList.size();
    }
}
