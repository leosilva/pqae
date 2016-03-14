package main

import visitor.ClassVisitor
import visitor.FieldVisitor
import visitor.MethodVisitor

import java.lang.reflect.Modifier

class ClassTemplateWriter {

	def static domain.Class clazz

	static def writeClass(cu) {
		new ClassVisitor().visit(cu, null)
		def type = ClassVisitor.declaration
		clazz = new domain.Class()

		clazz.modifiers = Modifier.toString(cu.getTypes()*.getModifiers()[0])
		clazz.name = cu.getTypes()*.getName()[0]
		clazz.type = type
		clazz.pack = cu.getPackage().getName()
		 
		def fv = new FieldVisitor()
		fv.visit(cu, null)
		fv.fieldsList.each {
			clazz.attributes += it
		}
		
		def mv = new MethodVisitor()
		mv.visit(cu, null)
		mv.methods.each {
			clazz.methods += it
		}
		return clazz
	}
	
}