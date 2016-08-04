//RxJava and comparison of reactive frameworks

//Functional reactive: Excel sheet

Observable<User> users = databse...

Observable<String> userNames = users
	.map(User user -> user.getName())
	.subscribeOn(Schedulers.io())
	.filter()
	.onBackpressureDrop()
	.sample(1, SECOND)
	.window(2, SECOND)
	.distinctUntilChanged()
	.distinct()
	.buffer(100)
	.reduce()
	.flatMap(...)

Observable<Long> points = //...

points.reduce(0, (a, x) -> a + x1)

userNames.subscribe(userName ->
	doSomething(userName)
)

users.concatWith(anotherStream)

vipUsers.mergeWith(normalUsers)

users.subscribe(
	user -> doSomethingWith(user),
	ex -> log.error("Opps", ex),
	() -> completed()
)

users
	.onErrorResume(ex -> 
		if(ex instanceof IOException)
			return Observable.empty();
		) else {
			return Observable.error(ex);
		}
	}
	.retry(ex -> )

//Hystrix

TestScheduler ts = Schedulers.test();

mouseClicks
	.buffer(100, MILLISECONDS, ts)

ts.advanceTimeBy(99, MILLISECONDS)
//TestSubscriber
//No event emitted

ts.advanceTimeBy(1, MILLISECONDS)

//exactly one event was emitted

Observable<MouseEvent> mouseClicks = //RxSwing

//unit test setup
mouseClicks
	.take(10)
	.concatWith(
		Observable.error(new RuntimeException()))

Observable<Exception> microservicesErrors = //...

microservicesErrors.subscribe(
	Exception event -> 
	ex -> 
	)

Observable<Long> longs

//ObservableLong - no such thing

longs.reduce(0, (a, x) -> a + x)

longs
	.buffer(100)
	.map(List<Long>)

longs.buffer(1, SECONDS)

Observable<List<T>> a = someObs.toList();

//java.util.Observable (JDK 1.0)

obs1.zipWith(obs2)

CompletableFuture<T> is like Observable<T>
(1 event)

CompletableFuture<HttpResponse>
Observable<HttpResponse>

rx.Single<T>

RxNetty

Observable<ByteBuf>

Retrofit (HTTP client)
RxAndroid
MongoDB Rx driver

Spring MVC Controller

httpResponse1
httpResponse2

httpResponse1.mergeWith(httpResponse2)

httpResponse1.concatWith(httpResponse2)

httpResponse1.zipWith(httpResponse2, (a, b))