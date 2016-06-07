package pbl7roboterapia.pbl7;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

/** Based on http://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service */

public class AppService extends Service {

    public enum MQTTConnectionStatus
    {
        INITIAL,
        CREATED,
        CONNECTED,
        SUBSCRIBED,
    }
    private MQTTConnectionStatus connectionStatus = MQTTConnectionStatus.INITIAL;

    /** Hardcoding the MQTT broker's details here */
    private final String    HOST = "iot.eclipse.org";
    private final int       PORT = 1883;
    private final String    ADDR = "tcp://" + HOST + ":" + PORT;
    private final String    TOPIC = "foo/Santiago/Simon";
    private final int       QOS = 0;

    /** EXTRAS for intents */

    public final static String EXTRA_DIALOG_REASON = "pbl7roboterapia.pbl7.DIALOG_REASON";

    /** Variables for notifications */
    private NotificationCompat.Builder ongoingNotification;
    private final static int ongoingNotiID = 7001;

    /** MQTT client declared as global to grant access for all methods */
    private MqttAsyncClient client = null;

    /**Shared preferences variables to store sender flags and operation codes */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEdit;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    /****************************************************************************************************/


    /** Nothing interesting here */
    @Override
    public void onCreate() {
        super.onCreate();
        connectionStatus = MQTTConnectionStatus.INITIAL;
    }

    /** START_STICKY to ensure that the service will be restarted shall system kill it to release memory */
    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId)
    {
        handleStart();
        return START_STICKY;
    }

    /** Actual magic happens in this method */
    synchronized void handleStart()
    {
        /** Initial SharedPref values reset */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);
        sharedEdit = sharedPref.edit();
        sharedEdit.putString("STATE", States.STATES.IDLE.name());
        sharedEdit.putBoolean("SENDER", false);
        sharedEdit.putBoolean("VOLUNTEER", false);
        sharedEdit.putString("SIGNIFICANTOTHER", "NULL");
        sharedEdit.putString("VOLUNTEER1", "NULL");
        sharedEdit.putString("VOLUNTEER2", "NULL");
        sharedEdit.apply();
        Toast.makeText(getApplicationContext(), "App reset and ready", Toast.LENGTH_LONG).show();

        /** Initial WiFi check */
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork == null) {

                // NO CONNECTION
                Intent intent = new Intent(this, DialogActivity.class);
                intent.putExtra(EXTRA_DIALOG_REASON, 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else if (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI) {
                // CONNECTED NOT BY WIFI
/*                Intent intent = new Intent(this, DialogActivity.class);
                intent.putExtra(EXTRA_DIALOG_REASON, 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            }
        }
        catch (Exception e)
        {

        }

        /** Initializing a MQTT client */
        try {
            client = new MqttAsyncClient(ADDR, MqttAsyncClient.generateClientId(), null);
            client.setCallback(new ServerCallback());
            connectionStatus = MQTTConnectionStatus.CREATED;
        }
        catch (MqttException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_client_toast), Toast.LENGTH_LONG).show();
        }

        /** Connecting MQTT client to the broker */
        MqttConnectOptions options = new MqttConnectOptions();
        try {
            client.connect(options);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.connect_toast), Toast.LENGTH_SHORT).show();
            /** Short sleep added as connection needs some time before app can subscribe to a topic */
            SystemClock.sleep(500);
            connectionStatus = MQTTConnectionStatus.CONNECTED;
        }
        catch (MqttException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_connect_toast), Toast.LENGTH_LONG).show();
        }

        /** Subscribing MQTT client to the topic */
        try {
            client.subscribe(TOPIC, QOS);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.subscribe_toast), Toast.LENGTH_SHORT).show();
            connectionStatus = MQTTConnectionStatus.SUBSCRIBED;
        }
        catch (MqttException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_subscribe_toast), Toast.LENGTH_LONG).show();
        }

        /** Setting up an ongoing notification to 1) remind users that the app is running 2) alert them if the service stopped */
        if (connectionStatus == MQTTConnectionStatus.SUBSCRIBED){
            ongoingNotification = new NotificationCompat.Builder(this);
            ongoingNotification.setSmallIcon(R.mipmap.ic_launcher);
            ongoingNotification.setWhen(System.currentTimeMillis());
            ongoingNotification.setContentTitle(getResources().getString(R.string.app_name));
            ongoingNotification.setContentText(getResources().getString(R.string.ongoing_text));
            ongoingNotification.setOngoing(true);

            Intent intentNoti = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNoti, PendingIntent.FLAG_UPDATE_CURRENT);
            ongoingNotification.setContentIntent(pendingIntent);

            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(ongoingNotiID, ongoingNotification.build());
        }else{
            // TODO: Setup failure has to be handled somehow.
            Intent intent = new Intent(this, DialogActivity.class);
            intent.putExtra(EXTRA_DIALOG_REASON, 2);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /** Unsubscribing & Disconnecting in when service is disabled */
    @Override
    public void onDestroy() {
        super.onDestroy();
        /** Unsubscribing */
        try {
            IMqttToken unsubToken = client.unsubscribe(TOPIC);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.unsubscribe_toast), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        /** Disconnecting */
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.disconnect_toast), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        /** Dismissing the ongoing notification */
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(ongoingNotiID);
    }

    /** Method used to push a message to MQTT server */
    public void publishMessage(int opcode) {

        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);
        String payload = opcode+":"+sharedPref.getString("USERNAME", "ERROR");

        try {
            MqttMessage message = new MqttMessage(payload.getBytes("UTF-8"));
            client.publish(TOPIC, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public class ServerCallback implements MqttCallback
    {
        public void connectionLost(Throwable cause) {
            // TODO: Handle loss of connection
            Toast.makeText(getApplicationContext(), "This is Toast", Toast.LENGTH_SHORT).show();
        }

        /** Here is handled the arrival of a message from broker */
        public void messageArrived(String topic, MqttMessage message)
        {
            /** Asking external method to handle the arrival of the message */
            HandleMessage handleMessage = new HandleMessage(getApplicationContext());
            handleMessage.handle(message.toString());
        }
        public void deliveryComplete(IMqttDeliveryToken token)
        {
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public AppService getServerInstance() {
            return AppService.this;
        }
    }
}
