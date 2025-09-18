package io.github.zrhmn.ssdl.core

import munit.FunSuite

/** Unit tests for core domain types. */
class CoreTypesSuite extends FunSuite {

    test("ElementId creation and value access") {
        val id = ElementId("TEST-001")
        assertEquals(id.value, "TEST-001", "ElementId should store the correct value")
    }

    test("PropertyValue types work correctly") {
        val stringValue  = PropertyValue.StringValue("test")
        val numberValue  = PropertyValue.NumberValue(42.0, Some("kg"))
        val booleanValue = PropertyValue.BooleanValue(true)
        val listValue    = PropertyValue.ListValue(List(stringValue, numberValue))

        // Test string value
        stringValue match {
            case PropertyValue.StringValue(v) => assertEquals(v, "test", "StringValue should match")
            case _                            => fail("Expected StringValue")
        }

        // Test number value with unit
        numberValue match {
            case PropertyValue.NumberValue(v, unit) =>
                assertEquals(v, 42.0, "NumberValue should match")
                assertEquals(unit, Some("kg"), "Unit should match")
            case _ => fail("Expected NumberValue")
        }

        // Test boolean value
        booleanValue match {
            case PropertyValue.BooleanValue(v) => assertEquals(v, true, "BooleanValue should match")
            case _                             => fail("Expected BooleanValue")
        }

        // Test list value
        assertEquals(
          listValue.asInstanceOf[PropertyValue.ListValue].values.length,
          2,
          "ListValue should contain 2 elements",
        )
    }

    test("System creation with components and requirements") {
        val component = Component(
          ElementId("COMP-001"),
          "Test Component",
          Some("A test component"),
          Map("mass" -> PropertyValue.NumberValue(1.5, Some("kg"))),
          Nil,
        )

        val requirement = Requirement(
          ElementId("REQ-001"),
          "Test Requirement",
          "System shall perform test function",
          RequirementType.Functional,
          Priority.Medium,
          VerificationMethod.Test,
          Nil,
        )

        val system = System(
          ElementId("SYS-001"),
          "Test System",
          Some("A test system"),
          Nil, // subsystems
          List(component),
          Nil, // interfaces
          List(requirement),
        )

        assertEquals(system.name, "Test System", "System name should match")
        assertEquals(system.components.length, 1, "Should have 1 component")
        assertEquals(system.requirements.length, 1, "Should have 1 requirement")
        assertEquals(
          system.components.head.properties("mass"),
          PropertyValue.NumberValue(1.5, Some("kg")),
          "Component mass should match",
        )
    }

    test("Interface creation and types") {
        val interface = Interface(
          ElementId("IF-001"),
          "Test Interface",
          Some("Data connection"),
          ElementId("COMP-001"),
          ElementId("COMP-002"),
          InterfaceType.Data,
          Nil,
        )

        assertEquals(interface.name, "Test Interface", "Interface name should match")
        assertEquals(interface.interfaceType, InterfaceType.Data, "Interface type should be Data")
        assertEquals(interface.source, ElementId("COMP-001"), "Source should match")
        assertEquals(interface.target, ElementId("COMP-002"), "Target should match")
    }

    test("Constraint creation") {
        val constraint = Constraint(
          ElementId("CONST-001"),
          "Mass Constraint",
          "Total system mass must not exceed 10 kg",
          ConstraintType.Mass,
          PropertyValue.NumberValue(10.0, Some("kg")),
        )

        assertEquals(constraint.name, "Mass Constraint", "Constraint name should match")
        assertEquals(
          constraint.description,
          "Total system mass must not exceed 10 kg",
          "Constraint description should match",
        )
        assertEquals(constraint.constraintType, ConstraintType.Mass, "Constraint type should be Mass")
    }

    test("Enum values are correctly defined") {
        // Test InterfaceType enum
        val dataType       = InterfaceType.Data
        val physicalType   = InterfaceType.Physical
        val electricalType = InterfaceType.Electrical
        val controlType    = InterfaceType.Control
        val thermalType    = InterfaceType.Thermal
        val opticalType    = InterfaceType.Optical

        assert(InterfaceType.values.contains(dataType), "Should contain Data type")
        assert(InterfaceType.values.contains(physicalType), "Should contain Physical type")
        assert(InterfaceType.values.contains(electricalType), "Should contain Electrical type")
        assert(InterfaceType.values.contains(controlType), "Should contain Control type")
        assert(InterfaceType.values.contains(thermalType), "Should contain Thermal type")
        assert(InterfaceType.values.contains(opticalType), "Should contain Optical type")

        // Test RequirementType enum
        assert(RequirementType.values.contains(RequirementType.Functional), "Should contain Functional requirement")
        assert(RequirementType.values.contains(RequirementType.Performance), "Should contain Performance requirement")
        assert(RequirementType.values.contains(RequirementType.Safety), "Should contain Safety requirement")

        // Test Priority enum
        assert(Priority.values.contains(Priority.Critical), "Should contain Critical priority")
        assert(Priority.values.contains(Priority.High), "Should contain High priority")
        assert(Priority.values.contains(Priority.Medium), "Should contain Medium priority")
        assert(Priority.values.contains(Priority.Low), "Should contain Low priority")
    }
}
