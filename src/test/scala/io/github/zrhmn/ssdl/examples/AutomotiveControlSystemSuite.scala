package io.github.zrhmn.ssdl.examples

import io.github.zrhmn.ssdl.core.*
import io.github.zrhmn.ssdl.dsl.*
import io.github.zrhmn.ssdl.plantuml.*
import io.github.zrhmn.ssdl.circe.CirceCodecs.given
import io.circe.syntax.*
import munit.FunSuite
import io.circe.parser.*

/** Functional test suite for automotive control system example.
  *
  * Demonstrates SSDL capabilities through a complete automotive system specification, serving as both functional tests
  * and usage examples.
  */
class AutomotiveControlSystemSuite extends FunSuite {

    val system = SystemDSL.system("ACS-001", "AutomotiveControlSystem")
        .withDescription("Advanced driver assistance and control system")
        .withComponent(
          SystemDSL.component("EC-001", "EngineController")
              .withDescription("Controls engine operations and performance")
              .withProperty("type", PropertyValue.StringValue("ECU"))
              .withProperty("processingPower", PropertyValue.NumberValue(2.4, Some("GHz")))
              .withProperty("version", PropertyValue.StringValue("2.1.0"))
              .withProperty("compliance", PropertyValue.StringValue("ISO 26262"))
              .build,
        )
        .withComponent(
          SystemDSL.component("BC-001", "BrakeController")
              .withDescription("Manages braking system and ABS")
              .withProperty("type", PropertyValue.StringValue("Safety Critical"))
              .withProperty("responseTime", PropertyValue.NumberValue(50.0, Some("ms")))
              .withProperty("maxPower", PropertyValue.NumberValue(150.0, Some("kW")))
              .build,
        )
        .withComponent(
          SystemDSL.component("SA-001", "SensorArray")
              .withDescription("Collection of environmental sensors")
              .withProperty(
                "sensors",
                PropertyValue.ListValue(List(
                  PropertyValue.StringValue("Camera"),
                  PropertyValue.StringValue("LiDAR"),
                  PropertyValue.StringValue("Radar"),
                )),
              )
              .withProperty("isActive", PropertyValue.BooleanValue(true))
              .build,
        )
        .withRequirement(
          SystemDSL.requirement("REQ-001", "Engine Response Time")
              .withDescription("Engine controller must respond to throttle input within 100ms")
              .ofType(RequirementType.Performance)
              .withPriority(Priority.High)
              .verifiedBy(VerificationMethod.Test)
              .build,
        )
        .withRequirement(
          SystemDSL.requirement("REQ-002", "Brake Safety")
              .withDescription("Brake controller must engage emergency braking if obstacle detected")
              .ofType(RequirementType.Safety)
              .withPriority(Priority.Critical)
              .verifiedBy(VerificationMethod.Inspection)
              .build,
        )
        .build

    test("system structure is correctly defined") {
        assertEquals(system.name, "AutomotiveControlSystem")
        assertEquals(system.id, ElementId("ACS-001"))
        assertEquals(system.components.length, 3)
        assertEquals(system.requirements.length, 2)
        assert(system.description.isDefined)
    }

    test("components have proper properties") {
        val engineController = system.components.find(_.name == "EngineController").get
        assertEquals(engineController.properties("type"), PropertyValue.StringValue("ECU"))
        assertEquals(engineController.properties("processingPower"), PropertyValue.NumberValue(2.4, Some("GHz")))

        val sensorArray = system.components.find(_.name == "SensorArray").get
        val expectedSensors = PropertyValue.ListValue(List(
          PropertyValue.StringValue("Camera"),
          PropertyValue.StringValue("LiDAR"),
          PropertyValue.StringValue("Radar"),
        ))
        assertEquals(sensorArray.properties("sensors"), expectedSensors)
    }

    test("requirements are properly classified") {
        val performanceReq = system.requirements.find(_.name == "Engine Response Time").get
        assertEquals(performanceReq.requirementType, RequirementType.Performance)
        assertEquals(performanceReq.priority, Priority.High)
        assertEquals(performanceReq.verification, VerificationMethod.Test)

        val safetyReq = system.requirements.find(_.name == "Brake Safety").get
        assertEquals(safetyReq.requirementType, RequirementType.Safety)
        assertEquals(safetyReq.priority, Priority.Critical)
        assertEquals(safetyReq.verification, VerificationMethod.Inspection)
    }

    test("BDD generation produces valid PlantUML") {
        val bdd = SysMLEncoder.generateBDD(system)

        assert(bdd.startsWith("@startuml BDD_ACS_001"))
        assert(bdd.contains("title Block Definition Diagram - AutomotiveControlSystem"))
        assert(bdd.contains("package \"AutomotiveControlSystem\" as ACS_001 <<system>>"))
        assert(bdd.contains("class \"EngineController\" as EC_001 <<block>>"))
        assert(bdd.contains("class \"BrakeController\" as BC_001 <<block>>"))
        assert(bdd.contains("class \"SensorArray\" as SA_001 <<block>>"))
        assert(bdd.contains("ACS_001 *-- EC_001"))
        assert(bdd.endsWith("@enduml"))
    }

    test("IBD generation includes component properties") {
        val ibd = SysMLEncoder.generateIBD(system)

        assert(ibd.startsWith("@startuml IBD_ACS_001"))
        assert(ibd.contains("title Internal Block Diagram - AutomotiveControlSystem"))
        assert(ibd.contains("component \"EngineController\" as EC_001"))
        assert(ibd.contains("type = \"ECU\""))
        assert(ibd.contains("processingPower = 2.4 GHz"))
        assert(ibd.contains("compliance = \"ISO 26262\""))
    }

    test("Requirements diagram shows all requirements") {
        val reqDiagram = SysMLEncoder.generateRequirementsDiagram(system)

        assert(reqDiagram.startsWith("@startuml REQ_ACS_001"))
        assert(reqDiagram.contains("title Requirements Diagram - AutomotiveControlSystem"))
        assert(reqDiagram.contains("rectangle \"Engine Response Time\" as REQ_001"))
        assert(reqDiagram.contains("rectangle \"Brake Safety\" as REQ_002"))
        assert(reqDiagram.contains("type: Performance"))
        assert(reqDiagram.contains("type: Safety"))
        assert(reqDiagram.contains("priority: High"))
        assert(reqDiagram.contains("priority: Critical"))
    }

    test("JSON serialization preserves system structure") {
        val json = system.asJson
        assert(json.noSpaces.contains("AutomotiveControlSystem"), "JSON should contain system name")
        assert(json.noSpaces.contains("EngineController"), "JSON should contain EngineController")
        assert(json.noSpaces.contains("BrakeController"), "JSON should contain BrakeController")
        assert(json.noSpaces.contains("SensorArray"), "JSON should contain SensorArray")

        // Test that we can parse it back
        val parseResult = parse(json.noSpaces)
        assert(parseResult.isRight)
    }

    test("Complete system documentation generates multiple diagrams") {
        val completeDoc = SysMLEncoder.generateCompleteSystemDoc(system)

        // Should contain all three diagram types
        assert(completeDoc.contains("@startuml BDD_ACS_001"))
        assert(completeDoc.contains("@startuml IBD_ACS_001"))
        assert(completeDoc.contains("@startuml REQ_ACS_001"))

        // Each should have its own @enduml
        assertEquals(completeDoc.count(_ == '@'), 6) // 3 start + 3 end
    }
}
