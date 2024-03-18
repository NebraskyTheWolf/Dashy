package eu.fluffici.dashy.ui.base;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import eu.fluffici.dashy.ui.activities.auth.LoginActivity;
import eu.fluffici.dashy.utils.Storage;

public class PDAAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Storage.getAccessToken(getApplicationContext()) == null && Storage.isAuthentified(getApplicationContext())) {
            Storage.removeAll(getApplicationContext());
        }

        if (!Storage.isAuthentified(getApplicationContext())) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(i.getFlags()
                    | FLAG_ACTIVITY_CLEAR_TOP
                    | FLAG_ACTIVITY_NEW_TASK
                    | FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(i);

            return;
        }
    }
}
