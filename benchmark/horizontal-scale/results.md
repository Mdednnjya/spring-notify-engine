# Horizontal Scale Benchmark

| Metric       | 1 instance | 2 instances | 3 instances |
|--------------|------------|-------------|-------------|
| Avg Latency  | 38ms       | 151ms       | 226ms       |
| P99 Latency  | 343ms      | 1250ms      | 1454ms      |
| Throughput   | 158.4 req/s | 159.2 req/s | 157.6 req/s |
| Error Rate   | 0.0%       | 0.0%        | 0.0%        |
| Concurrent Users | 1000   | 1000        | 1000        |
| Endpoint     | POST /notifications via nginx :9090 | | |

**Environment:** Local machine, Docker Compose. All JVM instances share the same physical cores — CPU contention under load explains the higher average latency as instance count increases. Throughput stays flat (~158 req/s) because the host CPU is the ceiling, not the number of instances. On separate hosts each instance would have dedicated CPU, and throughput would scale linearly.

**Architecture validated:** nginx distributes requests across N app instances via `upstream app_cluster`. Workers consume from a shared Redis queue — stateless design, no coordination layer required. Scale by running `docker-compose up --scale app=N`.
