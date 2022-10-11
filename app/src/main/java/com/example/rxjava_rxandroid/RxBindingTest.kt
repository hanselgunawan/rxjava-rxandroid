package com.example.rxjava_rxandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rxjava_rxandroid.databinding.ActivityRxBindingTestBinding

class RxBindingTest : AppCompatActivity() {

    private lateinit var binding: ActivityRxBindingTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRxBindingTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}