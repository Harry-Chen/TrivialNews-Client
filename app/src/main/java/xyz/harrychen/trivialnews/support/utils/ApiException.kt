package xyz.harrychen.trivialnews.support.utils

import java.io.IOException

class ApiException(errorCode: Int, errorMessage: String, reason: String) :
        IOException("News API returned error!\nCode: $errorCode\nMessage: $errorMessage\nReason: $reason")