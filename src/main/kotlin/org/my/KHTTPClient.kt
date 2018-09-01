package org.my

import java.net.*
import javax.net.ssl.HttpsURLConnection

class KHTTPClient(private val host: URL,
                  private val readTimeout: Int = 10000,
                  private val connectTimeout: Int = 10000) {

    private fun connect(path: String, method: String): URLConnection {
        val url = URL(host, path)
        val con = if (url.protocol == "https") {
            val c = url.openConnection() as HttpsURLConnection

            c
        } else url.openConnection() as HttpURLConnection


        con.readTimeout = readTimeout
        con.connectTimeout = connectTimeout

        con.requestMethod = method
        con.doOutput = true

        return con
    }

    fun post(path: String, params: Map<String, List<String>>?) {
        val con = connect(path, "POST")

        //con.getOutputStream().bufferedWriter(StandardCharsets.UTF_8).use { it. }
    }


}

fun toQuery(params: Map<String, List<String>>): String {
    return enc(
            params.toList().flatMap { pair -> pair.second.map { pair.first to it } }.foldIndexed("?") { i, acc, pair -> acc + "${if (i > 0) "&" else ""}${pair.first}=${pair.second}" })
}

fun String.splitQuery(): Map<String, List<String>> =
        this.split("&").map { splitQueryParameter(it) }.groupBy({ it.first }, { it.second })

fun splitQuery(url: URI): Map<String, List<String>> {
    return if (url.query.isNullOrEmpty()) {
        emptyMap()
    } else {
        url.query.splitQuery()
    }
}


private fun splitQueryParameter(it: String): Pair<String, String> {
    val idx = it.indexOf("=")
    val key = dec(if (idx > 0) it.substring(0, idx) else it)
    val value = dec(if (idx > 0 && it.length > idx + 1) it.substring(idx + 1) else "")
    return key to value
}

private fun dec(s: String): String = URLDecoder.decode(s, "UTF-8")
private fun enc(s: String): String = URLEncoder.encode(s, "UTF-8")

fun main(args: Array<String>) {
    println(toQuery(mapOf("name" to listOf("vasya"), "id" to listOf("10"), "address" to listOf("street"), "params" to listOf("param1", "param2", "param3"))))

    val s = "name=vasya&id=100&params=1&params=2&params=3"

    println(s.splitQuery())
}
