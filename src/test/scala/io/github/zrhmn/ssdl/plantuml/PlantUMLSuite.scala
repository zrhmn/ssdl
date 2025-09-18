package io.github.zrhmn.ssdl.plantuml

import io.github.zrhmn.ssdl.core.*
import io.github.zrhmn.ssdl.dsl.*
import munit.FunSuite

/** Unit tests for PlantUML SysML encoder. */
class PlantUMLSuite extends FunSuite {

    val testSystem = SystemDSL.system("TEST-001", "Test System")
        .withDescription("Simple test system for PlantUML generation")
        .withComponent(
          SystemDSL.component("COMP-001", "Test Component")
              .withDescription("A test component")
              .withProperty("mass", PropertyValue.NumberValue(1.5, Some("kg")))
              .withProperty("active", PropertyValue.BooleanValue(true))
              .build,
        )
        .withRequirement(
          SystemDSL.requirement("REQ-001", "Test Requirement")
              .withDescription("System shall be testable")
              .ofType(RequirementType.Functional)
              .withPriority(Priority.Medium)
              .verifiedBy(VerificationMethod.Test)
              .build,
        )
        .build

    test("BDD generation creates valid PlantUML structure") {
        val bdd = SysMLEncoder.generateBDD(testSystem)

        // Check basic structure
        assert(bdd.startsWith("@startuml BDD_TEST_001"))
        assert(bdd.endsWith("@enduml"))
        assert(bdd.contains("title Block Definition Diagram - Test System"))

        // Check system package
        assert(bdd.contains("package \"Test System\" as TEST_001 <<system>>"))

        // Check component
        assert(bdd.contains("class \"Test Component\" as COMP_001 <<block>>"))

        // Check relationships
        assert(bdd.contains("TEST_001 *-- COMP_001"))

        // Check properties in notes
        assert(bdd.contains("mass = 1.5 kg"))
        assert(bdd.contains("active = true"))
    }

    test("IBD generation includes component details") {
        val ibd = SysMLEncoder.generateIBD(testSystem)

        // Check basic structure
        assert(ibd.startsWith("@startuml IBD_TEST_001"))
        assert(ibd.endsWith("@enduml"))
        assert(ibd.contains("title Internal Block Diagram - Test System"))

        // Check component representation
        assert(ibd.contains("component \"Test Component\" as COMP_001"))

        // Check property notes
        assert(ibd.contains("mass = 1.5 kg"))
        assert(ibd.contains("active = true"))
    }

    test("Requirements diagram shows requirement details") {
        val reqDiagram = SysMLEncoder.generateRequirementsDiagram(testSystem)

        // Check basic structure
        assert(reqDiagram.startsWith("@startuml REQ_TEST_001"))
        assert(reqDiagram.endsWith("@enduml"))
        assert(reqDiagram.contains("title Requirements Diagram - Test System"))

        // Check requirement representation
        assert(reqDiagram.contains("rectangle \"Test Requirement\" as REQ_001"))

        // Check requirement properties
        assert(reqDiagram.contains("id: REQ-001"))
        assert(reqDiagram.contains("text: System shall be testable"))
        assert(reqDiagram.contains("type: Functional"))
        assert(reqDiagram.contains("priority: Medium"))
        assert(reqDiagram.contains("verification: Test"))
    }

    test("Complete system documentation contains all diagrams") {
        val completeDoc = SysMLEncoder.generateCompleteSystemDoc(testSystem)

        // Should contain all three diagram types with unique names
        assert(completeDoc.contains("@startuml BDD_TEST_001"))
        assert(completeDoc.contains("@startuml IBD_TEST_001"))
        assert(completeDoc.contains("@startuml REQ_TEST_001"))

        // Should have multiple @enduml statements
        val endumlCount = completeDoc.split("@enduml").length - 1
        assertEquals(endumlCount, 3)
    }

    test("ID sanitization works correctly") {
        val systemWithSpecialChars = SystemDSL.system("SYS-001.2", "System with Special-Chars")
            .withComponent(
              SystemDSL.component("COMP-001/A", "Component with/Slash")
                  .build,
            )
            .build

        val bdd = SysMLEncoder.generateBDD(systemWithSpecialChars)

        // Special characters should be sanitized
        assert(bdd.contains("SYS_001_2"))
        assert(bdd.contains("COMP_001_A"))
        assert(!bdd.contains("SYS-001.2"))
        assert(!bdd.contains("COMP-001/A"))
    }

    test("Property value formatting handles different types") {
        val component = SystemDSL.component("COMP-001", "Multi-Property Component")
            .withProperty("stringProp", PropertyValue.StringValue("test value"))
            .withProperty("numberProp", PropertyValue.NumberValue(42.5, Some("Hz")))
            .withProperty("boolProp", PropertyValue.BooleanValue(false))
            .withProperty(
              "listProp",
              PropertyValue.ListValue(List(
                PropertyValue.StringValue("item1"),
                PropertyValue.StringValue("item2"),
              )),
            )
            .build

        val system = SystemDSL.system("SYS-001", "Test System")
            .withComponent(component)
            .build

        val bdd = SysMLEncoder.generateBDD(system)

        // Check different property value formats
        assert(bdd.contains("stringProp = \"test value\""))
        assert(bdd.contains("numberProp = 42.5 Hz"))
        assert(bdd.contains("boolProp = false"))
        assert(bdd.contains("listProp = [\"item1\", \"item2\"]"))
    }

    test("System with subsystems creates nested structure") {
        val subsystem = SystemDSL.system("SUB-001", "Subsystem")
            .withDescription("A nested subsystem")
            .build

        val parentSystem = SystemDSL.system("PARENT-001", "Parent System")
            .withDescription("System with subsystems")
            .withSubsystem(subsystem)
            .build

        val bdd = SysMLEncoder.generateBDD(parentSystem)

        // Check nested package structure
        assert(bdd.contains("package \"Parent System\" as PARENT_001 <<system>>"))
        assert(bdd.contains("package \"Subsystem\" as SUB_001 <<system>>"))

        // Check composition relationship
        assert(bdd.contains("PARENT_001 *-- SUB_001"))
    }

    test("Empty system generates valid minimal PlantUML") {
        val emptySystem = SystemDSL.system("EMPTY-001", "Empty System").build

        val bdd = SysMLEncoder.generateBDD(emptySystem)

        // Should still have valid structure
        assert(bdd.startsWith("@startuml BDD_EMPTY_001"))
        assert(bdd.endsWith("@enduml"))
        assert(bdd.contains("title Block Definition Diagram - Empty System"))
        assert(bdd.contains("package \"Empty System\" as EMPTY_001 <<system>>"))
    }
}
