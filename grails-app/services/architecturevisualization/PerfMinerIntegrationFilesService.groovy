package architecturevisualization

import grails.transaction.Transactional

import java.math.RoundingMode

import domain.BlamedMethod
import domain.BlamedScenario
import domain.Commit
import domain.FileBlamedSignificance

@Transactional
class PerfMinerIntegrationFilesService {

    /**
	 * Método que lê e interpreta o arquivo no formato <sistema>-<versão da análise>_pu_blamed_methods_of_<degraded ou optimized>_scenarios_significance_<data>.txt
	 * @return
	 */
	def readBlamedMethodsScenariosFile(systemName, fileDegradedScenarios, fileOptimizedScenarios) {
		def files = []
		File fds, fos
		
		if (!fileDegradedScenarios.isEmpty()) {
			fds = new File(fileDegradedScenarios?.originalFilename);
			fileDegradedScenarios?.transferTo(fds);
			files += readFile(fds)
			fds?.delete()
		}
		
		if (!fileOptimizedScenarios.isEmpty()) {
			fos = new File(fileOptimizedScenarios?.originalFilename);
			fileOptimizedScenarios?.transferTo(fos);
			files += readFile(fos)
			fos?.delete()
		}
		
		files
	}
	
	private def readFile(resultFile) {
		def fileBlamed = null
		if (resultFile) {
			fileBlamed = new FileBlamedSignificance(fileName: resultFile.name, scenarios: [], methods: [])
			def lines = resultFile.readLines()
			def isDegradation = resultFile.name.contains("degraded") ? true : false
			def (isQtdScenario, isQtdMethods, isScenarioMethodRelation) = [true, true, true]
			lines.eachWithIndex { line, index ->
				if (line.startsWith("#") && line.contains("Members") && line.contains("scenario")) {
					fileBlamed = readScenario(fileBlamed, lines, index, isDegradation)
				}
			}
		}
		fileBlamed
	}
	
	/**
	 * Método que faz a leitura do cenário e dos seus respectivos métodos.
	 * @param fileBlamed
	 * @param lines
	 * @param index
	 * @param isDegradation
	 * @return
	 */
	def readScenario(fileBlamed, lines, index, isDegradation) {
		def scenarioLine = lines[index+2]?.split(";")
		def blamedScenario = new BlamedScenario(
			scenarioName: scenarioLine[0],
			pValueTTest: (scenarioLine[1] as BigDecimal).setScale(2, RoundingMode.DOWN),
			pValueUTest: (scenarioLine[2] as BigDecimal).setScale(2, RoundingMode.DOWN),
			avgExecutionTimePreviousVersion: (scenarioLine[3] as BigDecimal).setScale(2, RoundingMode.DOWN),
			avgExecutionTimeNextVersion: (scenarioLine[4] as BigDecimal).setScale(2, RoundingMode.DOWN),
			qtdExecutedPreviousVersion: scenarioLine[5] as Integer,
			qtdExecutedNextVersion: scenarioLine[6] as Integer,
			executionTimeDifference: (scenarioLine[7] as BigDecimal).setScale(2, RoundingMode.DOWN),
			variation: scenarioLine[8] as Integer,
			modifiedMethods: [],
			addedMethods: [],
			removedMethods: [],
			isDegraded: isDegradation)
		
		// verifica os métodos com variação de desempenho
		index += 3
		def qtdMembersWithDeviation = lines[index] as Integer
		if (qtdMembersWithDeviation > 0) {
			index = index + 2
			blamedScenario = readMethods(blamedScenario, lines, index, qtdMembersWithDeviation, "modifiedMethods")
		}
		
		// verifica os métodos adicionados
		def increment = 0
		if (qtdMembersWithDeviation >= 2) {
			increment = qtdMembersWithDeviation + 1
		} else if (qtdMembersWithDeviation == 1) {
			increment = 2
		}

		def qtdCommitsModifiedMethods = (blamedScenario.modifiedMethods.size() > 0 ? blamedScenario.modifiedMethods.collect { it.commits.size() + 1 }.sum() : 0) as Integer
		
		index = index + increment + qtdCommitsModifiedMethods 
		def qtdAddedMethods = lines[index] as Integer
		if (qtdAddedMethods > 0) {
			index += 2
			blamedScenario = readMethods(blamedScenario, lines, index, qtdAddedMethods, "addedMethods")
		}
		
		def qtdCommitsAddedMethods = (blamedScenario.addedMethods.size() > 0 ? blamedScenario.addedMethods.collect { it.commits.size() + 1 }.sum() : 0)
		
		// verifica os métodos removidos
		index = index + qtdCommitsAddedMethods + (qtdAddedMethods ? qtdAddedMethods + 1 : 2)
		def qtdRemovedMethods = lines[index] as Integer
		if (qtdRemovedMethods > 0) {
			index += 2
			blamedScenario = readMethods(blamedScenario, lines, index, qtdRemovedMethods, "removedMethods")
		}
		
		fileBlamed.scenarios << blamedScenario
		fileBlamed
	}
	
	/**
	 * Método que cria um objeto do tipo BlamedMethod, tanto para métodos modificados, adicionados ou removidos.
	 * @param blamedScenario
	 * @param lines
	 * @param index
	 * @param qtdMembers
	 * @param methods
	 * @return
	 */
	def readMethods(blamedScenario, lines, index, qtdMembers, methods) {
		def startRange = index
		def variableIndex = index
		def manualCounter = 0
		((startRange)..<(startRange + qtdMembers)).each {
			manualCounter = variableIndex
			def methodLine = lines[manualCounter]?.split(";")
			manualCounter += 1
			def qtdCommits = lines[manualCounter] as Integer
			def blamedMethod = new BlamedMethod(methodSignature: methodLine[0])
			if (methods == "modifiedMethods") {
				blamedMethod.pValueTTest = (methodLine[1] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.pValueUTest = (methodLine[2] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.avgExecutionTimePreviousVersion = (methodLine[3] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.avgExecutionTimeNextVersion = (methodLine[4] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.qtdExecutedPreviousVersion = methodLine[5] as Integer
				blamedMethod.qtdExecutedNextVersion = methodLine[6] as Integer
				blamedMethod.executionTimeDifference = (methodLine[7] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.variation = methodLine[8] as Integer
				blamedMethod.isModified = true
			} else if (methods == "addedMethods") {
				blamedMethod.avgExecutionTimeNextVersion = (methodLine[1] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.qtdExecutedNextVersion = methodLine[2] as Integer
				blamedMethod.isAdded = true
			} else if (methods == "removedMethods") {
				blamedMethod.avgExecutionTimePreviousVersion = (methodLine[1] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.qtdExecutedPreviousVersion = methodLine[2] as Integer
				blamedMethod.isRemoved = true
			}
			
			if (qtdCommits > 0) {
				def counter = manualCounter
				(1..qtdCommits).each { c ->
					counter += 1
					def responsibleCommit = readCommit(lines[counter])
					blamedMethod.commits << responsibleCommit
				}
			}
			blamedScenario."${methods}" << blamedMethod
			variableIndex += (qtdCommits + 2)
		}
		
		blamedScenario
	}
	
	def readCommit(line) {
		def commitLine = line?.split(";")
		def commit = new Commit(commitHash: commitLine[0])
		commit
	}
	
}
