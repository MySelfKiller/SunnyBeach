package cn.cqautotest.sunnybeach.model
import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/10
 * desc   : 摸鱼话题列表的数据bean类
 */
class FishPondTopicList : ArrayList<FishPondTopicList.TopicItem>(){
    data class TopicItem(
        @SerializedName("contentCount")
        val contentCount: Int,
        @SerializedName("cover")
        val cover: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("followCount")
        val followCount: Int,
        @SerializedName("hasFollowed")
        val hasFollowed: Boolean,
        @SerializedName("id")
        val id: String,
        @SerializedName("topicName")
        val topicName: String
    )
}