package com.amahop.farefirst.ffprotect.ui.dashboard.repositories.pojos

import com.google.gson.annotations.SerializedName

enum class GovAlert(val value: String) {
    @SerializedName("red")
    RED("red"),
    @SerializedName("yellow")
    YELLOW("yellow"),
    @SerializedName("blue")
    BLUE("blue"),
    @SerializedName("green")
    GREEN("green"),
}