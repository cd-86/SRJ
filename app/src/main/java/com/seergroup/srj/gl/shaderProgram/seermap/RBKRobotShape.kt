package com.seergroup.srj.gl.shaderProgram.seermap

import rbk.protocol.model.MessageModel

class RBKRobotShape(model: MessageModel.Message_Model) {
    enum class ShapeType { RECTANGLE, CIRCLE }

    var shapeType: ShapeType = ShapeType.CIRCLE
        private set
    var radius: Float = 0.5F
        private set
    var width: Float = 0F
        private set
    var tail: Float = 0F
        private set
    var head: Float = 0F
        private set

    init {
        MainLoop@ for (dt in model.deviceTypesList) {
            if (dt.name != "chassis") continue
            for (dp in dt.devicesList.first().deviceParamsList) {
                if (dp.key != "shape") continue
                for (cp in dp.comboParam.childParamsList) {
                    if (cp.key != dp.comboParam.childKey) continue
                    when (cp.key) {
                        "rectangle" -> {
                            shapeType = ShapeType.RECTANGLE
                            for (p in cp.paramsList) {
                                when (p.key) {
                                    "width" -> width = p.doubleValue.toFloat()
                                    "tail" -> tail = p.doubleValue.toFloat()
                                    "head" -> head = p.doubleValue.toFloat()
                                }
                            }
                            break@MainLoop
                        }
                        "circle" -> {
                            shapeType = ShapeType.CIRCLE
                            for (p in cp.paramsList) {
                                if (p.key != "radius") continue
                                radius = p.doubleValue.toFloat()
                                break@MainLoop
                            }
                            break@MainLoop
                        }
                    }
                }
            }
        }
    }
}