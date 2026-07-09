# Telegram Bot Message Sender

Spring Boot service that compares two Telegram delivery strategies:

- `POST /api/messages/queued` returns `202 Accepted` and sends from a bounded `BlockingQueue`.
- `POST /api/messages/direct` sends in the request thread.

Both paths share the same Telegram `429 retry_after` detection and retry logic. There is no database persistence and no local rate limiter, so the comparison focuses on request-thread sending versus `BlockingQueue` worker sending.

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

## Stress Comparison

Run both endpoints with the same payload and concurrency level. The queued endpoint should keep HTTP latency low while queue depth rises; the direct endpoint will expose request-thread blocking and any real Telegram `429 retry_after` delays.

Queued requests return a `trackingId`, `receivedAt`, and `status`. You can poll the queued message later with:

```powershell
Invoke-RestMethod http://localhost:8080/api/messages/queued/<trackingId>
```
