package donatrui

import org.scalajs.dom
import org.scalajs.dom.raw.WebSocket

import scala.scalajs.js
import scala.scalajs.js.JSON

object Websocket {
  def init(): Unit = {
    val schema =  if(dom.window.location.port == "") "wss://" else "ws://"
    val port = if(dom.window.location.port == "") "" else ":"+dom.window.location.port
    val wss = schema+dom.window.location.hostname+port+"/api/events"
    val ws = new WebSocket(wss)
    ws.onmessage = (msg) => {
      val jsonData = msg.data.asInstanceOf[String]
      val parsedObj = JSON.parse(jsonData)
      val obj = parsedObj.asInstanceOf[js.Object]
      val eventType = Symbol(js.Object.keys(obj)(0))
      States.updateState(eventType, obj)
    }
    ws.onclose = (e) => init()
  }
}
