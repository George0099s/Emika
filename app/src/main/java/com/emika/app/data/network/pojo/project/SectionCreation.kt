package com.emika.app.data.network.pojo.project


import com.google.gson.annotations.SerializedName

data class SectionCreation(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("payload")
    val payloadSectionCreation: PayloadSection
)