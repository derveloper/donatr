package donatrui

import org.scalajs.dom.raw.WebSocket

import scala.scalajs.js
import scala.scalajs.js.JSON

object Websocket {
  def init(): Unit = {
    val ws = new WebSocket("ws://localhost:8080/events")
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
