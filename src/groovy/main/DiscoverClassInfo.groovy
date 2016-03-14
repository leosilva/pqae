package main

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit

class DiscoverClassInfo {
	
	def javaFileSuffix = ".java"
	def linesIgnored = ["firstLine" : "@startuml", "lastLine" : "@enduml"]
	def nativeJavaPackages = ["java.lang", "java.math", "java.net", "java.util", "java.io", "java.applet", "java.awt", "java.beans", "java.nio", "java.rmi", "java.security",
	"java.sql", "java.text", "javax.accessibility", "javax.activation", "javax.activity", "javax.annotation", "javax.crypto", "javax.imageio", "javax.jws", "javax.management",
	"javax.naming", "javax.net", "javax.print", "javax.rmi", "javax.script", "javax.security", "javax.sound", "javax.sql", "javax.swing", "javax.tools", "javax.transaction",
	"javax.xml", "org.letf.jgss", "org.omg", "org.w3c", "org.xml"]
	def members = [
			"abstractClass" : "abstract class",
			"class" : "class",
			"enum" : "enum"
		]
	
	// variavel que armazena as classes encontradas no arquivo
	def classes = []
	
	// variavel temporaria usada para ajudar a obter os relacionamentos entre as classes
	def temp = []
	
	// variavel que mantem os relacionamentos entre as classes
	def relacionamentos = []

	def rootSource = "/Users/leosilva/Documents/Estudo/Mestrado/Dissertacao/Softwares_Exemplo/PagSeguro_Java/source/pagseguro-api/src/"
	def file = new File("/Users/leosilva/Documents/Estudo/Mestrado/Dissertacao/workspace/ArchitectureVisualization/teste.txt")

	def createBasedPlantUMLFile = new CreateBasedPlantUMLFile()
	
	def findClassInfo() {
		PlantUMLDependency.runPlantUMLDependency()
		
		file.eachLine { line ->
			if (!linesIgnored.containsValue(line) && !verifyIfIsANativeJavaClass(line)) {
				members.each {
					if (line.startsWith(it.value)) {
						classes << (line - it.value)?.replaceAll("\\s", "")
						temp << line
					}
				}
				relacionamentos << line
			}
		}
		
		// os relacionamentos sao obtidos pela subtracao da lista total e da lista temporaria
		relacionamentos = relacionamentos - temp
		
		// definindo as classes
		classes.each { clazz ->
			def f = new File(rootSource + clazz.replace(".", "/") + javaFileSuffix)
			CompilationUnit cu = JavaParser.parse(f)
			createBasedPlantUMLFile.writeClass(cu)
		}
		
		// inserindo os relacionamentos
		relacionamentos.eachWithIndex {it, index ->
			createBasedPlantUMLFile.writeRelationships(index, it)
		}
		
	}

	/**
	 * Verifica se a classe ou relacionamento em questão envolve uma classe nativa do Java.
	 * @param line
	 * @return
     */
	private def verifyIfIsANativeJavaClass(line) {
		def retorno = false
		nativeJavaPackages.each {
			if (line.contains(it)) {
				retorno = true
			}
		}
		retorno
	}
	
}
