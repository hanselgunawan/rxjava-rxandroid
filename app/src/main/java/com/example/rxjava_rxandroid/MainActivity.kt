package com.example.rxjava_rxandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val TAG = "myApp"
    private val greeting: String = "Hello from RxJava"
    private lateinit var myObservable: Observable<String>

    private lateinit var myObserver: DisposableObserver<String>

    private lateinit var myText: TextView

    private val composite: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myText = findViewById(R.id.tvGreeting)

        myObservable = Observable.just(greeting)

        myObserver = object : DisposableObserver<String>() {

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

        composite.add(
            myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(myObserver)
        )
    }
}