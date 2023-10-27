package cherhy.jung.gptinterview.controller

import cherhy.jung.gptinterview.authority.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authority")
class AuthorityController(
    private val authCustomerService: AuthCustomerService,
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-in")
    fun signIn(@Valid @RequestBody signInRequest: SignInRequest) =
        authCustomerService.signIn(signInRequest.toCustomerRequest())

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-up")
    fun signUp(@Valid @RequestBody signUpRequest: SignUpRequest) =
        authCustomerService.signUp(signUpRequest.toCustomerRequest())

    @PostMapping("/sign-out")
    fun signOut() {}

}