package com.rethinkscala.changefeeds.net

import com.rethinkscala.net.{CursorFactory, Token}

/**
 * Created with IntelliJ IDEA.
 * User: keyston
 * Date: 8/16/14
 * Time: 8:59 PM
 *
 */
object ChangeCursorFactory extends CursorFactory{
  override def apply[T]( token: Token[_],connectionId: Long, completed: Boolean) =  ??? //ChangeCursor[T](connectionId,token)
}
