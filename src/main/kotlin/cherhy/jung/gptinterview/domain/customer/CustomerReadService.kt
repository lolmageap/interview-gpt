package cherhy.jung.gptinterview.domain.customer

import cherhy.jung.gptinterview.annotation.ReadService
import cherhy.jung.gptinterview.domain.customer.dto.CustomerResponseS
import cherhy.jung.gptinterview.exception.DomainName
import cherhy.jung.gptinterview.exception.DomainName.EMAIL
import cherhy.jung.gptinterview.exception.ExistException
import cherhy.jung.gptinterview.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull

@ReadService
class CustomerReadService(
    private val customerRepository: CustomerRepository,
) {
    fun getCustomerById(id: Long): CustomerResponseS =
        customerRepository.findByIdOrNull(id)
            ?.let(CustomerResponseS::of)
            ?: throw NotFoundException(DomainName.CUSTOMER)

    fun getCustomerByEmail(email: String): CustomerResponseS =
        customerRepository.findByEmail(email)
            ?.let(CustomerResponseS::of)
            ?: throw NotFoundException(DomainName.CUSTOMER)

    fun checkDuplicatedEmail(email: String) =
        customerRepository.existsByEmail(email).let { isExist ->
            if (isExist) throw ExistException(EMAIL)
        }
}