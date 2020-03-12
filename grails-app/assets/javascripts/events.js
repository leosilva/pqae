function bindButtons() {
	bindZoomButtons();
	
	$("#packageButton").on("click", function(){
		var url = window.location.href;
		newUrl = url.replace(/callGraphVisualization/g, "callPackageGraphVisualization");
		window.location.href = newUrl;
	});

	$("#methodButton").on("click", function(){
		var url = window.location.href;
		newUrl = url.replace(/callPackageGraphVisualization/g, "callGraphVisualization");
		window.location.href = newUrl;
	});

	$("#highlightButton").on('click', function() {
		setTimeout(function (){
			if ($("#highlightButton")[0].attributes["aria-pressed"].nodeValue == "true") {
				doHighlightAllDeviationNodes();
				$("#highlightButton").data("clicked", true)
			} else {
				resetHighlight(true);
				$("#highlightButton").data("clicked", false)
			}
		}, 50);
	});
	
	$('#helpButton').popover({
        title: 'Help',
        trigger: 'manual',
        placement: 'left',
        container: 'body',
        html: true,
        content: helpPopoverContent
    }).on("show.bs.popover", function(){
    	$(this).data("bs.popover").tip().css("max-width", "400px")
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
	
	$('#perfOptHelp').popover({
        title: 'Percentage Performance Optimization',
        trigger: 'manual',
        placement: 'left',
        container: 'body',
        html: true,
        content: perfArrowMessage + perfOptOneArrow + perfOptTwoArrows + perfOptThreeArrows + perfOptFourArrows
    }).on("show.bs.popover", function(){
    	$(this).data("bs.popover").tip().css("max-width", "150px")
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
	
	$('#perfDegHelp').popover({
        title: 'Percentage Performance Degradation',
        trigger: 'manual',
        placement: 'left',
        container: 'body',
        html: true,
        content: perfArrowMessage + perfDegOneArrow + perfDegTwoArrows + perfDegThreeArrows + perfDegFourArrows
    }).on("show.bs.popover", function(){
    	$(this).data("bs.popover").tip().css("max-width", "150px")
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
	
	$("#legendBoxHeader").on('click', function(e) {
		if ($("#legendBoxChildDiv").hasClass("collapsed-box")) {
			$("#legendBox").css("width", "180px")
		} else {
			$("#legendBox").css("width", "60px")
		}
	});
	
}

function bindZoomButtons() {
	$('#zoomInButton').on('click', function() {
		doZoom(0.1)
	    $('#zoomInButton').trigger('after-click');
	});
	$('#zoomOutButton').on('click', function() {
		doZoom(-0.1)
		$('#zoomOutButton').trigger('after-click');
	});
	$('#zoomToFitButton').on('click', function() {
		doZoom(1.0)
		$('#zoomToFitButton').trigger('after-click');
	});
}

function doZoom(z) {
	var newScale
	
	newScale = V(paper.viewport).scale().sx + z; // the current paper scale changed by delta
	if (z == 1.0) {
		newScale = z
	}
	
    if (newScale > 0.4 && newScale <= 1.5) {
        paper.scale(newScale, newScale);
        paper.setOrigin(0, 0); // reset the previous viewport translation
    }
    
    if (newScale > 1.0) {
    	calculatePaperDimensionsByScale(z)
    } else if (newScale == 1.0) {
    	calculatePaperDimensions()
    }
}

function performHighlight(element, isResetHighlight) {
	resetHighlight(isResetHighlight)
	var nodesToHighlight = []
	if (element instanceof Array) {
		$.each(element, function(ind, elm) {
			$.each(graph.getElements(), function(i, e) {
				if (e.id == elm[0].attributes["data-id"].nodeValue) {
					nodesToHighlight = addNodesToHighlight(nodesToHighlight, e.attributes.attrs.id)
				}
			});
		});
	} else {
		var id = element.attributes["data-id"].nodeValue
		$.each(graph.getElements(), function(i, e) {
			if (e.id == id) {
				nodesToHighlight = addNodesToHighlight(nodesToHighlight, e.attributes.attrs.id)
			}
		});
	}
	var modelIdsToHightLight = []
	var dataIdsToHightLight = []
	var linksToHighlight = []
	$.each(nodesToHighlight, function(i, e) {
		modelIdsToHightLight.push("[model-id=" + e.id +"]")
		dataIdsToHightLight.push("[data-id=" + e.id +"]")
		$.each(graph.getCells(), function(ind, c) {
			if (c.attributes.type == "link") {
				if (c.attributes.source.id == e.id && c.attributes.target.id == nodesToHighlight[i+1].id) {
					linksToHighlight.push(c)
				}
			}
		});
	});
	doHighlight(modelIdsToHightLight, linksToHighlight, dataIdsToHightLight)
}

function bindOnDoubleClick(element, isResetHighlight) {
	element.on("dblclick", function() {
		performHighlight(element[0], isResetHighlight)
	})
}

function addNodesToHighlight(nodesToHighlight, id) {
	$.each(graph.getElements(), function(i, e) {
		if (e.attributes.attrs.id == id) {
			nodesToHighlight.push(e)
			addNodesToHighlight(nodesToHighlight, e.attributes.node.node.id)
		}
	});
	return nodesToHighlight
}

function resetHighlight(isResetHighlight) {
	if (isResetHighlight == true) {
		$("#paperNextVersion").find("svg").children().children().css("opacity", "")
		$("#paperNextVersion").children().not("svg").css("opacity", "")
	}
}

function resetHighlightButton() {
	if ($("#highlightButton")[0].attributes["aria-pressed"].nodeValue == "true") {
		$("#highlightButton")[0].attributes["aria-pressed"].nodeValue = "false";
		$("#highlightButton").toggleClass("active");
	}
}

function doHighlight(modelIdsToHightLight, linksToHighlight, dataIdsToHightLight) {
	$("#paperNextVersion").find("svg").children().children().not(modelIdsToHightLight.join(",")).css("opacity", "0.2")
	$.each(linksToHighlight, function(i, e) {
		$("[model-id=" + e.id + "]").css("opacity", "1.0")
	});
	$("#paperNextVersion").children().not("svg").not(dataIdsToHightLight.join(",")).css("opacity", "0.2")	
}

function doHighlightAllDeviationNodes() {
	var elements = []
	$.each(graph.getElements(), function(i, e) {
		if (e.attributes.node.hasDeviation == true || e.attributes.node.isAddedNode == true) {
			var element = $("[data-id=" + e.id + "]")
			elements.push(element)
		}
	});
	performHighlight(elements, true);
}

function bindClearHighlight(element, isResetHighlight) {
	element.on("dblclick", function(){
		resetHighlight(isResetHighlight);
		resetHighlightButton();
	})	
}