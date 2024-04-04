package cherhy.jung.gptinterview.usecase

import cherhy.jung.gptinterview.annotation.UseCase
import cherhy.jung.gptinterview.domain.customer.CustomerReadService
import cherhy.jung.gptinterview.external.gpt.GptClient
import cherhy.jung.gptinterview.external.gpt.GptResponseVo
import cherhy.jung.gptinterview.domain.question.QuestionHistoryWriteService
import cherhy.jung.gptinterview.domain.question.QuestionReadService
import cherhy.jung.gptinterview.controller.dto.GptRequest
import cherhy.jung.gptinterview.domain.question.AnswerWriteService
import cherhy.jung.gptinterview.domain.question.entity.Answer
import cherhy.jung.gptinterview.domain.question.entity.QuestionHistory
import cherhy.jung.gptinterview.util.Generator

@UseCase
class RequestAnswerToGptUseCase(
    private val gptClient: GptClient,
    private val customerReadService: CustomerReadService,
    private val questionReadService: QuestionReadService,
    private val answerWriteService: AnswerWriteService,
    private val questionHistoryWriteService: QuestionHistoryWriteService,
) {
    fun execute(customerId: Long, request: GptRequest): GptResponseVo {
        val customer = customerReadService.getCustomerById(customerId)
        val question = questionReadService.getQuestionByToken(request.questionToken)

        val questionToGpt = Generator.questionToGpt(question.title, request.answer)

        val feedback = gptClient.requestAndReceiveFeedback(questionToGpt)

        val answer = Answer.of(customer.id, question.id, feedback, request.answer)
        val saveAnswer = answerWriteService.addAnswer(answer)

        val questionHistory = QuestionHistory.of(question.id, customer.id, saveAnswer.id)
        val history = questionHistoryWriteService.addHistory(questionHistory)

        return GptResponseVo(history.token, feedback)
    }
}