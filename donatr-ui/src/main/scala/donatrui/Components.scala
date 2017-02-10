package donatrui

import donatrui.Api.{Donatable, Donater}
import org.scalajs.dom.Event

import scala.xml.Elem

object Components {
  import States._

  def DonaterComponent(donater: Donater): Elem = {
    def onClick(event: Event): Unit = {
      currentDonater := Right(donater)
    }

    <div class={DonatrStyles.donater.htmlClass}>
      <a onclick={onClick _} href={s"#/${donater.id}/donatables"}>
        <img src={s"https://www.gravatar.com/avatar/${md5(donater.email)}?s=115"}/>
        <div class={DonatrStyles.donaterName.htmlClass}>
          {donater.name}
        </div>
      </a>
    </div>
  }

  def DonatableComponent(donatable: Donatable): Elem = {
    def onClick: (Event) => Unit = { event: Event =>
      Api.donate(currentDonater.value.right.get, donatable)
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
      {donater.map(d => {
        <div class={DonatrStyles.currentDonaterAvatar.htmlClass}>
          <div>
            <img class={DonatrStyles.currentDonaterAvatarImage.htmlClass}
                 src={s"https://www.gravatar.com/avatar/${md5(d.email)}?s=40"}/>
            <span class={DonatrStyles.currentDonaterAvatarName.htmlClass}>{d.name}</span>
          </div>
          <div>{d.balance}</div>
        </div>
      }).getOrElse(<span>select user</span>)}
    </div>
  }
}
