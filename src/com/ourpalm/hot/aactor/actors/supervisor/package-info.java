/**
 * 
 */
/**
 * @author g
 *
 */
package com.ourpalm.hot.aactor.actors.supervisor;

/**
 * 重启策略 <br/>
 * 3.1 one_for_one<br/>
 *  如果一个子进程停止，则只重启该进程 <br/>
 * 3.2 one_for_all<br/>
 * 如果一个子进程停止，所有其他子进程也停止，然后所有进程重启<br/>
 *  3.3 rest_for_one
 * 如果一个子进程停止，则启动顺序中在它之后的所有其他子进程也停止，然后停止的这些进程重启（跟楼上那位不一样）<br/>
 *  3.4 simple_one_for_one<br/>
 * 一个简化的one_for_one supervisor，所有的子进程都是同样进程类型并且是动态添加的实例
 */
