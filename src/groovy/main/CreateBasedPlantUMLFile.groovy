package main

import groovy.json.JsonBuilder


class CreateBasedPlantUMLFile {
	
	def map = [
		"classes" : [],
		"relations" : []
		]
	
	def writeClass(cu) {
		map."classes" += new JsonBuilder(ClassTemplateWriter.writeClass(cu))
	}
	
	def writeRelationships(index, relation) {
		def relationSplitted = relation.split(" ")
		def rel = new domain.Relation()
		rel.classFrom = relationSplitted[0]
		rel.type = relationSplitted[1]
		rel.classTo = relationSplitted[2]
		map."relations" += [ "$index" : rel]
	}
	
}
