import org.scalajs.dom.Event
import org.scalajs.dom.raw.HTMLInputElement

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

package object donatrui {
  def md5(string: String) = {
    import scala.scalajs.js
    val spark = js.Dynamic.newInstance(js.Dynamic.global.SparkMD5)()
    spark.append(string)
    spark.end().asInstanceOf[String]
  }

  def inputEvent(f: HTMLInputElement => Unit): Event => Unit = {
    event: Event =>
      print(s"event: $event")
      event.target match {
        case e: HTMLInputElement =>
          print(s"event: $event")
          f(e)
        case _ => print(s"unknown event: $event")
      }
  }

  @js.native
  trait swal extends js.Object

  @JSName("swal")
  @js.native
  object swal extends js.Object {
    def apply(options: js.Object): swal = js.native
  }

  object swalOptions {
    def apply(title: String = "",
              text: String = "",
              html: String = null,
              `type`: String = "",
              customClass: String = "",
              animation: Boolean = true,
              allowOutsideClick: Boolean = true,
              allowEscapeKey: Boolean = true,
              showConfirmButton: Boolean = true,
              showCancelButton: Boolean = false,
              preConfirm: String = "",
              confirmButtonText: String = "OK",
              confirmButtonColor: String = "#000000",
              confirmButtonClass: String = "",
              cancelButtonText: String = "Cancel",
              cancelButtonColor: String = "#aaa",
              cancelButtonClass: String = "",
              buttonsStyling: Boolean = true,
              reverseButtons: Boolean = false,
              focusCancel: Boolean = false,
              showCloseButton: Boolean = false,
              showLoaderOnConfirm: Boolean = false,
              imageUrl: String = "",
              imageWidth: String = "",
              imageHeight: String = "",
              imageClass: String = "",
              timer: String = "",
              width: Int = 500,
              padding: Int = 20,
              background: String = "#000000",
              input: String = null,
              inputPlaceholder: String = "",
              inputValue: String = "",
              inputOptions: js.Object = null,
              inputAutoTrim: Boolean = true,
              inputClass: String = "",
              inputAttributes: js.Object = null,
              inputValidator: String = "",
              progressSteps: js.Array[js.Any] = new js.Array[js.Any](),
              currentProgressStep: String = "",
              progressStepsDistance: String = "40px",
              onOpen: (js.Any) => Unit = () => _,
              onClose: (js.Any) => Unit = () => _): js.Object =
      js.Dynamic.literal(
        title = title,
        text = text,
        html = html,
        `type` = `type`,
        customClass = customClass,
        animation = animation,
        allowOutsideClick = allowOutsideClick,
        allowEscapeKey = allowEscapeKey,
        showConfirmButton = showConfirmButton,
        showCancelButton = showCancelButton,
        preConfirm = preConfirm,
        confirmButtonText = confirmButtonText,
        confirmButtonColor = confirmButtonColor,
        confirmButtonClass = confirmButtonClass,
        cancelButtonText = cancelButtonText,
        cancelButtonColor = cancelButtonColor,
        cancelButtonClass = cancelButtonClass,
        buttonsStyling = buttonsStyling,
        reverseButtons = reverseButtons,
        focusCancel = focusCancel,
        showCloseButton = showCloseButton,
        showLoaderOnConfirm = showLoaderOnConfirm,
        imageUrl = imageUrl,
        imageWidth = imageWidth,
        imageHeight = imageHeight,
        imageClass = imageClass,
        timer = timer,
        width = width,
        padding = padding,
        background = background,
        input = input,
        inputPlaceholder = inputPlaceholder,
        inputValue = inputValue,
        inputOptions = inputOptions,
        inputAutoTrim = inputAutoTrim,
        inputClass = inputClass,
        inputAttributes = inputAttributes,
        inputValidator = inputValidator,
        progressSteps = progressSteps,
        currentProgressStep = currentProgressStep,
        progressStepsDistance = progressStepsDistance,
        onOpen = onOpen,
        onClose = onClose
      )
  }

}
