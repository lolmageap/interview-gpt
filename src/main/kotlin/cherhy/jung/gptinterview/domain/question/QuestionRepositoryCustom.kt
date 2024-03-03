package cherhy.jung.gptinterview.domain.question

import cherhy.jung.gptinterview.domain.question.constant.QuestionType
import cherhy.jung.gptinterview.domain.question.dto.QuestionRequestVo
import cherhy.jung.gptinterview.domain.question.entity.QFramework.framework
import cherhy.jung.gptinterview.domain.question.entity.QPrograming.programing
import cherhy.jung.gptinterview.domain.question.entity.QQuestion.question
import cherhy.jung.gptinterview.domain.question.entity.Question
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.BooleanExpression
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

interface QuestionRepositoryCustom {
    fun findByQuestionRequestS(
        questionRequestS: QuestionRequestVo,
        questionTokens: List<String>,
    ): List<Question>

    fun findByTokensIn(questionTokens: List<String>): List<Question>

    fun jpqlTest()
}

class QuestionRepositoryCustomImpl : QuestionRepositoryCustom, QuerydslRepositorySupport(Question::class.java) {
    override fun findByQuestionRequestS(
        questionRequestS: QuestionRequestVo,
        alreadyQuestion: List<String>,
    ): List<Question> {
        val query = from(question)

        return query
            .where(
                BooleanBuilder().let { condition ->
                    checkQuestionType(questionRequestS)?.let { condition.or(it) }
                    checkPrograming(questionRequestS)?.let { condition.or(it) }
                    checkFramework(questionRequestS)?.let { condition.or(it) }
                    checkLevel(questionRequestS)?.let { condition.and(it) }
                    condition.and(question.token.notIn(alreadyQuestion))
                }
            )
            .fetch()
            .also {
                it.shuffle()
            }
    }

    override fun findByTokensIn(alreadyQuestion: List<String>): List<Question> =
        from(question)
            .where(
                question.token.`in`(alreadyQuestion),
            )
            .fetch()
            .sortedBy {
                alreadyQuestion.indexOf(it.token)
            }
            .reversed()

    private fun checkLevel(questionRequestS: QuestionRequestVo): BooleanExpression? {
        return if (questionRequestS.levels.isNotEmpty()) {
            question.level.`in`(questionRequestS.levels)
        } else null
    }

    private fun checkQuestionType(questionRequestS: QuestionRequestVo): BooleanExpression? {
        return if (questionRequestS.questionTypes.isNotEmpty()) {
            question.questionType.`in`(questionRequestS.questionTypes)
        } else null
    }

    private fun checkPrograming(questionRequestS: QuestionRequestVo): BooleanExpression? {
        return if (questionRequestS.programingTypes.isNotEmpty()) {
            from(programing)
                .join(question)
                .on(programing.questionId.eq(question.id))
                .where(programing.programingType.`in`(questionRequestS.programingTypes))
                .select(programing.questionId)
                .fetch()
                .let {
                    question.id.`in`(it)
                }
        } else null
    }

    private fun checkFramework(questionRequestS: QuestionRequestVo): BooleanExpression? {
        return if (questionRequestS.frameworkTypes.isNotEmpty()) {
            from(framework)
                .join(question)
                .on(framework.questionId.eq(question.id))
                .where(framework.frameworkType.`in`(questionRequestS.frameworkTypes))
                .select(framework.questionId)
                .fetch()
                .let {
                    question.id.`in`(it)
                }
        } else null
    }

    override fun jpqlTest() {
        val jpql = jpql {
            select(
                entity(Question::class)
            ).from(
                entity(Question::class)
            ).where(
                entity(Question::class)(Question::id).eq(1L)
                    .and(entity(Question::class)(Question::token).eq("token"))
                    .or(entity(Question::class)(Question::questionType).eq(QuestionType.NETWORK))
            )
        }
    }
}