package cn.cqautotest.sunnybeach.paging.source.msg

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.MsgApi
import cn.cqautotest.sunnybeach.model.msg.QAMsg
import cn.cqautotest.sunnybeach.util.TAG
import cn.cqautotest.sunnybeach.util.logByDebug

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 问答评论列表消息 PagingSource
 */
class QAMsgPagingSource : PagingSource<Int, QAMsg.Content>() {

    private val msgApi = ServiceCreator.create<MsgApi>()

    override fun getRefreshKey(state: PagingState<Int, QAMsg.Content>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QAMsg.Content> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            logByDebug(msg = "$TAG load：===> page is $page")
            val response = msgApi.getQAMsgList(page)
            val responseData = response.data
            val prevKey = if (responseData.first) null else page - 1
            val nextKey = if (responseData.last) null else page + 1
            if (response.success) LoadResult.Page(
                data = responseData.content,
                prevKey = prevKey,
                nextKey = nextKey
            )
            else LoadResult.Error(ServiceException())
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}