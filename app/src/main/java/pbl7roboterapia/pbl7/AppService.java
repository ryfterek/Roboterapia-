package pbl7roboterapia.pbl7;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class AppService extends Service {
    public AppService() {
    }

    private NotificationCompat.Builder notification2;
    private final static int NotiID2 = 153567;

    private final String HOST = "iot.eclipse.org";
    private final int PORT = 1883;
    private final String uri = "tcp://" + HOST + ":" + PORT;

    @Override
    public void onCreate() {
        super.onCreate();

        MqttAsyncClient client = null;
        try {
            client = new MqttAsyncClient(uri, MqttAsyncClient.generateClientId(), null);
            client.setCallback(new ServerCallback());
        }
        catch (MqttException e1) {
            //e1.printStackTrace();
        }

        MqttConnectOptions options = new MqttConnectOptions();
        try {
            client.connect(options);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.ConnectedToast), Toast.LENGTH_SHORT).show();
            SystemClock.sleep(2000);
        }
        catch (MqttException e) {
            //Log.d(getClass().getCanonicalName(), "Connection attempt failed with reason code = " + e.getReasonCode() + ":" + e.getCause());
        }

        try
        {
            client.subscribe("foo/Santiago/Simon", 0);
            Toast.makeText(getApplicationContext(), "Subscribed", Toast.LENGTH_SHORT).show();
        }
        catch (MqttException e)
        {
            //Log.d(getClass().getCanonicalName(), "Subscribe failed with reason code = " + e.getReasonCode());
            Toast.makeText(getApplicationContext(), "NOT Subscribed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class ServerCallback implements MqttCallback
    {
        public void connectionLost(Throwable cause)
        {
        }
        public void messageArrived(String topic, MqttMessage message)
        {
            notification2 = new NotificationCompat.Builder(getApplicationContext());
            notification2.setSmallIcon(R.mipmap.ic_launcher);
            notification2.setTicker("Once!");
            notification2.setWhen(System.currentTimeMillis());
            notification2.setContentTitle(topic);
            notification2.setContentText(message.toString());

            Intent intentNoti = new Intent(getApplicationContext(), IdleActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentNoti, PendingIntent.FLAG_UPDATE_CURRENT);
            notification2.setContentIntent(pendingIntent);

            NotificationManager nm2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm2.notify(NotiID2, notification2.build());
        }
        public void deliveryComplete(IMqttDeliveryToken token)
        {
        }
    }
}
