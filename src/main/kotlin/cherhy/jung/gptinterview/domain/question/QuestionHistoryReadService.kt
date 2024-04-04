package cherhy.jung.gptinterview.domain.question

import cherhy.jung.gptinterview.annotation.ReadService
import org.springframework.data.domain.Pageable

@ReadService
class QuestionHistoryReadService(
    private val questionHistoryRepository: QuestionHistoryRepository,
) {
    fun getAllQuestionHistories(customerId: Long, pageable: Pageable) =
        questionHistoryRepository.findAllByCustomerId(customerId, pageable)

    fun getQuestionHistory(customerId: Long, token: String) =
        questionHistoryRepository.findByCustomerIdAndToken(customerId, token)
}

