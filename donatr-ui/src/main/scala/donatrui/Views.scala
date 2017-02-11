package donatrui

import donatrui.Components.{DonatableComponent, DonaterComponent}
import mhtml.Rx

import scala.xml.Elem

object Views {
  def donatersView(): Rx[Elem] =
    States.donaters
      .map { donaters =>
          <div>
            {donaters.map(DonaterComponent)}
          </div>
      }

  def donatablesView(): Rx[Elem] =
    States.donatables
      .map { donatables =>
        <div>
          {donatables.map(DonatableComponent)}
        </div>
      }
}
