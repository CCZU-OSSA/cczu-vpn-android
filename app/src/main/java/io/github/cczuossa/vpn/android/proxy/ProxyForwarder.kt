package io.github.cczuossa.vpn.android.proxy

import android.os.ParcelFileDescriptor
import io.github.cczuossa.vpn.android.service.AppVpnService
import io.github.cczuossa.vpn.android.utils.debug
import io.github.cczuossa.vpn.proto.EnlinkProtocol
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Arrays
import kotlin.concurrent.thread

object ProxyForwarder {

    private lateinit var ins: FileInputStream
    private lateinit var ous: FileOutputStream
    private lateinit var service: AppVpnService
    private lateinit var fd: FileDescriptor
    private var watchDog = false


    fun close() {
        runCatching { ins.close() }
        runCatching { ous.close() }
        watchDog = false
        "Forwarder closed.".debug()
    }

    fun setup(descriptor: ParcelFileDescriptor, service: AppVpnService) {
        this.service = service
        this.fd = descriptor.fileDescriptor
        ins = FileInputStream(descriptor.fileDescriptor)
        ous = FileOutputStream(descriptor.fileDescriptor)
        proxyIn()
        proxyOut()
        watchDog()
    }

    private fun watchDog() {
        if (watchDog) return
        watchDog = true
        thread {
            while (watchDog) {
                "Watchdog running...".debug()
                runCatching {
                    val s_fd = fd.valid()
                    val s_service = EnlinkProtocol.serviceAvailable()
                    "Proxy status: fd@$s_fd service@$s_service".debug()
                    if (!s_fd || !s_service) {
                        ins.close()
                        ous.close()
                        EnlinkProtocol.stopService()
                        "connection closed,try auth".debug()
                        service.auth()
                    }
                    Thread.sleep(1000L)
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    private fun proxyIn() {
        thread {
            val byte = ByteArray(1024)
            var read = 0
            while (fd.valid()) {
                runCatching {
                    read = ins.read(byte)
                    if (read > 0) {
                        "send packet ${byte.contentToString()}".debug()
                        EnlinkProtocol.sendTCPPacket(byte, read)
                    }
                }.onFailure {
                    it.printStackTrace()
                }
            }

        }
    }

    private fun proxyOut() {
        thread {
            while (fd.valid()) {
                runCatching {
                    val bytes = EnlinkProtocol.receivePacket(1024)
                    if (bytes.isNotEmpty()) {
                        ous.write(bytes, 0, bytes.size)
                    }
                }.onFailure {
                    it.printStackTrace()
                }
            }

        }
    }

}