package donatrui

import mhtml.Rx

import scala.util.Success
import scala.xml.Elem

object Views {
  def donatersView(): Rx[Elem] =
    Api.fetchDonaters
      .map { donaters =>
          <div>
            <h1>DONATERS!!!!</h1>
            {donaters.map(d => Components.DonaterComponent(d))}
          </div>
      }

  def donatablesView(): Rx[Elem] =
    Api.fetchDonatables
      .map {
        case None => <div>Loading</div>
        case Some(Success(donaters)) =>
          <div>
            {donaters.map(d => Components.DonatableComponent(d))}
          </div>
        case _ => <div>Failure!</div>
      }
}
