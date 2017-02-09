package donatrui

import donatrui.Api.{Donatable, Donater}
import org.scalajs.dom.Event

import scala.xml.Elem

object Components {
  def DonaterComponent(donater: Donater): Elem = {
    def onClick: (Event) => Unit = { event: Event => States.currentDonater := Right(donater) }

    <div class={DonatrStyles.donater.htmlClass}>
      <a onclick={onClick} href={s"#/${donater.id}/donatables"}>
        <img src={s"https://www.gravatar.com/avatar/${md5(donater.email)}?s=115"}/>
        <div class={DonatrStyles.donaterName.htmlClass}>
          {donater.name}
        </div>
      </a>
    </div>
  }

  def DonatableComponent(donatable: Donatable): Elem = {
    def onClick: (Event) => Unit = { event: Event =>
      Api.donate(States.currentDonater.value.right.get, donatable)
    }

    <div onclick={onClick} class={DonatrStyles.donater.htmlClass}>
      <img src={s"https://www.gravatar.com/avatar/${md5(donatable.name)}?s=115"}/>
      <div class={DonatrStyles.donaterName.htmlClass}>
        {donatable.name}
      </div>
    </div>
  }

  def CurrentDonaterComponent(donater: Either[Unit, Donater]): Elem = {
    <div class={DonatrStyles.currentDonater.htmlClass}>
      {donater.map(d => s"${d.name} ${d.balance}").getOrElse("select user")}
    </div>
  }
}
