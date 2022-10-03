# RxJava & RxAndroid

## RxJava
RxJava is the Java Virtual Machine (JVM) implementation of Reactive Extensions.

### Reactive Extensions
Reactive Extensions is a library for composing **asynchronous** (different parts of the program that runs at the same time) and **event-based** programs (executes the code based on the events generated by other parts of application, such as: API calls) by using **observable data streams**.

## Observable vs Observer
### Observable
It emits data.
### Observer
It gets data.

## Schedulers in RxJava
To handle multithreading in RxJava.
A scheduler can be recognized as a thread pool that manage one or more threads.

### Types of Schedulers
1) **Schedulers.io**
Used for non CPU intensive tasks, such as: database interactions, network communications, and interactions with the file system.
2) **AndroidSchedulers.mainThread()**
This is for the UI thread. Where user interactions happen.
3) **Schedulers.single()**
This has a single thread executing tasks one after another following the given order.
4) **AndroidSchedulers.trampoline()**
Executes tasks following First-In-First-Out (FIFO) basics. This is used for recurring tasks. All the scheduled tasks will be executed one by one by limiting the number of background threads to one.
5) **Schedulers.from(Executor executor)**
Creates & returns a custom scheduler backed by the specified executor.
6) **Schedulers.computation**
Used for CPU intensive tasks like processing huge data, bitmap processing etc.

## Disposables
### Why do we need to dispose our observer?
Let's say users going to a screen and then the users change their mind while the app is trying to observe the data. What will happen? App will CRASH!
So, to prevent that, we have to `dispose()` our observers inside `onDestroy()` method.

```
@Override
protected void onDestroy() {
  super.onDestroy();
  myObserver.dispose();
}
```

### Composite Disposable
To dispose all the observers at one time.
For example:
If we have two observers, `myObserver1` and `myObserver2`. We need to do this in order to dispose them:
```
myObserver1.dispose();
myObserver2.dispose();
```
To handle such case, we have to implement `CompositeDisposable` by calling `clear()` method inside `onDestroy()` method.
```
val composite: CompositeDisposable = CompositeDisposable()

myObserver = object Observer<Any>() {
  onSubscribe()
  onNext()
  onError()
  onComplete()
}
composite.add(myObserver)

myObserver2 = object Observer<Any>() {
  onSubscribe()
  onNext()
  onError()
  onComplete()
}
composite.add(myObserver2)

@Override
protected void onDestroy() {
  super.onDestroy();
  composite.clear();
}
```
#### CompositeDisposable's clear() vs dispose()
When you are using `CompositeDisposable`, if you call `dispose()` method, you will **no longer be able** to add disposables to that composite disposable.
But if you call to `clear()` method you can still add disposable to the composite disposable. `clear()` method just clears the disposables that are currently held within the instance.

## Operators
### just
`just` operator converts an item into an Observable that emits that item.
![Screen Shot 2022-09-28 at 6 51 10 PM](https://user-images.githubusercontent.com/10084360/192920267-9e84a935-7c5f-4872-a575-804a08a7dca4.png)

Example 1:
```
val myStr = arrayOf<String>("A","B","C")
myObservable.just(myStr);

onNext(t: String) {
  println(t) // will print the entire array
}
```

Example 2:
```
myObservable.just("A","B","C");

onNext(t: String) {
  println(t) // will print each string, e.g: "A", "B", and "C"
}
```

### fromArray
It creates an Observable from a set of items of the array using an `Iterable`.

**Java**
```
private Observable<String> myObservable;
private  DisposableObserver<String> myObserver;

private String[] greetings = {"a","b","c"};

myObservable = Observable.fromArray(greetings)
```

BUT, we can't do that in Kotlin.

`arrayOf()` in Kotlin returns primitive type. That means `arrayOf(1,2,3)` will return `int[]` which is NOT supported by `fromArray()`.
`fromArray()` only accept `Object[]`, such as `String[]`, `Integer[]`, `Float[]`, etc.
[[Read Here](https://github.com/ReactiveX/RxJava/wiki/Creating-Observables#fromarray)].

In Kotlin, we can use `fromIterable()` with `listOf()` to implement the same thing.

**Kotlin**
```
private lateinit var myObservable: Observable<String>
private lateinit var myObserver: DisposableObserver<String>
private val greetings = listOf("Hello A", "Hello B", "Hello C")

myObservable = Observable.fromIterable(greetings)
```

It will loop through every single item inside the array.

### Range
`range()` operator create an Observable that emits a particular range of sequential integers.
![Screen Shot 2022-10-02 at 4 35 07 PM](https://user-images.githubusercontent.com/10084360/193481261-f3dee523-d437-49c5-8ce2-82f50bb275cc.png)

```
myObservable.range(1,20)
```
It will print integer from `1` to `20`.

### Create
`create()` creates an Observable from scratch. With `create()` method, we can have a function body. So, we can have some control over our data before the emition.

![Screen Shot 2022-10-02 at 5 14 03 PM](https://user-images.githubusercontent.com/10084360/193482722-6600cb10-e1d2-42cb-8d5d-6897099ac274.png)

```
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
```
