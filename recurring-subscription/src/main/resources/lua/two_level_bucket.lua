-- Two-level token bucket: per-merchant limit + central (global) limit, atomic.
-- KEYS[1] = merchant bucket key  e.g. {pay}:merchant:123
-- KEYS[2] = global bucket key    e.g. {pay}:global
-- ARGV[1] = merchantMaxTokens
-- ARGV[2] = merchantRefillTokens
-- ARGV[3] = merchantWindowSeconds
-- ARGV[4] = globalMaxTokens
-- ARGV[5] = globalRefillTokens
-- ARGV[6] = globalWindowSeconds
--
-- Returns {1, 0}     -> allowed, send now
--         {0, waitMs} -> denied, retry after waitMs

local redisTime = redis.call('TIME')
local now = tonumber(redisTime[1]) * 1000 + math.floor(tonumber(redisTime[2]) / 1000)

local function load(key, maxTokens, refillTokens, windowSeconds)
    local rate = refillTokens / windowSeconds
    local vals = redis.call('HMGET', key, 'tokens', 'lastRefill')
    local tokens = tonumber(vals[1])
    local last = tonumber(vals[2])
    if tokens == nil then
        tokens = 0
    else
        local elapsed = now - last
        if elapsed > 0 then
            tokens = math.min(maxTokens, tokens + (elapsed * rate) / 1000)
        end
    end
    return tokens, rate
end

local function save(key, tokens, maxTokens, rate)
    redis.call('HSET', key, 'tokens', tokens, 'lastRefill', now)
    local ttl = math.ceil((maxTokens - tokens) / rate) + 60
    redis.call('EXPIRE', key, ttl)
end

local mMax = tonumber(ARGV[1])
local gMax = tonumber(ARGV[4])

local mTokens, mRate = load(KEYS[1], mMax, tonumber(ARGV[2]), tonumber(ARGV[3]))
local gTokens, gRate = load(KEYS[2], gMax, tonumber(ARGV[5]), tonumber(ARGV[6]))

if mTokens >= 1 and gTokens >= 1 then
    save(KEYS[1], mTokens - 1, mMax, mRate)
    save(KEYS[2], gTokens - 1, gMax, gRate)
    return {1, 0}
end

-- Denied: persist the refilled state, report the longer wait.
save(KEYS[1], mTokens, mMax, mRate)
save(KEYS[2], gTokens, gMax, gRate)

local waitM = 0
local waitG = 0
if mTokens < 1 then waitM = math.ceil((1 - mTokens) * 1000 / mRate) end
if gTokens < 1 then waitG = math.ceil((1 - gTokens) * 1000 / gRate) end
return {0, math.max(waitM, waitG)}