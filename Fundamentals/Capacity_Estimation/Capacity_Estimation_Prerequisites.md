Part 2: [How I Calculate Capacity for Systems Design - 2](https://dev.to/zeeshanali0704/how-i-calculate-capacity-for-systems-design-399o)

### Capacity Estimation in System Design

Capacity estimation is essential to ensure that a system can handle its expected load and perform efficiently. It involves calculating the resources needed for processing/traffic handling, storage, and network bandwidth.

**Key Rules for System Design Estimation Calculations**

1. **Rounding Approximations**
   - Simplify calculations by rounding to more manageable numbers.
   - Example: Instead of calculating for 86,400 seconds in a day, use 100,000 (100K) seconds to simplify.

2. **Powers of 2 and 10**
   - Familiarize yourself with powers of 2 and 10 for quick estimations.
   - Example values for powers of 2: 2, 4, 8, 16, 32, 64, etc.
   - Example values for powers of 10:
     - \(10^1 = 10\)
     - \(10^2 = 100\)
     - \(10^3 = 1,000\)
     - \(10^6 = 1,000,000\) (1 million)
     - \(10^9 = 1,000,000,000\) (1 billion)
     - \(10^{12} = 1,000,000,000,000\) (1 trillion)

3. **Metric System**
   - Use metric system units for large numbers:
     - 1 million = \(10^6\)
     - 1 billion = \(10^9\)
     - 1 trillion = \(10^{12}\)

4. **Storage Capacity**
   - Understand common storage units:
     - 1 KB = \(10^3\) bytes
     - 1 MB = \(10^6\) bytes
     - 1 GB = \(10^9\) bytes
     - 1 TB = \(10^{12}\) bytes
     - 1 PB = \(10^{15}\) bytes

 - Storage Assumption

| Storage Assumption | Size     |
|--------------------|----------|
| Single Char        | 2 bytes  |
| Long/Double        | 8 bytes  |
| Image              | 200 KB   |
| Video              | 2 MB     |

5. **Key Metrics to Memorize**
   - 1 million requests per day ≈ 12 requests/second
   - 1 million requests per minute ≈ 700 requests/second
   - 1 million requests per hour ≈ 4,200 requests/minute

6. **Latency Numbers**
   - Familiarize yourself with common latency benchmarks to make informed decisions during system design.

*Note: Google for a table of latency benchmarks for more details.*

### Table for Powers of 2 and 10

| Power of 2 | Value    | Power of 10 | Value                |
|------------|----------|-------------|----------------------|
| \(2^1\)    | 2        | \(10^1\)    | 10                   |
| \(2^2\)    | 4        | \(10^2\)    | 100                  |
| \(2^3\)    | 8        | \(10^3\)    | 1,000                |
| \(2^4\)    | 16       | \(10^6\)    | 1,000,000            |
| \(2^5\)    | 32       | \(10^9\)    | 1,000,000,000        |
| \(2^6\)    | 64       | \(10^{12}\) | 1,000,000,000,000    |


Article 2: [How I Calculate Capacity for Systems Design - 2](https://dev.to/zeeshanali0704/how-i-calculate-capacity-for-systems-design-399o)

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli

