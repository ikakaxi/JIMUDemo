package liuhc.me.module1.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.luojilab.component.componentlib.router.ui.UIRouter
import liuhc.me.commonservice.page.BaseFragment
import liuhc.me.commonservice.path.PagePath
import liuhc.me.commonservice.path.PagePath.pathModule2Activity
import liuhc.me.module1.R

/**
 * 描述:
 * 作者:liuhc
 * 创建日期：2018/4/11 on 下午1:15
 */
class Module1Fragment : BaseFragment() {

    private lateinit var textView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_module1, container, false)
        textView = root.findViewById(R.id.textView)
        val bundle = Bundle()
        bundle.putString("msg", "来自Module1Fragment的消息")
        textView.setOnClickListener {
            UIRouter.getInstance().openUri(this@Module1Fragment.activity, "${PagePath.hostModule2}${PagePath.pathModule2Activity}", bundle)
        }
        return root
    }

}