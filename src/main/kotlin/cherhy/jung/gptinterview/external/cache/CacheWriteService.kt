package cherhy.jung.gptinterview.external.cache

import cherhy.jung.gptinterview.annotation.WriteService
import cherhy.jung.gptinterview.controller.dto.QuestionRequest
import cherhy.jung.gptinterview.property.JwtProperty
import cherhy.jung.gptinterview.external.cache.CacheKey.CERTIFICATE
import cherhy.jung.gptinterview.external.cache.CacheKey.FRAMEWORK_TYPE
import cherhy.jung.gptinterview.external.cache.CacheKey.PROGRAMING_TYPE
import cherhy.jung.gptinterview.external.cache.CacheKey.QUESTION_LEVEL
import cherhy.jung.gptinterview.external.cache.CacheKey.QUESTION_TOKEN
import cherhy.jung.gptinterview.external.cache.CacheKey.QUESTION_TYPE
import cherhy.jung.gptinterview.external.cache.CacheKey.REFRESH_TOKEN
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit

@WriteService
class CacheWriteService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val jwtProperty: JwtProperty,
) {
    fun addJwtToken(refreshToken: String, email: String) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN + refreshToken, email)
        redisTemplate.expire(
            REFRESH_TOKEN + refreshToken,
            jwtProperty.refreshTokenValidityInSeconds.toLong(),
            TimeUnit.SECONDS,
        )
    }

    fun addQuestionToken(customerId: Long, questionToken: String) =
        redisTemplate.opsForList().rightPush(QUESTION_TOKEN + customerId, questionToken)

    fun addCertificate(email: String, certificate: String) {
        redisTemplate.opsForValue().set(CERTIFICATE + email, certificate)
        redisTemplate.expire(
            CERTIFICATE + email,
            180,
            TimeUnit.SECONDS,
        )
    }

    fun addQuestionAttributes(
        customerId: Long,
        request: QuestionRequest,
    ) {
        val opsForHash = redisTemplate.opsForHash<String, String>()

        val questionTypes = request.questionTypes.joinToString(", ") { it.name }
        val programingTypes = request.programingTypes.joinToString(", ") { it.name }
        val frameworkTypes = request.frameworkTypes.joinToString(", ") { it.name }
        val levels = request.levels.joinToString(", ") { it.name }

        opsForHash.put(QUESTION_TOKEN + customerId, QUESTION_TYPE, questionTypes)
        opsForHash.put(QUESTION_TOKEN + customerId, PROGRAMING_TYPE, programingTypes)
        opsForHash.put(QUESTION_TOKEN + customerId, FRAMEWORK_TYPE, frameworkTypes)
        opsForHash.put(QUESTION_TOKEN + customerId, QUESTION_LEVEL, levels)
    }

    fun setEntity(
        key: String,
        entity: Any,
    ) {
        // TODO: 여기 구현 해야함
        redisTemplate.opsForValue().set(key, entity)
        redisTemplate.expire(key, 1, TimeUnit.SECONDS)
    }
}