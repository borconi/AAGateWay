package uk.co.borconi.emil.aagateway;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;



public class MainActivity extends AppCompatActivity {



    private static final String TAG = "AAGateWay";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getIntent().getAction()!=null && getIntent().getAction().equalsIgnoreCase("android.intent.action.MAIN")) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {Looper.prepare();}
                    catch (Exception e)
                    {}
                    boolean m_usb_mgr = false;
                    try {


                        Process p;
                        p = Runtime.getRuntime().exec("su");
                        DataOutputStream os = new DataOutputStream(p.getOutputStream());
                        os.writeBytes("cat /data/system/users/0/usb_device_manager.xml; \n exit \n");
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));

                        int read;
                        char[] buffer = new char[4096];
                        StringBuffer output = new StringBuffer();
                        while ((read = reader.read(buffer)) > 0) {
                            output.append(buffer, 0, read);
                        }
                        reader.close();

                        p.waitFor();
                        if (output.indexOf("Permission denied") > 0)
                        {
                            showerror(null);
                            return;
                        }
                        Log.d(TAG,"Outstream is: " + output.toString());
                        if (output.indexOf("uk.co.borconi.emil.aagateway") > 0)
                            m_usb_mgr = true;

                        if (!m_usb_mgr) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("No defaults set for Android Auto USB");
                            builder.setMessage("App needs to be set as default action for Android Auto. \n");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    try {
                                        Process p; //Nasty, single and double quotes do make a difference, if we don't set chmod to 666 it will fail
                                        p = Runtime.getRuntime().exec("su");
                                        DataOutputStream os = new DataOutputStream(p.getOutputStream());
                                        os.writeBytes("rm /data/system/usb_device_manager.xml; rm /data/system/users/0/usb_device_manager.xml;");
                                        os.writeBytes("echo '<?xml version='\\''1.0'\\'' encoding='\\''utf-8'\\'' standalone='\\''yes'\\'' ?>\n" +
                                                "<settings>\n" +
                                                "        <preference package=\"uk.co.borconi.emil.aagateway\">\n" +
                                                "        <usb-accessory manufacturer=\"Android\" model=\"Android Auto\" version=\"1.0.\" />\n" +
                                                "    </preference>\n" +
                                                "</settings>' > /data/system/usb_device_manager.xml\n"+
                                                "chmod 666 /data/system/usb_device_manager.xml\n"+
                                                "exit\n");
                                        os.flush();
                                        p.waitFor();
                                        showerror("All done, please reboot device and re-open program to check if settings where applied.");
                                        //Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
                                        Thread.sleep(2000);

                                    } catch (Exception e) {
                                        showerror(null);
                                    }


                                }
                            });

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    builder.create().show();
                                }});

                        } else {
                            showerror("Nothing to do here, AAGateWay is set as the default app for Android Auto, you're good to go.");

                        }
                    } catch (Exception e) {
                        showerror(null);
                    }

                }
            }).start();

            Button button = findViewById(R.id.button2);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    finish();


                }
            });
        }

    }

    @Override
        protected void onResume() {
        super.onResume();

        Intent paramIntent = getIntent();
        Intent i = new Intent(this, HackerService.class);

        if (paramIntent.getAction()!=null && paramIntent.getAction().equalsIgnoreCase("android.hardware.usb.action.USB_ACCESSORY_DETACHED")) {
            Log.d("AAG", "USB DISCONNECTED");
            stopService(i);
            finish();
        }
        else if (paramIntent.getAction()!=null && paramIntent.getAction().equalsIgnoreCase("android.hardware.usb.action.USB_ACCESSORY_ATTACHED")) {

           // findViewById(R.id.textView).setVisibility(View.VISIBLE);
            //((TextView)findViewById(R.id.textView)).setText(paramIntent.getParcelableExtra("accessory").toString());


                if (paramIntent.getParcelableExtra("accessory") != null) {
                    i.putExtra("accessory", paramIntent.getParcelableExtra("accessory"));
                    startService(i);
                    finish();
                }

        }

}
    private void showerror(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (message == null)
                    ((TextView) findViewById(R.id.textView)).setText("Your device is not rooted. \n While root is not a requirement for the app to work, giving initial permission to Android Auto will be very difficult without a screen. \n " +
                            "If you have already given permission to the app to be the default app for Android Auto, you can ignore this message. \n" +
                            "If you are using this app on a phone, there is no need for root, simply make sure you select AAGateWay when prompted \n" +
                            "Please visit XDA Thread for details: \n " +
                            "https://forum.xda-developers.com/general/paid-software/android-3-0-proxy-gateway-android-auto-t3813163");
                else
                    ((TextView) findViewById(R.id.textView)).setText(message);
                findViewById(R.id.textView).setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent paramIntent)
    {
        Log.i("MainActivity", "Got new intent: " + paramIntent);
        super.onNewIntent(paramIntent);
        setIntent(paramIntent);
    }


}
