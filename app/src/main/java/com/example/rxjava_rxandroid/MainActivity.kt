package com.example.rxjava_rxandroid

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.AsyncSubject
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val TAG = "myApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        asyncSubjectDemo1()
        asyncSubjectDemo2()
    }

    private fun asyncSubjectDemo1() {

        val observable: Observable<String> =
            Observable
                .just(
                    "Apple",
                    "Banana",
                    "Cherry",
                    "Dragonfruit"
                )

        val subject: AsyncSubject<String> = AsyncSubject.create()
        observable.subscribe(subject)

        subject.subscribe(getFirstObserver())
        subject.subscribe(getSecondObserver())
        subject.subscribe(getThirdObserver())
    }

    private fun asyncSubjectDemo2() {

        val subject: AsyncSubject<String> = AsyncSubject.create()
        subject.subscribe(getFirstObserver())
        subject.onNext("Apple")
        subject.onNext("Banana")

        subject.subscribe(getSecondObserver())
        subject.onNext("Cherry")
        subject.onComplete()

        subject.subscribe(getThirdObserver())
    }

    private fun getFirstObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                println("First Observer onSubscribe")
            }

            override fun onNext(t: String) {
                println("First Observer Received $t")
            }

            override fun onError(e: Throwable) {
                println("First Observer onError")
            }

            override fun onComplete() {
                println("First Observer onComplete")
            }
        }
    }

    private fun getSecondObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                println("Second Observer onSubscribe")
            }

            override fun onNext(t: String) {
                println("Second Observer Received $t")
            }

            override fun onError(e: Throwable) {
                println("Second Observer onError")
            }

            override fun onComplete() {
                println("Second Observer onComplete")
            }
        }
    }

    private fun getThirdObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                println("Third Observer onSubscribe")
            }

            override fun onNext(t: String) {
                println("Third Observer Received $t")
            }

            override fun onError(e: Throwable) {
                println("Third Observer onError")
            }

            override fun onComplete() {
                println("Third Observer onComplete")
            }
        }
    }

}