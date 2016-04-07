package architecturevisualization

enum RepositoryType implements org.springframework.context.MessageSourceResolvable {
	
	GIT,
	SVN
	
	public Object[] getArguments() { [] as Object[] }
	
	public String[] getCodes() { ["${getClass().name}.${name()}"] as String[] }

	public String getDefaultMessage() { name() }
	
	public String getFullName() { "${getClass().name}.${name()}" }
}