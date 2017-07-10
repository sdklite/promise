## Promise

The promise library is an implementation of [Promises/A+](https://promisesaplus.com/) specification in Java 8.

## Getting Started

### Create a pending promise

```java
Promise<String> promise = new Promise<String>();
```

### Create a pending promise with an executor

```java
Promise<String> promise = new Promise<String>((resolve, reject) -> {
    ...
});
```

### Resolve with value

```java
Promise<String> promise = Promise.resolve("Promise/A+");
```

### Reject with an exception

```java
Promise<String> promise = Promise.reject(new RuntimeException("Oops!"));
```

### Asynchronous HTTP

```java
Promise.resolve('https://api.github.com/').then(url -> {
    try (final InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
        return IOUtils.readFully(reader);
    }
}).then(System.out::println, e -> e.printStackTrace());
```