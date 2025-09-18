package io.github.zrhmn.ssdl.analysis

import io.github.zrhmn.ssdl.core.*

/** Requirements analysis and traceability utilities.
  *
  * Provides automated analysis capabilities for requirements management, supporting INCOSE requirements engineering
  * processes:
  *   - Requirements traceability analysis
  *   - Coverage analysis for verification planning
  *   - Requirements conflict detection
  *   - Completeness and consistency checking
  *
  * These tools enable systematic requirements management and support verification and validation planning activities.
  */
object Requirements {

    /** Analyze requirements coverage across system elements.
      *
      * Identifies which system elements have requirements allocated and which may be missing requirements coverage.
      *
      * @param system
      *   System to analyze
      * @return
      *   Coverage analysis results
      */
    def analyzeCoverage(system: System): RequirementsCoverage = {
        val allElements                 = collectAllElements(system)
        val elementsWithRequirements    = allElements.filter(hasRequirements)
        val elementsWithoutRequirements = allElements.filterNot(hasRequirements)

        RequirementsCoverage(
          totalElements = allElements.length,
          elementsWithRequirements = elementsWithRequirements.length,
          elementsWithoutRequirements = elementsWithoutRequirements.map(_.id),
          coveragePercentage = if (allElements.nonEmpty)
              (elementsWithRequirements.length.toDouble / allElements.length * 100)
          else 0.0,
        )
    }

    /** Build requirements traceability matrix.
      *
      * Creates a mapping between requirements and system elements, supporting impact analysis and verification
      * planning.
      *
      * @param system
      *   System to analyze
      * @return
      *   Traceability matrix
      */
    def buildTraceabilityMatrix(system: System): TraceabilityMatrix = {
        val allElements = collectAllElements(system)

        val traces = for {
            element <- allElements
            req     <- getRequirementsFor(element)
        } yield TraceabilityLink(req.id, element.id, element.getClass.getSimpleName)

        TraceabilityMatrix(traces)
    }

    /** Detect potential requirements conflicts.
      *
      * Identifies requirements that may be contradictory or have conflicting constraints.
      *
      * @param system
      *   System to analyze
      * @return
      *   List of potential conflicts
      */
    def detectConflicts(system: System): List[RequirementConflict] = {
        val allRequirements = collectAllRequirements(system)

        // Simple conflict detection based on constraint types
        val conflicts = for {
            req1 <- allRequirements
            req2 <- allRequirements
            if req1.id != req2.id
            conflict <- findConstraintConflicts(req1, req2)
        } yield conflict

        conflicts.distinct
    }

    // Helper methods and case classes

    private def collectAllElements(system: System): List[SystemElement] = {
        val subsystemElements = system.subsystems.flatMap(collectAllElements)
        system :: (system.components ++ system.interfaces ++ subsystemElements)
    }

    private def hasRequirements(element: SystemElement): Boolean = element match {
        case s: System    => s.requirements.nonEmpty
        case c: Component => c.requirements.nonEmpty
        case i: Interface => i.requirements.nonEmpty
    }

    private def getRequirementsFor(element: SystemElement): List[Requirement] = element match {
        case s: System    => s.requirements
        case c: Component => c.requirements
        case i: Interface => i.requirements
    }

    private def collectAllRequirements(system: System): List[Requirement] = {
        val systemReqs    = system.requirements
        val subsystemReqs = system.subsystems.flatMap(collectAllRequirements)
        val componentReqs = system.components.flatMap(_.requirements)
        val interfaceReqs = system.interfaces.flatMap(_.requirements)

        systemReqs ++ subsystemReqs ++ componentReqs ++ interfaceReqs
    }

    private def findConstraintConflicts(req1: Requirement, req2: Requirement): Option[RequirementConflict] = {
        // Simplified conflict detection - could be enhanced with more sophisticated logic
        if (
          req1.priority == Priority.Critical && req2.priority == Priority.Critical &&
          req1.requirementType == req2.requirementType
        ) {
            Some(RequirementConflict(req1.id, req2.id, "Multiple critical requirements of same type"))
        } else None
    }

    case class RequirementsCoverage(
        totalElements: Int,
        elementsWithRequirements: Int,
        elementsWithoutRequirements: List[ElementId],
        coveragePercentage: Double,
    )

    case class TraceabilityLink(
        requirementId: ElementId,
        elementId: ElementId,
        elementType: String,
    )

    case class TraceabilityMatrix(links: List[TraceabilityLink]) {
        def getElementsFor(requirementId: ElementId): List[TraceabilityLink] =
            links.filter(_.requirementId == requirementId)

        def getRequirementsFor(elementId: ElementId): List[TraceabilityLink] =
            links.filter(_.elementId == elementId)
    }

    case class RequirementConflict(
        requirement1: ElementId,
        requirement2: ElementId,
        description: String,
    )
}
