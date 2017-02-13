package architecturevisualization

enum AnalyzedSystemStatus implements org.springframework.context.MessageSourceResolvable {
	
	PENDING,
	COMPLETED,
	ERROR
	
	public Object[] getArguments() { [] as Object[] }
	
	public String[] getCodes() { ["${getClass().name}.${name()}"] as String[] }

	public String getDefaultMessage() { name() }
	
	public String getFullName() { "${getClass().name}.${name()}" }
}