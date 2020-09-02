package es.unizar.gde.sensors.utilities

/**
 * Android fragments are deprecated and ugly to use, these are not android fragments, but the idea is the same
 */
interface Fragment {
    fun onCreate() {}
    fun onResume() {}
    fun onPause() {}
}


//        1.let { it.toString() } == "1"    // thing.let{ it.go() } == "gone"
//        1.run { this.toString() } == "1"  // thing.run{ this.run() } == "running"
//        1.also { it.toString() } == 1
//        1.apply { this.toString() } == 1
//
//        with(1) { this.toString() } == "1"
//        run { 1.toString() } == "1"