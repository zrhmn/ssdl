package io.github.zrhmn.ssdl.core

/** INCOSE Constraint - limitation on system design space and solution approaches.
  *
  * Constraints represent boundaries and limitations that restrict the design space, derived from physical laws,
  * regulations, stakeholder preferences, or technology limitations. They are essential for trade study boundaries and
  * design optimization.
  *
  * This implementation follows INCOSE trade study methodology and supports constraint satisfaction problem (CSP)
  * formulation common in functional programming and formal methods approaches.
  *
  * Key principles:
  *   - Typed constraint classification for automated analysis
  *   - Value specification with units for quantitative constraints
  *   - Unique identification for traceability to sources
  *   - Integration with requirements for complete design specification
  *
  * The functional design enables constraint propagation algorithms and automated feasibility analysis through pure
  * functions and immutable data.
  *
  * @param constraintType
  *   Classification for analysis workflows and optimization
  * @param value
  *   Quantitative or qualitative constraint specification with units
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 7.3 "Trade Studies"
  * @see
  *   SysML v1.6 Constraint Blocks and Parametric Diagrams
  */
case class Constraint(
    id: ElementId,
    name: String,
    description: String,
    constraintType: ConstraintType,
    value: PropertyValue,
)

/** Constraint classification taxonomy for systems engineering analysis.
  *
  * Constraint types enable systematic organization and automated analysis of design limitations. Each type typically
  * corresponds to specific engineering disciplines and analysis workflows.
  *
  * The enumeration supports constraint satisfaction algorithms and multi-objective optimization common in functional
  * programming approaches to system design and trade study automation.
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Appendix C "Design Constraints"
  */
enum ConstraintType {

    /** Mass and weight constraints including payload, structural, and total system mass. Critical for aerospace
      * systems, mobile systems, and performance optimization. Examples: "Total mass < 2kg", "Payload mass budget =
      * 200g", "Center of gravity within envelope"
      */
    case Mass

    /** Electrical power constraints including consumption, generation, and distribution. Essential for energy budgeting
      * and electrical system sizing. Examples: "Power consumption < 50W", "Battery capacity > 5000mAh", "Peak current <
      * 20A"
      */
    case Power

    /** Economic constraints including development cost, manufacturing cost, and lifecycle cost. Important for
      * commercial viability and resource allocation decisions. Examples: "Unit cost < $500", "Development budget =
      * $50K", "Manufacturing volume > 1000 units"
      */
    case Cost

    /** Physical size and dimensional constraints including volume, area, and geometric limits. Critical for packaging,
      * integration, and operational environment compatibility. Examples: "Length < 1.5m", "Wing span < 2m", "Storage
      * volume < 0.1m³"
      */
    case Size

    /** Thermal constraints including operating temperature, storage temperature, and thermal dissipation. Essential for
      * reliability, component selection, and thermal management design. Examples: "Operating temp: -20°C to +60°C",
      * "Junction temp < 85°C", "Thermal dissipation > 10W"
      */
    case Temperature

    /** Regulatory and standards compliance constraints including certification, legal, and safety standards. Critical
      * for market access, operational approval, and liability management. Examples: "FAA Part 107 compliance", "CE
      * marking required", "FCC emissions compliance"
      */
    case Regulatory
}
