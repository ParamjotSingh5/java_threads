# Java Concurrency Challenges


## Atomic Operations

- All reference assignments are atomic, we can get and set references to objects atomically.

```java
public int[] getAges() {
	return this.ages;
}

public void setName(String name){
	this.name = name;
}

pubic void setPerson(Person person){
	this.person = person;
}
```

## Primitive types

All assignments to primitive types are atomic except `long` and `double`, however declaring `long` and `double` with `volatile` keyword would make read, right operations atomic.

`volatile double height = 6.2;`

`java.util.concurrent.atomic` package contains more atomic alternative operations.