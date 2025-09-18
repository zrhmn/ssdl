package io.github.zrhmn.ssdl.core

/** Interface classification based on INCOSE systems engineering and SysML modeling principles.
  *
  * Interface types provide a taxonomy for organizing and analyzing system interactions. This classification supports:
  *   - Engineering discipline assignment (mechanical, electrical, software teams)
  *   - Analysis workflow routing (thermal analysis, signal integrity, data flow)
  *   - Design rule checking and constraint validation
  *   - Interface management and change impact analysis
  *
  * The enumeration follows functional programming ADT patterns, providing exhaustive pattern matching and compile-time
  * safety for interface analysis algorithms. Each case represents a fundamental physics domain or engineering
  * discipline.
  *
  * For complex systems (like RC aircraft), interfaces often span multiple types requiring composite modeling - this can
  * be achieved through interface aggregation patterns in the broader system model.
  *
  * @see
  *   INCOSE Systems Engineering Handbook v4, Section 4.4.2 "Interface Taxonomy"
  * @see
  *   SysML v1.6 Interface Blocks and Flow Properties
  * @see
  *   ISO/IEC/IEEE 42010:2011 Architecture description standard
  */
enum InterfaceType {

    /** Mechanical connections and structural interfaces.
      *
      * Covers force/moment transmission, structural load paths, mechanical fastening, and kinematic constraints.
      * Critical for structural integrity analysis and mechanical design validation.
      *
      * Examples: mounting brackets, gear meshes, structural joints, actuator attachments
      */
    case Physical // mechanical connections

    /** Electrical power and signal interfaces.
      *
      * Encompasses power distribution, electrical grounding, signal transmission, and electromagnetic compatibility.
      * Essential for power budgeting and electrical system architecture.
      *
      * Examples: power buses, control signals, sensor interfaces, communication lines
      */
    case Electrical // power, signals

    /** Information and data flow interfaces.
      *
      * Covers digital communication protocols, data structures, message formats, and information exchange patterns.
      * Critical for software architecture and system integration.
      *
      * Examples: CAN bus messages, telemetry data, configuration parameters, commands
      */
    case Data // information flow

    /** Command, control, and coordination interfaces.
      *
      * Represents control loops, command hierarchies, operational modes, and system coordination protocols. Essential
      * for behavioral modeling and control system design.
      *
      * Examples: flight control commands, mode switching, safety interlocks, supervisory control
      */
    case Control // command/control signals

    /** Thermal energy transfer and management interfaces.
      *
      * Covers heat conduction, convection, radiation, and thermal management. Critical for thermal analysis and
      * component reliability assessment.
      *
      * Examples: heat sinks, thermal interfaces, cooling airflow, component heat generation
      */
    case Thermal // heat transfer

    /** Optical and electromagnetic radiation interfaces.
      *
      * Encompasses light transmission, optical sensing, electromagnetic radiation, and photonic communication.
      * Important for sensor systems and electromagnetic compatibility analysis.
      *
      * Examples: camera interfaces, LED indicators, optical encoders, RF communication
      */
    case Optical // light/optical signals
}
