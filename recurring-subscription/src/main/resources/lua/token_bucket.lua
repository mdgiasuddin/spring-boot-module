local key = KEYS[1]
local maxTokens = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])

-- Get Redis server time (seconds, microseconds)
local redisTime = redis.call('TIME')
local currentTime = tonumber(redisTime[1]) * 1000 + math.floor(tonumber(redisTime[2]) / 1000)

local tokens = tonumber(redis.call('HGET', key, 'tokens'))
local lastRefill = tonumber(redis.call('HGET', key, 'lastRefill'))

if tokens == nil then
    tokens = maxTokens
    lastRefill = currentTime
else
    local elapsed = currentTime - lastRefill
    if elapsed > 0 then
        local refill = math.floor(elapsed * refillRate / 1000)
        if refill > 0 then
            tokens = math.min(maxTokens, tokens + refill)
            lastRefill = currentTime
        end
    end
end

local allowed = 0
if tokens >= 1 then
    tokens = tokens - 1
    allowed = 1
end

redis.call('HSET', key, 'tokens', tokens, 'lastRefill', lastRefill)

-- Calculate dynamic TTL: time to fully refill + safety buffer (e.g., 60 seconds)
local secondsToFull = math.ceil((maxTokens - tokens) / (refillRate / 1000))
local ttl = math.max(60, math.ceil(secondsToFull / 1000) + 60)
redis.call('EXPIRE', key, ttl)

if allowed == 1 then
    return tokens
else
    local waitTime = math.ceil(1000 / refillRate)
    return -waitTime
end