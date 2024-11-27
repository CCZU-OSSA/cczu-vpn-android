package io.github.cczuossa.vpn.proto

class EnlinkProtocol {

    /**
     * A native method that is implemented by the 'proto' native library,
     * which is packaged with this application.
     */



    companion object {
        // Used to load the 'proto' library on application startup.
        init {
            System.loadLibrary("cczuvpnproto")
            System.loadLibrary("native")
        }

        @JvmStatic
        external fun version(): String
    }
}