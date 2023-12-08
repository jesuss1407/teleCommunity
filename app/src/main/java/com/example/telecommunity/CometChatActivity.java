package com.example.telecommunity;

        import androidx.appcompat.app.AppCompatActivity;

        import android.os.Bundle;
        import android.util.Log;

        import com.cometchat.chat.core.AppSettings;
        import com.cometchat.chat.core.CometChat;
        import com.cometchat.chat.exceptions.CometChatException;


public class CometChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comet_chat);


        String appID = "249396b6f6292be5";
        String region = "us";

        AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region).build();

        CometChat.init(this, appID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d("CometChat", "Inicialización exitosa: " + successMessage);
                // Aquí puedes continuar con la lógica de tu chat
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("CometChat", "Error de inicialización: " + e.getMessage());
            }
        });






    }
}