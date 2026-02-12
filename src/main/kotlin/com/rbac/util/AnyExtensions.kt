package com.rbac.util

import cn.hutool.json.JSONUtil


fun Any?.toJSONString(): String {
    return JSONUtil.toJsonStr(this) ?: "null"
}
