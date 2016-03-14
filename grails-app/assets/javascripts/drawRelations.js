/**
 * Função responsável por chamar uma função específica que desenha um tipo de relacionamento. Essa função específica é
 * obtida através de um arquivo JSON (relations.json).
 * @param rel
 * @returns {*}
 */
function drawRelation(rel) {
    return relationsMap[rel.type](rel)
}

/**
 * Função responsável por desenhar um relacionamento do tipo Generalização.
 * @param rel
 * @returns {*}
 */
function drawGeneralization(rel) {
    return new uml.Generalization({ source: { id: classes[rel.classFrom].id }, target: { id: classes[rel.classTo].id }});
}

/**
 * Função responsável por desenhar um relacionamento do tipo Composição.
 * @param rel
 * @returns {*}
 */
function drawComposition(rel) {
    return new uml.Composition({ source: { id: classes[rel.classFrom].id }, target: { id: classes[rel.classTo].id }});
}

/**
 * Função responsável por desenhar um relacionamento do tipo Agragação.
 * @param rel
 * @returns {*}
 */
function drawAggregation(rel) {
    return new uml.Aggregation({ source: { id: classes[rel.classFrom].id }, target: { id: classes[rel.classTo].id }});
}

/**
 * Função responsável por desenhar um relacionamento do tipo Implementação de interfaces.
 * @param rel
 * @returns {*}
 */
function drawImplementation(rel) {
    return new uml.Implementation({ source: { id: classes[rel.classFrom].id }, target: { id: classes[rel.classTo].id }});
}

/**
 * Função responsável por desenhar um relacionamento do tipo Associação.
 * @param rel
 * @returns {*}
 */
function drawAssociation(rel) {
    return new uml.Association({ source: { id: classes[rel.classFrom].id }, target: { id: classes[rel.classTo].id }});
}