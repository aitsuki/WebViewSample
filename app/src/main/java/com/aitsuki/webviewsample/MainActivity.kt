package com.aitsuki.webviewsample

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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
        val editText = EditText(this)
        editText.inputType = InputType.TYPE_TEXT_VARIATION_URI
        editText.hint = "Please enter a url"
        editText.setText(defaultUrl)
        MaterialAlertDialogBuilder(this)
            .setTitle("Browser")
            .setView(editText)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Confirm") { _, _ ->
                val text = editText.text.toString()
                onConfirmClick(text)
            }
            .show()
    }
}