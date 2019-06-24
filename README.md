### 指纹验证(Fingerprint)使用说明
[![](https://jitpack.io/v/ShaoqiangPei/Fingerprint.svg)](https://jitpack.io/#ShaoqiangPei/Fingerprint)

此库是一个指纹验证库，方便大家调用涉及到指纹解锁功能时使用

效果图
![2.png](https://upload-images.jianshu.io/upload_images/6127340-0bb34f5cd887593e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![3.png](https://upload-images.jianshu.io/upload_images/6127340-34f714e55b34ad02.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![4.png](https://upload-images.jianshu.io/upload_images/6127340-473089243c2f78ae.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 一.依赖
Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add the dependency
```
  dependencies {
	        implementation 'com.github.ShaoqiangPei:Fingerprint:v1.0.3'//自己用的时候以最新版本为基准，此处仅作用例
	}
```
#### 二.直接引用指纹验证功能，不需要界面的使用介绍
初始化
```
Fingerprint.getInstance().init(Context context);//context 为activity上下文
```
启动指纹识别
```
Fingerprint.getInstance().callFingerPrint(new Fingerprint.OnCallBackListenr() {
                    @Override
                    public void onSupportFailed(int type, String message) {
                        LogUtil.i("=====onSupportFailed======="+message);
                    }

                    @Override
                    public void onInsecurity(int type, String message) {
                        LogUtil.i("=====onInsecurity======="+message);
                    }

                    @Override
                    public void onEnrollFailed(int type, String message) {
                        LogUtil.i("=====onEnrollFailed======="+message);
                    }

                    @Override
                    public void onAuthenticationStart() {
                        LogUtil.i("=======onAuthenticationStart===开始识别==");
                    }

                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        LogUtil.i("======onAuthenticationError======"+errString.toString());
                    }

                    @Override
                    public void onAuthenticationFailed(String message) {
                        LogUtil.i("=======onAuthenticationFailed===指纹识别失败=="+message);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        LogUtil.i("=====onAuthenticationHelp======="+helpString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        LogUtil.i("=====onAuthenticationSucceeded====指纹识别成功===");
                    }
                });
```
使用完后，需要销毁指纹识别功能
```
        //销毁指纹识别
        Fingerprint.getInstance().cancel();
```
#### 三.涉及到界面跳转的指纹识别
很多时候，我们只需要一个固定的指纹识别功能，当然，最好这个识别功能还自带界面，我们只需要稍稍调用就享受到它的好处就好了，
那么此库也提供了包含界面的指纹是被调用。
第一步，我们需要在自己项目中的 androidMainfast.xml中 注册此activity，如下：
```
  <activity
            android:name="com.p.fingerprintlib.ui.FingerprintActivity"
            android:configChanges="orientation|keyboardHidden"
            android:screenOrientation="portrait"
            android:windowSoftInputMode="adjustResize" />
```
当我们需要设置指纹识别的时候，可以类似以下方法跳到指纹识别的设置界面：
```
           //跳转设置指纹识别界面
           FingerprintActivity.setFingerActivity(MainActivity.this);
```
若是需要跳到指纹识别验证界面的时候，可以类似这样跳转：
```
           //跳转验证界面
           FingerprintActivity.verifyFingerActivity(MainActivity.this);
```
然后在我们的前一个界面，此处假设这个界面为 MainActivity,则需要在MainActivity中实现 onActivityResult 方法来接收 设置或者验证指纹是否成功的结果，
类似如下：
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FingerprintActivity.SET_TAG){//设置界面
            switch (resultCode) {
                case Activity.RESULT_CANCELED://返回键
                    LogUtil.i("======我是设置界面返回键======");
                    break;
                case Activity.RESULT_OK://设置成功
                    LogUtil.i("======设置成功======");
                    break;
                default:
                    break;
            }
        }else if(requestCode==FingerprintActivity.VERIFY_TAG){//验证界面
            switch (resultCode) {
                case Activity.RESULT_CANCELED://返回键
                    LogUtil.i("======我是验证界面返回键======");
                    break;
                case Activity.RESULT_OK://验证成功
                    LogUtil.i("======验证成功======");
                    break;
                default:
                    break;
            }
        }
    }
```
如果是涉及界面调用的时候，我们不需要在MainActivity中再调用指纹的销毁方法了，因为这些都已经在你注册的“FingerprintActivity”中完成了。
