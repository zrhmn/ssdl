package io.github.zrhmn.ssdl.dsl

import io.github.zrhmn.ssdl.core.*
import munit.FunSuite

/** Unit tests for DSL builders. */
class DSLSuite extends FunSuite {

    test("SystemBuilder creates systems correctly") {
        val system = SystemDSL.system("SYS-001", "Test System")
            .withDescription("A test system")
            .build

        assertEquals(system.id, ElementId("SYS-001"), "System ID should match")
        assertEquals(system.name, "Test System", "System name should match")
        assertEquals(system.description, Some("A test system"), "System description should match")
        assertEquals(system.components.length, 0, "Should have no components")
        assertEquals(system.requirements.length, 0, "Should have no requirements")
    }

    test("ComponentBuilder creates components with properties") {
        val component = SystemDSL.component("COMP-001", "Test Component")
            .withDescription("A test component")
            .withProperty("mass", PropertyValue.NumberValue(2.5, Some("kg")))
            .withProperty("active", PropertyValue.BooleanValue(true))
            .build

        assertEquals(component.id, ElementId("COMP-001"), "Component ID should match")
        assertEquals(component.name, "Test Component", "Component name should match")
        assertEquals(component.description, Some("A test component"), "Component description should match")
        assertEquals(component.properties.size, 2, "Should have 2 properties")
        assertEquals(
          component.properties("mass"),
          PropertyValue.NumberValue(2.5, Some("kg")),
          "Mass property should match",
        )
        assertEquals(component.properties("active"), PropertyValue.BooleanValue(true), "Active property should match")
    }

    test("RequirementBuilder creates requirements correctly") {
        val requirement = SystemDSL.requirement("REQ-001", "Test Requirement")
            .withDescription("System shall perform test function")
            .ofType(RequirementType.Performance)
            .withPriority(Priority.High)
            .verifiedBy(VerificationMethod.Analysis)
            .build

        assertEquals(requirement.id, ElementId("REQ-001"))
        assertEquals(requirement.name, "Test Requirement")
        assertEquals(requirement.description, "System shall perform test function")
        assertEquals(requirement.requirementType, RequirementType.Performance)
        assertEquals(requirement.priority, Priority.High)
        assertEquals(requirement.verification, VerificationMethod.Analysis)
    }

    test("SystemBuilder with components and requirements") {
        val component = SystemDSL.component("COMP-001", "GPS")
            .withProperty("accuracy", PropertyValue.NumberValue(3.0, Some("m")))
            .build

        val requirement = SystemDSL.requirement("REQ-001", "Navigation")
            .withDescription("Provide accurate navigation")
            .ofType(RequirementType.Functional)
            .build

        val system = SystemDSL.system("SYS-001", "Navigation System")
            .withDescription("GPS-based navigation")
            .withComponent(component)
            .withRequirement(requirement)
            .build

        assertEquals(system.components.length, 1)
        assertEquals(system.requirements.length, 1)
        assertEquals(system.components.head.name, "GPS")
        assertEquals(system.requirements.head.name, "Navigation")
    }

    test("Builder immutability - modifications return new instances") {
        val builder1 = SystemDSL.system("SYS-001", "System 1")
        val builder2 = builder1.withDescription("Modified description")

        // Original builder should be unchanged
        assertEquals(builder1.description, None)
        assertEquals(builder2.description, Some("Modified description"))

        // They should be different instances
        assert(builder1 ne builder2)
    }

    test("ComponentBuilder with multiple properties") {
        val component = SystemDSL.component("SENSOR-001", "Multi-Sensor")
            .withProperty("temperature", PropertyValue.NumberValue(-40.0, Some("°C")))
            .withProperty("humidity", PropertyValue.NumberValue(85.0, Some("%")))
            .withProperty(
              "sensors",
              PropertyValue.ListValue(List(
                PropertyValue.StringValue("temp"),
                PropertyValue.StringValue("humidity"),
                PropertyValue.StringValue("pressure"),
              )),
            )
            .build

        assertEquals(component.properties.size, 3)
        assertEquals(component.properties("temperature"), PropertyValue.NumberValue(-40.0, Some("°C")))

        val sensorList = component.properties("sensors").asInstanceOf[PropertyValue.ListValue]
        assertEquals(sensorList.values.length, 3)
    }

    test("SystemBuilder with subsystems") {
        val subsystem = SystemDSL.system("SUB-001", "Subsystem")
            .withDescription("A subsystem")
            .build

        val parentSystem = SystemDSL.system("SYS-001", "Parent System")
            .withSubsystem(subsystem)
            .build

        assertEquals(parentSystem.subsystems.length, 1)
        assertEquals(parentSystem.subsystems.head.name, "Subsystem")
    }

    test("Interface creation and system integration") {
        val component1 = SystemDSL.component("COMP-001", "Component 1").build
        val component2 = SystemDSL.component("COMP-002", "Component 2").build

        val interface = Interface(
          ElementId("IF-001"),
          "Data Link",
          Some("High-speed data connection"),
          component1.id,
          component2.id,
          InterfaceType.Data,
          Nil,
        )

        val system = SystemDSL.system("SYS-001", "Connected System")
            .withComponent(component1)
            .withComponent(component2)
            .withInterface(interface)
            .build

        assertEquals(system.interfaces.length, 1)
        assertEquals(system.interfaces.head.name, "Data Link")
        assertEquals(system.interfaces.head.source, component1.id)
        assertEquals(system.interfaces.head.target, component2.id)
    }
}
