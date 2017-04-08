package donatrui

import donatrui.Api.{Donatable, Donater, Fundable}
import mhtml.Rx
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLFormElement, HTMLInputElement}
import org.scalajs.dom.{Event, Node}

import scala.xml.Elem

object Components {
  import States._

  def Link(href: String, child: Elem): Elem = {
    def onClick(event: Event): Unit = {
      event.stopPropagation()
      event.preventDefault()
      dom.window.history.pushState(href, null, href)
      States.currentRoute := href
    }
    <a onclick={onClick _} href={href}>
      {child}
    </a>
  }

  def DonaterComponent(donater: Donater): Elem = {
    <div class={"DonatrStyles-donater"}>
      {Link(s"/${donater.id}/donatables", {
        <span>
          <img src={s"https://www.gravatar.com/avatar/${md5(donater.email)}?s=115"}/>
          <div class={"DonatrStyles-donaterName"}>
            {donater.name}
          </div>
        </span>
      })}
    </div>
  }

  def DonatableComponent(donatable: Donatable): Elem = {
    def onSubmit: (Event) => Unit = { event: Event =>
      event.preventDefault()
      val form = event.target.asInstanceOf[HTMLFormElement]
      val multiplicator = form.elements.namedItem("multiplicator").asInstanceOf[HTMLInputElement].value.toInt
      val donater = form.elements.namedItem("donater").asInstanceOf[HTMLInputElement].value
      for (_ <- 1 to multiplicator) {
        Api.donate(donater, donatable)
      }
      currentMultiplicator := 1
    }

    <form onsubmit={onSubmit}>
      <input type="hidden" name="donater" value={currentDonater.map(_.map(_.id))} />
      <input type="hidden" name="multiplicator" value={currentMultiplicator.map(_.toString)} />
      <button type="submit" class={"DonatrStyles-donater"}>
        <img src={s"${donatable.imageUrl}"} width="115" height="115" />
        <div class={"DonatrStyles-donaterName"}>
          {donatable.name} / {donatable.minDonationAmount}
        </div>
      </button>
    </form>
  }

  def FundableComponent(fundable: Fundable): Elem = {
    def onClick: (Event) => Unit = { event: Event =>
      Layout.fund(event, fundable)
    }

    <div onclick={onClick} class={"DonatrStyles-donater"}>
      <img src={s"${fundable.imageUrl}"} width="115" height="115" />
      <div class={"DonatrStyles-donaterName"}>
        {fundable.name} / {fundable.balance} / {fundable.fundingTarget}
      </div>
    </div>
  }

  def CurrentDonaterComponent(donater: Option[Donater]): Elem = {
    <div class={"DonatrStyles-currentDonater"}>
      {donater.map(d => {
        <div class={"DonatrStyles-currentDonaterAvatar"}>
          <div>
            <img class={"DonatrStyles-currentDonaterAvatarImage"}
                 src={s"https://www.gravatar.com/avatar/${md5(d.email)}?s=40"}/>
            <span class={"DonatrStyles-currentDonaterAvatarName"}>{d.name}</span>
          </div>
          <div>{d.balance}</div>
        </div>
      }).getOrElse(<span class={"DonatrStyles-currentDonaterAvatarName"}>select user</span>)}
    </div>
  }

  def CreateDonaterDialog(): Elem = {
    def onSubmit(e: Event) = {
      e.preventDefault()
      val form = e.target.asInstanceOf[HTMLFormElement]
      Api.createDonater(
        form.elements.namedItem("name").asInstanceOf[HTMLInputElement].value,
        form.elements.namedItem("email").asInstanceOf[HTMLInputElement].value
      )
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Create a User</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input required={true} name="name" placeholder="name" />
          <input required={true} name="email" type="email" placeholder="email" />
          <button class="swal2-styled" type="submit">Create</button></form>
      </div>
    </div>
  }

  def CreateDonatableDialog(): Elem = {
    def onSubmit(e: Event) = {
      e.preventDefault()
      val form = e.target.asInstanceOf[HTMLFormElement]
      val name = form.elements.namedItem("name").asInstanceOf[HTMLInputElement].value
      val imageUrl = form.elements.namedItem("imageUrl").asInstanceOf[HTMLInputElement].value
      val minDonationAmount = form.elements.namedItem("minDonationAmount").asInstanceOf[HTMLInputElement].value.toDouble
      Api.createDonatable(name, imageUrl, minDonationAmount)
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Create an Item</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input required={true} name="name" placeholder="name" />
          <input required={true} name="imageUrl" placeholder="image url" />
          <input required={true} name="minDonationAmount" type="number" step="any" placeholder="price" />
          <button class="swal2-styled" type="submit">Create</button></form>
      </div>
    </div>
  }

  def CreateFundableDialog(): Elem = {
    def onSubmit(e: Event) = {
      e.preventDefault()
      val form = e.target.asInstanceOf[HTMLFormElement]
      val name = form.elements.namedItem("name").asInstanceOf[HTMLInputElement].value
      val imageUrl = form.elements.namedItem("imageUrl").asInstanceOf[HTMLInputElement].value
      val fundingTarget = form.elements.namedItem("fundingTarget").asInstanceOf[HTMLInputElement].value.toDouble
      Api.createFundable(name, imageUrl, fundingTarget)
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Create a Funding</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input required={true} name="name" placeholder="name" />
          <input required={true} name="imageUrl" placeholder="image url" />
          <input required={true} name="fundingTarget"
                 type="number" step="any" placeholder="funding target" />
          <button class="swal2-styled" type="submit">Create</button></form>
      </div>
    </div>
  }

  def FundDialog(fundable: Fundable): Elem = {
    def onSubmit(e: Event) = {
      e.preventDefault()
      val form = e.target.asInstanceOf[HTMLFormElement]
      val donater = form.elements.namedItem("donater").asInstanceOf[HTMLInputElement].value
      val value = form.elements.namedItem("amount").asInstanceOf[HTMLInputElement].value.toDouble
      Api.donate(donater, fundable, value)
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Deposit!</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input type="hidden" name="donater" value={currentDonater.map(_.map(_.id))} />
          <input required={true} name="amount"
                 type="number" step="any" placeholder="amount" />
          <button class="swal2-styled" type="submit">Fund</button></form>
      </div>
    </div>
  }

  def DepositDialog(): Elem = {
    def onSubmit(e: Event) = {
      e.preventDefault()
      val form = e.target.asInstanceOf[HTMLFormElement]
      val donater = form.elements.namedItem("donater").asInstanceOf[HTMLInputElement].value
      val amount = form.elements.namedItem("amount").asInstanceOf[HTMLInputElement].value.toDouble
      Api.donate(donater, amount)
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Deposit!</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input type="hidden" name="donater" value={currentDonater.map(_.map(_.id))} />
          <input required={true} name="amount"
                 type="number" step="any" placeholder="amount" />
          <button class="swal2-styled" type="submit">Deposit</button></form>
      </div>
    </div>
  }

  def BaseDialog(): Rx[Elem] = {
    currentDialog.map {
      case Some(dialog) =>
        <div onclick={(e: Event) => {
          // TODO: get rid of this nasty hack
          val cls = e.target.asInstanceOf[Node].attributes.getNamedItem("class")
          if(cls != null && cls.value.contains("swal2-in"))
            currentDialog := None
        }}
             class="swal2-container swal2-fade swal2-in">
          {dialog}
        </div>
      case None => <span/>
    }
  }
}
