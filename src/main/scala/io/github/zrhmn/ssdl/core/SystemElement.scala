package io.github.zrhmn.ssdl.core

/** INCOSE System Element - fundamental building block of any engineered system.
  *
  * Based on INCOSE SE Handbook Section 4.1: "A system element is a discrete part of a system that can be implemented to
  * fulfill specified requirements." This trait represents the fundamental abstraction that all system constituents must
  * implement.
  *
  * The design follows functional programming principles with immutable data and algebraic data types (ADTs) for safe
  * composition and transformation.
  *
  * Key INCOSE principles captured:
  *   - Unique identification for traceability
  *   - Clear naming for stakeholder communication
  *   - Optional description for documentation and rationale
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 4.1 "System Elements"
  * @see
  *   ISO/IEC/IEEE 15288:2015 Systems and software engineering
  */
sealed trait SystemElement {
    def id: ElementId
    def name: String
    def description: Option[String]
}

/** INCOSE System - collection of interacting elements that achieve stakeholder objectives.
  *
  * Represents a complete system as defined by INCOSE: "A combination of interacting elements organized to achieve one
  * or more stated purposes." Systems can be hierarchically decomposed into subsystems following the systems-of-systems
  * approach.
  *
  * This implementation follows SysML Block Definition Diagrams (BDD) concepts:
  *   - Hierarchical composition through subsystems
  *   - Part composition through components
  *   - Interface modeling for element interactions
  *   - Requirements allocation and traceability
  *
  * The functional design uses immutable collections and supports compositional patterns typical in the Cats/Typelevel
  * ecosystem.
  *
  * @param subsystems
  *   Hierarchical decomposition supporting systems-of-systems approaches
  * @param components
  *   Atomic elements that cannot be further decomposed in current context
  * @param interfaces
  *   Explicit modeling of element interactions (SysML Internal Block Diagrams)
  * @param requirements
  *   Allocated requirements for verification and validation
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 4.2 "System Architecture"
  * @see
  *   SysML v1.6 Block Definition Diagrams
  */
case class System(
    id: ElementId,
    name: String,
    description: Option[String] = None,
    subsystems: List[System] = Nil,
    components: List[Component] = Nil,
    interfaces: List[Interface] = Nil,
    requirements: List[Requirement] = Nil,
) extends SystemElement

/** INCOSE Component - atomic system element that performs specific functions.
  *
  * Represents the lowest level of decomposition in the current system view. Components are the "leaf nodes" in system
  * hierarchy and contain properties that define their operational characteristics.
  *
  * This maps to SysML Part Properties and UML Components, providing:
  *   - Property-based specification following parametric modeling
  *   - Requirements allocation for component-level verification
  *   - Type-safe property modeling using algebraic data types
  *
  * The functional approach enables safe composition and transformation through immutable data structures and lens-based
  * property access patterns.
  *
  * @param properties
  *   Typed characteristics defining component behavior/constraints
  * @param requirements
  *   Component-level requirements for verification activities
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 4.3 "Component Architecture"
  * @see
  *   SysML v1.6 Parametric Diagrams
  */
case class Component(
    id: ElementId,
    name: String,
    description: Option[String] = None,
    properties: Map[String, PropertyValue] = Map.empty,
    requirements: List[Requirement] = Nil,
) extends SystemElement

/** INCOSE Interface - defines interaction between system elements.
  *
  * Interfaces are first-class citizens in systems engineering, explicitly modeling the "connections" between elements.
  * This follows INCOSE interface management principles and SysML Interface Blocks for precise interaction
  * specification.
  *
  * Key principles implemented:
  *   - Explicit source/target modeling for directed interactions
  *   - Type classification for interface management and analysis
  *   - Requirements allocation for interface verification
  *   - Bidirectional modeling support through interface pairs
  *
  * The typed interface approach enables static analysis of system connectivity and supports formal verification
  * techniques from the functional programming world.
  *
  * @param source
  *   Element providing the interface capability
  * @param target
  *   Element consuming the interface capability
  * @param interfaceType
  *   Classification for engineering analysis and management
  * @param requirements
  *   Interface-specific requirements (protocols, performance, etc.)
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 4.4 "Interface Management"
  * @see
  *   SysML v1.6 Interface Blocks and Item Flows
  */
case class Interface(
    id: ElementId,
    name: String,
    description: Option[String] = None,
    source: ElementId,
    target: ElementId,
    interfaceType: InterfaceType,
    requirements: List[Requirement] = Nil,
) extends SystemElement
