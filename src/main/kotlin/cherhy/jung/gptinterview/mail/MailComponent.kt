package cherhy.jung.gptinterview.mail

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.util.*


@Component
class MailComponent(
    private val javaMailSender: JavaMailSender,

    @Value("\${spring.mail.username}")
    private val sender: String,
) {

    fun sendMessage(email: String, certificate: String): Unit {
        val msg: String = """
                <h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">
                이메일 주소 확인
                </h1>
                <p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">
                아래 확인 코드를 회원가입 화면에서 입력해주세요.
                </p>
                <div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\">
                <table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4;
                height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\">
                <tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">
                $certificate
                "</td></tr></tbody></table></div>"
            """.trimIndent()

        val message = javaMailSender.createMimeMessage()
            .also { memeMessage ->
                memeMessage.addRecipients(MimeMessage.RecipientType.TO, email)
                memeMessage.subject = "ITerview 회원가입 인증 코드"
                memeMessage.setText(msg, "utf-8", "html")
                memeMessage.setFrom(InternetAddress(sender, "admin"))
            }

        javaMailSender.send(message)
    }

    fun sendPasswordMessage(email: String, password: String): Unit {
        val msg = """
                <h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">
                임시 비밀번호 발급 완료
                </h1>
                <p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">
                아래 임시 비밀번호로 로그인을 진행해주세요.
                </p>
                <p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">
                임시 비밀번호 발급을 요청하지 않았을 경우, 고객센터로 문의 부탁드립니다.
                </p>
                <div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\">
                <table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4;
                height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\">
                <tbody> <tr> <td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">
                $password
                </td></tr></tbody></table></div>
            """.trimIndent()

        val message = javaMailSender.createMimeMessage()
            .also { memeMessage ->
                memeMessage.addRecipients(MimeMessage.RecipientType.TO, email)
                memeMessage.subject = "ITerview 임시 비밀번호 발급 메일"
                memeMessage.setText(msg, "utf-8", "html")
                memeMessage.setFrom(InternetAddress(sender, "admin"))
            }
        javaMailSender.send(message)
    }

}