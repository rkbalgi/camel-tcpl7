# camel-tcpl7
A camel based TCP L7 load balancer

A simple demo about using Apache Camel load balancer to load balance (round robin/failover) custom TCP protocol at L7.

Start the TCPServer and then start the LoadBalancer application. The client (LoadBalancer) sends a "Hello World" to the server and the server responds with a length prefixed random message back to the client.


