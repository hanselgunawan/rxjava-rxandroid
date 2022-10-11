package com.example.rxjava_rxandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rxjava_rxandroid.databinding.ActivityRxBindingTestBinding
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges

class RxBindingTest : AppCompatActivity() {

    private lateinit var binding: ActivityRxBindingTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRxBindingTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edittext.textChanges()
            .subscribe {
                binding.textview.text = it
            }

        binding.materialbutton.clicks()
            .subscribe {
                binding.textview.text = ""
                binding.edittext.setText("")
            }
    }
}