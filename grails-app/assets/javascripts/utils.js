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