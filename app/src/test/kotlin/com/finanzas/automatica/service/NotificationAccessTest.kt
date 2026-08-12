package com.finanzas.automatica.service

import org.junit.jupiter.api.Test

class NotificationAccessTest {

    private val packageName = "com.finanzas.automatica"
    private val className = "com.finanzas.automatica.service.NotificationCaptureService"

    @Test
    fun `detect own listener in enabled listeners list`() {
        val enabled = "com.other.app/com.other.app.Listener:com.finanzas.automatica/com.finanzas.automatica.service.NotificationCaptureService"
        assert(NotificationAccess.isListenerEnabled(enabled, packageName, className))
    }

    @Test
    fun `returns false when no listener is enabled`() {
        assert(!NotificationAccess.isListenerEnabled(null, packageName, className))
        assert(!NotificationAccess.isListenerEnabled("", packageName, className))
        assert(!NotificationAccess.isListenerEnabled("   ", packageName, className))
    }

    @Test
    fun `returns false when only other listeners are enabled`() {
        val enabled = "com.other.app/com.other.app.Listener"
        assert(!NotificationAccess.isListenerEnabled(enabled, packageName, className))
    }

    @Test
    fun `matches exactly our component and not a partial prefix`() {
        val similar = "com.finanzas.automatica/com.finanzas.automatica.service.NotificationCaptureService2"
        assert(!NotificationAccess.isListenerEnabled(similar, packageName, className))
    }
}
