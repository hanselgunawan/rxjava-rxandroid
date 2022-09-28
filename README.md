# RxJava & RxAndroid

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
Executes tasks following First-In-First-Out (FIFO) basics. This is used for recurring tasks.
5) **Schedulers.from(Executor executor)**
Creates & returns a custom scheduler backed by the specified executor.

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
