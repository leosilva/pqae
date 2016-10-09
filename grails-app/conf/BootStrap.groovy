import grails.converters.JSON
import architecturevisualization.Node;
import architecturevisualization.Project
import architecturevisualization.RepositoryType
import architecturevisualization.Scenario;

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
		
    }
    def destroy = {
    }
}
