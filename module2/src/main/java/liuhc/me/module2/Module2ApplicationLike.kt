package liuhc.me.module2

import com.luojilab.component.componentlib.applicationlike.IApplicationLike
import com.luojilab.component.componentlib.router.ui.UIRouter
import liuhc.me.commonservice.path.PagePath

/**
 * 描述:
 * 作者:liuhc
 * 创建日期：2018/4/11 on 下午3:35
 */
class Module2ApplicationLike : IApplicationLike {
    private val uiRouter = UIRouter.getInstance()

    override fun onCreate() {
        uiRouter.registerUI(PagePath.hostModule2)
    }

    override fun onStop() {
        uiRouter.unregisterUI(PagePath.hostModule2)
    }
}