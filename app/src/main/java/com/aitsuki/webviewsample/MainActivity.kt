package com.aitsuki.webviewsample

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addButton("Sample") {
            showUrlInputDialog {
                startActivity(Intent(this, BrowserActivity::class.java).putExtra("url", it))
            }
        }
        addButton("Agreement Sample") {
            startActivity(Intent(this, AgreementActivity::class.java))
        }
        addButton("Javascript Interface") {
            startActivity(
                Intent(this, BrowserActivity::class.java)
                    .putExtra("url", "file:///android_asset/index.html")
            )
        }
    }

    private fun addButton(text: String, action: () -> Unit) {
        val container = findViewById<ViewGroup>(R.id.container)
        val button = MaterialButton(this)
        button.text = text
        button.setOnClickListener { action() }
        container.addView(button)
    }

    private fun showUrlInputDialog(
        defaultUrl: String = "https://www.bilibili.com",
        onConfirmClick: (String) -> Unit
    ) {
        MaterialDialog(this)
            .input(
                hint = "Please enter a url",
                prefill = defaultUrl,
                inputType = InputType.TYPE_TEXT_VARIATION_URI
            )
            .title(text = "Browser")
            .negativeButton(text = "Cancel")
            .positiveButton(text = "Confirm") {
                onConfirmClick(it.getInputField().text.toString())
            }.show()
    }
}