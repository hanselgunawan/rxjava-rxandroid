package com.example.rxjava_rxandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val TAG = "myApp"
    private val greeting: String = "Hello from RxJava"
    private lateinit var myObservable: Observable<String>

    private lateinit var myObserver: Observer<String>

    private lateinit var myText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myText = findViewById(R.id.tvGreeting)

        myObservable = Observable.just(greeting)

        myObservable.subscribeOn(Schedulers.io())

        myObservable.observeOn(AndroidSchedulers.mainThread())

        myObserver = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.i(TAG, "onSubscribe invoked")
            }

            override fun onNext(t: String) {
                Log.i(TAG, "onNext invoked")
                myText.text = t
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "onError invoked")
            }

            override fun onComplete() {
                Log.i(TAG, "onComplete invoked")
            }

        }

        // Observable won't emit data until someone subscribes to it
        myObservable.subscribe(myObserver)
    }
}