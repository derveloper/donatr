package donatrui

import scala.language.postfixOps
import scalacss.Defaults._

object DonatrStyles extends StyleSheet.Inline {
  import dsl._

  val header = style(
    textAlign.center
  )

  val commonBorder = mixin(
    border(2 px, solid, rgb(0, 255, 0))
  )

  val donater = style(
    commonBorder,
    display.inlineBlock,
    wordWrap.breakWord,
    margin(1 rem),
    verticalAlign.top,
    width(115 px),
    &.hover(
      cursor.pointer
    ),
    &.active(
      backgroundColor(rgba(0, 255, 0, 0.3))
    )
  )

  val currentDonaterAvatar = style(
    display.flex,
    justifyContent.spaceBetween,
    alignItems.center
  )

  val currentDonaterAvatarName = style(
    verticalAlign.middle,
    display.inlineBlock,
    lineHeight(3 rem)
  )

  val currentDonaterAvatarImage = style(
    verticalAlign.middle
  )

  val currentDonater = style(
    commonBorder,
    width :=! "calc((100 vw) - (2 rem))",
    margin(0 px, 1 rem),
    fontSize(1.4 rem),
    padding(0.8 rem),
    height(3 rem),
    &.active(
      backgroundColor(rgba(0, 255, 0, 0.3))
    )
  )

  val donaterName = style(
    fontSize(1.2 rem),
    paddingLeft(0.5 rem),
    paddingBottom(0.3 rem),
    wordWrap.breakWord
  )

  val nav = style(
    height(3 rem)
  )
  val navItem = style(
    display.inlineBlock,
    listStyleType := "none",
    fontSize(1.4 rem),
    lineHeight(4 rem),
    marginRight(0.7 rem)
  )
  val hr = style(border(1 px, solid, rgb(0, 255, 0)))
}
