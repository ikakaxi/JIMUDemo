package liuhc.me.module2.page

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.luojilab.router.facade.annotation.RouteNode
import liuhc.me.commonservice.path.PagePath
import liuhc.me.module2.R

/**
 * 描述:
 * 作者:liuhc
 * 创建日期：2018/4/11 on 下午6:36
 */
@RouteNode(path = PagePath.pathModule2Activity, desc = "Module2ActivityDesc")
class Module2Activity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module2)
        val fragment = Module2Fragment()
        fragment.arguments = intent.extras
        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commitAllowingStateLoss()
    }
}