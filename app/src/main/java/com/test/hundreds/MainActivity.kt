package com.test.hundreds

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings


class MainActivity : AppCompatActivity() {

    lateinit var mFirebaseRemoteConfig :FirebaseRemoteConfig

    lateinit var txtVi :TextView


    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var googleActivityResult: ActivityResultLauncher<Intent>? = null
   // private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

        txtVi = findViewById(R.id.textView_txt)


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)


        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("TAG", "Config params updated: $updated")
                    Toast.makeText(
                        this@MainActivity, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                displayWelcomeMessage()
            }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)
        setUpGoogle()
        txtVi.setOnClickListener {


            googleActivityResult?.launch(mGoogleSignInClient.signInIntent)

        }

    }

    fun displayWelcomeMessage(){

        Toast.makeText(

            this@MainActivity, mFirebaseRemoteConfig.getString("version")+" Test3"
           , Toast.LENGTH_SHORT
        ).show()
        Log.d("v","v")
        Log.d("f","f")

    }

    private fun setUpGoogle() {
        setUpGoogleSignIn(

            onSignedIn = { idToken, googleSignInAccount ->
//                viewModel.socialSigning(
//                    socialId = idToken,
//                    client = SocialClient.Google,
//                    user = User(
//                        email = googleSignInAccount.email,
//                        firstName = googleSignInAccount.displayName,
//                        lastName = null,
//                        mobileNo = null,
//                        socialUserImage = googleSignInAccount.photoUrl.toString()
//                    )
//                )
            },
            onFailed = { Toast.makeText(this,"failed to fetch FB data",Toast.LENGTH_SHORT).show() }
        )
    }

    fun setUpGoogleSignIn(

        onSignedIn: (id: String, googleAccount: GoogleSignInAccount) -> Unit,
        onFailed: (String) -> Unit,
    ) {
        googleActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.exception != null) {
                    onFailed("Something went wrong! " + task.exception?.message)
                } else {
                    val idToken = task.result.id
                    if (idToken != null) {
                        onSignedIn(idToken, task.result)
                       // signOutFromSocials()
                    } else {
                        onFailed("Unable to get user data, try again")
                    }
                }
            }
    }


}