package io.github.zrhmn.ssdl.squants

import io.github.zrhmn.ssdl.core.*
import io.github.zrhmn.ssdl.dsl.*
import io.github.zrhmn.ssdl.squants.SquantsIntegration.*
import squants.mass.Kilograms
import squants.energy.Watts
import squants.thermal.Celsius
import munit.FunSuite

/** Simple Squants integration demo test. */
class SquantsIntegrationSuite extends FunSuite {

    test("Squants quantities convert to PropertyValue") {
        val massValue  = mass(Kilograms(2.5))
        val powerValue = power(Watts(50))
        val tempValue  = temperature(Celsius(25))

        assertEquals(massValue, PropertyValue.NumberValue(2.5, Some("kg")), "Mass conversion should match")
        assertEquals(powerValue, PropertyValue.NumberValue(50.0, Some("W")), "Power conversion should match")
        assertEquals(tempValue, PropertyValue.NumberValue(25.0, Some("°C")), "Temperature conversion should match")
    }

    test("Extension methods work correctly") {
        val massValue  = Kilograms(1.5).toPropertyValue
        val powerValue = Watts(100).toPropertyValue

        assertEquals(massValue, PropertyValue.NumberValue(1.5, Some("kg")), "Mass extension should match")
        assertEquals(powerValue, PropertyValue.NumberValue(100.0, Some("W")), "Power extension should match")
    }

    test("Component with Squants properties") {
        val component = SystemDSL.component("MOTOR-001", "Electric Motor")
            .withProperty("mass", Kilograms(2.3).toPropertyValue)
            .withProperty("power", Watts(750).toPropertyValue)
            .withProperty("efficiency", PropertyValue.NumberValue(0.95, None)) // unitless
            .build

        assertEquals(
          component.properties("mass"),
          PropertyValue.NumberValue(2.3, Some("kg")),
          "Component mass should match",
        )
        assertEquals(
          component.properties("power"),
          PropertyValue.NumberValue(750.0, Some("W")),
          "Component power should match",
        )
        assertEquals(
          component.properties("efficiency"),
          PropertyValue.NumberValue(0.95, None),
          "Component efficiency should match",
        )
    }

    test("Constraint with Squants values") {
        val massConstraint = Constraint(
          ElementId("CONST-001"),
          "Mass Limit",
          "Motor mass must not exceed 3.0 kg for weight-critical applications",
          ConstraintType.Mass,
          Kilograms(3.0).toPropertyValue,
        )

        assertEquals(
          massConstraint.value,
          PropertyValue.NumberValue(3.0, Some("kg")),
          "Constraint value should use Squants conversion",
        )
        assertEquals(massConstraint.constraintType, ConstraintType.Mass, "Constraint type should be Mass")
    }
}
