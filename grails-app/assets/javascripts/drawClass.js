/**
 * Arquivo responsável por criar as estruturas das classes a serem adicionadas ao diagrama de classes UML.
 */

function drawClass(clazz) {
    var c
    if (clazz.content.modifiers.includes("abstract")) {
        c = drawAbstractClass(clazz)
    } else {
        c = drawConcreteClass(clazz)
    }
    return c
}

/**
 * Função responsável por criar uma classe abstrata no JointJS.
 * @param clazz
 * @returns {*}
 */
function drawAbstractClass(clazz) {
    var c = new uml.Abstract({
        position: { x:300  , y: 300 },
        size: { width: 220, height: 100 },
        name: clazz.content.name,
//        attributes: drawAttributes(clazz),
//        methods: drawMethods(clazz),
        attrs: {
            '.uml-class-name-rect': {
                fill: '#68ddd5',
                stroke: '#ffffff',
                'stroke-width': 0.5
            },
            '.uml-class-attrs-rect, .uml-class-methods-rect': {
                fill: '#68ddd5',
                stroke: '#fff',
                'stroke-width': 0.5
            },
            '.uml-class-methods-text, .uml-class-attrs-text': {
                fill: '#fff'
            },
            a: {
            	'xlink:href': 'http://www.globo.com',
            	'xlink:show': 'new',
            	cursor: 'pointer'
            }
        }
    });
    return c
}

/**
 * Função responsável por criar uma classe concreta no JointJS.
 * @param clazz
 * @returns {*}
 */
function drawConcreteClass(clazz) {
    var c = new uml.Class({
        position: { x:20  , y: 190 },
        size: { width: 220, height: 100 },
        name: clazz.content.name,
//        attributes: drawAttributes(clazz),
//        methods: drawMethods(clazz),
        attrs: {
            '.uml-class-name-rect': {
                fill: '#ff8450',
                stroke: '#fff',
                'stroke-width': 0.5,
            },
            '.uml-class-attrs-rect, .uml-class-methods-rect': {
                fill: '#fe976a',
                stroke: '#fff',
                'stroke-width': 0.5
            },
            '.uml-class-attrs-text': {
                ref: '.uml-class-attrs-rect',
                'ref-y': 0.5,
                'y-alignment': 'middle'
            },
            '.uml-class-methods-text': {
                ref: '.uml-class-methods-rect',
                'ref-y': 0.5,
                'y-alignment': 'middle'
            }
        }
    });
    return c
}