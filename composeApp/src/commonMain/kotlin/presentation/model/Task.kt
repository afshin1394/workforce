package com.irancell.nwg.wfm.presentation.model

import kotlin.reflect.KProperty

 class Task(
    val title: String = "Huawei External Alarm, T5712, Bater ... Huawei External Alarm, T5712, Bater ...",
    val step: String ="HSE pre-check",
    val faultLevel: String ="Level: 1",
    val type: String ="TT",
    val address: String = "Tehran, Amanieh, Zarin stre...",
    val remainingTime: String ="0h 47m",
    ) {



    fun hasremaining(){

    }
     operator fun getValue(nothing: Nothing?, property: KProperty<*>): Task {
       return this
     }


 }


