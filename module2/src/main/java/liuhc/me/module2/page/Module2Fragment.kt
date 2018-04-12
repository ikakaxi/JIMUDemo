package liuhc.me.module2.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.luojilab.router.facade.annotation.Autowired
import liuhc.me.commonservice.page.BaseFragment
import liuhc.me.module2.R

/**
 * 描述:
 * 作者:liuhc
 * 创建日期：2018/4/11 on 下午1:15
 */
class Module2Fragment : BaseFragment() {

    @Autowired
    @JvmField
    var msg: String = ""

    private lateinit var textView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_module2, container, false)
        textView = root.findViewById(R.id.textView)
        textView.text = msg
        return root
    }

}