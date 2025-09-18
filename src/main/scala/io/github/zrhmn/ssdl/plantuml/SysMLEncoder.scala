package io.github.zrhmn.ssdl.plantuml

import io.github.zrhmn.ssdl.core.*

/** SysML diagram generator using PlantUML syntax.
  *
  * Generates PlantUML code for various SysML diagram types following OMG SysML specification. This enables automated
  * diagram generation from SSDL system models, supporting:
  *   - Block Definition Diagrams (BDD) for system structure
  *   - Internal Block Diagrams (IBD) for component interactions
  *   - Requirements Diagrams for traceability visualization
  *   - Parametric Diagrams for constraint relationships
  *
  * The generated PlantUML can be rendered to various formats (PNG, SVG, PDF) using PlantUML processors, enabling
  * integration into documentation workflows.
  *
  * @see
  *   PlantUML SysML documentation: https://plantuml.com/sysml
  * @see
  *   OMG SysML v1.6 specification
  */
object SysMLEncoder {

    /** Generate a Block Definition Diagram (BDD) showing system structure and composition.
      *
      * Creates a hierarchical view of systems, subsystems, and components with their relationships and properties. This
      * is the primary structural view in SysML.
      *
      * @param system
      *   Root system to diagram
      * @return
      *   PlantUML code for BDD
      */
    def generateBDD(system: System): String = {
        val sb          = StringBuilder()
        val diagramName = s"BDD_${sanitizeId(system.id.value)}"
        sb.append(s"@startuml $diagramName\n")
        val _ = sb.append("title Block Definition Diagram - ").append(system.name).append("\n\n")

        // Generate system blocks
        generateSystemBlock(system, sb, 0)

        // Generate relationships
        generateCompositionRelationships(system, sb)

        sb.append("\n@enduml")
        sb.toString
    }

    /** Generate an Internal Block Diagram (IBD) showing component interactions and interfaces.
      *
      * Shows the internal structure of a system with components and their interfaces, providing a detailed view of
      * system connectivity and data/energy flows.
      *
      * @param system
      *   System to show internal structure for
      * @return
      *   PlantUML code for IBD
      */
    def generateIBD(system: System): String = {
        val sb          = StringBuilder()
        val diagramName = s"IBD_${sanitizeId(system.id.value)}"
        sb.append(s"@startuml $diagramName\n")
        val _ = sb.append("title Internal Block Diagram - ").append(system.name).append("\n\n")

        // Generate component blocks
        system.components.foreach { component =>
            sb.append(s"component \"${component.name}\" as ${sanitizeId(component.id.value)} {\n")
            component.properties.foreach { case (key, value) =>
                sb.append(s"  note : $key = ${formatPropertyValue(value)}\n")
            }
            sb.append("}\n\n")
        }

        // Generate interfaces
        system.interfaces.foreach { interface =>
            val sourceId      = sanitizeId(interface.source.value)
            val targetId      = sanitizeId(interface.target.value)
            val interfaceType = interface.interfaceType.toString.toLowerCase

            sb.append(s"$sourceId --> $targetId : ${interface.name}\\n<<$interfaceType>>\n")
        }

        sb.append("\n@enduml")
        sb.toString
    }

    /** Generate a Requirements Diagram showing requirements hierarchy and traceability.
      *
      * Visualizes requirements structure, derivation relationships, and allocation to system elements, supporting
      * requirements management and traceability analysis.
      *
      * @param system
      *   System with requirements to diagram
      * @return
      *   PlantUML code for Requirements Diagram
      */
    def generateRequirementsDiagram(system: System): String = {
        val sb          = StringBuilder()
        val diagramName = s"REQ_${sanitizeId(system.id.value)}"
        sb.append(s"@startuml $diagramName\n")
        val _ = sb.append("title Requirements Diagram - ").append(system.name).append("\n\n")

        // Generate requirement blocks
        val allRequirements = collectAllRequirements(system)
        allRequirements.foreach { req =>
            sb.append(s"rectangle \"${req.name}\" as ${sanitizeId(req.id.value)} {\n")
            sb.append(s"  note : id: ${req.id.value}\n")
            sb.append(s"  note : text: ${req.description}\n")
            sb.append(s"  note : type: ${req.requirementType}\n")
            sb.append(s"  note : priority: ${req.priority}\n")
            sb.append(s"  note : verification: ${req.verification}\n")
            sb.append("}\n\n")
        }

        // Generate derivation relationships
        allRequirements.foreach { req =>
            req.derivedFrom.foreach { parentId =>
                sb.append(s"${sanitizeId(parentId.value)} --> ${sanitizeId(req.id.value)} : derives\n")
            }
        }

        sb.append("\n@enduml")
        sb.toString
    }

    /** Generate a complete system overview with multiple diagram views.
      *
      * Creates a comprehensive PlantUML document with BDD, IBD, and Requirements diagrams for complete system
      * documentation.
      *
      * @param system
      *   System to document
      * @return
      *   PlantUML code with multiple diagrams
      */
    def generateCompleteSystemDoc(system: System): String = {
        val sb = StringBuilder()

        val _ = sb.append(generateBDD(system)).append("\n\n")
        val _ = sb.append(generateIBD(system)).append("\n\n")
        val _ = sb.append(generateRequirementsDiagram(system)).append("\n\n")

        sb.toString
    }

    // Helper methods

    private def generateSystemBlock(system: System, sb: StringBuilder, indent: Int): Unit = {
        val indentStr = "  " * indent

        // Generate system as a package with proper stereotype
        sb.append(s"${indentStr}package \"${system.name}\" as ${sanitizeId(system.id.value)} <<system>> {\n")

        // Add description as a note if present
        system.description.foreach { desc =>
            sb.append(s"$indentStr  note \"$desc\" as N_${sanitizeId(system.id.value)}\n")
        }

        // Add subsystems
        system.subsystems.foreach { subsystem =>
            generateSystemBlock(subsystem, sb, indent + 1)
        }

        // Add components as blocks
        system.components.foreach { component =>
            sb.append(s"$indentStr  class \"${component.name}\" as ${sanitizeId(component.id.value)} <<block>>\n")
            // Add component properties as notes
            if (component.properties.nonEmpty) {
                sb.append(s"$indentStr  note right of ${sanitizeId(component.id.value)} : \\l")
                component.properties.foreach { case (key, value) =>
                    sb.append(s"$key = ${formatPropertyValue(value)}\\l")
                }
                sb.append("\n")
            }
        }

        sb.append(s"$indentStr}\n\n")
    }

    private def generateCompositionRelationships(system: System, sb: StringBuilder): Unit = {
        system.subsystems.foreach { subsystem =>
            sb.append(s"${sanitizeId(system.id.value)} *-- ${sanitizeId(subsystem.id.value)}\n")
            generateCompositionRelationships(subsystem, sb)
        }

        system.components.foreach { component =>
            sb.append(s"${sanitizeId(system.id.value)} *-- ${sanitizeId(component.id.value)}\n")
        }
    }

    private def collectAllRequirements(system: System): List[Requirement] = {
        val systemReqs    = system.requirements
        val subsystemReqs = system.subsystems.flatMap(collectAllRequirements)
        val componentReqs = system.components.flatMap(_.requirements)
        val interfaceReqs = system.interfaces.flatMap(_.requirements)

        systemReqs ++ subsystemReqs ++ componentReqs ++ interfaceReqs
    }

    private def sanitizeId(id: String): String = {
        id.replaceAll("[^a-zA-Z0-9_]", "_")
    }

    private def formatPropertyValue(value: PropertyValue): String = value match {
        case PropertyValue.StringValue(v)             => s"\"$v\""
        case PropertyValue.NumberValue(v, Some(unit)) => s"$v $unit"
        case PropertyValue.NumberValue(v, None)       => v.toString
        case PropertyValue.BooleanValue(v)            => v.toString
        case PropertyValue.ListValue(values)          => values.map(formatPropertyValue).mkString("[", ", ", "]")
    }
}
