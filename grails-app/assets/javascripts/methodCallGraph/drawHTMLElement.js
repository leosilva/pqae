// Create a custom element.
// ------------------------

joint.shapes.html = {};
joint.shapes.html.Element = joint.shapes.basic.Rect.extend({
    defaults: joint.util.deepSupplement({
        type: 'html.Element'
    }, joint.shapes.basic.Rect.prototype.defaults)
});

// Create a custom view for that element that displays an HTML div above it.
// -------------------------------------------------------------------------

joint.shapes.html.ElementView = joint.dia.ElementView.extend({

    template: [
        '<div class="html-element" data-id>',
        '<span class="timeSpan"></span>',
        '<span class="infoSpan"><i class="fa fa-ellipsis-h fa-lg" aria-hidden="true"></i></span>',
        '<div class="divDeviationArrows"><div></div></div>',
        '</div>'
    ].join(''),

    initialize: function() {
        _.bindAll(this, 'updateBox');
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.$box = $(_.template(this.template)());
        this.$box.id = this.model.id
        // Update the box position whenever the underlying model changes.
        this.model.on('change', this.updateBox, this);
        $('#zoomInButton').bind('after-click', this.updateBox);
        $('#zoomOutButton').bind('after-click', this.updateBox);
        $('#zoomToFitButton').bind('after-click', this.updateBox);
        
        var popoverContent = ""
        	
        popoverContent += mountPopoverContentPackageDetails(this.model)
        //popoverContent += mountPopoverContentExecutionTimeDetails(this.model)
        popoverContent += mountTotalExecutionTimeProgressBars(this.model)
        popoverContent += mountSelfExecutionTimeProgressBars(this.model)
        popoverContent += mountPopoverContentPotenciallyCausedDeviation(this.model)
        popoverContent += mountPopoverContentAddedNodes(this.model)
        popoverContent += mountPopoverContentParametersDetails(this.model)
        
        if (popoverContent == "") {
        	popoverContent = popoverNoDetails
        }
        
        this.$box.find('.infoSpan').popover({
            title: 'Details',
            trigger: 'manual',
            placement: 'auto',
            container: 'body',
            html: true,
            content: popoverContent
        }).on("mouseenter", function () {
            var _this = this;
            $(this).popover("show");
            $(".popover").on("mouseleave", function () {
                $(_this).popover('hide');
            });
        }).on("mouseleave", function () {
            var _this = this;
            setTimeout(function () {
                if (!$(".popover:hover").length) {
                    $(_this).popover("hide");
                }
            }, 100);
        });
        
        this.updateBox();
    },
    render: function() {
    	this.$box.attr("data-id", this.model.id)
    	var box = this.$box 
    	var hasDataId = false
    	var element
    	// codigo para evitar que elementos duplicados sejam adicionados a visualizacao.
    	$.each(this.paper.el.childNodes, function (index, value) {
    		if($(value).hasClass('html-element')) {
    			if (box.attr("data-id") == $(value).attr("data-id")) {
    				hasDataId = true
    				element = value
    			}
    		}
    	});
		joint.dia.ElementView.prototype.render.apply(this, arguments);
		this.paper.$el.prepend(this.$box);
		if (hasDataId) {
			this.paper.el.removeChild(element)
		}
		
		/* 
		 * Realiza o bind do duplo-clique para os nós com desvio ou adicionados.
		 * O duplo-clique faz o highlight do caminho desde o nó raiz até o nó desviado.
		 */
		if (this.model.attributes.node.hasDeviation == true || this.model.attributes.node.isAddedNode == true) {
			bindOnDoubleClick($("[data-id=" + this.model.id + "]"), true)
		} else {
			bindClearHighlight($("[data-id=" + this.model.id + "]"), true);
		}
		this.updateBox();
        return this;
    },
    updateBox: function() {
    	var scale = V(paper.viewport).scale().sx
    	var bbox = this.model.getBBox();
    	this.$box.find(".divDeviationArrows div").html(defineArrows(this.model))
    	
    	var padding = parseInt(findProperty('.evolve-paper-graph', 'padding').replace("px", ""))
    	var fontSize = findProperty(".html-element span", "font-size").replace("px", "")
    	var fontSizeArrow = findProperty(".divDeviationArrows", "font-size").replace("px", "")
    	var widthArrow = findProperty(".divDeviationArrows", "width").replace("px", "")
    	this.$box.find('.timeSpan').css({'font-size': fontSize * scale});
    	this.$box.find('.infoSpan').css({'font-size': fontSize * scale});
    	this.$box.find('.divDeviationArrows > div').css({'font-size': fontSizeArrow * scale});
    	this.$box.find('.divDeviationArrows').css({'width': widthArrow * scale});
        this.$box.find('.timeSpan').text(this.model.get('select'));
        
        // ajusta as margens das setas, para que sejam dispostas uma acima da outra, com um espaço entre elas.
        $.each($(this.$box.find(".divDeviationArrows")).find("div").children(), function(index, value) {
            var margin = parseInt($(value).css("margin-bottom").replace("px", ""))
            if (margin > 0) {
                $(value).attr("style", "margin-bottom: " + (margin * scale) + "px;")
            }
        })
        
        // verifica se esconde ou não a div que fica em cima do nó, de acordo com a escala do zoom.
        if (scale <= 0.7) {
        	this.$box.find('.timeSpan').css({visibility : 'hidden'});	
        	this.$box.find('.infoSpan').css({visibility : 'hidden'});	
        	this.$box.find('.divDeviationArrows').css({visibility : 'hidden'});	
        } else {
        	this.$box.find('.timeSpan').css({visibility : 'visible'});	
        	this.$box.find('.infoSpan').css({visibility : 'visible'});
        	this.$box.find('.divDeviationArrows').css({visibility : 'visible'});
        }
        this.$box.css({ width: bbox.width * scale, height: bbox.height * scale, left: ((bbox.x * scale) + padding), top: ((bbox.y * scale) + padding), transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)' });
    }
});

/**
 * Função que cria o elemento HTML que ficará por cima do nó.
 * @param width
 * @param height
 * @param node
 * @param memberToShow
 * @returns {joint.shapes.html.Element}
 */
function createHTMLElement(width, height, node, memberToShow) {
	var fillRect = defineNodeColor(node)
	var nodeTime = defineNodeTime(node)
	var minWidth = 230
	var minWidthGroupedNode = 110
	var minHeight = 50
	if (node.isGroupedNode == false && width < minWidth) {
		width = minWidth
	} else if (node.isGroupedNode) {
		width = minWidthGroupedNode
	}
	if (height < minHeight) {
		height = minHeight
	}
	var element = new joint.shapes.html.Element({
			size: { width: width, height: minHeight },
			select: nodeTime,
			node: node,
			attrs: {
	        	id: node.id,
	        	rect: { fill: fillRect, minWidth : 200 },
	        	text: { text: memberToShow, fill: 'black', 'ref-y': 35},
	        	root: node.isRootNode
			}
		});
	return element;
}

/**
 * Função que determina os detalhes dos nós adicionados.
 * @param popoverContent
 * @returns
 */
function mountPopoverContentAddedNodes(model) {
	var content = ""
	if (model.get('node').addedNodes.length > 0) {
		content += "<p><span class='text-bold'>" + popoverAddedNodes + " (" + model.get('node').addedNodes.length + "):</span></p>"
		content += "<ul>"
		for (var n in model.get('node').addedNodes) {
			content += "<li>" + removeMethodParams(model.get('node').addedNodes[n]) + "</li>"
		}
		content += "</ul>"
	}
	return content
}
 
/**
 * Função que determina o pacote do nó na seção de detalhes.
 * @param model
 * @returns {String}
 */
function mountPopoverContentPackageDetails(model) {
	var content = ""
	if (model.get('node').isGroupedNode == false) {
		var node = model.get('node')
		var memberToShow = node.member;
	    if (node.member != "[...]") {
	    	var parameters = node.member.substring(node.member.indexOf('(') + 1, node.member.indexOf(')'));
	    	memberToShow = memberToShow.replace("(" + parameters + ")", '');
	    	var splitted = memberToShow.split('\.');
	    	var param = ""
	    		if (parameters != null && parameters.trim() != "") {
	    			param = "..."
	    		}
	    	// retira elementos do vetor até sobrar apenas o nome dos pacotes.
	    	for (var s in splitted) {
	    		var char = splitted.pop().charAt(0)
	    		if (char === char.toUpperCase() && char !== char.toLowerCase()) {
	    			break
	    		}
			}
	    	memberToShow = splitted.join('.')
	    }
		content = "<p><span class='text-bold'>" + popoverPackage + ":</span> " + memberToShow + "</p>"
	}
	return content
}
 
/**
 * Função que monta os parâmetros do nó na seção de detalhes.
 * @param model
 * @returns {String}
 */
function mountPopoverContentParametersDetails(model) {
    var memberToShow = "";
    var node = model.get('node');
    if (node.member != "[...]") {
    	var params = node.member.substring(node.member.indexOf('(') + 1, node.member.indexOf(')')).split(",");
    	if (params.length > 0 && params != "") {
    		memberToShow += "<p><span class='text-bold'>" + popoverParameters + " (" + params.length + "):</span> </p>"
    		memberToShow += "<ul>"
			for (var p in params) {
				memberToShow += "<li>" + params[p] + "</li>"
			}
    		memberToShow += "</ul>"
    	} else {
    		memberToShow += "<p><span class='text-bold'>" + popoverNoParameters + "</span></p>"
    	}
    }
    return memberToShow
}
 
/**
 * Função que determina a mensagem dos nós potencialmente responsáveis por causar desvio de desempenho.
 * @param model
 * @returns {String}
 */
function mountPopoverContentPotenciallyCausedDeviation(model) {
	var node = model.get('node');
	var content = ""
	if (node.hasDeviation == true && node.isAddedNode == true) {
		content += "<p><span class='potentially-caused-deviation'>" + popoverPotenciallyCausedDeviation + "<span></p>"
	}
	return content
}

/**
 * Função que determina os tempos de execução das versões, na seção de detalhes.
 * @param model
 * @returns {String}
 */
function mountPopoverContentExecutionTimeDetails(model) {
	var node = model.get('node');
	var content = ""
	if (node.hasDeviation == true) {
		content += "<p>"
		if (node.isAddedNode == true) {
			content += "<span class='text-bold'>" + popoverPreviousVersionTotalTime + ": </span>-<br/>"
			content += "<span class='text-bold'>" + popoverPreviousVersionSelfTime + ": </span>-<br/>"
		} else {
			content += "<span class='text-bold'>" + popoverPreviousVersionTotalTime + ": </span>" + node.previousExecutionTime + " ms<br/>"
			content += "<span class='text-bold'>" + popoverPreviousVersionSelfTime + ": </span>" + node.previousExecutionRealTime + " ms<br/>"
		}
		content += "<span class='text-bold'>" + popoverNextVersionTotalTime + ": </span>" + node.nextExecutionTime + " ms<br/>"
		content += "<span class='text-bold'>" + popoverNextVersionSelfTime + ": </span>" + node.nextExecutionRealTime + " ms<br/>"
		if (node.isAddedNode == true) {
			content += "<span class='text-bold'>" + popoverDeviation + ": </span>-"
		} else {
			content += "<span class='text-bold'>" + popoverDeviation + ": </span>" + node.timeVariation + " ms"
		}
		content += "<p>"
	}
	return content
}

/**
 * Função que determina a cor do nó.
 * @param node
 * @returns {String}
 */
function defineNodeColor(node) {
	var fillRect = findProperty('.legend-no-deviation', 'background-color');
	if (node.deviation == "optimization") {
		fillRect = findProperty('.legend-optimization', 'background-color');
	} else if (node.deviation == "degradation") {
		fillRect = findProperty('.legend-degradation', 'background-color');
	}
	if (node.addedNodes.length > 0 || node.isAddedNode) {
		fillRect = findProperty('.legend-added', 'background-color');
	}
	return fillRect
}

/**
 * Função que determina o tempo do nó que será apresentado em tela.
 * @param node
 */
function defineNodeTime(node) {
	var nodeTime = ""
	if (node.hasDeviation == false && node.isGroupedNode == false) {
		nodeTime = "total: " + node.nextExecutionTime + " ms, self: " + node.nextExecutionRealTime + " ms"
	} else if (node.timeVariation == null && node.isGroupedNode == false && node.hasDeviation == false) {
		nodeTime = node.nextExecutionTime + " ms "
	} else if (node.hasDeviation == true && node.isAddedNode == false) {
		nodeTime = "total: " + node.nextExecutionTime + " ms, self: " + node.nextExecutionRealTime + " ms (" + node.timeVariation + " ms)"
	} else if (node.isGroupedNode == true) {
		nodeTime = "total: " + node.nextExecutionTime + " ms"
	} else if (node.isAddedNode == true) {
		nodeTime = "total: " + node.nextExecutionTime + " ms, self: " + node.nextExecutionRealTime + " ms"
	}
	return nodeTime
}

/**
 * Função que calcula as setas que irão aparecer nos nós que tiveram desvio de desempenho. Cada seta representa um desvio de 25% do tempo anterior, para mais ou para menos.
 * @param model
 * @returns {String}
 */
function defineArrows(model) {
	var html = ""
	var hasDeviation = model.attributes.node.hasDeviation
	var isAddedNode = model.attributes.node.isAddedNode
	var deviation = model.attributes.node.deviation
	var pvTime = model.attributes.node.previousExecutionTime
	var nvTime = model.attributes.node.nextExecutionTime
	var tv = Math.abs(model.attributes.node.timeVariation)
	if (hasDeviation == true && isAddedNode == false) {
		var arrowDirection = (deviation == "optimization") ? "up" : "down"
		if ((tv <= (pvTime * 25) / 100) || (tv >= (pvTime * 25) / 100)) {
			html += "<i class='ionicons ion-arrow-" + arrowDirection + "-b " + deviation + " arrow'></i>"
		} 
		if ((tv > (pvTime * 25) / 100) || (tv >= (pvTime * 50) / 100)) {
			html += "<i class='ionicons ion-arrow-" + arrowDirection + "-b " + deviation + " arrow arrow2'></i>"
		}
		if ((tv > (pvTime * 50) / 100) || (tv >= (pvTime * 75) / 100)) {
			html += "<i class='ionicons ion-arrow-" + arrowDirection + "-b " + deviation + " arrow arrow3'></i>"
		}
		if ((tv > (pvTime * 75) / 100) || tv >= ((pvTime * 100) / 100)) {
			html += "<i class='ionicons ion-arrow-" + arrowDirection + "-b " + deviation + " arrow arrow4'></i>"
		}
	}
	return html
}

function mountTotalExecutionTimeProgressBars(model) {
	var node = model.get('node');
	var content = ""
	if (node.hasDeviation == true && node.isAddedNode == false) {
		var totalExecutionTime = node.previousExecutionTime + node.nextExecutionTime
		var percentPET = (node.previousExecutionTime * 100) / totalExecutionTime
		var percentNET = (node.nextExecutionTime * 100) / totalExecutionTime
		content += "<span class='text-bold'>" + popoverTotalTime + ":</span><br/>"
		content += "<span class='span-info-progress-bar'><small class='small-info-progress-bar'>previous</small></span>"
		content += "<div class='progress'>"
		content += "<div class='progress-bar progress-bar-yellow' style='width:" + percentPET + "%;'>" + node.previousExecutionTime + " ms</div></div>"
		content += "<span class='span-info-progress-bar'><small class='small-info-progress-bar'>current</small></span>"
		content += "<div class='progress'>"
		content += "<div class='progress-bar progress-bar-green' style='width:" + percentNET + "%;'>" + node.nextExecutionTime + " ms</div></div>"
	} else if (node.hasDeviation == true && node.isAddedNode == true) {
		content += "<span class='text-bold'>" + popoverTotalTime + ":</span>"
		content += "<div class='progress'>"
		content += "<div class='progress-bar progress-bar-green' style='width:100%; max-width: 100% !important;'>" + node.nextExecutionTime + " ms</div>"
		content += "</div>"
	}
	return content
}

function mountSelfExecutionTimeProgressBars(model) {
	var node = model.get('node');
	var content = ""
	if (node.hasDeviation == true && node.isAddedNode == false) {
		var totalSelfExecutionTime = node.previousExecutionRealTime + node.nextExecutionRealTime
		var percentPET = (node.previousExecutionRealTime * 100) / totalSelfExecutionTime
		var percentNET = (node.nextExecutionRealTime * 100) / totalSelfExecutionTime
		content += "<span class='text-bold'>" + popoverSelfTime + ":</span><br/>"
		content += "<span class='span-info-progress-bar'><small class='small-info-progress-bar'>previous</small></span>"
		content += "<div class='progress'>"
		content += "<div class='progress-bar progress-bar-yellow' style='width:" + percentPET + "%;'>" + node.previousExecutionRealTime + " ms</div></div>"
		content += "<span class='span-info-progress-bar'><small class='small-info-progress-bar'>current</small></span>"
		content += "<div class='progress'>"
		content += "<div class='progress-bar progress-bar-green' style='width:" + percentNET + "%;'>" + node.nextExecutionRealTime + " ms</div></div>"
	} else if (node.hasDeviation == true && node.isAddedNode == true) {
		content += "<span class='text-bold'>" + popoverSelfTime + ":</span>"
		content += "<div class='progress'>"
		content += "<div class='progress-bar progress-bar-green' style='width:100%; max-width: 100% !important;'>" + node.nextExecutionRealTime + " ms</div>"
		content += "</div>"
	}
	if (node.isAddedNode == true) {
		content += "<p><span class='text-bold'>" + popoverDeviation + ": </span>-</p>"
	} else if (node.hasDeviation == true) {
		content += "<p><span class='text-bold'>" + popoverDeviation + ": </span>" + node.timeVariation + " ms</p>"
	}
	return content
}