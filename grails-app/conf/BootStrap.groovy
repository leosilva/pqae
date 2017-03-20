import grails.converters.JSON
import architecturevisualization.AnalyzedSystem
import architecturevisualization.Node

class BootStrap {

    def init = { servletContext ->
		
//		15.times {
//			def p = new Project(name: "Project ${it + 1}", repositoryUrl: "project${it + 1}@github.com", repositoryType: RepositoryType.GIT)
//			p.save(flush: true)
//		}
		
		JSON.registerObjectMarshaller(Node) {
			def returnArray = [:]
			returnArray['id'] = it.id
			returnArray['member'] = it.member
			returnArray['time'] = it.time
			returnArray['node'] = ["id" : it?.node?.id]
			returnArray['deviation'] = it.deviation
			returnArray['timeVariation'] = it.timeVariation
			returnArray['timeVariationSignal'] = it.timeVariationSignal
			returnArray['hasDeviation'] = it.hasDeviation
			returnArray['isGroupedNode'] = it.isGroupedNode
			returnArray['isAddedNode'] = it.isAddedNode
			returnArray['isRootNode'] = it.isRootNode
			returnArray['previousExecutionTime'] = it.previousExecutionTime
			returnArray['previousExecutionRealTime'] = it.previousExecutionRealTime
			returnArray['nextExecutionTime'] = it.nextExecutionTime
			returnArray['nextExecutionRealTime'] = it.nextExecutionRealTime
			returnArray['qtdExecutedPreviousVersion'] = it.qtdExecutedPreviousVersion
			returnArray['qtdExecutedNextVersion'] = it.qtdExecutedNextVersion
			returnArray['loopTimes'] = it.loopTimes
			returnArray['commits'] = it.commits.collect { c ->
				["commitHash" : c.commitHash]
			}
			returnArray['nodes'] = it.nodes.collect { n ->
				["id" : n.id]
			}
			returnArray['addedNodes'] = it.addedNodes.collect { n ->
				[
					"id" : n.id,
					"member" : n.member
				]
			}
			return returnArray
		}
		
		JSON.registerObjectMarshaller(AnalyzedSystem) {
			def returnArray = [:]
			returnArray['id'] = it.id
			returnArray['systemName'] = it.systemName
			returnArray['previousVersion'] = it.previousVersion
			returnArray['nextVersion'] = it.nextVersion
			returnArray['analyzedSystemStatus'] = it.analyzedSystemStatus
			returnArray['analyzedScenarios'] = it.analyzedScenarios.collect { ans ->
				[
					"id" : ans.id,
					"name" : ans.name,
					"previousTime" : ans.previousTime,
					"nextTime" : ans.nextTime,
					"totalNodes" : ans.totalNodes,
					"qtdDeviationNodes" : ans.qtdDeviationNodes,
					"qtdOptimizedNodes" : ans.qtdOptimizedNodes,
					"qtdDegradedNodes" : ans.qtdDegradedNodes,
					"qtdAddedNodes" : ans.qtdAddedNodes, 
					"qtdRemovedNodes" : ans.qtdRemovedNodes,
					"qtdShowingNodes" : ans.qtdShowingNodes,
					"date" : ans.date,
					"analysisDuration" : ans.analysisDuration,
					"isDegraded" : ans.isDegraded
				]
			}
			return returnArray
		}
		
    }
    def destroy = {
    }
}
