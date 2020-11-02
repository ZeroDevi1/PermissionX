package com.permissionx.zerodevi1dev

import androidx.fragment.app.FragmentActivity

/**
 * 1) : 将 PermissionX 指定成单例类,为了让 PermissionX 中的接口能够更加方便地被调用
 * 2) : 在 PermissionX 中定义了一个 request() 方法,
 *      这个方法接收一个 FragmentActivity 参数、一个可变长度的 permissions 参数列表,以及一个 callback 回调
 *      其中 FragmentActivity 是 AppCompatActivity 的父类
 *
 *      在 request() 方法中,首先获取 FragmentManager 的实例,
 *      然后调用 findFragmentByTag() 方法来判断传入的 Activity 参数中是否包含了指定 TAG 的 Fragment
 *      也就是我们刚才编写的 InvisibleFragment,如果已经包含则直接使用该 Fragment,否则就创建一个新的 InvisibleFragment
 *      并将它添加到 Activity 中,同时指定一个 TAG
 *      注意,在添加结束后一定要调用 commitNow() 方法,而不能调用 commit(),因为 commit() 方法并不会立即执行添加操作
 *      因为无法保证下一行代码执行时 InvisibleFragment 已经被添加到 Activity
 *
 *      有了  InvisibleFragment 的实例之后,接下来我们只需要调用它的 requestNow() 方法就能去申请运行时权限
 *      申请的结果会自动回调到 callback 参数中
 *      这里 permissions 参数实际上是一个数组,可以使用 *permissions 将它转换成可变长度参数传递进去
 */
object PermissionX {

    private const val TAG = "InvisibleFragment"

    fun request(
        activity: FragmentActivity,
        vararg permissions: String,
        callback: PermissionCallback
    ) {
        val fragmentManager = activity.supportFragmentManager
        val existedFragment = fragmentManager.findFragmentByTag(TAG)
        val fragment = if (existedFragment != null) {
            existedFragment as InvisibleFragment
        } else {
            val invisibleFragment = InvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, TAG).commitNow()
            invisibleFragment
        }
        fragment.requestNow(callback, *permissions)
    }
}