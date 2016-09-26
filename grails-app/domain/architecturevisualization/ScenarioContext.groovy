package architecturevisualization

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ScenarioContext implements Serializable {

	Long id
	String key
	String value
	Scenario scenario

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append id
		builder.append key
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append id, other.id
		builder.append key, other.key
		builder.isEquals()
	}

	static belongsTo = [Scenario]

	static mapping = {
		datasources(["DEFAULT", "msrNextVersion"])
		id composite: ["id", "key"]
		version false
	}

	static constraints = {
		value nullable: true
	}
}
