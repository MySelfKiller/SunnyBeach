package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.ArticleApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/10/31
 *    desc   : 文章获取
 */
object ArticleNetwork {

    private val articleApi = ServiceCreator.create<ArticleApi>()

    suspend fun loadUserArticleList(userId: String, page: Int) =
        articleApi.loadUserArticleList(userId, page)
}