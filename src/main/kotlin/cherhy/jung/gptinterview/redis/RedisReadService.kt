package cherhy.jung.gptinterview.redis

import cherhy.jung.gptinterview.annotation.ReadService
import cherhy.jung.gptinterview.exception.DomainName.CERTIFICATE_NUMBER
import cherhy.jung.gptinterview.exception.NotFoundException
import cherhy.jung.gptinterview.redis.RedisKey.ACCESS_TOKEN
import cherhy.jung.gptinterview.redis.RedisKey.CERTIFICATE
import cherhy.jung.gptinterview.redis.RedisKey.QUESTION_TOKEN
import org.springframework.data.redis.core.RedisTemplate

@ReadService
class RedisReadService(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    fun getJwtToken(accessToken: String): String? =
        redisTemplate.opsForValue()
            .get(ACCESS_TOKEN + accessToken)
            ?.toString()


    fun getQuestionTokens(customerId: Long, start: Long = 0, end: Long = -1): MutableList<String> =
        redisTemplate.opsForList()
            .range(QUESTION_TOKEN + customerId, start, end)
            ?.map { it.toString() }?.toMutableList()
            ?: mutableListOf()

    fun getCertificate(certificate: String) {
        redisTemplate.opsForValue().get(CERTIFICATE + certificate)
            ?: throw NotFoundException(CERTIFICATE_NUMBER)
    }

}