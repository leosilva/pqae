function drawElement(clazz) {
    if (clazz.content.type == "class") {
        return drawClass(clazz)
    } else if (clazz.content.type == "interface") {
        return drawInterface(clazz)
    } else if (clazz.content.type = "enum") {
        return drawEnum(clazz)
    }
}



/**
 * Função que cria os atributos de uma classe a ser desenhada no JointJS.
 * @param clazz
 * @returns {Array}
 */
function drawAttributes(clazz) {
    var attrs = []
    $.each(clazz.content.attributes, function (key, val) {
        attrs.push(val.name + ": " + val.type)
    })
    return attrs
}

/**
 * Função que cria os métodos de uma classe a ser desenhada no JointJS.
 * @param clazz
 * @returns {Array}
 */
function drawMethods(clazz) {
    var methods = []
    $.each(clazz.content.methods, function(key, val) {
        methods.push(val.name + "(" + val.parameters + "): " + val.returnn)
    });
    return methods
}