package com.didichuxing.doraemonkit.rpc.ability


import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.mock.proxy.ProxyRequest
import com.didichuxing.doraemonkit.kit.mc.mock.proxy.ProxyResponse
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil
import com.didichuxing.doraemonkit.rpc.extension.string
import didihttp.Headers
import didihttp.Request
import didihttp.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * didi Create on 2022/3/10 .
 *
 * Copyright (c) 2022/3/10 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/3/10 7:45 下午
 * @Description 用一句话说明文件功能
 */
object ProxyUtils {


    fun createHeaders(headers: Headers): String {
        return headers.toString()
    }


    fun nowTime(): String {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS")
        return df.format(Date())
    }


    fun createProxyRequest(did: String, request: Request): ProxyRequest {
        val aid = DoKitMcManager.currentActionId
        val time = nowTime()
        val url = request.url()
        val scheme = url.scheme()
        val host = url.host()
        val path = url.encodedPath()
        val query = string(url.encodedQuery())
        val fragment = string(url.fragment())
        val headers = createHeaders(request.headers())
        val body = request.body()
        val contentType = string(body?.contentType().toString())
        val contentLength = body?.contentLength() ?: 0
        val bodyString = body?.string() ?: ""
        val protocol = "http-" + request.method()
        return ProxyRequest(
            did, aid, url.toString(), scheme, host, path, query, fragment,
            time, headers, contentType, contentLength, bodyString, protocol
        )
    }


    fun createEmptyProxyResponse(did: String): ProxyResponse {
        return ProxyResponse(
            did, nowTime(), "",
            "", 0, "", 404, false, "mock"
        )
    }

    fun createProxyResponse(did: String, response: Response): ProxyResponse {
        val time = nowTime()
        val headers = createHeaders(response.headers())
        val code = response.code()
        val body = response.body()
        val contentType = body?.contentType().toString() ?: ""
        val image = InterceptorUtil.isImg(contentType)
        val contentLength = body?.contentLength() ?: 0
        var bodyString = ""
        if (!image) {
            try {
                bodyString = body?.string() ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val protocol = response.protocol().toString()


        return ProxyResponse(
            did, time, headers,
            contentType, contentLength, bodyString, code, image, protocol
        )
    }


    fun filterRequest(request: Request): Boolean {
        val host = request.url().host()
        if (host.equals(NetworkManager.MOCK_HOST, ignoreCase = true)) {
            return true
        } else if (host.equals(NetworkManager.DOKIT_HOST, ignoreCase = true)) {
            return true
        }
        return false
    }

    private fun string(arg: String?): String {
        return arg ?: ""

    }

}
