package architecturevisualization

import domain.Commit


class Node {

	// persistent fields
	String exception
	BigInteger time
	Boolean constructor
	String member
	Long realTime
	Node node
	
	// transient fields
	String deviation
	String timeVariationSignal
	BigDecimal timeVariation
	Boolean hasDeviation = false
	Boolean isGroupedNode = false
	Boolean isAddedNode = false
	Boolean isRemovedNode = false
	Boolean isRootNode = false
	Set addedNodes = new HashSet()
	Set removedNodes = new HashSet()
	List<Commit> commits = new ArrayList<Commit>()
	/*
	 * Atributos preenchidos após a leitura do arquivo resultante do PerfMiner.
	 * Só terão esses atributos preenchidos os nós que tiveram alguma variação no desempenho. 
	 */
	BigDecimal previousExecutionTime
	BigDecimal previousExecutionRealTime
	BigDecimal nextExecutionTime
	BigDecimal nextExecutionRealTime
	Integer qtdExecutedPreviousVersion
	Integer qtdExecutedNextVersion
	Integer loopTimes = 1

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
		node lazy: false
	}

	static constraints = {
		exception nullable: true
		time nullable: true
		constructor nullable: true
		member nullable: true
		realTime nullable: true
		deviation bindable : true
	}
	
	static transients = ['deviation',
		'timeVariation',
		'timeVariationSignal',
		'hasDeviation',
		'addedNodes',
		'isGroupedNode',
		'isAddedNode',
		'isRootNode',
		'previousExecutionTime',
		'previousExecutionRealTime',
		'nextExecutionTime',
		'nextExecutionRealTime',
		'qtdExecutedPreviousVersion',
		'qtdExecutedNextVersion',
		'loopTimes',
		'commits',
		'isRemovedNode',
		'removedNodes']
	
}