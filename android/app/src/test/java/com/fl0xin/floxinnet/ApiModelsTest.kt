package com.fl0xin.floxinnet

import com.fl0xin.floxinnet.data.Status
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiModelsTest {
    @Test fun statusModelKeepsBackendFields() {
        val status = Status(true, 123, "DATA_SAVER", "Cloudflare", 42, 5353)
        assertEquals("DATA_SAVER", status.mode)
        assertEquals(5353, status.port)
    }
}
