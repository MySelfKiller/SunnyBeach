package cn.cqautotest.sunnybeach.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.*
import cn.cqautotest.sunnybeach.other.FollowState
import cn.cqautotest.sunnybeach.paging.source.RichPagingSource
import cn.cqautotest.sunnybeach.paging.source.UserFollowPagingSource
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import com.blankj.utilcode.util.RegexUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 用户 ViewModel
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    // 用户基础数据信息
    private val _userBasicInfo = MutableLiveData<UserBasicInfo?>()
    val userBasicInfo: LiveData<UserBasicInfo?> get() = _userBasicInfo

    private val phoneLiveData = MutableLiveData<String>()

    val userAvatarLiveData = Transformations.switchMap(phoneLiveData) { account ->
        Repository.queryUserAvatar(account)
    }

    /**
     * 修改密码（通过旧密码修改）
     */
    fun modifyPassword(modifyPwd: ModifyPwd) = Repository.modifyPassword(modifyPwd)

    /**
     * 检查手机验证码是否正确
     */
    fun checkSmsCode(phoneNumber: String, smsCode: String) =
        Repository.checkSmsCode(phoneNumber, smsCode)

    /**
     * 获取找回密码的手机验证码（找回密码）
     */
    fun sendForgetSmsVerifyCode(smsInfo: SmsInfo) = Repository.sendForgetSmsVerifyCode(smsInfo)

    /**
     * 注册账号
     */
    fun registerAccount(user: User) = Repository.registerAccount(user)

    /**
     * 获取注册的手机验证码（注册）
     */
    fun sendRegisterSmsVerifyCode(smsInfo: SmsInfo) = Repository.sendRegisterSmsVerifyCode(smsInfo)

    /**
     * 获取用户 Sob 币的收支（Income & Expenditures）明细列表
     */
    fun getSobIEDetailList(userId: String, page: Int) = Repository.getSobIEDetailList(userId, page)

    /**
     * 获取VIP列表
     */
    fun getVipUserList() = Repository.getVipUserList()

    /**
     * 个人中心获取成就
     */
    fun getAchievement() = Repository.getAchievement()

    /**
     * 个人中心获取自己的sob币总数
     */
    fun queryTotalSobCount() = Repository.queryTotalSobCount()

    /**
     * 个人中心获取账号信息
     */
    fun queryUserInfo() = Repository.queryUserInfo()

    /**
     * 检查是否领取过津贴
     */
    fun checkAllowance() = Repository.checkAllowance()

    /**
     * 领取津贴
     */
    fun getAllowance() = Repository.getAllowance()

    /**
     * 获取用户的关注/粉丝列表
     */
    fun getUserFollowList(
        userId: String,
        followState: FollowState
    ): Flow<PagingData<UserFollow.UserFollowItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                UserFollowPagingSource(userId, followState)
            }).flow.cachedIn(viewModelScope)
    }

    /**
     * 取消关注用户
     */
    fun unfollowUser(userId: String) = Repository.unfollowUser(userId)

    /**
     * 关注用户
     */
    fun followUser(userId: String) = Repository.followUser(userId)

    /**
     * 自己与目标用户的关注状态
     *
     * 0：表示没有关注对方，可以显示为：关注
     * 1：表示对方关注自己，可以显示为：回粉
     * 2：表示已经关注对方，可以显示为：已关注
     * 3：表示相互关注，可以显示为：相互关注
     */
    fun followState(userId: String) = Repository.followState(userId)

    /**
     * 获取用户信息
     */
    fun getUserInfo(userId: String) = Repository.getUserInfo(userId)

    /**
     * 获取当前用户的成就
     */
    fun getAchievement(userId: String = "") = Repository.getAchievement(userId)

    /**
     * 通过账号（手机号）获取用户头像
     */
    fun queryUserAvatar(account: String) {
        phoneLiveData.value = account
    }

    /**
     * 获取富豪榜
     */
    fun getRichList(): Flow<PagingData<RichList.RichUserItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                RichPagingSource()
            }).flow.cachedIn(viewModelScope)
    }

    /**
     * 退出登录
     */
    fun logout() = Repository.logout()

    /**
     * 用户账号登录
     */
    fun login(userAccount: String, password: String, captcha: String) {
        viewModelScope.launch {
            val userInfoValid =
                userAccount.isUserAccountValid() && password.isPasswordValid() && captcha.isVerifyCodeValid()
            if (userInfoValid.not()) {
                _userBasicInfo.value = null
                return@launch
            }
            val userBasicInfo = Repository.login(userAccount, password, captcha)
            _userBasicInfo.value = userBasicInfo
        }
    }

    /**
     * 用户账号登录
     */
    // fun login(userAccount: String, password: String, captcha: String) {
    //     viewModelScope.launch {
    //         val context = getApplication<Application>()
    //         var userBasicInfoResponse: ApiResponse<UserBasicInfo>? = null
    //         runCatching {
    //             val user = User(
    //                 phoneNum = userAccount, password = password.md5
    //                     .toLowerCase(Locale.SIMPLIFIED_CHINESE)
    //             )
    //             val loginResult = userApi.login(captcha = captcha, user = user)
    //             val tempUserBasicInfoResponse = withContext(Dispatchers.IO) { userApi.checkToken() }
    //             val tempUserBasicInfoResponseData = tempUserBasicInfoResponse.getData()
    //             // 只有Token校验成功获取到数据才算登录成功，否则也是属于登录失败了
    //             if (SUNNY_BEACH_HTTP_OK_CODE == loginResult.getCode() && SUNNY_BEACH_HTTP_OK_CODE == tempUserBasicInfoResponse.getCode()) {
    //                 userBasicInfoResponse = tempUserBasicInfoResponse
    //                 Timber.d("userBasicInfoResponseData is ${tempUserBasicInfoResponseData.toJson()}")
    //                 _userBasicInfo.value = tempUserBasicInfoResponseData
    //                 // 将基本信息缓存到本地
    //                 UserManager.saveUserBasicInfo(tempUserBasicInfoResponseData)
    //                 // 更新 Token
    //                 EasyConfig.getInstance()
    //                     .addParam(IntentKey.TOKEN, tempUserBasicInfoResponseData.token)
    //                 loginResult
    //             } else {
    //                 throw LoginFailedException()
    //             }
    //         }.onSuccess {
    //             _loginResult.value =
    //                 LoginResult(
    //                     success = LoggedInUserView(
    //                         displayName = userBasicInfoResponse?.getData()?.nickname ?: ""
    //                     )
    //                 )
    //             setupAutoLogin(true)
    //         }.onFailure {
    //             it.printStackTrace()
    //             _loginResult.value =
    //                 LoginResult(error = context.getString(R.string.login_failed))
    //             Timber.d("登陆失败，error msg is $it")
    //             setupAutoLogin(false)
    //         }
    //     }
    // }

    /**
     * 检查用户 Token
     */
    fun checkToken() {
        viewModelScope.launch {
            Repository.checkToken()
        }
    }

    /**
     * 手机号码格式检查
     */
    private fun String.isUserAccountValid(): Boolean =
        RegexUtils.isMobileExact(this)

    /**
     * 账号密码长度检查
     */
    private fun String.isPasswordValid(): Boolean = length > 5

    /**
     * 验证码检查
     */
    private fun String.isVerifyCodeValid(): Boolean = isNotEmpty()
}