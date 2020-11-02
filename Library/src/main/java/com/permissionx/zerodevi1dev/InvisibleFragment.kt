package com.permissionx.zerodevi1dev

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

/**
 * 1) : 对运行时权限的 API 进行封装并不是一件容易的事,因为这个操作是有特定的上下文依赖的
 *      一般需要在 Activity 中接收 onRequestPermissionRequest() 方法的回调才行,所以不能简单地将整个操作封装到一个独立的类中
 *          a. 将运行时权限的操作封装到 BaseActivity 中
 *          b. 提供一个透明的 Activity 来处理运行时权限
 * 2) : 这里使用另外一种小技巧,之前所有申请运行时权限的操作都是在 Activity 中进行的,事实上,Google 在 Fragment 中也提供了一份相同的 API
 *      使得我们在 Fragment 中也能申请运行时权限
 *      但是 Fragment 并不像 Activity 那样必须拥有界面,我们可以向 Activity 中添加一个隐藏的 Fragment,
 *      然后在这个隐藏的 Fragment 中对运行时权限的 API 进行封装
 *      这是一种非常轻量级的做法,不用担心隐藏 Fragment 会对 Activity 的性能造成什么影响
 *
 * 3) : 定义一个 callback 变量作为运行时权限申请结果的回调通知方式,并将它声明成一个函数类型变量,
 *      该函数类型接收 Boolean 和 List<String> 这两种类型的参数,并且没有返回值
 *
 *      然后定义一个 requestNow() 方法,该方法接收一个与 callback 变量类型相同的函数类型参数,
 *      同时使用 vararg 关键字接收一个可变长度的 permissions 参数列表
 *      在 requestNow() 方法中,我们将传递进来的函数类型参数赋值给 callback 变量,
 *      然后调用 Fragment 中提供的 requestPermissions() 方法去立即申请运行时权限,
 *      并将 permissions 参数列表传递进去,这样就可以实现由外部调用方自主指定要申请哪些权限的功能
 *
 *      接下来重写 onRequestPermissionsResult() 方法,并在这里处理运行时权限的申请结果
 *      这里使用一个 deniedList 列表来记录所有被用户拒绝的权限,然后遍历 grantResults 数组
 *      如果发现某个权限未被用户授权,就将它添加到 deniedList 中
 *      遍历结束后使用一个 allGranted 变量来标识是否所有申请的权限均已被授权,判断的依据就是 deniedList 列表是否为空
 *      最后使用 callback 变量对运行时权限的申请结果进行回调
 *
 *      在 InvisibleFragment 中,我们并没有重写 onCreateView() 方法来加载某个布局
 *      因此它自然就是一个不可见的 Fragment,待会只需要将它添加到 Activity 中即可
 */
/**
 * typealias : 可以给任意类型指定一个别名,比如这里将 (Boolean, List<String>) -> Unit 指定成 PermissionCallback
 * 从而让代码更加简洁
 */
typealias PermissionCallback = (Boolean, List<String>) -> Unit

class InvisibleFragment : Fragment() {

    private var callback: PermissionCallback? = null

    fun requestNow(cb: PermissionCallback, vararg permissions: String) {
        callback = cb
        requestPermissions(permissions, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            val deniedList = ArrayList<String>()
            for ((index, result) in grantResults.withIndex()) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[index])
                }
            }
            val allGranted = deniedList.isEmpty()
            callback?.let {
                it(allGranted, deniedList)
            }
        }
    }
}