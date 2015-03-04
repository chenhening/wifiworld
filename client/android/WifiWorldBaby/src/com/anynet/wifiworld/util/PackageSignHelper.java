package com.anynet.wifiworld.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

/**
 * APK签名校验工具
 * 
 * @author yangqiang
 * 
 */
public class PackageSignHelper
{
    //这是手雷的签名文件指纹，如果用了其他的keystore，请手动更换。
    private final  static String ReleaseSignature = "30820255308201bea00302010202044e155bea300d06092a864886f70d0101050500306e310b300906035504061302636e31123010060355040813096775616e67646f6e673111300f060355040713087368656e7a68656e3110300e060355040a13077468756e646572310f300d060355040b130678756e6c6569311530130603550403130c7a686f6e6762696e6778696e3020170d3131303730373037313033345a180f32303930303531333037313033345a306e310b300906035504061302636e31123010060355040813096775616e67646f6e673111300f060355040713087368656e7a68656e3110300e060355040a13077468756e646572310f300d060355040b130678756e6c6569311530130603550403130c7a686f6e6762696e6778696e30819f300d06092a864886f70d010101050003818d003081890281810080e4cd31199427d90816a46ba87bb476000cded8fae5b0166cd0ad150dcdeb7e7c0cf0d2b11817d28685782b7b0efda455bc84b8050da334d941734e1b1739ceb77cda1fae4ff87949f831b3123e9baed8655ec0c7f5709036a572797d7afcbaf7dbb0ea2d53f4922819644ffbfc48db247730f0fbe64e28ac56ed6455a5b06f0203010001300d06092a864886f70d0101050500038181003589bfc1b4f3728e891f8f7dc4ef48c21c43ec43b558048360f36cb13967321cbccd6b826f03d2a6e4910f6d2e7d8419e2f065ae76ea9d072f05b24cb89a3cb1c0bb99679ad2cfe8082c29b1a8ee39e010017fb595472c09ead202f654a38a885fc16a1db0c099ab13878e1ae03f337acccec92e2feb9df9d2eb12dfa671c044";
    
    /**
     * 判断是否是正式包
     * 
     * @param context
     * @return
     */
    public static boolean isRelease(Context context)
    {
        boolean isRelease = true;
        
        try
        {
            PackageInfo pis = context.getPackageManager().getPackageInfo("com.anynet.wifiworld", PackageManager.GET_SIGNATURES);
            Signature[] sigs = pis.signatures;
            
            String sig = new String(sigs[0].toChars());
            isRelease = ReleaseSignature.equalsIgnoreCase(sig);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return isRelease;
    }
}
