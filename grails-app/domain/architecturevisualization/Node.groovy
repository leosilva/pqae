package architecturevisualization


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
	Boolean isRootNode = false
	Set addedNodes = new HashSet()
	/*
	 * Atributos preenchidos após a leitura do arquivo resultante do PerfMiner.
	 * Só terão esses atributos preenchidos os nós que tiveram alguma variação no desempenho. 
	 */
	BigDecimal previousExecutionTime
	BigDecimal nextExecutionTime
	Integer qtdExecutedPreviousVersion
	Integer qtdExecutedNextVersion

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
		'nextExecutionTime',
		'qtdExecutedPreviousVersion',
		'qtdExecutedNextVersion']
	
}