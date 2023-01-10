- [Kernel TLS](https://docs.kernel.org/networking/tls.html)
- [Extending in-kernel TLS support](https://lwn.net/Articles/892216/)
- [Java Socket native options](https://blog.termian.dev/posts/java-socket-native-options/):
  how to add new *native* socket options to Java using JNA/refection
- [JDK-8285224: Support for Do not fragment IP socket options](https://bugs.openjdk.org/browse/JDK-8285224):
  CSR for adding a new [extended Socket option (i.e. `jdk/net/ExtendedSocketOptions`)](
  https://docs.oracle.com/en/java/javase/19/docs/api/jdk.net/jdk/net/ExtendedSocketOptions.html)to JDK 19
- see [`ExtendedSocketOptions.SO_PEERCRED`](
  https://docs.oracle.com/en/java/javase/19/docs/api/jdk.net/jdk/net/ExtendedSocketOptions.html#SO_PEERCRED)
  for an example how we could implement kTLS in Java.
- [Improving NGINX Performance with Kernel TLS and SSL_sendfile()](
  https://www.nginx.com/blog/improving-nginx-performance-with-kernel-tls/)
