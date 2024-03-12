package org.gymapp.backend.security.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader

class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private val cachedBody: ByteArray

    init {
        val inputStream = request.inputStream
        cachedBody = inputStream.readBytes()
    }

    override fun getInputStream(): ServletInputStream {
        return CachedBodyServletInputStream(cachedBody)
    }

    override fun getReader(): BufferedReader {
        val reader = InputStreamReader(ByteArrayInputStream(cachedBody))
        return BufferedReader(reader)
    }

    private class CachedBodyServletInputStream(private val cachedBody: ByteArray) : ServletInputStream() {
        private val inputStream: InputStream = ByteArrayInputStream(cachedBody)

        override fun isFinished(): Boolean = inputStream.available() == 0

        override fun isReady(): Boolean = true

        override fun setReadListener(readListener: ReadListener?) {
            throw RuntimeException("Not implemented")
        }

        override fun read(): Int = inputStream.read()
    }
}

@Component
class RequestLoggingFilter : OncePerRequestFilter() {

    private val reqLogger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrappedRequest = CachedBodyHttpServletRequest(request)
        reqLogger.info("Incoming request ${wrappedRequest.method} ${wrappedRequest.requestURI}")

        val requestBody = wrappedRequest.reader.use(BufferedReader::readText)
        reqLogger.info("Request body: $requestBody")

        filterChain.doFilter(wrappedRequest, response)
    }
}