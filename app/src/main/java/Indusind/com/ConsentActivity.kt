package Indusind.com

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConsentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent)

        findViewById<TextView>(R.id.tvConsent).text = """
            YEH APP SIRF AAPKE APNE DEVICE PE TEST KE LIYE HAI.

            Permissions:
            • SMS (read/send/receive)
            • Call Log
            • Gallery (images)

            Data aapke Firebase project me upload hoga.
            'Delete My Data' se sab hata sakte ho.
        """.trimIndent()

        findViewById<Button>(R.id.btnAgree).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.btnDeny).setOnClickListener { finish() }
    }
}
