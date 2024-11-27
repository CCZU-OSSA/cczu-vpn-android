package io.github.cczuossa.vpn.proto

class EnlinkProtocol {

    /**
     * A native method that is implemented by the 'proto' native library,
     * which is packaged with this application.
     */
    external fun version(): String

    companion object {
        // Used to load the 'proto' library on application startup.
        init {
            System.loadLibrary("proto")
        }
    }
}