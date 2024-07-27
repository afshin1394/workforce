package com.irancell.nwg.wfm.presentation.model

import domain.models.form_struct.FormStructDomain


data class ProcessLevel(val activityID : Int,val activityTitle : String,var isActive : Boolean = false,

)
