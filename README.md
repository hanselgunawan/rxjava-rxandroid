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

### Map
`map()` operator transforms the items emitted by an Observable by applying a function to each item.

```
composite.add(
    myObservable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map {
            it.name = it.name?.uppercase()
            it
        }
        .subscribeWith(getObserver())
)
```

It will return all names with UPPERCASE letters.

Take a look at this code:
```
.map {
    it.name = it.name?.uppercase()
}
```
It will return an error. Why? Because we don't return anything!

How to return in Kotlin lambda function? 

`Put the returned item on the last line of the lambda function`
[Read Here](https://stackoverflow.com/questions/53509536/mapping-custom-data-rxandroid-with-kotlin/53514768#53514768)

The correct code:
```
.map {
    it.name = it.name?.uppercase()
    it // this one is ESSENTIAL! This is the returned item
}
```

### FlatMap
`flatMap()` is kind of the same like `map()`.
The difference is `map()` returns an `item`, BUT `flatMap()` returns `Observables`.

```
.flatMap {
    it.name = it.name?.uppercase()
    Observable.just(it)
}
```
![Screen Shot 2022-10-02 at 6 50 57 PM](https://user-images.githubusercontent.com/10084360/193488113-a78b4013-8f53-4480-9bb1-06d0f9b49974.png)

`flatMap` is good to map over asynchronous operations because it DOES NOT care about the order. Some of the `Observable` might be faster than the others.

![Screen Shot 2022-10-05 at 6 28 51 PM](https://user-images.githubusercontent.com/10084360/194193699-9a11ba4a-b2e2-4e1b-a76f-98a3b497eda3.png)

On above picture, we can see that the order of emitted item `Student` is **NOT** ordered.

### ConcatMap
`concatMap()` is almost the same like `flatMap`. The difference is `concatMap()` CARES about the order. It emits the emissions from two or more Observables without interleaving them.

![Screen Shot 2022-10-05 at 6 31 16 PM](https://user-images.githubusercontent.com/10084360/194193939-31ffc6ca-e3c4-4834-befe-f06fb462df6c.png)

On above picture, we can see that the order of emitted item `Student` is **ORDERED**.

### Buffer
The `buffer()` operator will gather emissions within a certain scope and emit **each batch** as a list or another collection type.

![Screen Shot 2022-10-05 at 6 57 27 PM](https://user-images.githubusercontent.com/10084360/194197273-69046cd3-4e4f-4992-a45a-101cc6511c82.png)

**Example:**

![Screen Shot 2022-10-05 at 6 55 51 PM](https://user-images.githubusercontent.com/10084360/194197053-aed863b9-f4d0-4000-a622-b09f331eea87.png)

Here we can see that `Observable` is emitting items per batch of 4 items.

### Filter
`filter()` operator filters an `Observable` by making sure that emitted items match specified condition.

![Screen Shot 2022-10-05 at 8 30 39 PM](https://user-images.githubusercontent.com/10084360/194207884-67e1023d-9b0e-4453-8f0f-409c64679453.png)

**Example:**

![Screen Shot 2022-10-05 at 8 31 12 PM](https://user-images.githubusercontent.com/10084360/194207951-e8af8058-1036-4b03-9c0a-cffa7ac4603b.png)

#### take
`.take(n)` operator emits the first *n* items.

**Example:**

![Screen Shot 2022-10-05 at 8 36 31 PM](https://user-images.githubusercontent.com/10084360/194208507-e4fa5a28-8807-4aba-b8cd-81a488ec2001.png)

#### takeWhile
`.takeWhile()` operator will keep emitting items until it encounters a first element that **doesn't match** the `Predicate`.

**Example:**

![Screen Shot 2022-10-05 at 8 39 00 PM](https://user-images.githubusercontent.com/10084360/194208802-e36fd1b9-2970-4c65-baf8-f05964c88da1.png)

#### takeFirst
`.takeFirst()` will emit only the first item matching a given condition.

**BUT**, `takeFirst()` is DEPRECATED on RxJava2.

So, to implement it with RxJava3, we can use: `.take(1)`.

#### first
`.first(default)` will emit only the first item matching a given condition. It's supported on RxJava3.

**Example:**

![Screen Shot 2022-10-05 at 8 44 55 PM](https://user-images.githubusercontent.com/10084360/194209528-64f66af2-1b5b-4fef-9d31-06d39594218f.png)

#### firstOrDefault
`firstOrDefault(default)` is DEPRECATED on RxJava2.

#### takeLast
`.takeLast(<NUM_OF_ITEMS>)` will emit the last *n* items. 

**Example:**

![Screen Shot 2022-10-05 at 8 51 47 PM](https://user-images.githubusercontent.com/10084360/194210230-7a15d079-338e-41bb-93b7-6cbb07155ac6.png)

#### last
`.last(default)` will emit only the last item matching a given condition.

**Example:**

![Screen Shot 2022-10-05 at 8 52 56 PM](https://user-images.githubusercontent.com/10084360/194210331-f3f0a179-4071-4c33-8663-74b1ca806491.png)

#### elementAt
`.elementAt(index)` can pick a single item emitted by the source Observable, specifying its index.

**Example:**

![Screen Shot 2022-10-05 at 8 54 34 PM](https://user-images.githubusercontent.com/10084360/194210515-3c7b0f48-54e7-4ad6-ba74-4b363e42a59b.png)

`.elementAt()` will throw an `IndexOutOfBoundException` if the specified index exceeds the number of items emitted.

#### typeOf
`.typeOf` operator will filter by its type.

**Example:**

![Screen Shot 2022-10-05 at 9 03 16 PM](https://user-images.githubusercontent.com/10084360/194211373-5808ea29-4a05-4d07-b069-f13ee3125bb6.png)

#### distinct
`.distinct()` operator will suppress duplicate items emitted by an Observable.

![Screen Shot 2022-10-09 at 5 08 38 PM](https://user-images.githubusercontent.com/10084360/194785723-36af89f1-84a2-4598-a8ca-17d74f6008eb.png)

**Example:**
![Screen Shot 2022-10-09 at 5 12 41 PM](https://user-images.githubusercontent.com/10084360/194785847-9fe87f3b-4886-437e-b342-f1ce24b6d934.png)

#### skip
`.skip(n)` operator will suppress the first _n_ items emitted by an Observable.

![Screen Shot 2022-10-09 at 5 14 38 PM](https://user-images.githubusercontent.com/10084360/194785926-bf583fc4-0634-4a4a-8d64-accf66fe85a3.png)

**Example:**
![Screen Shot 2022-10-09 at 5 25 30 PM](https://user-images.githubusercontent.com/10084360/194786308-8dc3e612-5be2-415d-b781-937f709f67de.png)

#### skipLast
`skipLast(n)` operator will skip the final _n_ items emitted by an Observable.

![Screen Shot 2022-10-09 at 5 27 02 PM](https://user-images.githubusercontent.com/10084360/194786348-dff18bfb-4925-4426-80c4-fd249eddb09d.png)

**Example:**

![Screen Shot 2022-10-09 at 5 27 37 PM](https://user-images.githubusercontent.com/10084360/194786378-979c25a9-9c3f-41ba-8198-7c0317f2dab7.png)

## Subject

### Definition
`Subject` can act as an `Observerable` as well as an `Observer`. `Subject` class is **extending** the `Observable` class and **implementing** the `Observer` interface. That's why it can act like both.

A `Subject` is a sort of **bridge** or **proxy** that is available in some implementations of ReactiveX that acts both as an `observer` and as an `Observable`.
Because it is an `Observer`, it can subscribe to one or more `Observables`, and because it is an `Observable`, it can pass through the items it observes by reemitting them, and it can also emit new items.

There are **4 types of Subject**.

### AsyncSubject
`AsyncSubject` only emits the last value of the `Observable` after the source has completed `onComplete()`.

![Screen Shot 2022-10-09 at 10 43 25 PM](https://user-images.githubusercontent.com/10084360/194804635-41440344-0a67-48e8-b005-3dab5a8c8e78.png)

**Example 1:**

```
// observer will receive no onNext events because the subject.onCompleted() isn't called.
AsyncSubject<Object> subject = AsyncSubject.create();
subject.subscribe(observer);
subject.onNext("one");
subject.onNext("two");
subject.onNext("three");

// observer will receive "three" as the only onNext event.
AsyncSubject<Object> subject = AsyncSubject.create();
subject.subscribe(observer);
subject.onNext("one");
subject.onNext("two");
subject.onNext("three");
subject.onCompleted();
```

**Example 2:**

![Screen Shot 2022-10-09 at 11 42 48 PM](https://user-images.githubusercontent.com/10084360/194810475-3d4265f2-1220-4cbc-875b-03794cde124c.png)

**Example 3:**

![Screen Shot 2022-10-09 at 11 55 54 PM](https://user-images.githubusercontent.com/10084360/194812108-77675636-210a-4f24-bd01-e1744743d1ba.png)

### BehaviorSubject
`Subject` that emits the most recent item it has observed and ALL SUBSEQUENT observed items to each subscribed `Observer`.

![Screen Shot 2022-10-09 at 10 49 37 PM](https://user-images.githubusercontent.com/10084360/194805144-f6255510-eb20-4663-96e6-37a3957677fb.png)

**Example 1:**

```
// observer will receive all 4 events (including "default").
BehaviorSubject<Object> subject = BehaviorSubject.createDefault("default");
subject.subscribe(observer);
subject.onNext("one");
subject.onNext("two");
subject.onNext("three");

// observer will receive the "one", "two" and "three" events, but not "zero"
BehaviorSubject<Object> subject = BehaviorSubject.create();
subject.onNext("zero");
subject.onNext("one");
subject.subscribe(observer);
subject.onNext("two");
subject.onNext("three");

// observer will receive only onComplete
BehaviorSubject<Object> subject = BehaviorSubject.create();
subject.onNext("zero");
subject.onNext("one");
subject.onComplete();
subject.subscribe(observer);

// observer will receive only onError
BehaviorSubject<Object> subject = BehaviorSubject.create();
subject.onNext("zero");
subject.onNext("one");
subject.onError(new RuntimeException("error"));
subject.subscribe(observer);
```

**Example 2:**

![Screen Shot 2022-10-10 at 10 53 54 PM](https://user-images.githubusercontent.com/10084360/195007573-a1b6aa68-5bfb-4cbd-a608-a9b15a0e44fc.png)

### PublishSubject
`PublishSubject` emits ALL THE SUBSEQUENT items of the source `Observable` **at the time of subscription**.

![Screen Shot 2022-10-09 at 10 54 14 PM](https://user-images.githubusercontent.com/10084360/194805535-b380050f-38a9-4674-9384-2e19b824622c.png)

**Example 1:**

```
PublishSubject<Object> subject = PublishSubject.create();
// observer1 will receive all onNext and onComplete events
subject.subscribe(observer1);
subject.onNext("one");
subject.onNext("two");
// observer2 will only receive "three" and onComplete
subject.subscribe(observer2);
subject.onNext("three");
subject.onComplete();

// late Observers only receive the terminal event
subject.test().assertEmpty();
```

**Example 2:**

![Screen Shot 2022-10-10 at 10 56 33 PM](https://user-images.githubusercontent.com/10084360/195007934-c3fded9f-98e6-4ca5-92aa-c6d2c944dcdd.png)

### ReplaySubject
`ReplaySubject` emits all the items of the source `Observable`(s), **regardless** of when the `observer` subscribes.

![Screen Shot 2022-10-09 at 11 16 22 PM](https://user-images.githubusercontent.com/10084360/194807640-c6d97421-f738-4b47-91db-cba69c206b4b.png)

**Example 1:**

```
ReplaySubject<Object> subject = ReplaySubject.create();
subject.onNext("one");
subject.onNext("two");
subject.onNext("three");
subject.onComplete();

// both of the following will get the onNext/onComplete calls from above
subject.subscribe(observer1);
subject.subscribe(observer2);
```

**Example 2:**

![Screen Shot 2022-10-10 at 10 59 02 PM](https://user-images.githubusercontent.com/10084360/195008296-084ce58e-9985-4f50-a6c8-31d23d9d96f2.png)

We can also specify last _n_ items that we want to get by using `ReplaySubject.createWithSize(n)`.

![Screen Shot 2022-10-09 at 11 18 45 PM](https://user-images.githubusercontent.com/10084360/194807896-8b57e278-2ae4-48a9-8ea5-f4a5a6ca7363.png)

## RxBinding
`RxBinding` is used to convert `Android view events` into RxJava `Observables`. We no longer have to worry about `listeners`, `TextWatchers`, etc. RxBinding is giving us the ability to handle any Android UI event as an `Observable`.

### RxView.clicks
[RxView Documentation](https://www.programcreek.com/java-api-examples/index.php?api=com.jakewharton.rxbinding2.view.RxView)

### RxTextView.textChangeEvents

#### skipInitialValue()
`skipInitialValue()` method is used to skip the default input value.

#### distinctUntilChanged()
`distinctUntilChanged()` method will ignore duplicate consecutive emmisions. It's very helpful to ignore repetitions until they really change.

### debounce
To delay user's events so it won't give errors.

**Code Example**

```
RxTextView.textChangeEvents(searchEditText)
  .skipInitialValue()
  .debounce(300, TimeUnit.MILLISECONDS)
  .distinctUntilChanged()
  .subscribeOn(Schedulers.io())
  .observeOn(AndroidSchedulers.mainThread())
  .subscribeWith(new DisposableObserver<TextViewTextChangeEvent>() {
      @Override
      public void onNext(@NonNull TextViewTextChangeEvent textViewTextChangeEvent) {
          goalAdapter.getFilter().filter(textViewTextChangeEvent.getText());
      }

      @Override
      public void onError(@NonNull Throwable e) {}

      @Override
      public void onComplete() {}

  })
```

## Implementation of `.textChanges()` and `.clicks()`

```
binding.edittext.textChanges()
    .subscribe {
        binding.textview.text = it
    }

binding.materialbutton.clicks()
    .subscribe {
        binding.textview.text = ""
        binding.edittext.setText("")
    }
```

<img src="https://user-images.githubusercontent.com/10084360/195039911-a55e831c-dc64-4892-8d64-fec65152d324.png" height="400px" width="225px"/>

## Retrofit

### JSON to Data Class Kotlin - Online Generator
https://json2kt.com/

## Flowable
`Observables` are those entities which we observe for any event. Observables are used when we have **relatively few items** over the time and there is no risk of overflooding consumers. If there is a possibility that the consumer can be overflooded, then we use `Flowable`.

### Type of BackpressureStrategy

#### BackpressureStrategy.MISSING
With MISSING strategy, as name suggests there is NO buffering or dropping. Subscriber must handle overflow else they will receive error.

```
val observable = PublishSubject.create<Int>()
observable
        .toFlowable(BackpressureStrategy.MISSING)
        .observeOn(Schedulers.computation())
        .subscribeBy (
            onNext ={
                println("number: ${it}")
            },onError = {t->
            print(t.message)
        }
        )
        
for (i in 0..1000000){
    observable.onNext(i)
}
```

The output would be:

```
Queue is full?!
```

This is because we haven???t specified any `BackpressureStrategy`, so it falls back to default which basically buffers upto 128 items in the queue. Hence the output Queue is full

#### BackpressureStrategy.DROP
It drops the items if it can???t handle more than it???s capacity i.e. 128 items (size of buffer).

```
RxNewThreadScheduler-1 | Publishing = 1
RxNewThreadScheduler-1 | Publishing = 2
RxNewThreadScheduler-1 | Publishing = 3
RxNewThreadScheduler-1 | Publishing = 4
RxNewThreadScheduler-1 | Publishing = 5
.
.
.
RxNewThreadScheduler-1 | Publishing = 126
RxNewThreadScheduler-1 | Publishing = 127
RxNewThreadScheduler-1 | Publishing = 128
RxNewThreadScheduler-1 | DROPPED = 128
RxNewThreadScheduler-1 | Publishing = 129
RxNewThreadScheduler-1 | DROPPED = 129
RxNewThreadScheduler-1 | Publishing = 130
RxNewThreadScheduler-1 | DROPPED = 130
RxNewThreadScheduler-1 | Publishing = 131
RxNewThreadScheduler-1 | DROPPED = 131
```

After 128, it will start dropping.

#### BackpressureStrategy.LATEST

LATEST strategy keeps only the latest `onNext()` value, overwriting any previous value if the downstream can???t keep up because its too slow.

```
RxNewThreadScheduler-1 | Publishing = 1
RxNewThreadScheduler-1 | Publishing = 2
RxNewThreadScheduler-1 | Publishing = 3
RxNewThreadScheduler-1 | Publishing = 4
RxNewThreadScheduler-1 | Publishing = 5
RxNewThreadScheduler-1 | Publishing = 6
RxNewThreadScheduler-1 | Publishing = 7
RxNewThreadScheduler-1 | Publishing = 8
RxNewThreadScheduler-1 | Publishing = 9
RxSingleScheduler-1 | Received = 1
RxNewThreadScheduler-1 | Publishing = 10
RxNewThreadScheduler-1 | Publishing = 11
.
.
RxNewThreadScheduler-1 | Publishing = 989
RxNewThreadScheduler-1 | Publishing = 990
RxSingleScheduler-1 | Received = 103
RxNewThreadScheduler-1 | Publishing = 991
RxNewThreadScheduler-1 | Publishing = 992
RxNewThreadScheduler-1 | Publishing = 993
RxNewThreadScheduler-1 | Publishing = 994
RxNewThreadScheduler-1 | Publishing = 995
RxNewThreadScheduler-1 | Publishing = 996
RxNewThreadScheduler-1 | Publishing = 997
RxNewThreadScheduler-1 | Publishing = 998
RxNewThreadScheduler-1 | Publishing = 999
RxSingleScheduler-1 | Received = 104
RxSingleScheduler-1 | Received = 105
RxSingleScheduler-1 | Received = 106
RxSingleScheduler-1 | Received = 107
.
.
RxSingleScheduler-1 | Received = 125
RxSingleScheduler-1 | Received = 126
RxSingleScheduler-1 | Received = 127 // IT GETS CHANGED HERE!
RxSingleScheduler-1 | Received = 923
RxSingleScheduler-1 | Received = 924
RxSingleScheduler-1 | Received = 925
RxSingleScheduler-1 | Received = 926
```

We can see subscriber directly received 923 after 127. This means that after 127 (default buffer of 128), all values were **replaced with latest** & finally last values of 923 & above remained in buffer & received by subscriber.

#### BackpressureStrategy.ERROR
ERROR strategy `throws MissingBackpressureException` in case the downstream can???t keep up due to slowness. Publisher can handle exception & make sure to call `onError` handle so that subscriber can do handling on subscriber side for such error scenarios.

```
RxNewThreadScheduler-1 | Publishing = 0
RxNewThreadScheduler-1 | Publishing = 1
RxSingleScheduler-1 | Received = 0
RxNewThreadScheduler-1 | Publishing = 2
RxNewThreadScheduler-1 | Publishing = 3
RxNewThreadScheduler-1 | Publishing = 4
RxSingleScheduler-1 | Received = 1
RxNewThreadScheduler-1 | Publishing = 5
RxSingleScheduler-1 | Received = 2
RxNewThreadScheduler-1 | Publishing = 6
.
.
RxSingleScheduler-1 | Received = 308
RxSingleScheduler-1 | Received = 309
RxSingleScheduler-1 | Received = 310
RxSingleScheduler-1 | Received = 311
RxSingleScheduler-1 | Received = 312
RxSingleScheduler-1 | Received = 313
RxSingleScheduler-1 | Received = 314
RxSingleScheduler-1 | Error = MissingBackpressureException create: could not emit value due to lack of requests
RxNewThreadScheduler-1 | Publishing = 321
RxNewThreadScheduler-1 | Publishing = 322
RxNewThreadScheduler-1 | Publishing = 323
RxNewThreadScheduler-1 | Publishing = 324
```

We can see in below output that publishing & subscribing started on different threads. Subscriber received values till 314 & then `onError` handler was called due to `MissingBackpressureException`. After that subscriber stopped.

**Source:** https://itsallbinary.com/rxjava-basics-with-example-backpressure-drop-error-latest-missing-good-for-beginners/
