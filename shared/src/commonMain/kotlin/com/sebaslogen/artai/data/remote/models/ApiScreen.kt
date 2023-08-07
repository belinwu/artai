package com.sebaslogen.artai.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.*


@Serializable
data class ApiScreenResponse(
    val screen: ApiScreen,
    val cache: ApiCacheData? = null
)

/**
 * Dictionary of cached data that can be updated on any response.
 */
@Serializable
data class ApiCacheData(
    /**
     * List of currently favorited items.
     * Important note: "null" value means there is no update on the list of favorites. Only a non-null empty list means the list is empty.
      */
    val favorites: List<String>? = null // TODO: Use here and in all other ids a value class: @JvmInline value class ArtId(val value: String)
)

@Serializable
data class ApiScreen(
    val id: String,
    val sections: List<ApiSection>
)

@Serializable
sealed class ApiSection {

    abstract val id: String?

    companion object {
        val serializers = SerializersModule {
            polymorphic(ApiSection::class) {
                subclass(ApiCarousel::class)
                subclass(ApiFooter::class)
                defaultDeserializer { ApiUnknown.serializer() }
            }
        }
    }

    @Serializable
    @SerialName("carousel")
    data class ApiCarousel(
        override val id: String,
        val header: ApiSectionHeader,
        val style: ApiCarouselStyle,
        val items: List<ApiCarouselItem>
    ) : ApiSection() {
        enum class ApiCarouselStyle {
            Squared, Circle, RoundedSquares
        }
    }

    @Serializable
    @SerialName("footer")
    data class ApiFooter(
        override val id: String,
        val text: String
    ) : ApiSection()

    @Serializable
    @SerialName("list")
    data class ApiList(
        override val id: String,
        val header: ApiSectionHeader,
        val items: List<ApiListItem>
    ) : ApiSection()

    @Serializable
    data class ApiUnknown(val type: String, override val id: String? = null) : ApiSection()
}