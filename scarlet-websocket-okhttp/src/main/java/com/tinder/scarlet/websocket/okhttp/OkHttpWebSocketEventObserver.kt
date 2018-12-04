/*
 * © 2018 Match Group, LLC.
 */

package com.tinder.scarlet.websocket.okhttp

import com.tinder.scarlet.Message
import com.tinder.scarlet.ShutdownReason
import com.tinder.scarlet.WebSocket
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import okhttp3.Response
import okhttp3.WebSocketListener
import okio.ByteString

internal class OkHttpWebSocketEventObserver : WebSocketListener() {
    private val processor = PublishProcessor.create<WebSocket.Event>()

    fun observe(): Flowable<WebSocket.Event> = processor

    fun terminate() = processor.onComplete()

    override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
        processor.offer(WebSocket.Event.OnConnectionOpened(webSocket))
    }

    override fun onMessage(webSocket: okhttp3.WebSocket, bytes: ByteString) {
        processor.offer(WebSocket.Event.OnMessageReceived(Message.Bytes(bytes.toByteArray())))
    }

    override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
        processor.offer(WebSocket.Event.OnMessageReceived(Message.Text(text)))
    }

    override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        processor.offer(WebSocket.Event.OnConnectionClosing(ShutdownReason(code, reason)))
    }

    override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        processor.offer(WebSocket.Event.OnConnectionClosed(ShutdownReason(code, reason)))
    }

    override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: Response?) {
        processor.offer(WebSocket.Event.OnConnectionFailed(t))
    }
}
