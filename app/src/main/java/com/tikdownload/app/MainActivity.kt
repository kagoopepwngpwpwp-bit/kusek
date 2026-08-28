package com.tikdownload.app

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tikdownload.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.processButton.setOnClickListener {
            val url = binding.urlInput.text?.toString()?.trim().orEmpty()

            if (url.isBlank()) {
                binding.statusText.text = getString(R.string.enter_url)
                binding.downloadButton.isEnabled = false
                return@setOnClickListener
            }

            val uri = runCatching { Uri.parse(url) }.getOrNull()

            if (uri?.scheme != "https") {
                binding.statusText.text = getString(R.string.https_required)
                binding.downloadButton.isEnabled = false
                return@setOnClickListener
            }

            binding.statusText.text = getString(R.string.ready_status)
            binding.downloadButton.isEnabled = true
        }

        binding.downloadButton.setOnClickListener {
            val url = binding.urlInput.text?.toString()?.trim().orEmpty()
            downloadDirectFile(url)
        }
    }

    private fun downloadDirectFile(url: String) {
        val fileName = URLUtil.guessFileName(
            url,
            null,
            "video/mp4"
        )

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription(
                getString(R.string.download_description)
            )
            .setNotificationVisibility(
                DownloadManager.Request
                    .VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MOVIES,
                "TikDownload/$fileName"
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)

        val manager =
            getSystemService(Context.DOWNLOAD_SERVICE)
                    as DownloadManager

        manager.enqueue(request)

        Toast.makeText(
            this,
            getString(R.string.download_started),
            Toast.LENGTH_LONG
        ).show()

        binding.statusText.text =
            getString(R.string.download_started)
    }
}
