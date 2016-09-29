package architecturevisualization

class Node {

	String exception
	BigInteger time
	Boolean constructor
	String member
	Long realTime
	Node node
	String deviation
	String timeVariationSignal
	Long timeVariation
	Long tempId

	static hasMany = [annotations: Annotation,
	                  nodeScenarios: NodeScenario,
	                  nodes: Node,
	                  scenarios: Scenario]

	static mapping = {
		datasources(["msrPreviousVersion", "msrNextVersion"])
		id generator: "assigned"
		node column: "parent_id"
		version false
		annotations lazy: true
		nodeScenarios lazy: true
		nodes lazy: true
		scenarios lazy: true
		node lazy: true
	}

	static constraints = {
		exception nullable: true
		time nullable: true
		constructor nullable: true
		member nullable: true
		realTime nullable: true
		deviation bindable : true
	}
	
	static transients = ['deviation', 'timeVariation', 'timeVariationSignal', 'tempId']
	
}
