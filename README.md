# SSDL - Scala Systems Description Language

[![CI](https://github.com/zrhmn/ssdl/workflows/CI/badge.svg)](https://github.com/zrhmn/ssdl/actions/workflows/ci.yml)
[![Release](https://github.com/zrhmn/ssdl/workflows/Release/badge.svg)](https://github.com/zrhmn/ssdl/actions/workflows/release.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.zrhmn/ssdl_3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.zrhmn/ssdl_3)
[![Scala 3](https://img.shields.io/badge/Scala-3.7.3-red.svg)](https://scala-lang.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A functional, type-safe Scala domain-specific language for describing engineered systems following INCOSE Systems Engineering principles and SysML modeling standards.

## Installation

Add SSDL to your `build.sbt`:

```scala
libraryDependencies += "io.github.zrhmn" %% "ssdl" % "1.0.0"
```

## Usage

### Basic Example

```scala
import io.github.zrhmn.ssdl.core.*
import io.github.zrhmn.ssdl.dsl.*
import io.github.zrhmn.ssdl.squants.SquantsIntegration.*
import squants.mass.Kilograms
import squants.energy.Watts

// Define components with type-safe properties
val motor = SystemDSL.component("MOTOR-001", "Electric Motor")
  .withDescription("High-efficiency brushless motor")
  .withProperty("mass", Kilograms(2.3).toPropertyValue)
  .withProperty("power", Watts(750).toPropertyValue)
  .withProperty("efficiency", PropertyValue.NumberValue(0.95, None))
  .build

// Define requirements with classification and verification
val powerRequirement = SystemDSL.requirement("REQ-001", "Motor Power")
  .withDescription("Motor shall provide minimum 750W continuous power")
  .ofType(RequirementType.Performance)
  .withPriority(Priority.High)
  .verifiedBy(VerificationMethod.Test)
  .build

// Build complete system
val driveSystem = SystemDSL.system("DRIVE-001", "Propulsion System")
  .withDescription("Complete electric propulsion system")
  .withComponent(motor)
  .withRequirement(powerRequirement)
  .build
```

## PlantUML SysML Diagrams

Generate SysML diagrams from your system models:

```scala
import io.github.zrhmn.ssdl.plantuml.SysMLEncoder

val bdd = SysMLEncoder.generateBDD(system)
val ibd = SysMLEncoder.generateIBD(system)
val reqDiagram = SysMLEncoder.generateRequirementsDiagram(system)
```

## JSON Serialization

```scala
import io.github.zrhmn.ssdl.circe.CirceCodecs.given
import io.circe.syntax.*

// Serialize to JSON
val jsonString = system.toJsonString

// Parse from JSON
val parseResult = jsonString.parseAsSystem
```

## Example: Automotive Control System

```scala
val automotiveSystem = SystemDSL.system("ACS-001", "AutomotiveControlSystem")
  .withDescription("Advanced driver assistance and control system")
  .withComponent(
    SystemDSL.component("EC-001", "EngineController")
      .withDescription("Controls engine operations and performance")
      .withProperty("type", PropertyValue.StringValue("ECU"))
      .withProperty("processingPower", PropertyValue.NumberValue(2.4, Some("GHz")))
      .withProperty("compliance", PropertyValue.StringValue("ISO 26262"))
      .build
  )
  .withComponent(
    SystemDSL.component("BC-001", "BrakeController")
      .withDescription("Manages braking system and ABS")
      .withProperty("type", PropertyValue.StringValue("Safety Critical"))
      .withProperty("responseTime", PropertyValue.NumberValue(50.0, Some("ms")))
      .withProperty("maxPower", PropertyValue.NumberValue(150.0, Some("kW")))
      .build
  )
  .withRequirement(
    SystemDSL.requirement("REQ-001", "Engine Response Time")
      .withDescription("Engine controller must respond to throttle input within 100ms")
      .ofType(RequirementType.Performance)
      .withPriority(Priority.High)
      .verifiedBy(VerificationMethod.Test)
      .build
  )
  .withRequirement(
    SystemDSL.requirement("REQ-002", "Brake Safety")
      .withDescription("Brake controller must engage emergency braking if obstacle detected")
      .ofType(RequirementType.Safety)
      .withPriority(Priority.Critical)
      .verifiedBy(VerificationMethod.Inspection)
      .build
  )
  .build
```

### Squants Integration Example
```scala
import squants.mass.Kilograms
import squants.energy.Watts
import squants.electro.Volts

val motor = SystemDSL.component("MOTOR-001", "Electric Motor")
  .withProperty("mass", Kilograms(2.3).toPropertyValue)
  .withProperty("power", Watts(750).toPropertyValue)
  .withProperty("voltage", Volts(48).toPropertyValue)
  .build

val massConstraint = Constraint(
  ElementId("CONST-001"),
  "Mass Limit",
  "Motor mass must not exceed 3.0 kg for weight-critical applications",
  ConstraintType.Mass,
  Kilograms(3.0).toPropertyValue
)
```
