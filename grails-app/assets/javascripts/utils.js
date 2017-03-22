/*
* Este arquivo define funções utilitárias que podem ser usadas por scripts em toda a aploicação.
*/

/**
 * Função utilitária para recuperar o valor de determinado atributo de um seletor CSS. A função navega em todos os arquivos CSS da aplicação.
 * Exemplo: findProperty('.class', 'display') busca pelo valor da propriedade 'display' na classe chamada 'class'.
 * @param selector
 * @param property
 * @returns {Boolean}
 */
function findProperty(selector, property) {
	var styleSheets = document.styleSheets
	for (s in styleSheets) {
		var rules = styleSheets[s].cssRules 
		for(i in rules) {
			if (rules[i].selectorText != null && rules[i].selectorText.includes(selector)) {
				return rules[i].style[property]
			}
		}
	}
    return false;
}

function convertNanoToMilis(time) {
	return parseFloat(time / 1000000).toFixed(2)
}

function convertMilisToSec(time) {
	return parseFloat(time / 1000).toFixed(2)
}

function defineNumberAndExtension(number) {
	var returnArray = []
	var time = number
	var ext = "ns"
	if (time > 1000000 || time < -1000000) {
		time = convertNanoToMilis(time)
		ext = "ms"
		if (time > 1000 || time < -1000) {
			time = convertMilisToSec(time)
			ext = "s"
		}
	}
	returnArray[0] = time
	returnArray[1] = ext
	return returnArray
}

function defineBiggerTimeExtension(ext1, ext2) {
	if (ext1 == "s" || ext2 == "s") {
		return "s"
	} else if (ext1 == "ms" || ext2 == "ms") {
		return "ms"
	} else if (ext1 == "ns" || ext2 == "ns") {
		return "ns"
	}
}