package cherhy.jung.gptinterview.domain.customer

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {
    // 메서드와 메서드 사이는 고백을 넣어볼까요?
    fun existsByEmail(email: String): Boolean

    @EntityGraph(attributePaths = ["customerAuthorities"])
    fun findWithAuthorityByEmail(email: String): Customer?

    fun findByEmail(email: String): Customer?

}