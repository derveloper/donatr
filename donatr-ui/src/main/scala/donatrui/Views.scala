package donatrui

import donatrui.Components.{DonatableComponent, DonaterComponent, FundableComponent}
import mhtml.Rx

import scala.xml.Elem

object Views {
  def donatersView(): Rx[Elem] = {
    println("donatersView")
    Api.fetchDonaters
      .map { donaters =>
        <div>
          {donaters.map(DonaterComponent)}
        </div>
      }
  }

  def donatablesView(): Rx[Elem] = {
    println("donatablesView")
    Api.fetchDonatables
      .map { donatables =>
        <div>
          {donatables.map(DonatableComponent)}
        </div>
      }
  }

  def fundablesView(): Rx[Elem] = {
    println("fundablesView")
    Api.fetchFundables
      .map { fundables =>
        <div>
          {fundables.map(FundableComponent)}
        </div>
      }
  }
}
