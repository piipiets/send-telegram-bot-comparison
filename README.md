# Telegram Bot Message Sender

Spring Boot service that compares two Telegram delivery strategies:

- `POST /api/messages/queued` returns `202 Accepted` and sends from a bounded `BlockingQueue`.
- `POST /api/messages/direct` sends in the request thread.

This comparison highlights how different architectural approaches handle Telegram's rate-limiting behavior, specifically `429 Too Many Requests` responses.

# Tech Stack

- **Java 22**
- **Spring Boot 4**
- **Blocking Queue**
- **RestTemplate Pooling**
- **Jmeter**
 
## Configuration

Set these environment variables before running:

```powershell
$env:TELEGRAM_BOT_TOKEN="123456:bot-token"
```

## Run

```powershell
mvn spring-boot:run
```

## Send Messages

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/api/messages/queued `
  -ContentType "application/json" `
  -Body '{"chatId":"123456789","text":"queued hello"}'

Invoke-RestMethod -Method Post http://localhost:8080/api/messages/direct `
  -ContentType "application/json" `
  -Body '{"chatId":"123456789","text":"direct hello"}'
```

Check queue pressure:

```powershell
Invoke-RestMethod http://localhost:8080/api/messages/queued/stats
```

The stats response now includes how many queued messages were sent successfully and how many failed.

# Stress Testing with JMeter

This project includes a JMeter test plan located in `load-tests/telegram-rate-limit.jmx` to help compare the performance of these strategies under load.

1.  **Open JMeter**: Load the `load-tests/telegram-rate-limit.jmx` file.
2.  **Configure**:
    - Adjust the `Thread Group` settings to match your desired concurrency and test duration.
    - Configure the `CSV Data Set Config` to point to a CSV file containing your test `chatId` values.
3.  **Execute**: Run the test plan.
    - The **Direct** strategy will demonstrate how request threads block when encountering Telegram `429` retry delays.
    - The **Queued** strategy will demonstrate how to maintain low HTTP response latency by delegating sending tasks to the background.
4.  **Analyze**: Use the "Summary Report" to compare response latency and throughput between the two delivery paths.
