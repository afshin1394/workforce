package domain.mappers

import database.entity.PhotoEntity
import domain.models.PhotoDomain


fun PhotoDomain.toPhotoEntity() : PhotoEntity {
    return PhotoEntity(
        pk=this.pk,
        ticket_number = this.ticket_number,
        component_key=this.component_key,
        index_row=this.index_row,
        origin_uri=this.origin_uri,
        edited_uri=this.edited_uri,
        angle=this.angle

    )
}
fun List<PhotoDomain>.toPhotoEntityList():List<PhotoEntity>{
    return map { it.toPhotoEntity() }
}

fun List<PhotoEntity>.toPhotoDomainList():List<PhotoDomain>{
    return map { it.toPhotoDomain() }
}

fun PhotoEntity.toPhotoDomain() : PhotoDomain {
    return PhotoDomain(
        ticket_number = this.ticket_number,
        component_key=this.component_key,
        index_row=this.index_row,
        origin_uri=this.origin_uri,
        edited_uri=this.edited_uri,
        angle=this.angle
    )
}