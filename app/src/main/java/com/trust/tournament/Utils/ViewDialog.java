package com.trust.tournament.Utils;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trust.tournament.R;
import com.trust.tournament.activiy.MainActivity;

public class ViewDialog {

    //InterstitialAds

    public void showDialog(Activity activity, String no, String names, String newTime,String date) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);

        TextView tournamentNo = dialog.findViewById(R.id.tournamentNo);
        TextView time = dialog.findViewById(R.id.time);
        LinearLayout okeyThanks = dialog.findViewById(R.id.okeyThanks);
        TextView dateTxt=dialog.findViewById(R.id.date);

        tournamentNo.setText("#" + no + " " + names);
        time.setText(newTime + "");
        dateTxt.setText("On this "+date+" date");

        okeyThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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

        tournamentNo.setText("#" + no + " " + names);
        okeyThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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

        tournamentNo.setText("#" + no + " " + names);
        roomId.setText(String.valueOf(id2));
        roomPassword.setText(String.valueOf(password));

        copyId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", id2);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, id2 + " Copied", Toast.LENGTH_SHORT).show();
            }
        });
        copyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", password);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, password + " Copied", Toast.LENGTH_SHORT).show();
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
