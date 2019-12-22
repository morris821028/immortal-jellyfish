# Persistent Data Structure

> Purely Functional Implementation in Java ([Persistent Data Structure](https://en.wikipedia.org/wiki/Persistent_data_structure))

The library includes immutable collections, like stack, queue, and [deque](https://en.wikipedia.org/wiki/Double-ended_queue).

## Features

* Efficient operation in real time
* Efficient memory management

# Example

See more detail at [wiki page](https://github.com/morris821028/PersistentDataStructure/wiki).

## Stack ##

* `pop()`: O(1)
* `push(value)`: O(1)
* `top()`: O(1)

```java
PStack<Integer> stk = PCollections.emptyStack(); // []
PStack<Integer> stk1 = stk.push(1);              // [1]
PStack<Integer> stk2 = stk1.push(2);             // [1, 2]
PStack<Integer> stk3 = stk2.pop();               // [1]
PStack<Integer> stk4 = stk3.push(3);             // [1, 3]

Assertions.assertEquals(1, stk1.top());
Assertions.assertEquals(2, stk2.top());
Assertions.assertEquals(1, stk3.top());
Assertions.assertEquals(3, stk4.top());
```

## Queue ##

* `pop()`: O(1)
* `push(value)`: O(1)
* `front()`: O(1)

Implementation option: Realtime (Default), Prev-Evaluation, or Realtime-Extra

```java
PQueue<Integer> que = PCollections.emptyQueue(); // []
PQueue<Integer> que1 = que.push(1);              // [1]
PQueue<Integer> que2 = que1.push(2);             // [1, 2]
PQueue<Integer> que3 = que2.pop();               // [2]
PQueue<Integer> que4 = que3.push(3);             // [2, 3]

Assertions.assertNotNull(que1);
Assertions.assertEquals(1, que1.front());
Assertions.assertEquals(1, que2.front());
Assertions.assertEquals(2, que3.front());
Assertions.assertEquals(2, que4.front());
```

## Deque (Double-Ended Queue) ##

* `popFront()`: O(1)
* `popBack()`: O(1)
* `pushFront(value)`: O(1)
* `pushBack(value)`: O(1)
* `front()`: O(1)
* `back()`: O(1)

Implementation option: Realtime (Default), Prev-Evaluation

```java
PDeque<Integer> que = PCollections.emptyDeque(); // []
PDeque<Integer> que1 = que.pushFront(1);         // [1]
PDeque<Integer> que2 = que1.pushBack(2);         // [1, 2]
PDeque<Integer> que3 = que2.popFront();          // [2]
PDeque<Integer> que4 = que3.pushFront(3);        // [3, 2]

Assertions.assertEquals(1, que1.back());
Assertions.assertEquals(2, que2.back());
Assertions.assertEquals(2, que3.front());
Assertions.assertEquals(2, que4.back());
```

# Implementation Note

Coming soon

# Related Repository

* [mikea/concrete](https://github.com/mikea/concrete)
* [functionaljava/functionaljava](https://github.com/functionaljava/functionaljava)

# Reference Reading

Partial resouce uploaded in folder /reference

* REAL-TIME QUEUE OPERATIONS IN PURE LISP, Robert HOOD and Robert MEVILL, 1981
* REAL-TIME DEQUES, MULTIHEAD TURING MACHINES and PURELY FUNCTIONAL PROGRAMMING, Tyng-Runey Chuang and Benjamin Goldberg, 1993
* SIMPLE AND EFFICIENT PURELY FUNCTIONAL QUEUES AND DEQUES, Chris Okasaki, 1995