-- Token bucket rate limiter (parameterized window with precise fractional tracking)
-- KEYS[1] = bucket key
-- ARGV[1] = maxTokens      (bucket capacity)
-- ARGV[2] = refillTokens   (tokens added per window)
-- ARGV[3] = windowSeconds  (length of the refill window, in seconds)

local key = KEYS[1]
local maxTokens = tonumber(ARGV[1])
local refillTokens = tonumber(ARGV[2])
local windowSeconds = tonumber(ARGV[3])

if not maxTokens or not refillTokens or not windowSeconds
   or maxTokens <= 0 or refillTokens <= 0 or windowSeconds <= 0 then
    return redis.error_reply('maxTokens, refillTokens and windowSeconds must be positive numbers')
end

local refillRate = refillTokens / windowSeconds   -- tokens per second
local msPerToken = 1000 / refillRate

-- Get Redis server time (seconds, microseconds)
local redisTime = redis.call('TIME')
local currentTime = tonumber(redisTime[1]) * 1000 + math.floor(tonumber(redisTime[2]) / 1000)

local vals = redis.call('HMGET', key, 'tokens', 'lastRefill')
local tokens = tonumber(vals[1])
local lastRefill = tonumber(vals[2])

if tokens == nil then
    tokens = maxTokens
    lastRefill = currentTime
else
    local elapsed = currentTime - lastRefill
    if elapsed > 0 then
        local refill = (elapsed * refillRate) / 1000
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

local msToFull = (maxTokens - tokens) * msPerToken
local ttl = math.ceil(msToFull / 1000) + 60
redis.call('EXPIRE', key, ttl)

if allowed == 1 then
    return tokens
else
    -- Wait time derived directly from the precise fractional token count,
    -- not from a time-delta modulo (which loses phase info once lastRefill
    -- is snapped to currentTime on every refill).
    local waitTime = math.ceil((1 - tokens) * msPerToken)
    return -waitTime
end