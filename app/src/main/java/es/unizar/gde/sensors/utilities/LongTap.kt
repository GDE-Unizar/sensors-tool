//package es.unizar.gde.sensors.utilities
//
//import android.view.View
//import android.widget.Toast
//
//fun initLongTap(vararg views: View) {
//    views.forEach { view ->
//        view.setOnLongClickListener {
//            view.contentDescription?.let { Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show() }
//            true
//        }
//    }
//}