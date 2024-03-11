package com.seergroup.srj.gl.shaderProgram.seermap

import kotlin.math.max
import kotlin.math.min

class Bound {
    var xMax: Float = Float.NEGATIVE_INFINITY
    var xMin: Float = Float.POSITIVE_INFINITY
    var yMax: Float = Float.NEGATIVE_INFINITY
    var yMin: Float = Float.POSITIVE_INFINITY

    operator fun plusAssign(bound: Bound) {
        xMax = max(xMax, bound.xMax)
        yMax = max(yMax, bound.yMax)
        xMin = min(xMin, bound.xMin)
        yMin = min(yMin, bound.yMin)
    }

    operator fun plus(bound: Bound): Bound {
        return Bound().run {
            this.xMax = max(xMax, bound.xMax);
            this.yMax = max(yMax, bound.yMax);
            this.xMin = min(xMin, bound.xMin);
            this.yMin = min(yMin, bound.yMin);
            this
        }
    }
}