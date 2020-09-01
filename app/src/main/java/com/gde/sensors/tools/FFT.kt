package com.gde.sensors.tools

import org.jtransforms.fft.DoubleFFT_1D
import java.lang.Math.sqrt

/**
 * Performs a FFT (Fast Fourier Transform) over an array of values generate at Hz Hertz
 * @return a pair (values,frecuencies) ready to be plotted
 */
fun List<Double>.FFT(Hz: Double): Pair<List<Double>, List<Double>> {

    val L = this.size

    val sinValue_re_im = DoubleArray(L * 2) // because FFT takes an array where its positions alternate between real and imaginary

    for (i in 0 until L) {
        sinValue_re_im[2 * i] = this[i] // real part
        sinValue_re_im[2 * i + 1] = 0.0 //imaginary part
    }

    // matlab
    // tf = fft(y1);
    val fft = DoubleFFT_1D(L.toLong())
    fft.complexForward(sinValue_re_im)
    val tf = sinValue_re_im.clone()

    // matlab
    // P2 = abs(tf/L);
    val P2 = DoubleArray(L)
    for (i in 0 until L) {
        val re = tf[2 * i] / L
        val im = tf[2 * i + 1] / L
        P2[i] = sqrt(re * re + im * im)
    }

    // P1 = P2(1:L/2+1);
    val P1 = DoubleArray(L / 2) // single-sided: the second half of P2 has the same values as the first half
    System.arraycopy(P2, 0, P1, 0, L / 2)
    // P1(2:end-1) = 2*P1(2:end-1);
    if (L >= 4) { // error when P1.size < 1
        System.arraycopy(P1, 1, P1, 1, L / 2 - 2)
        for (i in 1 until P1.size - 1) {
            P1[i] = 2 * P1[i]
        }
    }

    // extra: logarithmic Y scale
    for (i in 1 until P1.size - 1) {
        P1[i] = 20 * kotlin.math.log(P1[i], 10.0)
    }


    // f = Fs*(0:(L/2))/L;
    val f = DoubleArray(L / 2 + 1)
    for (i in 0 until L / 2 + 1) {
        f[i] = Hz * i.toDouble() / L
    }

    return P1.toList() to f.toList()
}