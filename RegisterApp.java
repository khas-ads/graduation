import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
 
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
 
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobilhanem.webviewkullanimi.Anasayfa;
import com.mobilhanem.webviewkullanimi.SplashScreen;
 
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
  
  
public class RegisterApp extends AsyncTask<Void, Void, String> {
  
 private static final String TAG = "Mobilhanem GCM";
 Context ctx;
 GoogleCloudMessaging gcm;//Google Cloud referansı
 final String PROJECT_ID = "149270419021";//Bu değer Google Apı sayfasında Owerview menüsünde(Giriş sayfası) yukarıda yer alır. Project Number:987... şeklinde
 String regid = null;
 private int appVersion;
  
 public RegisterApp(Context ctx, GoogleCloudMessaging gcm, int appVersion){ //SplassScreen den gelen değerleri aldık
  this.ctx = ctx;
  this.gcm = gcm;
  this.appVersion = appVersion;
 }
   
   
 @Override
 protected void onPreExecute() {
  super.onPreExecute();
 }
  
  
 @Override
 protected String doInBackground(Void... arg0) { //
  String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(ctx);//GCM objesi oluşturduk ve gcm referansına bağladık
            }
            regid = gcm.register(PROJECT_ID);//gcm objesine PROJECT_ID mizi göndererek regid değerimizi aldık.Bu değerimizi hem sunucularımıza göndereceğiz Hemde Androidde saklıyacağız
            msg = "Registration ID=" + regid;
 
             
            sendRegistrationIdToBackend();//Sunuculara regid gönderme işlemini yapacak method
             
            storeRegistrationId(ctx, regid);//Androidde regid saklı tutacak method
             
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            
        }
        return msg;
 }
  
 private void storeRegistrationId(Context ctx, String regid) {//Androidde regid ve appversion saklı tutacak method
     //Burada SharedPreferences kullanarak kayıt yapmaktadır
     //SharedPreferences hakkında ayrıntılı dersi Bloğumuzda bulabilirsiniz.
   final SharedPreferences prefs = ctx.getSharedPreferences(SplashScreen.class.getSimpleName(),
             Context.MODE_PRIVATE);
     Log.i(TAG, "Saving regId on app version " + appVersion);
     SharedPreferences.Editor editor = prefs.edit();
     editor.putString("registration_id", regid);
     editor.putInt("appVersion", appVersion);
     editor.commit();
    
 }
  
  
 private void sendRegistrationIdToBackend() {//Sunucuya regid değerini gönderecek method
     //Arkadaşlar biz burda get methodu ile gönderdik .
     //Siz isterseniz post methoduda kullanabilirsiniz
     //HTTP Post ie ilgili dersimiz blog umuzda bulunmaktadır.
  URI url = null;
  try {
   url = new URI("http://www.mobilhanem.com/test/register.php?regId=" + regid);
  } catch (URISyntaxException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  HttpClient httpclient = new DefaultHttpClient();
  HttpGet request = new HttpGet();
  request.setURI(url);
  try {
   httpclient.execute(request);
  } catch (ClientProtocolException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  } catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }
  
  
 @Override
 protected void onPostExecute(String result) {
     //doInBackground işlemi bittikten sonra çalışır
  super.onPostExecute(result);
  Toast.makeText(ctx, "Registration Tamamlandı.Artık notification alabilirsiniz", Toast.LENGTH_SHORT).show();
  Log.v(TAG, result);
  Intent i = new Intent(ctx,Anasayfa.class);//Anasayfaya Yönlendir
  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
  i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
  ctx.startActivity(i);
  
 }
}
