package io.github.zrhmn.ssdl.core

/** INCOSE Requirement - specification of system capability or constraint.
  *
  * Requirements are the foundation of systems engineering, representing stakeholder needs translated into verifiable
  * system specifications. This implementation follows INCOSE requirements engineering best practices and IEEE 29148
  * standard.
  *
  * Key principles implemented:
  *   - Unique identification for traceability matrices
  *   - Typed classification for analysis and management workflows
  *   - Priority assignment for trade-off studies and resource allocation
  *   - Verification method specification for V&V planning
  *   - Constraint modeling for design space boundaries
  *   - Derivation traceability for requirements hierarchy
  *
  * The functional design enables requirements analysis through pure functions, supporting formal verification and
  * automated consistency checking typical in functional programming and theorem proving.
  *
  * @param requirementType
  *   Classification for engineering analysis workflows
  * @param priority
  *   Relative importance for trade studies and resource allocation
  * @param verification
  *   How the requirement will be validated (V&V planning)
  * @param constraints
  *   Design space limitations and boundaries
  * @param derivedFrom
  *   Parent requirement for traceability hierarchy
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Chapter 5 "Requirements Analysis"
  * @see
  *   IEEE Std 29148-2018 Requirements Engineering
  * @see
  *   SysML v1.6 Requirements Diagrams
  */
case class Requirement(
    id: ElementId,
    name: String,
    description: String,
    requirementType: RequirementType,
    priority: Priority,
    verification: VerificationMethod,
    constraints: List[Constraint] = Nil,
    derivedFrom: Option[ElementId] = None,
)

/** INCOSE Requirement Classification following ISO 29148 taxonomy.
  *
  * Requirement types enable systematic analysis and allocation across engineering disciplines. Each type implies
  * specific verification approaches and design considerations, supporting the functional decomposition process.
  *
  * The enumeration provides exhaustive pattern matching for requirements analysis algorithms and automated verification
  * planning tools.
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 5.2.1 "Requirement Types"
  * @see
  *   IEEE Std 29148-2018 Section 5.2 "Requirement Classification"
  */
enum RequirementType {

    /** Functional requirements - what the system must do.
      *
      * Specifies system behaviors, operations, and transformations. These drive architectural decisions and functional
      * allocation. Examples: "System shall maintain altitude within ±10m", "Autopilot shall execute waypoint
      * navigation"
      */
    case Functional // what the system must do

    /** Performance requirements - how well the system must perform.
      *
      * Quantifies system capabilities, throughput, timing, and quality metrics. Critical for verification planning and
      * acceptance criteria. Examples: "Response time < 100ms", "Endurance > 30 minutes", "Accuracy ±1m"
      */
    case Performance // how well the system must perform

    /** Interface requirements - how system interacts with external entities.
      *
      * Defines protocols, data formats, physical connections, and interaction patterns. Essential for integration
      * planning and system-of-systems architectures. Examples: "MAVLink protocol compliance", "RC transmitter
      * compatibility", "Ground station interface"
      */
    case Interface // how system interacts

    /** Design constraints - limitations on solution space.
      *
      * Restricts design choices based on technology, standards, or implementation constraints. Guides architectural
      * decisions and technology selection. Examples: "Weight < 2kg", "Operating temperature -20°C to +60°C", "Open
      * source software only"
      */
    case Design // design constraints

    /** Operational requirements - how system is used and maintained.
      *
      * Defines operational scenarios, maintenance procedures, and lifecycle considerations. Important for logistics and
      * operational concept development. Examples: "Field maintainable", "Single operator deployment", "Weather
      * operational limits"
      */
    case Operational // operational conditions

    /** Safety requirements - hazard mitigation and safety assurance.
      *
      * Specifies safety functions, hazard responses, and risk mitigation measures. Critical for safety analysis and
      * certification processes. Examples: "Fail-safe flight termination", "Ground proximity warning", "Battery
      * overcurrent protection"
      */
    case Safety // safety requirements

    /** Security requirements - protection against threats and vulnerabilities.
      *
      * Defines cybersecurity, access control, and information protection measures. Increasingly important for connected
      * and autonomous systems. Examples: "Encrypted telemetry", "Authentication required", "Tamper detection"
      */
    case Security // security requirements
}

/** INCOSE Priority Classification for requirements management and trade studies.
  *
  * Priority levels enable systematic trade-off analysis when requirements conflict or resources are constrained. This
  * supports rational decision-making in complex system development.
  *
  * The ordering supports automated prioritization algorithms and resource allocation optimization typical in functional
  * programming approaches.
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 5.3 "Requirements Prioritization"
  */
enum Priority {

    /** Critical priority - system cannot function without this requirement. Failure represents mission failure or
      * safety hazard.
      */
    case Critical

    /** High priority - significant impact on system effectiveness. Should be implemented unless major technical or cost
      * barriers exist.
      */
    case High

    /** Medium priority - contributes to system effectiveness. Implementation depends on available resources and
      * technical feasibility.
      */
    case Medium

    /** Low priority - nice to have enhancement. Implemented only if resources permit after higher priorities satisfied.
      */
    case Low
}

/** INCOSE Verification Methods following IEEE 29148 and ISO 29148 standards.
  *
  * Verification methods define how requirements compliance will be demonstrated. Each method has different cost,
  * schedule, and confidence implications, supporting systematic V&V planning and resource allocation.
  *
  * The enumeration enables automated test planning and verification strategy optimization through functional
  * programming techniques.
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 6.2 "Verification Methods"
  * @see
  *   IEEE Std 29148-2018 Section 6 "Verification and Validation"
  */
enum VerificationMethod {

    /** Test - physical or simulation-based verification through controlled experiments. Highest confidence but often
      * highest cost. Provides quantitative evidence. Examples: Flight testing, hardware-in-the-loop simulation,
      * environmental testing
      */
    case Test

    /** Analysis - mathematical or computational verification through modeling and calculation. Cost-effective for
      * complex systems. Enables early verification in design phase. Examples: Performance modeling, thermal analysis,
      * control system stability analysis
      */
    case Analysis

    /** Inspection - visual or automated examination of artifacts and documentation. Low cost verification for design
      * and implementation compliance. Examples: Code review, design document inspection, interface specification review
      */
    case Inspection

    /** Demonstration - operational exhibition of system capabilities in representative scenarios. Provides stakeholder
      * confidence through realistic operational scenarios. Examples: Mission scenario demonstration, operational use
      * case exhibition, capability showcase
      */
    case Demonstration
}
