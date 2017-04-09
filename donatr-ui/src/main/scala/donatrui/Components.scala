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
      val multiplicator = getInputValueFrom(form, "multiplicator").toInt
      val donater = getInputValueFrom(form, "donater")
      for (_ <- 1 to multiplicator) {
        Api.donate(donater, donatable)
      }
      currentMultiplicator := 1
    }

    <form onsubmit={onSubmit}>
      <input type="hidden" name="donater" value={currentDonaterId} />
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

  def CurrentDonaterComponent(): Rx[Elem] = {
    currentDonaterId.map(_.getOrElse("")).flatMap { id =>
      Api.fetchDonater(id).map {
        case Some(donater) => CurrentDonaterDetails(donater)
        case _ => EmptyDonater
      }
    }
  }

  private def EmptyDonater = {
    <div class={"DonatrStyles-currentDonater"}>
      <span class={"DonatrStyles-currentDonaterAvatarName"}>select user</span>
    </div>
  }

  private def CurrentDonaterDetails(donater: Donater) = {
    <div class={"DonatrStyles-currentDonater"}>
      <div class={"DonatrStyles-currentDonaterAvatar"}>
        <div>
          <img class={"DonatrStyles-currentDonaterAvatarImage"}
               src={s"https://www.gravatar.com/avatar/${md5(donater.email)}?s=40"}/>
          <span class={"DonatrStyles-currentDonaterAvatarName"}>
            {donater.name}
          </span>
        </div>
        <div>
          {donater.balance}
        </div>
      </div>
    </div>
  }

  def CreateDonaterDialog(): Elem = {
    def onSubmit(e: Event) = {
      e.preventDefault()
      val form = e.target.asInstanceOf[HTMLFormElement]
      val name = "name"
      Api.createDonater(
        getInputValueFrom(form, name),
        getInputValueFrom(form, "email")
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

  private def getInputValueFrom(form: HTMLFormElement, name: String) = {
    form.elements.namedItem(name).asInstanceOf[HTMLInputElement].value
  }

  def CreateDonatableDialog(): Elem = {
    def onSubmit(e: Event) = {
      e.preventDefault()
      val form = e.target.asInstanceOf[HTMLFormElement]
      val name = getInputValueFrom(form, "name")
      val imageUrl = getInputValueFrom(form, "imageUrl")
      val minDonationAmount = getInputValueFrom(form, "minDonationAmount").toDouble
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
      val name = getInputValueFrom(form, "name")
      val imageUrl = getInputValueFrom(form, "imageUrl")
      val fundingTarget = getInputValueFrom(form, "fundingTarget").toDouble
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
      val donater = getInputValueFrom(form, "donater")
      val value = getInputValueFrom(form, "amount").toDouble
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
      val donater = getInputValueFrom(form, "donater")
      val amount = getInputValueFrom(form, "amount").toDouble
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
