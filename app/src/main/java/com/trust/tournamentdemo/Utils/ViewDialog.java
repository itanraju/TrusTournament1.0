package com.trust.tournamentdemo.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.trust.tournamentdemo.R;
import com.trust.tournamentdemo.activiy.MainActivity;
import com.trust.tournamentdemo.kprogresshud.KProgressHUD;

public class ViewDialog {

    //InterstitialAds

    private int id;
    public InterstitialAd mInterstitialAd;
    private KProgressHUD hud;

    public void showDialog(Activity activity, String no, String names, String newTime) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);

        TextView tournamentNo = dialog.findViewById(R.id.tournamentNo);
        TextView time = dialog.findViewById(R.id.time);
        LinearLayout okeyThanks = dialog.findViewById(R.id.okeyThanks);

        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(activity.getString(R.string.InterstitialAd_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                switch (id) {
                    case 1:
                        dialog.dismiss();
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

        tournamentNo.setText("#" + no + " " + names);
        time.setText(newTime + "");

        okeyThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    public void showDialogForNotRegistration(Activity activity, String no, String names) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.not_registered_dialog_layout);

        TextView tournamentNo = dialog.findViewById(R.id.tournamentNo);
        LinearLayout okeyThanks = dialog.findViewById(R.id.okeyThanks);

        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(activity.getString(R.string.InterstitialAd_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                switch (id) {
                    case 1:
                        dialog.dismiss();
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

        tournamentNo.setText("#" + no + " " + names);
        okeyThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void showDialogForRoomIdPassword(Context context, String no, String names, String id2, String password) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.join_custom_dialog_layout);

        TextView tournamentNo = dialog.findViewById(R.id.tournamentNo);
        TextView roomId = dialog.findViewById(R.id.roomId);
        TextView roomPassword = dialog.findViewById(R.id.roomPassword);
        ImageView copyId = dialog.findViewById(R.id.copyRoomId);
        ImageView copyPassword = dialog.findViewById(R.id.copyRoomPassword);
        ImageView close = dialog.findViewById(R.id.close);

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.InterstitialAd_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                switch (id) {
                    case  1:
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copied", id2);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, id2 + " Copied", Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        ClipboardManager clipboard2 = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip2 = ClipData.newPlainText("Copied", password);
                        clipboard2.setPrimaryClip(clip2);
                        Toast.makeText(context, password + " Copied", Toast.LENGTH_SHORT).show();
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

        tournamentNo.setText("#" + no + " " + names);
        roomId.setText(String.valueOf(id2));
        roomPassword.setText(String.valueOf(password));

        copyId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    ClipData clip = ClipData.newPlainText("Copied", id2);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, id2 + " Copied", Toast.LENGTH_SHORT).show();
                }
            }
        });
        copyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                id = 2;
                                mInterstitialAd.show();
                            }
                        }
                    }, 2000);
                }
                else {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied", password);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, password + " Copied", Toast.LENGTH_SHORT).show();
                }

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialogForRegistration(Activity activity, String no, String names) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.congratulations_dialog_layout);

        TextView tournamentNo = dialog.findViewById(R.id.tournamentNo);
        LinearLayout okeyThanks = dialog.findViewById(R.id.okeyThanks);

        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(activity.getString(R.string.InterstitialAd_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                switch (id) {
                    case 1:
                        dialog.dismiss();
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
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

        tournamentNo.setText("#" + no + " " + names);

        okeyThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    dialog.dismiss();
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();
                }
            }
        });

        dialog.show();
    }

    public void showDialogForProgress(Activity activity, int id) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_progress_dialog_layout);
        dialog.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // Close dialog after 1000ms
                dialog.cancel();
            }
        }, 4000);
    }

    public void showDialogForProgressTwoSecond(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_progress_dialog_layout);
        dialog.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Close dialog after 1000ms
                dialog.cancel();
            }
        }, 2000);
    }

    public void showDialogForNoInternet(Activity activity, String no, String names) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.no_internet_dialog);

        TextView tournamentNo = dialog.findViewById(R.id.tournamentNo);
        LinearLayout okeyThanks = dialog.findViewById(R.id.okeyThanks);

        tournamentNo.setText("#" + no + " " + names);

        okeyThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }
        });

        dialog.show();
    }

}
