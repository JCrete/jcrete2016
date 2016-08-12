# Tools & Techniques to diagnose concurrency issues
Convenor: Jack Shirazi

## Known to be useful
* stack trace already lists deadlock (and can use mxbean to ask too)
* repeated stack traces and compare the stacks looking for methods that are at the top because they are not being allowed to progress fast enough (Note: Give thread pools a name, it makes analysing stacks soooooo much easier)
* monitor queue sizes
* monitor thread pools for threads in-use
* ThreadMXBean see Thread Contention Monitoring, & ThreadInfo[] getThreadInfo(long[] ids, boolean lockedMonitors, boolean lockedSynchronizers)
* hacking java.util.concurrent classes to add in some counters of failed lock attempts
* CPU monitoring, specifically looking for decreases from the baseline (not caused by context switches, so you need to monitor those too)

## May be useful
* http://vmlens.com/
* https://github.com/google/thread-weaver
* timing segments of requests to identify the segment that takes too long
* thread profilers
* stress test a known multi-threaded update code fragment
* flight recorder (not sure how?)
* static code analysers (ThreadSafe, google gchord, intellij common errors, like forgot to unlock, close, etc)

## Additions bei Richard Warburton:

One tactic when bug hunting non-deterministic concurrency bugs:
That is trying to vary the amount of available CPU. 
Sometimes you'll have a race condition that reliably appears with more or less available CPU cores because it depends upon the order of events and this order changes if you more or less available CPU resources.
So you can try to taskset a JVM to a subset of cores to try and reduce available CPU or run something like `dd if=/dev/urandom of=/dev/null` to just burn a core in the background. Increasing resources may simply be a case of closing another application that's running on your machine or re-running the test harness on faster hardware. This latter approach is much easier now that you can just boot up a large VM on IAAS platforms like EC2.

## Additions bei Viktor Klang:  
Another option is to downclock the CPU. (either by decreasing multiplier or by lowering the equivalent of the FSB)

## Additions by Kirk Pepperdine:
what I’ve done in the past is added a bit of code to where I thought I might be able to bias the race condition in one direction or the other.
