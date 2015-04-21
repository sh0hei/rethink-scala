package com.rethinkscala.net

import java.nio.charset.Charset

import com.typesafe.scalalogging.slf4j.LazyLogging
import io.netty.buffer.ByteBuf
import io.netty.channel.{SimpleChannelInboundHandler, ChannelHandlerContext, ChannelInboundHandlerAdapter}

/**
 * Created by IntelliJ IDEA.
 * User: Keyston
 * Date: 4/21/2015
 * Time: 2:48 PM 
 */
case class RethinkConfigureHandler(version: Version3)
  extends SimpleChannelInboundHandler[ByteBuf] with LazyLogging {


  private[this] val AUTH_RESPONSE = "SUCCESS"

  override def channelActive(ctx: ChannelHandlerContext) = {

    val channel = ctx.channel()

    channel.write(version.version)
    channel.write(version.authKey)
    channel.writeAndFlush(ql2.Ql2.VersionDummy.Protocol.JSON)

  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) = {

    val promise = ChannelAttribute.Future.get(ctx.channel())


    val response = msg.toString(Charset.forName("US-ASCII"))

    logger.debug(s"Server auth responsed with : -$response-")

    if (!response.startsWith(AUTH_RESPONSE))
      throw new RethinkDriverError(s"Server dropped connection with message: '$response'")


    val pipe = ctx.channel().pipeline()


    pipe.remove(this)
    val encoder = pipe.remove("encoder")

    pipe.addLast("frameDecoder", new JsonFrameDecoder())
    pipe.addLast("encoder", encoder)

    pipe.addLast(new JsonChannelHandler)
    promise.trySuccess()
  }


}
