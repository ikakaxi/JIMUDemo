package liuhc.me.module1.service

import android.support.v4.app.Fragment
import liuhc.me.commonservice.service.Module1Service
import liuhc.me.module1.page.Module1Fragment

/**
 * 描述:
 * 作者:liuhc
 * 创建日期：2018/4/11 on 下午2:29
 */
class Module1ServiceImpl : Module1Service {
    override fun provideModule1Fragment(): Fragment {
        return Module1Fragment()
    }
}