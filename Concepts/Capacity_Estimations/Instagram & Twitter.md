Article related to Capacity Calculations
[How I Calculate Capacity for Systems Design - 2](https://dev.to/zeeshanali0704/how-i-calculate-capacity-for-systems-design-399o)

# Instagram Calculations

## 1. Daily Active Users (DAU) and Posts

- **Total users:** 600 million
- **Daily Active Users (DAU):** 200 million
- **Assume Percentage of DAU posting 2 posts each day:** 25%

### Write Request
  
**Total posts per day**= 200 million x 25% x 2 = 100 million posts/day - 100M posts/day

**Total posts per second** =  100 million posts/seconds in a day 
 = 100M/100K = 1000 / seconds


### Read Request

- **Views per day:**

**Total views per day** = 200 million x 100 reads = 20 billion 
views/day

**Total views per seconds** = 20 billion views / seconds in a day = 200K/seconds


Overall Traffic Estimations:

**Write per second: 1000/seconds**
**Read per second: 200K/seconds**


## 2. Storage Calculation for One Day

let's stick to the above calculation where 100M write requests we have.

Breaking them into Image Post & Reel Post only for now.

Here, you can also consider another post request like like, comment, share etc..


### Images

- **Percentage of posts that are images:** 70% of 100M

So,
- **Number of image posts per day:**

  Image posts per day = 70% times 100 million = 70 million


- **Storage per image:** 200 KB

- **Total storage for images:**

  Storage for images = 70 million x 200 KB = 14,000,000,000 KB = 14,000 TB


### Reels

- **Percentage of posts that are reels:** 30%
so,
- **Number of reel posts per day:**

  Reel posts per day = 30% times 100 million = 30 million

- **Storage per reel:** 2 MB
- **Total storage for reels:**

Storage for reels = 30 million x 2 MB = 60,000,000 MB = 60,000 TB


### Total Storage Needed for One Day

- **Total storage:**

  Total storage = Storage for images + Storage for reels


  Total storage = 14,000 TB (images) + 60,000 TB (reels) = 74,000 TB = 74 petabytes (PB)


## 3. Storage Calculation for Five Years

- **Assuming 365 days per year:**
- **Storage needed for one day:** 74 PB
- **Storage needed for five years:**

  Total storage for five years = 74 PB times 365 days times 5 years


  Total storage for five years = 135,350 PB = 135.35 ~ 130 exabytes (EB)


## 4. Bandwidth Calculation

### 1. Incoming Data Bandwidth

- **Total data coming to the server per day:** 74 PB
- **Convert to Mbps:**

  74 PB = 74 x 1000 x 1000 x 1000 MB

Data per day = 74,000,000,000 MB


Data per second = 74,000,000,000 MB /  86400 seconds ~ 856,481.48 MBps ~ 836.67 GBps ~ 800 GB/sec


### 2. Outgoing Data Bandwidth

- Views per day: 20 billion

Assuming 50% for images and 50% for reels:

#### Image Posts Assumption

- Each image post: 200 KB
- Total outgoing data for images:

`20 billion × 0.5 × 200 KB = 2,000,000,000,000 KB = 2,000,000,000 MB = 2,000 TB `


#### Reels View Assumption

- Each reel post: 2 MB
- The user watches 1 out of 5 reels:

`20 billion × 0.5 × 1/5 × 2 MB = 4,000,000,000 MB = 4,000 TB`


**Total Outgoing Data**

`2,000 TB (image posts) + 4,000 TB (reels) = 6,000 TB`

Convert to GBps:

`6,000 TB = 6,000,000 GB`

Data per second = 6,000,000 GB / 86,400 seconds ≈ 69.44 GBps ~ 70GBps

## Summary

- Daily Storage Requirement: **74 PB**
- Five-Year Storage Requirement: **135 EB**
- Incoming Bandwidth Requirement: **~856,481.48 MBps (or ~800 GBps)**
- Outgoing Bandwidth Requirement: **~69.44 GBps (or ~70 GBps)**



# Twitter Calculations

Twitter's Traffic Estimations and storage requirements:

### Assumptions:

1. **300 million monthly active users (MAU)**: This is the total number of unique users who use Twitter at least once a month.

2. **50% of users use Twitter daily**: This implies that half of the monthly active users are active on Twitter every day.

3. **Users post 2 tweets per day on average**.

4. **10% of tweets contain media**: Media can include images, videos, etc.

5. **Data is stored for 5 years**: This is the retention period for storing the tweets and media data.


### Estimations:

#### Daily Active Users (DAU):

DAU = 300 million  x 50% = 150 million 

#### Tweets QPS (Queries per Second):

- **Total tweets per day**:

Total tweets /  day = 150 million users x 2 tweets per user = 300    million tweets 

- **Tweets per second** (assuming tweets are uniformly distributed throughout the day):

Tweets / second  = 300 million tweets  x 24 hours x 3600    seconds  ~ 3472 tweets per second 

approx **3500** tweets per second  (rounded for simplicity)

- **Peak QPS**: Often, systems need to handle peak loads, which can be higher than the average. Assuming the peak QPS is double the average:
text Peak QPS = 2  x 3500 approx 7000    tweets per second 

#### Media Storage Estimation:

**Average tweet sizes**:

  - tweet_id: 64 bytes
  - text: 140 bytes
  - media: 1 MB (only for tweets containing media, which is 10%)

**Daily media storage**:

Daily media storage = 150 million users  x 2 tweets per user  x 10%  x 1 MB 

300 million tweets x 10%  x 1 MB  ~
30 million tweets with media  x 1 MB  ~
30 TB per day ~ 30TB/Day


**5-year media storage**:

5-year media storage = 30 TB per day  x 365 days  x 5 years 
30 TB/day  x 1825 days 

54,750 TB 

Approx 55 PB  (rounded for simplicity, where 1 PB = 1000 TB)


### Summary:
1. **Daily Active Users (DAU)**: 150 million
2. **Tweets QPS**:
   - Average: ~3500 tweets per second
   - Peak: ~7000 tweets per second
3. **Media Storage**:
   - Daily: 30 TB
   - 5-year: ~55 PB

These estimations provide an understanding of the scale at which Twitter operates in terms of query load and storage requirements.


Thanks
Zeeshan


Article related to Capacity Calculations
[How I Calculate Capacity for Systems Design - 2](https://dev.to/zeeshanali0704/how-i-calculate-capacity-for-systems-design-399o)

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli


