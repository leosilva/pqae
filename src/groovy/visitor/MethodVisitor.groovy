package visitor

import domain.Method;

import java.lang.reflect.Modifier;
import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Simple visitor implementation for visiting MethodDeclaration nodes. 
 */
public class MethodVisitor extends VoidVisitorAdapter {
	
	def List<Method> methods = []

    public void visit(MethodDeclaration n, Object arg) {
		def m = new Method()
		m.name = n.getName()
		m.modifiers = Modifier.toString(n.getModifiers())
		m.returnn = n.getType().toString()
//		m.parameters = n.getParameters().join(", ")
		m.parameters = ""
		n.setComment(null)
		n.setAnnotations(null)
		methods << m
    }
}