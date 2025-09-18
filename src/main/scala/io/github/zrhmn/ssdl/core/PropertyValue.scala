package io.github.zrhmn.ssdl.core

/** Type-safe property value representation supporting parametric modeling and constraint specification.
  *
  * PropertyValue provides a typed approach to component characteristics and constraint values, following SysML
  * parametric modeling principles and functional programming type safety. This enables automated analysis, unit
  * checking, and value validation.
  *
  * The algebraic data type (ADT) design supports:
  *   - Compile-time type safety for property operations
  *   - Exhaustive pattern matching for analysis algorithms
  *   - Compositional property modeling through nested structures
  *   - Unit-aware calculations for engineering analysis
  *   - Serialization/deserialization for persistence and exchange
  *
  * This approach is inspired by the Cats/Typelevel ecosystem's emphasis on type safety and pure functional programming
  * for domain modeling.
  *
  * @see
  *   SysML v1.6 Parametric Diagrams and Value Types
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 4.5 "Parametric Modeling"
  */
enum PropertyValue {

    /** String-valued properties for textual specifications and identifiers.
      *
      * Used for qualitative properties, identifiers, enumerations as strings, and textual specifications that don't
      * require numerical analysis.
      *
      * Examples: material types, operational modes, part numbers, configuration strings
      */
    case StringValue(value: String)

    /** Numerical properties with optional engineering units for quantitative analysis.
      *
      * Supports dimensional analysis, unit checking, and mathematical operations. Units follow engineering conventions
      * (SI preferred) and enable automated consistency checking across system models.
      *
      * The Option[String] for units allows unitless properties (ratios, counts) while supporting full dimensional
      * analysis where needed.
      *
      * Examples: mass (kg), voltage (V), frequency (Hz), efficiency (unitless ratio)
      *
      * @param value
      *   Numerical quantity as Double for engineering precision
      * @param unit
      *   Optional unit string following engineering conventions (SI preferred)
      */
    case NumberValue(value: Double, unit: Option[String] = None)

    /** Boolean properties for binary characteristics and flags.
      *
      * Used for yes/no decisions, feature flags, operational states, and logical conditions in constraint expressions.
      *
      * Examples: redundancy available, safety critical, field replaceable, weatherproof
      */
    case BooleanValue(value: Boolean)

    /** Composite properties supporting nested and collection-based modeling.
      *
      * Enables hierarchical property modeling, arrays of measurements, and complex data structures. Supports recursive
      * composition for arbitrarily complex property specifications.
      *
      * Examples: coordinate arrays, measurement series, nested configuration objects, property matrices for parametric
      * studies
      */
    case ListValue(values: List[PropertyValue])
}
