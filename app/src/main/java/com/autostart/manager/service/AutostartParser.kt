package com.autostart.manager.service

/** Boot receivers grouped by package, plus the state that decides whether each app starts after reboot. */
data class BootSnapshot(
    val receiversByPackage: Map<String, List<String>>,
    val enabledReceivers: Set<String>,
    val appOpIgnored: Set<String>,
) {
    fun isAllowed(pkg: String): Boolean =
        pkg !in appOpIgnored && receiversByPackage[pkg].orEmpty().any { it in enabledReceivers }
}

object AutostartParser {
    val BOOT_ACTIONS = listOf(
        "android.intent.action.BOOT_COMPLETED",
        "android.intent.action.LOCKED_BOOT_COMPLETED",
        "android.intent.action.QUICKBOOT_POWERON",
        "com.htc.intent.action.QUICKBOOT_POWERON",
    )

    private const val MATCH_DISABLED_COMPONENTS = 0x200

    /** Every boot receiver, disabled ones included. */
    val ALL_RECEIVERS_QUERY = BOOT_ACTIONS.joinToString("; ") {
        "pm query-receivers --components --query-flags $MATCH_DISABLED_COMPONENTS -a $it"
    }

    /** Only the receivers the package manager would actually deliver a boot broadcast to. */
    val ENABLED_RECEIVERS_QUERY = BOOT_ACTIONS.joinToString("; ") { "pm query-receivers --components -a $it" }

    // Not every ROM defines a BOOT_COMPLETED appop; there this prints an error and yields no packages.
    const val IGNORED_APPOP_QUERY = "cmd appops query-op --user 0 BOOT_COMPLETED ignore"

    private val packageName = Regex("[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+")
    private val className = Regex("[A-Za-z_\$][A-Za-z0-9_\$]*(\\.[A-Za-z_\$][A-Za-z0-9_\$]*)*")

    fun isPackageName(value: String): Boolean = packageName.matches(value)

    /** Turns `pkg/.Receiver` or `pkg/full.Receiver` into `pkg/pkg.Receiver`; null for anything else. */
    fun normalize(component: String): String? {
        val pkg = component.substringBefore('/', "")
        val cls = component.substringAfter('/', "")
        if (!isPackageName(pkg) || cls.isEmpty()) return null
        val fullClass = if (cls.startsWith(".")) pkg + cls else cls
        return if (className.matches(fullClass)) "$pkg/$fullClass" else null
    }

    /** Parses `pm query-receivers --components` output, one component per line. */
    fun parseComponents(output: String): Set<String> = output.lineSequence()
        .mapNotNull { normalize(it.trim()) }
        .toSet()

    /** `cmd appops query-op` prints one package per line, or a status/error line. */
    fun parsePackageList(output: String): Set<String> = output.lineSequence()
        .map { it.trim() }
        .filter(::isPackageName)
        .toSet()

    fun groupByPackage(components: Collection<String>): Map<String, List<String>> =
        components.groupBy { it.substringBefore('/') }.mapValues { (_, receivers) -> receivers.sorted() }

    /** AppOp + every boot receiver in one shell transaction; success is verified by re-reading state. */
    fun switchCommand(pkg: String, receivers: List<String>, allow: Boolean): String {
        require(isPackageName(pkg)) { "Invalid package name: $pkg" }
        receivers.forEach { require(normalize(it) == it && it.startsWith("$pkg/")) { "Invalid component: $it" } }
        val (op, pmVerb) = if (allow) "allow" to "enable" else "ignore" to "disable"
        val commands = listOf("cmd appops set $pkg BOOT_COMPLETED $op") + receivers.map { "pm $pmVerb '$it'" }
        return commands.joinToString("; ")
    }
}
