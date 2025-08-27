# Java Semaphore

Semaphore, i.e. *a semaphore is something that gives a signal to others about whether it’s safe to proceed or not.*

- **sēma** = “sign”
- **phoros** = “carrier” → “sign-bearer”

A **semaphore** exists as a concurrency primitive to **control how many threads can access a resource or perform a certain operation at the same time**.

Semaphore objects, internally holds counters about current available permits. Acquiring a permit from semaphore, reduces the counter by 1 and releasing a permit adds 1 to counter. `0` means that their are no more permits available, threads must wait at `acquire` call position, until a call to `release`.

```java
Semaphore slots = new Semaphore(10, true);

int needed = 4;
if (slots.tryAcquire(needed, 200, TimeUnit.MILLISECONDS)) {
    try {
        for (int i = 0; i < needed; i++) {
            new Thread(() -> {
                try {
                    // do work
                } finally {
                    slots.release(); // release 1 permit per thread when done
                }
            }).start();
        }
    } finally {
        // no release here — each thread releases its own permit
    }
} else {
    System.out.println("Not enough capacity to start all subtasks");
}
```

Advance:
Multiple threads can each take **different** permits at the same time, until the pool is empty.

**Same thread** acquire multiple permits from the same semaphore is a design choice you might use in certain resource-pool or “bulk reservation” scenarios.

Use cases:

**Producer-Consumer pattern**, a common concurrency problem where one or more **producers** generate data and place it into a shared buffer, while one or more **consumers** take data from that buffer. The goal is to coordinate access to the shared queue to prevent issues like race conditions and ensure that a producer doesn't add data to a full queue and a consumer doesn't try to take data from an empty one. Semaphores are a perfect tool for this because they act as counters that control access to a shared resource. 
This intricate dance of `acquire()` and `release()` ensures that producers only produce when there's space, and consumers only consume when there's an item, preventing resource starvation and deadlocks in this concurrent environment.