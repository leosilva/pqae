package architecturevisualization

import grails.transaction.Transactional

import java.nio.file.Paths

import domain.BlamedMethod
import domain.BlamedScenario
import domain.FileBlamedSignificance

@Transactional
class PerfMinerIntegrationFilesService {

    /**
	 * Método que lê e interpreta o arquivo no formato <sistema>-<versão da análise>_pu_blamed_methods_of_<degraded ou optimized>_scenarios_significance_<data>.txt
	 * @return
	 */
	def readBlamedMethodsDegradedScenariosFile() {
		def f = Paths.get("repositories/jetty/9.2.6_to_9.3.0.M1/jetty_9.2.6_x_9.3.0.M1_pu_blamed_methods_of_degraded_scenarios_significance_2015-03-17_17h21min.txt")
		def fileBlamed = new FileBlamedSignificance(fileName: f.fileName, scenarios: [], methods: [], isDegradation: true)
		def lines = f.readLines()
		def (isQtdScenario, isQtdMethods, isScenarioMethodRelation) = [true, true, true]
		lines.eachWithIndex { line, index ->
			// le e cria os objetos para os cenarios no arquivo
			if (line.isNumber() && isQtdScenario) {
				fileBlamed.qtdAffectedScenarios = (line as Integer)
				isQtdScenario = false
				((index+1)..(index+fileBlamed.qtdAffectedScenarios)).each {
					def bs = new BlamedScenario(scenarioName: lines[it], methods: [])
					fileBlamed.scenarios << bs
				}
			} else if (line.isNumber() && isQtdMethods) { // le e cria os metodos para cada cenario contido no arquivo
				fileBlamed.qtdAffectedMethods = (line as Integer)
				isQtdMethods = false
				((index+1)..(index+fileBlamed.qtdAffectedMethods)).each {
					def signature = lines[it].split(";")[0]
					def bs = new BlamedMethod(methodSignature: signature)
					fileBlamed.methods << bs
				}
			} else if (line.isNumber() && isScenarioMethodRelation) { // le e associa os metodos para cada cenario contido no arquivo
				isScenarioMethodRelation = false
				((index+1)..(index+(line as Integer))).each {
					def splitted = lines[it].split(";")
					def scenario = fileBlamed.scenarios.find { it.scenarioName == splitted[0] }
					def method = fileBlamed.methods.find { it.methodSignature == splitted[1] }
					scenario.methods << method
				}
			}
		}
		fileBlamed
	}
	
}
