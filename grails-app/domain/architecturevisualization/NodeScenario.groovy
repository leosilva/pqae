package architecturevisualization

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class NodeScenario implements Serializable {

	Scenario scenario
	Node node

	static belongsTo = [Node, Scenario]

	static mapping = {
		datasources(["DEFAULT", "msrNextVersion"])
		id composite: ["node", "scenario"]
		version false
	}
}
