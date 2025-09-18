package io.github.zrhmn.ssdl.analysis

import io.github.zrhmn.ssdl.core.*

/** Interface analysis and connectivity verification utilities.
  *
  * Provides automated analysis of system interfaces and connectivity, supporting INCOSE interface management
  * principles:
  *   - Interface completeness checking
  *   - Connectivity analysis and validation
  *   - Interface type consistency verification
  *   - Data flow path analysis
  *
  * These tools enable systematic interface management and support system integration planning and verification.
  */
object Interfaces {

    /** Analyze interface connectivity within a system.
      *
      * Verifies that all interface connections reference valid system elements and identifies potential connectivity
      * issues.
      *
      * @param system
      *   System to analyze
      * @return
      *   Connectivity analysis results
      */
    def analyzeConnectivity(system: System): ConnectivityAnalysis = {
        val allElementIds = collectAllElementIds(system)
        val interfaces    = system.interfaces

        val validInterfaces = interfaces.filter { interface =>
            allElementIds.contains(interface.source) && allElementIds.contains(interface.target)
        }

        val invalidInterfaces = interfaces.filterNot { interface =>
            allElementIds.contains(interface.source) && allElementIds.contains(interface.target)
        }

        val orphanedElements = allElementIds.filterNot { elementId =>
            interfaces.exists(i => i.source == elementId || i.target == elementId)
        }

        ConnectivityAnalysis(
          totalInterfaces = interfaces.length,
          validInterfaces = validInterfaces.length,
          invalidInterfaces = invalidInterfaces,
          orphanedElements = orphanedElements.toList,
          connectivityScore = if (interfaces.nonEmpty)
              (validInterfaces.length.toDouble / interfaces.length * 100)
          else 100.0,
        )
    }

    /** Build interface dependency graph for system integration analysis.
      *
      * Creates a directed graph representation of interface dependencies supporting integration sequencing and
      * dependency analysis.
      *
      * @param system
      *   System to analyze
      * @return
      *   Interface dependency graph
      */
    def buildDependencyGraph(system: System): InterfaceDependencyGraph = {
        val nodes = collectAllElementIds(system).map(DependencyNode.apply).toList
        val edges = system.interfaces.map { interface =>
            DependencyEdge(interface.source, interface.target, interface.interfaceType, interface.id)
        }

        InterfaceDependencyGraph(nodes, edges)
    }

    /** Analyze interface types distribution and consistency.
      *
      * Provides statistics on interface types used in the system and identifies potential design patterns or issues.
      *
      * @param system
      *   System to analyze
      * @return
      *   Interface type analysis
      */
    def analyzeInterfaceTypes(system: System): InterfaceTypeAnalysis = {
        val interfaces       = system.interfaces
        val typeDistribution = interfaces.groupBy(_.interfaceType).view.mapValues(_.length).toMap

        val mostCommonType  = typeDistribution.maxByOption(_._2).map(_._1)
        val leastCommonType = typeDistribution.minByOption(_._2).map(_._1)

        InterfaceTypeAnalysis(
          totalInterfaces = interfaces.length,
          typeDistribution = typeDistribution,
          mostCommonType = mostCommonType,
          leastCommonType = leastCommonType,
          diversityScore = typeDistribution.size.toDouble / InterfaceType.values.length * 100,
        )
    }

    // Helper methods and case classes

    private def collectAllElementIds(system: System): Set[ElementId] = {
        val systemIds    = Set(system.id)
        val subsystemIds = system.subsystems.flatMap(collectAllElementIds).toSet
        val componentIds = system.components.map(_.id).toSet
        val interfaceIds = system.interfaces.map(_.id).toSet

        systemIds ++ subsystemIds ++ componentIds ++ interfaceIds
    }

    case class ConnectivityAnalysis(
        totalInterfaces: Int,
        validInterfaces: Int,
        invalidInterfaces: List[Interface],
        orphanedElements: List[ElementId],
        connectivityScore: Double,
    )

    case class DependencyNode(elementId: ElementId)

    case class DependencyEdge(
        source: ElementId,
        target: ElementId,
        interfaceType: InterfaceType,
        interfaceId: ElementId,
    )

    case class InterfaceDependencyGraph(
        nodes: List[DependencyNode],
        edges: List[DependencyEdge],
    ) {
        def getIncomingEdges(elementId: ElementId): List[DependencyEdge] =
            edges.filter(_.target == elementId)

        def getOutgoingEdges(elementId: ElementId): List[DependencyEdge] =
            edges.filter(_.source == elementId)

        def findPaths(from: ElementId, to: ElementId): List[List[DependencyEdge]] = {
            // Simplified path finding - could be enhanced with more sophisticated algorithms
            val directEdges = edges.filter(e => e.source == from && e.target == to)
            directEdges.map(List(_))
        }
    }

    case class InterfaceTypeAnalysis(
        totalInterfaces: Int,
        typeDistribution: Map[InterfaceType, Int],
        mostCommonType: Option[InterfaceType],
        leastCommonType: Option[InterfaceType],
        diversityScore: Double,
    )
}
