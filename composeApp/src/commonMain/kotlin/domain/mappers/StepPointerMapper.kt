package domain.mappers

import database.entity.StepPointerEntity
import domain.models.steps.StepPointerDomain

fun StepPointerEntity.toStepPointerDomain() : StepPointerDomain = StepPointerDomain(this.ticketNumber,this.activeActivity,this.edited)