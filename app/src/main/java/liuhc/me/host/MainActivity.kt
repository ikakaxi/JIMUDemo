package liuhc.me.host

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.luojilab.component.componentlib.router.Router
import liuhc.me.commonservice.service.Module1Service

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val router = Router.getInstance()
        router.getService(Module1Service::class.java.simpleName)?.let {
            supportFragmentManager.beginTransaction().replace(R.id.fragment, (it as Module1Service).provideModule1Fragment()).commitAllowingStateLoss()
        }
    }
}
