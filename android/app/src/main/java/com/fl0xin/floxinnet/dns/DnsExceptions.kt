package com.fl0xin.floxinnet.dns

sealed class DnsException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class MalformedQuery : DnsException("Malformed DNS query")
    class UpstreamTimeout : DnsException("DNS upstream timeout")
    class UpstreamUnavailable(cause: Throwable? = null) : DnsException("DNS upstream unavailable", cause)
}
