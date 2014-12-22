import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobilhanem.gcm.RegisterApp;
 
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;
  
public class SplashScreen extends Activity {
   
 private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
 public static final String PROPERTY_REG_ID = "registration_id";
 private static final String PROPERTY_APP_VERSION = "appVersion";
 private static final String TAG = "Mobilhanem GCM";
 GoogleCloudMessaging gcm;
 String regid;
  
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.splash);
 
   
  if (checkPlayServices()) {//GOOGLE PLAY SERVİCE APK YÜKLÜMÜ
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());//GoogleCloudMessaging objesi oluşturduk
        regid = getRegistrationId(getApplicationContext()); //registration_id olup olmadığını kontrol ediyoruz
         
        if(regid.isEmpty()){//YENİ KAYIT
              //regid değerimiz boş gelmişse uygulama ya ilk kez acılıyor yada güncellenmiş demektir.Registration işlemleri tekrardan yapılacak.
              new RegisterApp(getApplicationContext(), gcm, getAppVersion(getApplicationContext())).execute(); //RegisterApp clasını çalıştırıyoruz ve değerleri gönderiyoruz
        }else{
              //regid değerimiz boş gelmemişse önceden registration işlemleri tamamlanmış ve güncelleme olmamış demektir.Yani uygulama direk açılacak
              //Arkadaşlar eğer splash ekranının gözükmesini istiyorsanız thread kullanıp 2 3 sn bekletebilirsiniz.Daha sonra aşağıdaki işlemlere başlayabilirsiniz
 
               Toast.makeText(getApplicationContext(), "Bu cihaz önceden kaydedilmiş", Toast.LENGTH_SHORT).show();
               Intent i = new Intent(getApplicationContext(),Anasayfa.class);//Anasayfaya Yönlendir
               startActivity(i);
               finish();
           }
           
  }
    
 }
  
 private boolean checkPlayServices() {
    //Google Play Servis APK yüklümü
     //Yüklü Değilse Log basıp kapatıcak uygulamayı
     //Siz kullanıcıya uyarı verdirip Google Play Apk Kurmasını isteyebilirsiniz
      
     int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
     if (resultCode != ConnectionResult.SUCCESS) {
         if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
             GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                     PLAY_SERVICES_RESOLUTION_REQUEST).show();
         } else {
             Log.i(TAG, "Google Play Servis Yükleyin.");
             finish();
         }
         return false;
     }
     return true;
 }
 private String getRegistrationId(Context context) { //registration_id geri döner
     //Bu method registration id ye bakar.
     //Bu uygulamada registration id nin önceden olabilmesi için uygulamanın önceden açılmış ve registration işlemlerini yapmış olması lazım
     //Uygulama önceden acıldıysa registration_id SharedPreferences yardımı ile kaydedilir.
      
     final SharedPreferences prefs = getGCMPreferences(context);
     String registrationId = prefs.getString(PROPERTY_REG_ID, "");//registration_id değeri alındı
     if (registrationId.isEmpty()) {//eğer boşsa önceden kaydedilmemiş yani uygulama ilk kez çalışıyor.
         Log.i(TAG, "Registration id bulunamadı.");
         return "";
     }
     
     int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
     int currentVersion = getAppVersion(getApplicationContext());//yine SharedPreferences a kaydedilmiş version değerini aldık
     if (registeredVersion != currentVersion) {//versionlar uyuşmuyorsa güncelleme olmuş demektir. Yani tekrardan registration işlemleri yapılcak
         Log.i(TAG, "App version değişmiş.");
         return "";
     }
     return registrationId;
 }
   
 private SharedPreferences getGCMPreferences(Context context) {
     return getSharedPreferences(SplashScreen.class.getSimpleName(),
             Context.MODE_PRIVATE);
 }
   
 private static int getAppVersion(Context context) { //Versiyonu geri döner
     try {
         PackageInfo packageInfo = context.getPackageManager()
                 .getPackageInfo(context.getPackageName(), 0);
         return packageInfo.versionCode;
     } catch (NameNotFoundException e) {
         // should never happen
         throw new RuntimeException("Paket versiyonu bulunamadı: " + e);
     }
 }
}
