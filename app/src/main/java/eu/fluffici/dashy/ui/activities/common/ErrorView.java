package eu.fluffici.dashy.ui.activities.common;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import eu.fluffici.dashy.ui.activities.MainActivity;
import eu.fluffici.dashy.R;

public class ErrorView extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_activity);

        boolean hasSuccess = this.getIntent().getBooleanExtra("isSuccess", false);

        if (hasSuccess) {
            PopupDialog.getInstance(this)
                    .setStyle(Styles.SUCCESS)
                    .setHeading(this.getIntent().getStringExtra("title"))
                    .setDescription(this.getIntent().getStringExtra("message"))
                    .setCancelable(false)
                    .setLottieRepeatCount(1)
                    .showDialog(new OnDialogButtonClickListener() {
                        @Override
                        public void onDismissClicked(Dialog dialog) {
                            super.onDismissClicked(dialog);

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    });
        }  else {
            PopupDialog.getInstance(this)
                    .setStyle(Styles.FAILED)
                    .setHeading(this.getIntent().getStringExtra("title"))
                    .setDescription(this.getIntent().getStringExtra("message"))
                    .setCancelable(false)
                    .setLottieRepeatCount(1)
                    .showDialog(new OnDialogButtonClickListener() {
                        @Override
                        public void onDismissClicked(Dialog dialog) {
                            super.onDismissClicked(dialog);

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        finish();
    }
}
