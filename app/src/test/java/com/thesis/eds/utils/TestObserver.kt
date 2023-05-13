package com.thesis.eds.utils

import androidx.lifecycle.Observer
import junit.framework.Assert.assertEquals

class TestObserver<T> : Observer<T> {
    private val values = mutableListOf<T>()

    override fun onChanged(value: T) {
        values.add(value)
    }

    fun assertValues(vararg expectedValues: T) {
        assertEquals(expectedValues.toList(), values)
    }
}
