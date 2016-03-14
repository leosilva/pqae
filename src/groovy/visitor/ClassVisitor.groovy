package visitor

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.body.VariableDeclaratorId
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

public class ClassVisitor extends VoidVisitorAdapter {
	
	def static String declaration
 
    @Override
    public void visit(ClassOrInterfaceDeclaration typeDeclaration, Object arg) {
		if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
			if (classOrInterfaceDeclaration.isInterface()) {
				declaration = "interface"
			} else {
				declaration = "class"
			}
		}
    }

	@Override
	public void visit(EnumDeclaration enumDeclaration, Object arg) {
		if (enumDeclaration instanceof EnumDeclaration) {
			declaration = "enum"
		}
	}
}