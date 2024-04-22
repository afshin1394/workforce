package domain.mappers

import domain.models.PhotoDomain
import domain.models.SuspendTaskDomain
import irancell.nwg.wfm.db.PhotoEntity
import irancell.nwg.wfm.db.SuspendTaskEntity

fun PhotoDomain.toPhotoEntity() : PhotoEntity {
    return PhotoEntity(
        pk=0,
        component_key=this.component_key,
        index_row=this.index_row,
        origin_uri=this.origin_uri,
        edited_uri=this.edited_uri,
        angle=this.angle

    )
}

fun List<PhotoEntity>.toPhotoDomainList():List<PhotoDomain>{
    return map { it.toPhotoDomain() }
}

fun PhotoEntity.toPhotoDomain() : PhotoDomain {
    return PhotoDomain(
        component_key=this.component_key,
        index_row=this.index_row,
        origin_uri=this.origin_uri,
        edited_uri=this.edited_uri,
        angle=this.angle

    )
}