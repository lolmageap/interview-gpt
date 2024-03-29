package cherhy.jung.gptinterview.external.redis

import cherhy.jung.gptinterview.annotation.ReadService
import cherhy.jung.gptinterview.controller.dto.QuestionAttributeResponse
import cherhy.jung.gptinterview.domain.question.constant.FrameworkType
import cherhy.jung.gptinterview.domain.question.constant.ProgramingType
import cherhy.jung.gptinterview.domain.question.constant.QuestionLevel
import cherhy.jung.gptinterview.domain.question.constant.QuestionType
import cherhy.jung.gptinterview.exception.MessageType.CERTIFICATE_NUMBER
import cherhy.jung.gptinterview.exception.MessageType.EMAIL
import cherhy.jung.gptinterview.exception.NotFoundException
import cherhy.jung.gptinterview.external.redis.RedisKey.CERTIFICATE
import cherhy.jung.gptinterview.external.redis.RedisKey.FRAMEWORK_TYPE
import cherhy.jung.gptinterview.external.redis.RedisKey.PROGRAMING_TYPE
import cherhy.jung.gptinterview.external.redis.RedisKey.QUESTION_LEVEL
import cherhy.jung.gptinterview.external.redis.RedisKey.QUESTION_TOKEN
import cherhy.jung.gptinterview.external.redis.RedisKey.QUESTION_TYPE
import cherhy.jung.gptinterview.external.redis.RedisKey.REFRESH_TOKEN
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.access.AccessDeniedException

@ReadService
class RedisReadService(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    fun getEmailByRefreshToken(refreshToken: String?): String {
        if (refreshToken.isNullOrBlank()) {
            throw AccessDeniedException("잘못된 토큰")
        }

        return redisTemplate.opsForValue()
            .get(REFRESH_TOKEN + refreshToken)
            ?.toString()
            ?: throw AccessDeniedException("잘못된 토큰")
    }

    fun getQuestionTokens(customerId: Long, start: Long = 0, end: Long = -1): MutableList<String> =
        redisTemplate.opsForList()
            .range(QUESTION_TOKEN + customerId, start, end)
            ?.map { it.toString() }?.toMutableList()
            ?: mutableListOf()

    fun checkCertificate(email: String, certificateNumber: String) {
        val certificate = redisTemplate.opsForValue().get(CERTIFICATE + email)
            ?.toString()
            ?: throw NotFoundException(EMAIL)

        if (certificateNumber != certificate) throw NotFoundException(CERTIFICATE_NUMBER)
        redisTemplate.delete(CERTIFICATE + email)
    }

    fun getQuestionAttributes(id: Long): QuestionAttributeResponse {
        val hash = redisTemplate.opsForHash<String, String>()
            .entries(QUESTION_TOKEN + id)
            .mapValues { it.value.split(", ") }
            .toMap()

        val questionTypes =
            hash[QUESTION_TYPE]?.map(QuestionType::valueOf)
                ?: emptyList()

        val programingTypes =
            hash[PROGRAMING_TYPE]?.map(ProgramingType::valueOf)
                ?: emptyList()

        val frameworkTypes =
            hash[FRAMEWORK_TYPE]?.map(FrameworkType::valueOf)
                ?: emptyList()

        val levels =
            hash[QUESTION_LEVEL]?.map(QuestionLevel::valueOf)
                ?: emptyList()

        return QuestionAttributeResponse(
            questionTypes = questionTypes,
            programingTypes = programingTypes,
            frameworkTypes = frameworkTypes,
            levels = levels,
        )
    }
}