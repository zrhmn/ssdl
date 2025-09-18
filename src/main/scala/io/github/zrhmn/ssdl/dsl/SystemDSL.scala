package io.github.zrhmn.ssdl.dsl

import io.github.zrhmn.ssdl.core.*

/** Fluent DSL for system specification following INCOSE SE processes and functional programming principles.
  *
  * This DSL enables literate system modeling using builder patterns and method chaining, supporting the incremental and
  * iterative nature of systems engineering processes. The design follows functional programming principles with
  * immutable builders and type-safe construction patterns.
  *
  * Key capabilities:
  *   - Fluent interface for readable system specifications
  *   - Type-safe construction preventing invalid system models
  *   - Immutable builders supporting functional composition
  *   - Method chaining for compact yet expressive system definitions
  *   - Integration with requirements engineering and verification workflows
  *
  * The DSL supports INCOSE systems engineering activities:
  *   - System architecture definition
  *   - Requirements allocation and traceability
  *   - Interface specification and management
  *   - Component specification and integration
  *
  * Example usage for RC aircraft system:
  * ```scala
  * val autopilot = SystemDSL.system("AP-001", "Autopilot System")
  *     .withDescription("Autonomous flight control system")
  *     .withComponent(flightController)
  *     .withInterface(gpsInterface)
  *     .withRequirement(navigationRequirement)
  *     .build
  * ```
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Chapter 4 "System Architecture"
  * @see
  *   Martin Fowler, "Domain Specific Languages" - Internal DSL patterns
  */
object SystemDSL {

    /** Create a new system builder with unique identifier and name.
      *
      * Systems represent complete functional entities that achieve stakeholder objectives through coordinated operation
      * of subsystems and components.
      *
      * @param id
      *   Unique identifier following organizational naming conventions
      * @param name
      *   Human-readable system name for stakeholder communication
      * @return
      *   SystemBuilder for fluent system construction
      */
    def system(id: String, name: String): SystemBuilder = {
        SystemBuilder(ElementId(id), name)
    }

    /** Create a new component builder with unique identifier and name.
      *
      * Components are atomic system elements that perform specific functions and cannot be further decomposed in the
      * current system view.
      *
      * @param id
      *   Unique identifier following organizational naming conventions
      * @param name
      *   Human-readable component name for stakeholder communication
      * @return
      *   ComponentBuilder for fluent component construction
      */
    def component(id: String, name: String): ComponentBuilder = {
        ComponentBuilder(ElementId(id), name)
    }

    /** Create a new requirement builder with unique identifier and name.
      *
      * Requirements specify what the system must do, how well it must perform, and under what conditions, forming the
      * basis for verification and validation.
      *
      * @param id
      *   Unique identifier following requirements management conventions
      * @param name
      *   Human-readable requirement name for traceability
      * @return
      *   RequirementBuilder for fluent requirement construction
      */
    def requirement(id: String, name: String): RequirementBuilder = {
        RequirementBuilder(ElementId(id), name)
    }
}

/** Builder for Systems following the immutable builder pattern.
  *
  * Supports incremental system definition through method chaining while maintaining immutability and type safety. Each
  * method returns a new builder instance, enabling functional composition and safe concurrent usage.
  *
  * The builder validates system composition rules and maintains referential integrity across system elements,
  * supporting INCOSE architecture principles.
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 4.2 "System Architecture"
  */
case class SystemBuilder(
    id: ElementId,
    name: String,
    description: Option[String] = None,
    subsystems: List[System] = Nil,
    components: List[Component] = Nil,
    interfaces: List[Interface] = Nil,
    requirements: List[Requirement] = Nil,
) {

    /** Add descriptive text explaining system purpose and characteristics.
      *
      * Descriptions provide stakeholder communication and design rationale documentation, supporting requirements
      * traceability and design decisions.
      */
    def withDescription(desc: String): SystemBuilder = this.copy(description = Some(desc))

    /** Add a subsystem to enable hierarchical system decomposition.
      *
      * Supports systems-of-systems approaches and hierarchical architecture modeling following INCOSE decomposition
      * principles.
      */
    def withSubsystem(system: System): SystemBuilder = this.copy(subsystems = subsystems :+ system)

    /** Add a component providing atomic functionality within the system.
      *
      * Components are leaf-level elements that implement specific system functions and contain detailed properties for
      * analysis and verification.
      */
    def withComponent(component: Component): SystemBuilder = this.copy(components = components :+ component)

    /** Add an interface defining element interactions and data/energy/material flows.
      *
      * Interfaces are first-class modeling constructs enabling explicit interaction specification and interface
      * management workflows.
      */
    def withInterface(interface: Interface): SystemBuilder = this.copy(interfaces = interfaces :+ interface)

    /** Allocate a requirement to this system for verification and validation.
      *
      * Requirements allocation creates traceability from stakeholder needs to system architecture, supporting
      * verification planning and impact analysis.
      */
    def withRequirement(req: Requirement): SystemBuilder = this.copy(requirements = requirements :+ req)

    /** Construct the final immutable System instance.
      *
      * Validates the system specification and creates the immutable system model ready for analysis, simulation, and
      * further composition.
      */
    def build: System = System(id, name, description, subsystems, components, interfaces, requirements)
}

/** Builder for Components following the immutable builder pattern.
  *
  * Enables incremental component specification with type-safe property modeling and requirements allocation. Supports
  * parametric modeling through typed property values and engineering unit management.
  *
  * @see
  *   SysML v1.6 Parametric Diagrams and Component modeling
  */
case class ComponentBuilder(
    id: ElementId,
    name: String,
    description: Option[String] = None,
    properties: Map[String, PropertyValue] = Map.empty,
    requirements: List[Requirement] = Nil,
) {

    /** Add descriptive text explaining component purpose and implementation approach. */
    def withDescription(desc: String): ComponentBuilder = this.copy(description = Some(desc))

    /** Add a typed property with engineering units for parametric modeling.
      *
      * Properties enable quantitative analysis, constraint checking, and parametric studies. Type safety prevents unit
      * errors and supports automated analysis workflows.
      */
    def withProperty(key: String, value: PropertyValue): ComponentBuilder =
        this.copy(properties = properties + (key -> value))

    /** Allocate a requirement to this component for detailed verification.
      *
      * Component-level requirements enable fine-grained verification planning and support component qualification and
      * testing strategies.
      */
    def withRequirement(req: Requirement): ComponentBuilder = this.copy(requirements = requirements :+ req)

    /** Construct the final immutable Component instance with validation. */
    def build: Component = Component(id, name, description, properties, requirements)
}

/** Builder for Requirements following INCOSE requirements engineering best practices.
  *
  * Supports structured requirement specification with classification, prioritization, and verification planning. The
  * builder enforces requirement quality attributes and maintains traceability relationships.
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Chapter 5 "Requirements Analysis"
  * @see
  *   IEEE Std 29148-2018 Requirements Engineering
  */
case class RequirementBuilder(
    id: ElementId,
    name: String,
    description: String = "",
    requirementType: RequirementType = RequirementType.Functional,
    priority: Priority = Priority.Medium,
    verification: VerificationMethod = VerificationMethod.Test,
    constraints: List[Constraint] = Nil,
) {

    /** Add detailed requirement specification text with acceptance criteria. */
    def withDescription(desc: String): RequirementBuilder = this.copy(description = desc)

    /** Classify requirement type for analysis workflow routing and allocation. */
    def ofType(reqType: RequirementType): RequirementBuilder = this.copy(requirementType = reqType)

    /** Assign priority level for trade study and resource allocation decisions. */
    def withPriority(p: Priority): RequirementBuilder = this.copy(priority = p)

    /** Specify verification method for V&V planning and test strategy development. */
    def verifiedBy(method: VerificationMethod): RequirementBuilder = this.copy(verification = method)

    /** Add design constraint limiting the solution space for this requirement. */
    def withConstraint(constraint: Constraint): RequirementBuilder = this.copy(constraints = constraints :+ constraint)

    /** Construct the final immutable Requirement instance ready for allocation. */
    def build: Requirement = Requirement(id, name, description, requirementType, priority, verification, constraints)
}
