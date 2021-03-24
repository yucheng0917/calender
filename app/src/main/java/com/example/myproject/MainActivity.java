package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SignInButton signin;
    int RC_SIGN_IN =0;
    GoogleSignInClient mGoogleSignInClient;
    private AppBarConfiguration mAppBarConfiguration;

    private static final String TAG = FacebookActivity.class.getSimpleName();
    private ImageView mImgPhoto;
    private TextView mTextDescription;

    // FB
    private LoginManager loginManager;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // init facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        // init LoginManager & CallbackManager
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        findViewById(R.id.mImgFBBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Facebook Login
                loginFB();
            }
        });

        // method_1.判斷用戶是否登入過
        if (Profile.getCurrentProfile() != null) {
            Profile profile = Profile.getCurrentProfile();
            // 取得用戶大頭照
            Uri userPhoto = profile.getProfilePictureUri(300, 300);
            String id = profile.getId();
            String name = profile.getName();
            Log.d(TAG, "Facebook userPhoto: " + userPhoto);
            Log.d(TAG, "Facebook id: " + id);
            Log.d(TAG, "Facebook name: " + name);
        }

        // method_2.判斷用戶是否登入過
        /*if (AccessToken.getCurrentAccessToken() != null) {
            Log.d(TAG, "Facebook getApplicationId: " + AccessToken.getCurrentAccessToken().getApplicationId());
            Log.d(TAG, "Facebook getUserId: " + AccessToken.getCurrentAccessToken().getUserId());
            Log.d(TAG, "Facebook getExpires: " + AccessToken.getCurrentAccessToken().getExpires());
            Log.d(TAG, "Facebook getLastRefresh: " + AccessToken.getCurrentAccessToken().getLastRefresh());
            Log.d(TAG, "Facebook getToken: " + AccessToken.getCurrentAccessToken().getToken());
            Log.d(TAG, "Facebook getSource: " + AccessToken.getCurrentAccessToken().getSource());
        }*/
        signin=findViewById(R.id.sign_in_button);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });


        setContentView(R.layout.login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build ();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }
    //以下google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Intent intent =new Intent(MainActivity.this, googleSignIn.class);
            startActivity(intent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    //以上google

    private void loginFB() {// 設定FB login的顯示方式 ; 預設是：NATIVE_WITH_FALLBACK
        /**
         * 1. NATIVE_WITH_FALLBACK
         * 2. NATIVE_ONLY
         * 3. KATANA_ONLY
         * 4. WEB_ONLY
         * 5. WEB_VIEW_ONLY
         * 6. DEVICE_AUTH
         */
        loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        // 設定要跟用戶取得的權限，以下3個是基本可以取得，不需要經過FB的審核
        List<String> permissions = new ArrayList<>();
        //permissions.add("public_profile
               // permissions.add("email
                        //permissions.add("user_friends
                                // 設定要讀取的權限
                                loginManager.logInWithReadPermissions(this, permissions);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // 登入成功
        /* 可以取得相關資訊，這裡就請各位自行打印出來
        Log.d(TAG, "Facebook getApplicationId: " + loginResult.getAccessToken().getApplicationId());
        Log.d(TAG, "Facebook getUserId: " + loginResult.getAccessToken().getUserId());
        Log.d(TAG, "Facebook getExpires: " + loginResult.getAccessToken().getExpires());
        Log.d(TAG, "Facebook getLastRefresh: " + loginResult.getAccessToken().getLastRefresh());
        Log.d(TAG, "Facebook getToken: " + loginResult.getAccessToken().getToken());
        Log.d(TAG, "Facebook getSource: " + loginResult.getAccessToken().getSource());
        Log.d(TAG, "Facebook getRecentlyGrantedPermissions: " + loginResult.getRecentlyGrantedPermissions());
        Log.d(TAG, "Facebook getRecentlyDeniedPermissions: " + loginResult.getRecentlyDeniedPermissions());*/
                // 透過GraphRequest來取得用戶的Facebook資訊
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (response.getConnection().getResponseCode() == 200) {
                                long id = object.getLong("id");
                                String name = object.getString("name");
                                String email = object.getString("email");
                                Log.d(TAG, "Facebook id:" + id);
                                Log.d(TAG, "Facebook name:" + name);
                                Log.d(TAG, "Facebook email:" + email);
                                // 此時如果登入成功，就可以順便取得用戶大頭照
                                Profile profile = Profile.getCurrentProfile();
                                // 設定大頭照大小
                                Uri userPhoto = profile.getProfilePictureUri(300, 300);
                                Glide.with(MainActivity.this)
                                        .load(userPhoto.toString())
                                       // .crossFade()
                                        .into(mImgPhoto);
                                mTextDescription.setText(String.format(Locale.TAIWAN, "Name:%s\nE-mail:%s", name, email));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // https://developers.facebook.com/docs/android/graph?locale=zh_TW
                // 如果要取得email，需透過添加參數的方式來獲取(如下)
                // 不添加只能取得id & name
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                // 用戶取消
                Log.d(TAG, "Facebook onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                // 登入失敗
                Log.d(TAG, "Facebook onError:" + error.toString());
            }
        });
    }

    public void registered(View view){

        Intent intent=new Intent(MainActivity.this, login2.class);
        startActivity(intent);

    }

    public void login(View view){

        Intent intent=new Intent(MainActivity.this, main.class);
        startActivity(intent);

    }
}