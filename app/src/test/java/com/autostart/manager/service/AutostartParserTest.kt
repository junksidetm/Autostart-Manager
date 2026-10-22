package com.autostart.manager.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class AutostartParserTest {

    @Test
    fun `normalizes short, full and inner-class component names`() {
        assertEquals("com.whatsapp/com.whatsapp.boot.BootReceiver", AutostartParser.normalize("com.whatsapp/.boot.BootReceiver"))
        assertEquals("com.app/org.lib.Starter", AutostartParser.normalize("com.app/org.lib.Starter"))
        assertEquals("com.app/com.app.Boot\$Receiver", AutostartParser.normalize("com.app/.Boot\$Receiver"))
    }

    @Test
    fun `rejects status lines and injection attempts`() {
        assertNull(AutostartParser.normalize("No receivers found"))
        assertNull(AutostartParser.normalize("com.app/.A;reboot"))
        assertNull(AutostartParser.normalize("com.app/.A'"))
    }

    @Test
    fun `parses pm query-receivers output`() {
        val output = "  com.whatsapp/.boot.BootReceiver\nNo receivers found\norg.telegram.messenger/.AppStartReceiver\n"

        assertEquals(
            setOf("com.whatsapp/com.whatsapp.boot.BootReceiver", "org.telegram.messenger/org.telegram.messenger.AppStartReceiver"),
            AutostartParser.parseComponents(output),
        )
    }

    @Test
    fun `ignores appop error output`() {
        assertEquals(emptySet<String>(), AutostartParser.parsePackageList("Error: Unknown operation string: BOOT_COMPLETED"))
    }

    @Test
    fun `allowed needs an enabled receiver and no ignored appop`() {
        val snapshot = BootSnapshot(
            receiversByPackage = mapOf(
                "a.one" to listOf("a.one/a.one.R1", "a.one/a.one.R2"),
                "b.two" to listOf("b.two/b.two.R"),
                "c.three" to listOf("c.three/c.three.R"),
            ),
            enabledReceivers = setOf("a.one/a.one.R2", "c.three/c.three.R"),
            appOpIgnored = setOf("c.three"),
        )

        assertTrue(snapshot.isAllowed("a.one"))
        assertFalse(snapshot.isAllowed("b.two"))
        assertFalse(snapshot.isAllowed("c.three"))
    }

    @Test
    fun `switch command quotes components and toggles appop`() {
        val command = AutostartParser.switchCommand("com.app", listOf("com.app/com.app.Boot\$R"), allow = false)

        assertEquals("cmd appops set com.app BOOT_COMPLETED ignore; pm disable 'com.app/com.app.Boot\$R'", command)
    }

    @Test
    fun `switch command rejects components of another package`() {
        assertThrows(IllegalArgumentException::class.java) {
            AutostartParser.switchCommand("com.app", listOf("com.other/com.other.R"), allow = true)
        }
    }
}
