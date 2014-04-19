package parser.semanticAnalysis.abstractSyntaxTree.conditions.relation;

/**
 * Defines a mathematical relation (e.g. equality, inequality).
 */
public interface IRelation {

	/**
	 * Gets the negation of the relation.
	 */
	Relation getNegation();
}
