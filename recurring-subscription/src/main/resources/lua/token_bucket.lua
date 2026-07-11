local key = KEYS[1]

local maxTokens = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])

-- Get Redis server time
local redisTime = redis.call('TIME')
local currentTime =
	tonumber(redisTime[1]) * 1000 +
	math.floor(tonumber(redisTime[2]) / 1000)

local tokens = tonumber(redis.call('HGET', key, 'tokens'))
local lastRefill = tonumber(redis.call('HGET', key, 'lastRefill'))

if tokens == nil then

	tokens = maxTokens
	lastRefill = currentTime

end

local elapsed = currentTime - lastRefill

local refill = math.floor(elapsed * refillRate / 1000)

if refill > 0 then

	tokens = math.min(maxTokens, tokens + refill)

	lastRefill = currentTime

end

if tokens > 0 then

	tokens = tokens - 1

	redis.call(
		'HSET',
		key,
		'tokens',
		tokens,
		'lastRefill',
		lastRefill
	)

	return tokens

end

redis.call(
	'HSET',
	key,
	'tokens',
	tokens,
	'lastRefill',
	lastRefill
)

local waitTime = math.ceil(1000 / refillRate)

return -waitTime