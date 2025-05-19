package net.archasmiel.dnd5backend.api.exception.user

import net.archasmiel.dnd5backend.api.exception.ApiException

class ResourceNotFoundException(
    resource: String,
    id: Any
) : ApiException(
    "RESOURCE_NOT_FOUND",
    "$resource with $id not found"
)