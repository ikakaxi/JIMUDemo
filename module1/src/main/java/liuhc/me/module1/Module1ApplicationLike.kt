package liuhc.me.module1

import com.luojilab.component.componentlib.applicationlike.IApplicationLike
import com.luojilab.component.componentlib.router.Router
import liuhc.me.commonservice.service.Module1Service
import liuhc.me.module1.service.Module1ServiceImpl

/**
 * 描述:
 * 作者:liuhc
 * 创建日期：2018/4/11 on 下午3:35
 */
class Module1ApplicationLike : IApplicationLike {
    private val router = Router.getInstance()

    override fun onCreate() {
        router.addService(Module1Service::class.java.simpleName, Module1ServiceImpl())
    }

    override fun onStop() {
        router.removeService(Module1Service::class.java.simpleName)
    }
}