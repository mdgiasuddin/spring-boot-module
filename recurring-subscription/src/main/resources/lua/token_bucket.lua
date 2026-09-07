-- Token bucket rate limiter
-- KEYS[1] = bucket key
-- ARGV[1] = maxTokens (bucket capacity)
-- ARGV[2] = refillRate (tokens per second)
--
-- Returns:
--   tokens remaining (>= 0)  if the request is allowed
--   -waitTime (< 0)          if the request is rejected, where waitTime
--                            is the ms until the next token is available

local key = KEYS[1]
local maxTokens = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])

if not maxTokens or not refillRate or maxTokens <= 0 or refillRate <= 0 then
    return redis.error_reply('maxTokens and refillRate must be positive numbers')
end

-- Get Redis server time (seconds, microseconds)
local redisTime = redis.call('TIME')
local currentTime = tonumber(redisTime[1]) * 1000 + math.floor(tonumber(redisTime[2]) / 1000)

local vals = redis.call('HMGET', key, 'tokens', 'lastRefill')
local tokens = tonumber(vals[1])
local lastRefill = tonumber(vals[2])

local msPerToken = 1000 / refillRate

if tokens == nil then
    tokens = maxTokens
    lastRefill = currentTime
else
    local elapsed = currentTime - lastRefill
    if elapsed > 0 then
        local refill = math.floor(elapsed * refillRate / 1000)
        if refill > 0 then
            local oldTokens = tokens
            tokens = math.min(maxTokens, tokens + refill)
            local tokensAdded = tokens - oldTokens

            -- Advance lastRefill only by the exact time consumed by the tokens added,
            -- preserving the fractional remainder for the next check.
            if tokens == maxTokens then
                lastRefill = currentTime
            else
                lastRefill = lastRefill + math.floor(tokensAdded * 1000 / refillRate)
            end
        end
    end
end

local allowed = 0
if tokens >= 1 then
    tokens = tokens - 1
    allowed = 1
end

redis.call('HSET', key, 'tokens', tokens, 'lastRefill', lastRefill)

local msToFull = (maxTokens - tokens) / (refillRate / 1000)
local ttl = math.ceil(msToFull / 1000) + 60
redis.call('EXPIRE', key, ttl)

if allowed == 1 then
    return tokens
else
    -- Time remaining until the next token becomes available, accounting
    -- for however much of the current refill cycle has already elapsed.
    local elapsedSinceRefill = currentTime - lastRefill
    local waitTime = math.ceil(msPerToken - (elapsedSinceRefill % msPerToken))
    return -waitTime
end