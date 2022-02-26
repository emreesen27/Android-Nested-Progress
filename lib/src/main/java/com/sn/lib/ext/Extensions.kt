package com.sn.lib.ext


import android.content.res.Resources.getSystem

val Float.dp: Float
    get() = (this * getSystem().displayMetrics.density + 0.5f)
