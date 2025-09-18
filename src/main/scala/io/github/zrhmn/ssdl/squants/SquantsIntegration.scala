package io.github.zrhmn.ssdl.squants

import io.github.zrhmn.ssdl.core.PropertyValue
import squants._
import squants.electro._

/** Helper functions for integrating Squants quantities with SSDL PropertyValue.
  *
  * Provides convenient conversion between Squants type-safe quantities and SSDL PropertyValue, enabling both type
  * safety and backward compatibility.
  */
object SquantsIntegration {

    /** Convert a Squants Mass quantity to PropertyValue.NumberValue */
    def mass(quantity: Mass): PropertyValue.NumberValue =
        PropertyValue.NumberValue(quantity.toKilograms, Some("kg"))

    /** Convert a Squants Power quantity to PropertyValue.NumberValue */
    def power(quantity: Power): PropertyValue.NumberValue =
        PropertyValue.NumberValue(quantity.toWatts, Some("W"))

    /** Convert a Squants Energy quantity to PropertyValue.NumberValue */
    def energy(quantity: Energy): PropertyValue.NumberValue =
        PropertyValue.NumberValue(quantity.toJoules, Some("J"))

    /** Convert a Squants Temperature quantity to PropertyValue.NumberValue */
    def temperature(quantity: Temperature): PropertyValue.NumberValue =
        PropertyValue.NumberValue(quantity.toCelsiusScale, Some("°C"))

    /** Convert a Squants ElectricPotential quantity to PropertyValue.NumberValue */
    def voltage(quantity: ElectricPotential): PropertyValue.NumberValue =
        PropertyValue.NumberValue(quantity.toVolts, Some("V"))

    /** Convert a Squants ElectricCurrent quantity to PropertyValue.NumberValue */
    def current(quantity: ElectricCurrent): PropertyValue.NumberValue =
        PropertyValue.NumberValue(quantity.toAmperes, Some("A"))

    /** Convert a Squants Time quantity to PropertyValue.NumberValue */
    def time(quantity: Time): PropertyValue.NumberValue =
        PropertyValue.NumberValue(quantity.toSeconds, Some("s"))

    /** Extension methods for convenient usage */
    extension (quantity: Mass)
        def toPropertyValue: PropertyValue.NumberValue = mass(quantity)

    extension (quantity: Power)
        def toPropertyValue: PropertyValue.NumberValue = power(quantity)

    extension (quantity: Energy)
        def toPropertyValue: PropertyValue.NumberValue = energy(quantity)

    extension (quantity: Temperature)
        def toPropertyValue: PropertyValue.NumberValue = temperature(quantity)

    extension (quantity: ElectricPotential)
        def toPropertyValue: PropertyValue.NumberValue = voltage(quantity)

    extension (quantity: ElectricCurrent)
        def toPropertyValue: PropertyValue.NumberValue = current(quantity)

    extension (quantity: Time)
        def toPropertyValue: PropertyValue.NumberValue = time(quantity)
}
