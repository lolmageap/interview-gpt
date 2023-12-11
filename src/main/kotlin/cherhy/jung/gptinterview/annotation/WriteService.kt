package cherhy.jung.gptinterview.annotation

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = false)
annotation class WriteService