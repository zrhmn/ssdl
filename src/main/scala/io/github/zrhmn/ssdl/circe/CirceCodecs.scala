package io.github.zrhmn.ssdl.circe

import io.github.zrhmn.ssdl.core.*
import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.syntax.*
import cats.syntax.traverse.*

/** Circe JSON codecs for SSDL core types.
  *
  * Provides comprehensive JSON serialization/deserialization for SSDL system models, enabling data exchange,
  * persistence, and integration with external tools.
  *
  * The codecs follow JSON best practices:
  *   - Consistent field naming (camelCase)
  *   - Enumeration as strings for readability
  *   - Opaque types handled transparently
  *   - Nested structures preserved
  *
  * This enables integration with JSON-based toolchains, web APIs, and configuration management systems commonly used in
  * systems engineering.
  */
object CirceCodecs {

    // ElementId codecs (opaque type handling)
    given Encoder[ElementId] = Encoder[String].contramap(_.value)
    given Decoder[ElementId] = Decoder[String].map(ElementId.apply)

    // Enum codecs
    given Encoder[InterfaceType] = Encoder[String].contramap(_.toString)
    given Decoder[InterfaceType] = Decoder[String].emap { str =>
        InterfaceType.values.find(_.toString == str)
            .toRight(s"Invalid InterfaceType: $str")
    }

    given Encoder[RequirementType] = Encoder[String].contramap(_.toString)
    given Decoder[RequirementType] = Decoder[String].emap { str =>
        RequirementType.values.find(_.toString == str)
            .toRight(s"Invalid RequirementType: $str")
    }

    given Encoder[Priority] = Encoder[String].contramap(_.toString)
    given Decoder[Priority] = Decoder[String].emap { str =>
        Priority.values.find(_.toString == str)
            .toRight(s"Invalid Priority: $str")
    }

    given Encoder[VerificationMethod] = Encoder[String].contramap(_.toString)
    given Decoder[VerificationMethod] = Decoder[String].emap { str =>
        VerificationMethod.values.find(_.toString == str)
            .toRight(s"Invalid VerificationMethod: $str")
    }

    given Encoder[ConstraintType] = Encoder[String].contramap(_.toString)
    given Decoder[ConstraintType] = Decoder[String].emap { str =>
        ConstraintType.values.find(_.toString == str)
            .toRight(s"Invalid ConstraintType: $str")
    }

    // PropertyValue codec with proper recursion handling
    given propertyValueEncoder: Encoder[PropertyValue] = Encoder.instance {
        case PropertyValue.StringValue(value) =>
            Json.obj("type" -> "string".asJson, "value" -> value.asJson)
        case PropertyValue.NumberValue(value, unit) =>
            Json.obj(
              "type"  -> "number".asJson,
              "value" -> value.asJson,
              "unit"  -> unit.asJson,
            )
        case PropertyValue.BooleanValue(value) =>
            Json.obj("type" -> "boolean".asJson, "value" -> value.asJson)
        case PropertyValue.ListValue(values) =>
            Json.obj("type" -> "list".asJson, "values" -> Json.fromValues(values.map(propertyValueEncoder(_))))
    }

    given propertyValueDecoder: Decoder[PropertyValue] = Decoder.instance { cursor =>
        cursor.downField("type").as[String].flatMap {
            case "string" => cursor.downField("value").as[String].map(PropertyValue.StringValue.apply)
            case "number" =>
                for {
                    value <- cursor.downField("value").as[Double]
                    unit  <- cursor.downField("unit").as[Option[String]]
                } yield PropertyValue.NumberValue(value, unit)
            case "boolean" => cursor.downField("value").as[Boolean].map(PropertyValue.BooleanValue.apply)
            case "list" =>
                cursor.downField("values").as[List[Json]].flatMap { jsonList =>
                    jsonList.traverse(_.as[PropertyValue](using propertyValueDecoder))
                }.map(PropertyValue.ListValue.apply)
            case other => Left(DecodingFailure(s"Unknown PropertyValue type: $other", cursor.history))
        }
    }

    // Core type codecs using semiauto derivation
    given Encoder[Constraint] = deriveEncoder[Constraint]
    given Decoder[Constraint] = deriveDecoder[Constraint]

    given Encoder[Requirement] = deriveEncoder[Requirement]
    given Decoder[Requirement] = deriveDecoder[Requirement]

    given Encoder[Interface] = deriveEncoder[Interface]
    given Decoder[Interface] = deriveDecoder[Interface]

    given Encoder[Component] = deriveEncoder[Component]
    given Decoder[Component] = deriveDecoder[Component]

    given Encoder[System] = deriveEncoder[System]
    given Decoder[System] = deriveDecoder[System]

    // Convenience methods for encoding/decoding
    extension (system: System) {

        /** Convert system to pretty-printed JSON string */
        def toJsonString: String = system.asJson.spaces2

        /** Convert system to compact JSON string */
        def toJsonCompact: String = system.asJson.noSpaces
    }

    extension (jsonString: String) {

        /** Parse JSON string to System */
        def parseAsSystem: Either[Error, System] = parser.decode[System](jsonString)
    }

    /** Schema information for external tool integration */
    object Schema {
        val version     = "1.0.0"
        val description = "SSDL System Model JSON Schema"

        def generateJsonSchema(): Json = Json.obj(
          "$schema"     -> "https://json-schema.org/draft/2020-12/schema".asJson,
          "title"       -> "SSDL System Model".asJson,
          "version"     -> version.asJson,
          "description" -> description.asJson,
          "type"        -> "object".asJson,
          "properties" -> Json.obj(
            "id"          -> Json.obj("type" -> "string".asJson),
            "name"        -> Json.obj("type" -> "string".asJson),
            "description" -> Json.obj("type" -> "string".asJson),
            "subsystems" -> Json.obj(
              "type"  -> "array".asJson,
              "items" -> Json.obj("$ref" -> "#".asJson),
            ),
            "components" -> Json.obj(
              "type"  -> "array".asJson,
              "items" -> Json.obj("$ref" -> "#/definitions/Component".asJson),
            ),
            "interfaces" -> Json.obj(
              "type"  -> "array".asJson,
              "items" -> Json.obj("$ref" -> "#/definitions/Interface".asJson),
            ),
            "requirements" -> Json.obj(
              "type"  -> "array".asJson,
              "items" -> Json.obj("$ref" -> "#/definitions/Requirement".asJson),
            ),
          ),
          "required" -> Json.arr("id".asJson, "name".asJson),
        )
    }
}
