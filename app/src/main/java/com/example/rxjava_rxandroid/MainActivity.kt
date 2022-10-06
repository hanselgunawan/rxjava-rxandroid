package com.example.rxjava_rxandroid

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val TAG = "myApp"
    private lateinit var myObservable: Observable<Student>

    private lateinit var myObserver: DisposableObserver<Student>

    private lateinit var myText: TextView

    private val composite: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myText = findViewById(R.id.tvGreeting)

        myObservable = Observable.create { emitter ->

            try {
                val studentArrayList = getStudents()

                for (student in studentArrayList) {
                    emitter.onNext(student)
                }

                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }

        composite.add(
            myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap {
                    val delay = (0..10).random()
                    it.name = it.name?.uppercase()
                    Observable
                        .just(it)
                        .delay(delay.toLong(), TimeUnit.SECONDS)
                }
                .subscribeWith(getObserver())
        )
    }

    private fun getObserver(): DisposableObserver<Student> {
        myObserver = object : DisposableObserver<Student>() {

            override fun onNext(t: Student) {
                Log.i(TAG, "onNext invoked ${t.name}")
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "onError invoked")
            }

            override fun onComplete() {
                Log.i(TAG, "onComplete invoked")
            }

        }

        return myObserver
    }

    private fun getStudents(): ArrayList<Student> {
        val students: ArrayList<Student> = ArrayList()

        val student1 = Student(
            "student 1",
            "student1@gmail.com",
            27
        )
        students.add(student1)

        val student2 = Student(
            "student 2",
            "student2@gmail.com",
            20
        )
        students.add(student2)

        val student3 = Student(
            "student 3",
            "student3@gmail.com",
            20
        )
        students.add(student3)

        val student4 = Student(
            "student 4",
            "student4@gmail.com",
            20
        )
        students.add(student4)

        val student5 = Student(
            "student 5",
            "student5@gmail.com",
            20
        )
        students.add(student5)

        return students
    }
}