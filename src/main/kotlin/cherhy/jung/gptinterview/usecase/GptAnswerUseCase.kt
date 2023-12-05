package cherhy.jung.gptinterview.usecase

import cherhy.jung.gptinterview.annotation.UseCase
import cherhy.jung.gptinterview.domain.customer.CustomerReadService
import cherhy.jung.gptinterview.domain.gpt.GptApi
import cherhy.jung.gptinterview.domain.gpt.GptResponseS
import cherhy.jung.gptinterview.domain.question.QuestionHistoryWriteService
import cherhy.jung.gptinterview.domain.question.QuestionReadService
import cherhy.jung.gptinterview.domain.question.dto.QuestionHistoryRequestS
import cherhy.jung.gptinterview.domain.question.dto.toQuestionHistory
import cherhy.jung.gptinterview.restcontroller.GptRequest
import cherhy.jung.gptinterview.util.Generator

// 모든 유스케이스들은 도메인 밑에 엔티티에 속하게 하는건 어떨까요?
@UseCase
class GptAnswerUseCase(
    private val gptApi: GptApi,
    private val customerReadService: CustomerReadService,
    private val questionReadService: QuestionReadService,
    private val questionHistoryWriteService: QuestionHistoryWriteService,
) {

    // 코드에 중간중간 문맥처럼 엔터를 넣어서 가독성을 높혀볼까요?
    // 지금은 너무 다 붙어있어요.
    fun requestAnswerToGpt(customerId: Long, gptRequest: GptRequest): GptResponseS {
        val customer = customerReadService.getCustomerById(customerId)
        val question = questionReadService.getQuestionByToken(gptRequest.questionToken)
        val questionToGpt = Generator.generateQuestionToGpt(question.title, gptRequest.answer)
        val feedback = gptApi.generateText(questionToGpt)
        val questionHistory = QuestionHistoryRequestS.of(question.id, customer.id, feedback, gptRequest.answer)
        val history = questionHistoryWriteService.addHistory(questionHistory.toQuestionHistory())
        return GptResponseS(history.token, feedback)
    }

    fun requestOnlyAnswerKeyToGpt(customerId: Long, token: String): GptResponseS {
        val customer = customerReadService.getCustomerById(customerId)
        val question = questionReadService.getQuestionByToken(token)
        val questionToGpt = Generator.generateAnswerKeyToGpt(question.title)
        val feedback = gptApi.generateText(questionToGpt)
        val questionHistory = QuestionHistoryRequestS.of(question.id, customer.id, feedback)
        val history = questionHistoryWriteService.addHistory(questionHistory.toQuestionHistory())
        return GptResponseS(history.token, feedback)
    }

}