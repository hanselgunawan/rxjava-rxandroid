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

