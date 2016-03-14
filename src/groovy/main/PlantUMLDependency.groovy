package main

import net.sourceforge.plantumldependency.cli.main.program.PlantUMLDependencyProgram
import net.sourceforge.plantumldependency.commoncli.command.CommandLine
import net.sourceforge.plantumldependency.commoncli.command.impl.CommandLineImpl
import net.sourceforge.plantumldependency.commoncli.program.JavaProgram
import net.sourceforge.plantumldependency.commoncli.program.execution.JavaProgramExecution

class PlantUMLDependency {
	
	def static runPlantUMLDependency() {
		String[] arguments = ["-o",
				"teste.txt",
				"-b",
				"/Users/leosilva/Documents/Estudo/Mestrado/Dissertacao/Softwares_Exemplo/PagSeguro_Java/source/pagseguro-api/src/br/com/uol/pagseguro/domain/",
				"-dt",
				"abstract_classes,classes,enums,extensions,implementations,imports,interfaces"
			]
		
		// Creates the PlantUML Dependency arguments as they would be written in the command line
		CommandLine commandLineArguments = new CommandLineImpl(arguments)

		// Creates the PlantUML Dependency program instance
		final JavaProgram plantumlDependencyProgram = new PlantUMLDependencyProgram();

		// Get the PlantUML Dependency program execution instance following the command line arguments, ready to be executed
		final JavaProgramExecution plantumlDependencyProgramExecution = plantumlDependencyProgram.parseCommandLine(commandLineArguments);

		// Executes the PlantUML Dependency program
		plantumlDependencyProgramExecution.execute();
	}

}
