# Programming-Assignment-3
by Jackie Lin

To run the code:

javac BirthdayPresent.java

java BirthdayPresent

javac AtmosphericTemp.java

java AtmosphericTemp

```

# 1. The Birthday Present Party

Initial Attempt Issues:
 ```
* Threads were not able to successfuly run the problem in our desired time, which was due to poor implementation of thread pool

 *Set up problem that didn't follow the problem guidline, in terms of having no random instances. Which although produced a quick runtime, considering the threads prcoessed the functions in terms linear order.

```

Solution and Efficiency
 ```
*Implemented the usage of java functionalities such as ExecutorService Java Class, ReentrantLock Java Class, and others synchrnoize threads, handle shared resources, and produced a efficent solution to the problem


*Adhereed to the project guidlelines for checking randomn present tag but was successful in producing quick runtimes.

*DUE to how random we get our numbers, runtime would be rather complex for far larger number of presents than our given set amount due to the large range of numbers to run through, yet has produced results in resonabke time(seconds) and even under 1 second for lucky cases. 
```


# 2. Atmospheric Temperature Reading Module

Initial Attempt Issues:
 ```
* Initial method of thread implementation proved to hard and ineffecitve at times, probably due to poor impementaion

*Annoying syntax error when implementing multiple unique arrays and other data structures to follow project guidelines, which resulted in several compilation error

```

Solution and Efficiency
 ```

*IMplementaiton of tools such as ExecutorService, ReentrantLock Java Class, and others, allow effective management and usage of threads.

*In additon, it allowed thread synchronization, as well as quicker and more effcient method in executing the problem

```

##Advantages and Disadvantages

#### Both parts have set conditions or guidlines which directed to one method to the problem

* No real advantages or disadvantages, besides the fact that implemtation of the method took awhile, with syntax errors here and here, yet once including the hava class tools and other methods, allowed me to solve the given problems.
