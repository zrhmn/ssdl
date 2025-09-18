package io.github.zrhmn.ssdl.core

/** Unique identifier for system elements following INCOSE SE principles.
  *
  * In INCOSE systems engineering, every system element must be uniquely identifiable for:
  *   - Requirements traceability (ISO 15288)
  *   - Configuration management
  *   - Impact analysis and change propagation
  *   - Verification and validation activities
  *
  * This opaque type provides compile-time safety while maintaining string compatibility for serialization. The design
  * follows functional programming principles by being immutable and using type safety to prevent mixing different kinds
  * of identifiers.
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 4.1.2 "System Element Identification"
  */
opaque type ElementId = String

object ElementId {

    /** Create a new ElementId from a string identifier.
      *
      * In practice, these identifiers should follow a structured naming convention (e.g., hierarchical like
      * "SYS-001.SUB-002.COMP-003" or UUID-based).
      */
    def apply(id: String): ElementId = id

    /** Extension methods for ElementId following the newtype pattern.
      *
      * This provides safe access to the underlying string value while maintaining type safety boundaries. The extension
      * method pattern is preferred over implicit conversions for better compile-time guarantees.
      */
    extension (id: ElementId) {
        def value: String = id
    }
}
