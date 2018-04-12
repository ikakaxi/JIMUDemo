package liuhc.me.commonservice.page

import android.os.Bundle
import android.support.v4.app.Fragment
import com.luojilab.component.componentlib.service.AutowiredService

/**
 * 描述:
 * 作者:liuhc
 * 创建日期：2018/4/11 on 下午2:25
 */
open class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AutowiredService.Factory.getInstance().create().autowire(this)
    }
}