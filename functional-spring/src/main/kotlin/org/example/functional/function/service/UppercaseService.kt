package org.example.functional.function.service

import org.springframework.stereotype.Service

@Service
class UppercaseService {

    fun uppercase(value: String) : String {
        return value.toUpperCase()
    }
}
