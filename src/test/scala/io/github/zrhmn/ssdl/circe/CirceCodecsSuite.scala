package io.github.zrhmn.ssdl.circe

import io.github.zrhmn.ssdl.core.*
import io.github.zrhmn.ssdl.dsl.*
import io.github.zrhmn.ssdl.circe.CirceCodecs.given
import io.github.zrhmn.ssdl.circe.CirceCodecs.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import munit.FunSuite

/** Unit tests for Circe JSON codecs. */
class CirceCodecsSuite extends FunSuite {

    test("ElementId encoding and decoding") {
        val id      = ElementId("TEST-001")
        val json    = id.asJson
        val decoded = json.as[ElementId]

        assert(decoded.isRight)
        assertEquals(decoded.getOrElse(fail("Decoding failed")), id)
    }

    test("PropertyValue encoding and decoding - all types") {
        val stringValue  = PropertyValue.StringValue("test")
        val numberValue  = PropertyValue.NumberValue(42.5, Some("kg"))
        val booleanValue = PropertyValue.BooleanValue(true)
        val listValue    = PropertyValue.ListValue(List(stringValue, numberValue))

        // Test string value
        val stringJson    = stringValue.asJson
        val stringDecoded = stringJson.as[PropertyValue]
        assert(stringDecoded.isRight)
        assertEquals(stringDecoded.getOrElse(fail("String decoding failed")), stringValue)

        // Test number value
        val numberJson    = numberValue.asJson
        val numberDecoded = numberJson.as[PropertyValue]
        assert(numberDecoded.isRight)
        assertEquals(numberDecoded.getOrElse(fail("Number decoding failed")), numberValue)

        // Test boolean value
        val booleanJson    = booleanValue.asJson
        val booleanDecoded = booleanJson.as[PropertyValue]
        assert(booleanDecoded.isRight)
        assertEquals(booleanDecoded.getOrElse(fail("Boolean decoding failed")), booleanValue)

        // Test list value (recursive)
        val listJson    = listValue.asJson
        val listDecoded = listJson.as[PropertyValue]
        assert(listDecoded.isRight)
        assertEquals(listDecoded.getOrElse(fail("List decoding failed")), listValue)
    }

    test("Component encoding and decoding") {
        val component = Component(
          ElementId("COMP-001"),
          "Test Component",
          Some("A test component"),
          Map(
            "mass"   -> PropertyValue.NumberValue(1.5, Some("kg")),
            "active" -> PropertyValue.BooleanValue(true),
          ),
          Nil,
        )

        val json    = component.asJson
        val decoded = json.as[Component]

        assert(decoded.isRight)
        val decodedComponent = decoded.getOrElse(fail("Component decoding failed"))
        assertEquals(decodedComponent.id, component.id)
        assertEquals(decodedComponent.name, component.name)
        assertEquals(decodedComponent.description, component.description)
        assertEquals(decodedComponent.properties, component.properties)
    }

    test("Requirement encoding and decoding") {
        val requirement = Requirement(
          ElementId("REQ-001"),
          "Test Requirement",
          "System shall perform test function",
          RequirementType.Performance,
          Priority.High,
          VerificationMethod.Analysis,
          Nil,
        )

        val json    = requirement.asJson
        val decoded = json.as[Requirement]

        assert(decoded.isRight)
        val decodedReq = decoded.getOrElse(fail("Requirement decoding failed"))
        assertEquals(decodedReq.id, requirement.id)
        assertEquals(decodedReq.name, requirement.name)
        assertEquals(decodedReq.requirementType, requirement.requirementType)
        assertEquals(decodedReq.priority, requirement.priority)
        assertEquals(decodedReq.verification, requirement.verification)
    }

    test("System encoding and decoding") {
        val component = Component(
          ElementId("COMP-001"),
          "GPS",
          Some("GPS receiver"),
          Map("accuracy" -> PropertyValue.NumberValue(3.0, Some("m"))),
          Nil,
        )

        val requirement = Requirement(
          ElementId("REQ-001"),
          "Navigation",
          "Provide accurate navigation",
          RequirementType.Functional,
          Priority.Medium,
          VerificationMethod.Test,
          Nil,
        )

        val system = System(
          ElementId("SYS-001"),
          "Navigation System",
          Some("GPS-based navigation"),
          Nil, // subsystems
          List(component),
          Nil, // interfaces
          List(requirement),
        )

        val json    = system.asJson
        val decoded = json.as[System]

        assert(decoded.isRight)
        val decodedSystem = decoded.getOrElse(fail("System decoding failed"))
        assertEquals(decodedSystem.id, system.id)
        assertEquals(decodedSystem.name, system.name)
        assertEquals(decodedSystem.components.length, 1)
        assertEquals(decodedSystem.requirements.length, 1)
        assertEquals(decodedSystem.components.head.name, "GPS")
        assertEquals(decodedSystem.requirements.head.name, "Navigation")
    }

    test("Enum encoding and decoding") {
        // Test InterfaceType
        val dataType    = InterfaceType.Data
        val dataJson    = dataType.asJson
        val dataDecoded = dataJson.as[InterfaceType]
        assert(dataDecoded.isRight)
        assertEquals(dataDecoded.getOrElse(fail("InterfaceType decoding failed")), dataType)

        // Test RequirementType
        val perfType    = RequirementType.Performance
        val perfJson    = perfType.asJson
        val perfDecoded = perfJson.as[RequirementType]
        assert(perfDecoded.isRight)
        assertEquals(perfDecoded.getOrElse(fail("RequirementType decoding failed")), perfType)

        // Test Priority
        val highPriority = Priority.High
        val highJson     = highPriority.asJson
        val highDecoded  = highJson.as[Priority]
        assert(highDecoded.isRight)
        assertEquals(highDecoded.getOrElse(fail("Priority decoding failed")), highPriority)
    }

    test("Round-trip encoding and decoding with DSL-created system") {
        val system = SystemDSL.system("ROUND-001", "Round Trip System")
            .withDescription("Testing round-trip JSON encoding")
            .withComponent(
              SystemDSL.component("COMP-001", "Test Component")
                  .withProperty("value", PropertyValue.NumberValue(123.45, Some("V")))
                  .withProperty("name", PropertyValue.StringValue("test"))
                  .withProperty("enabled", PropertyValue.BooleanValue(true))
                  .build,
            )
            .withRequirement(
              SystemDSL.requirement("REQ-001", "Test Requirement")
                  .withDescription("Must work correctly")
                  .ofType(RequirementType.Safety)
                  .withPriority(Priority.Critical)
                  .verifiedBy(VerificationMethod.Inspection)
                  .build,
            )
            .build

        // Encode to JSON
        val jsonString = system.toJsonString

        // Parse back from JSON
        val parseResult = parse(jsonString)
        assert(parseResult.isRight)

        val decoded = parseResult.flatMap(_.as[System])
        assert(decoded.isRight)

        val roundTripSystem = decoded.getOrElse(fail("Round-trip decoding failed"))

        // Verify all properties are preserved
        assertEquals(roundTripSystem.id, system.id)
        assertEquals(roundTripSystem.name, system.name)
        assertEquals(roundTripSystem.description, system.description)
        assertEquals(roundTripSystem.components.length, system.components.length)
        assertEquals(roundTripSystem.requirements.length, system.requirements.length)

        // Verify component properties
        val originalComp  = system.components.head
        val roundTripComp = roundTripSystem.components.head
        assertEquals(roundTripComp.properties, originalComp.properties)
    }

    test("Error handling for invalid JSON") {
        val invalidJson = """{"invalid": "structure"}"""
        val parseResult = parse(invalidJson).flatMap(_.as[System])

        assert(parseResult.isLeft)
    }

    test("Extension methods work correctly") {
        val system = SystemDSL.system("EXT-001", "Extension Test").build

        // Test toJsonString extension
        val jsonString = system.toJsonString
        assert(jsonString.contains("EXT-001"), "JSON should contain EXT-001")
        assert(jsonString.contains("Extension Test"), "JSON should contain Extension Test")

        // Test parseAsSystem extension
        val parseResult = jsonString.parseAsSystem
        assert(parseResult.isRight, "Parse should succeed")
        assertEquals(
          parseResult.getOrElse(fail("Parse failed")).name,
          "Extension Test",
          "Parsed system name should match",
        )
    }
}
