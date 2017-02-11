package donatrui

import donatrui.Api.{Donatable, Donater, Fundable}
import mhtml.{Rx, Var}
import org.scalajs.dom.{Event, Node}

import scala.xml.Elem

object Components {
  import States._

  def DonaterComponent(donater: Donater): Elem = {
    def onClick(event: Event): Unit = {
      currentDonater := Some(donater)
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
      for (_ <- 0 to currentMultiplicator.value) {
        Api.donate(currentDonater.value.get, donatable)
      }
      currentMultiplicator := 1
    }

    <div onclick={onClick} class={DonatrStyles.donater.htmlClass}>
      <img src={s"https://www.gravatar.com/avatar/${md5(donatable.name)}?s=115"}/>
      <div class={DonatrStyles.donaterName.htmlClass}>
        {donatable.name} / {donatable.minDonationAmount}
      </div>
    </div>
  }

  def FundableComponent(fundable: Fundable): Elem = {
    def onClick: (Event) => Unit = { event: Event =>
      Layout.fund(event, fundable)
    }

    <div onclick={onClick} class={DonatrStyles.donater.htmlClass}>
      <img src={s"https://www.gravatar.com/avatar/${md5(fundable.name)}?s=115"}/>
      <div class={DonatrStyles.donaterName.htmlClass}>
        {fundable.name} / {fundable.balance} / {fundable.fundingTarget}
      </div>
    </div>
  }

  def CurrentDonaterComponent(donater: Option[Donater]): Elem = {
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

  def CreateDonaterDialog(): Elem = {
    val name = Var("")
    val email = Var("")

    def onSubmit(e: Event) = {
      e.preventDefault()
      Api.createDonater(name.value, email.value)
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Create a User</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input required={true} oninput={inputEvent(name := _.value)} placeholder="name" />
          <input required={true} oninput={inputEvent(email := _.value)} type="email" placeholder="email" />
          <button class="swal2-styled" type="submit">Create</button></form>
      </div>
    </div>
  }

  def CreateDonatableDialog(): Elem = {
    val name = Var("")
    val minDonationAmount = Var(0.0)

    def onSubmit(e: Event) = {
      e.preventDefault()
      Api.createDonatable(name.value, minDonationAmount.value)
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Create an Item</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input required={true} oninput={inputEvent(name := _.value)} placeholder="name" />
          <input required={true} oninput={inputEvent(minDonationAmount := _.value.toDouble)}
                 type="number" step="any" placeholder="price" />
          <button class="swal2-styled" type="submit">Create</button></form>
      </div>
    </div>
  }

  def CreateFundableDialog(): Elem = {
    val name = Var("")
    val fundingTarget = Var(0.0)

    def onSubmit(e: Event) = {
      e.preventDefault()
      Api.createFundable(name.value, fundingTarget.value)
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Create a Funding</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input required={true} oninput={inputEvent(name := _.value)} placeholder="name" />
          <input required={true} oninput={inputEvent(fundingTarget := _.value.toDouble)}
                 type="number" step="any" placeholder="funding target" />
          <button class="swal2-styled" type="submit">Create</button></form>
      </div>
    </div>
  }

  def FundDialog(fundable: Fundable): Elem = {
    val value = Var(0.0)

    def onSubmit(e: Event) = {
      e.preventDefault()
      Api.donate(currentDonater.value.get, fundable, value.value)
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Deposit!</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input required={true}
                 oninput={inputEvent(value := _.value.toDouble)}
                 type="number" step="any" placeholder="amount" />
          <button class="swal2-styled" type="submit">Fund</button></form>
      </div>
    </div>
  }

  def DepositDialog(): Elem = {
    val value = Var(0.0)

    def onSubmit(e: Event) = {
      e.preventDefault()
      Api.donate(currentDonater.value.get, value.value)
      currentDialog := None
    }

    <div class="swal2-modal swal2-show">
      <h2>Deposit!</h2>
      <div class="swal2-content">
        <form onsubmit={onSubmit _}>
          <input required={true}
                 oninput={inputEvent(value := _.value.toDouble)}
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
