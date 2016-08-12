# Concurrency Rules Of Thumb session
Convenor: Jack Shirazi

The reason for this session is that despite being written by many of the experts in our industry, we
see race conditions exposed from the low-level JVM features up through all layers to the application; and from contained data structure like
ConcurrentHashMap across to frameworks like fork-join. Concurrent programming is incredibly hard to
get right and even the experts fail (though obviously less often), so I was looking for rules of thumb that will reduce the likelihood that your average
programmer will create a concurrency bug. These are what came up in the session:

* Concurrency has to be a primary consideration during development, not an afterthought. For shared fields (shared across threads, that is), use variable names that are dramatically different so it is crystal clear you are working on data that will be managed concurrently.
* Do not provide accessors to multithreaded-shared fields. Do not expose them outside of the class managing them - once the variable is accessible outside the class, it becomes impossible to reason about its thread-safety.
* Fail fast and don't recover in the case of state failure - it's incredibly difficult to know what is a consistent state given the random state that ensues after a failure, so you are much better off restarting from a known valid state. Leaving the system up in an inconsistent state is likely to lead to even more corruption.
* Not all work is more efficient parallelized. If the overheads of the parallelization management is more than the benefit you gain, it's much much better to remain serial, as there is no need to worry about concurrency issues. The advice of NQ < 10,000 and stay serial is a valid starting point (where roughly N is the number of tasks to process and Q is the number of instructions needed to process each task) though this will depend on many things. The primary point here is staying serial may be fast enough and is much much simpler, so consider that first.
* Actors provide a "don't share" paradigm, but you are exchanging the data races for other issues such as inbox leaks (consumer can't keep up - AKKA has bounded inboxes so avoids OOME, but you still need to consider what to do about the higher volume producer requests) and deadlocks. Additionally too many Actors leads to unmaintainable code leaving you with a worse situation than if you hadn't used Actors in the first place.
* If your problem space is that of accepting a set of external inputs and processing those in parallel mostly independently to produce results using a set of relatively straightforwardly transformable results, then rxjava might be the right solution to bypass direct concurrency implementations.
* Don't use Java8 parallel streams.
* You have three generic options when dealing with multithreaded data fields: don't share (eg always copy); don't mutate (eg use immutables); coordinate access (preferably using a data structure that's already created and in extensive use - use a standard concurrent collection if it exists for the problem).
* Bound queues and use back pressure to prevent upstream systems from overwhelming downstreams systems
* One way to avoid mutating directly is to add deltas rather than overwrite (persistent data structures, similar to journaling). The thread adding the delta will see consistent data, as will other threads, though the different threads may see different aggregate data depending on how and when the deltas are combined.
* If you have more than one shared field that needs to be consistently updated across the set of fields, there are 3 generic ways to achieve this: read and update all fields in a synchronized block; use an immutable class holding the set of fields and an AtomicReference to instances of the class; use a state machine which reads the fields once and holds the data locally and can reason about the relationship between the variables, so that it can determine desireable states when updates can proceed.
* For CPU intensive processing, the ideal is 1 thread per core, ideally bound to the core with other threads excluded from that core.
* Microbenchmarks are not useful 

