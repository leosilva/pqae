package architecturevisualization

class Scenario {

	Date date
	String name
	Long threadId
	Execution execution
	Node node

	static hasMany = [nodeScenarios: NodeScenario,
	                  scenarioContexts: ScenarioContext]
	static belongsTo = [Execution, Node]

	static mapping = {
		datasources(["DEFAULT", "msrNextVersion"])
		id generator: "assigned"
		node column: "root_id"
		execution column: "execution_id"
		version false
	}

	static constraints = {
		date nullable: true
		name nullable: true
		threadId nullable: true
	}
}
