package visitor;

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.comments.Comment
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import domain.Attribute

import java.lang.reflect.Modifier

/**
 * Simple visitor implementation for visiting FieldDeclaration nodes. 
 */
public class FieldVisitor extends VoidVisitorAdapter {

	def List<Attribute> fieldsList = []

    public void visit(FieldDeclaration n, Object arg) {
		def attr = new Attribute()
		n.setComment(null)
		n.setAnnotations(null)
		attr.modifier = Modifier.toString(n.getModifiers())
		attr.type = n.getType().toString()
		attr.name = n.getVariables()[0]
		fieldsList << attr
    }

}