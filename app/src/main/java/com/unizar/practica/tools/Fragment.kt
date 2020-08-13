package com.unizar.practica.tools

interface Fragment {
    fun onCreate() {}
    fun onResume() {}
    fun onPause() {}
}



//        1.let { it.toString() } == "1"
//        1.run { this.toString() } == "1"
//        1.also { it.toString() } == 1
//        1.apply { this.toString() } == 1
//
//        with(1) { this.toString() } == "1"
//        run { 1.toString() } == "1"