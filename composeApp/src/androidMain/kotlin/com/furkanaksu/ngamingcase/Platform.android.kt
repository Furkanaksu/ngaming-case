package com.furkanaksu.ngamingcase

import android.os.Build

actual fun platformName(): String = "Android ${Build.VERSION.RELEASE}"
