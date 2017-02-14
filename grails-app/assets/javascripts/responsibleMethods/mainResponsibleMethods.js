$(document).ready(function() {
	var width = ($("body").width()) - (($("body").width() * 6.5) / 100)
	var height = ($("body").height()) - (($("body").height() * 38) / 100)
	var widthDiscount = 0
	var methodsJson = $.parseJSON($("#jsonResponsibleMethods").val())
	
	var left = "80px"
	var top = "140px"

	// Function for moving nodes to front
	d3.selection.prototype.moveToFront = function() {
		return this.each(function() {
			this.parentNode.appendChild(this);
		});
	};

	// Function for moving to back
	d3.selection.prototype.moveToBack = function() {
		return this.each(function() {
			var firstChild = this.parentNode.firstChild;
			if (firstChild) {
				this.parentNode.insertBefore(this, firstChild);
			}
		});
	};

	var scaleFactor = 1;
	var translation = [ 0, 0 ];

	//var smallCircleSize = 4.5;
	var scenarioCircleSize = 4;

	var minWidthPoly1 = 255;
	var minWidthPoly2 = 355;
	var xMargin = 20;
	var yPosFaculty = 170;
	var yPosEntry = 90;
	var yMargin = 20;
	var legendEntryPadding = 10;
	var legendFacultyPadding = 5;

	// Configure force layout
	var force = d3.layout.force();

	performDrawGraph(methodsJson)

	function color(nodeName) {
		if (nodeName.includes("(") && nodeName.includes(")")) {
			return "#009933"
		} else {
			return "#ff0000"
		}
	}

	function performDrawGraph(methodsJson) {

		var allShowing = true;
		var facultySelected = false;
		var nodeHighlighted = false;
		var timeout;

		var mousePos = [ 0, 0 ];
		var newMousePos = [ 0, 0 ];

		/*** Configure zoom behaviour ***/
		var zoomer = d3.behavior.zoom().scaleExtent([ 0.1, 10 ])
		//allow 10 times zoom in or out
		.on("zoom", zoom);
		//define the event handler function

		function zoom(d) {
			if (d3.event.sourceEvent && !nodeHighlighted) {
				d3.event.sourceEvent.stopPropagation();
			}
			scaleFactor = d3.event.scale;
			translation = d3.event.translate;
			tick(); //update positions
		}

		/*** Configure drag behaviour ***/
		var isDrag = false;
		var drag = d3.behavior.drag().origin(function(d) {
			return d;
		}) //center of circle
		.on("dragstart", dragstarted).on("drag", dragged).on("dragend", dragended);

		var getMousePos;

		function dragstarted(d) {
			if (d3.select(this).classed("activeNode")) {
				getMousePos = d3.mouse(vis.node());
				mousePos[0] = getMousePos[0];
				mousePos[1] = getMousePos[1];
				d3.select(this).moveToFront();
				d3.event.sourceEvent.stopPropagation();
				d3.select(this).classed("dragging", true);
				force.stop(); //stop ticks while dragging
				isDrag = true;
			}
		}
		function dragged(d) {
			if (d3.select(this).classed("activeNode")) {
				if (d.fixed)
					return; //root is fixed

				//get mouse coordinates relative to the visualization
				//coordinate system:
				var mouse = d3.mouse(vis.node());
				d.x = (mouse[0] - translation[0]) / scaleFactor;
				d.y = (mouse[1] - translation[1]) / scaleFactor;
				tick();//re-position this node and any links
			}
		}
		function dragended(d) {
			if (d3.select(this).classed("activeNode")) {
				getMousePos = d3.mouse(vis.node());
				newMousePos[0] = getMousePos[0];
				newMousePos[1] = getMousePos[1];
				var shortDrag = Math.abs(newMousePos[0] - mousePos[0]) < 5
						&& Math.abs(newMousePos[1] - mousePos[1]) < 5;
				if (shortDrag) { // Short drag means click
					connectedNodes(d, allShowing || facultySelected, this); // else highlight connected nodes
				}

				d3.select(this).classed("dragging", false);
				if (!shortDrag) {
					force.resume();
				} // Resume force layout only if not a short drag
				isDrag = false;
			}
		}

		//Initialize SVG
		var graph = d3.select("#svgResponsibleMethods").append("svg").append("g").attr("class",
				"graph").on("mousedown", function() {
			mousePos = d3.mouse(this);
			if (mousePos[0] < minWidthPoly1 && mousePos[1] < height)
				d3.event.stopImmediatePropagation(); //Only clicks no drag or pan on menu area
		}).call(zoomer);
		
		graph.append("rect").attr("width", "100%").attr("height", "100%").attr(
				"fill", "#f7f7f7").attr("class", "background").attr(
				"fill-opacity", 0.9);

		// Rectangle to catch mouse events for zoom
		var rect = graph.append("rect").attr("width", "100%")
				.attr("height", "100%").style("margin", "0 auto").style("fill",
						"none").style("pointer-events", "all").style("cursor",
						"move").on("click", function() {
					if (d3.event.defaultPrevented)
						return;
					showAllNodes();
				});

		// Create a group that will hold all content to be zoomed
		var vis = graph.append("svg:g").attr("class", "plotting-area");

		// Pinned tooltip
		var pinnedTooltip = d3.select("body").append("div").attr("class",
				"tooltip pinned").style("opacity", 0);

		// Tooptip in top left corner
		var tooltip = d3.select("body").append("div").attr("class", "tooltip")
				.style("opacity", "0");

		// Create nodes for each unique source and target.
		var nodesByName = {};

		var links = []
		
		$.each(methodsJson, function (m, k) {
			k.forEach(function(s) {
				links.push({
					"target" : m,
					"source" : s
				})
			})
		});

		links.forEach(function(link) {
			link.source = nodeByName(link.source);
			link.target = nodeByName(link.target);
		});
		function nodeByName(name) {
			return nodesByName[name] || (nodesByName[name] = {
				name : name
			});
		}

		// Extract the array of nodes from the map by name.
		var nodes = d3.values(nodesByName);

		// Create the link lines.
		var link = vis.selectAll(".link").data(links).enter().append("line").attr(
				"class", "link");

		// Create the node circles.
		var node = vis
				.selectAll(".node")
				.data(nodes)
				.enter()
				.append("circle")
				.attr("class", "node")
				.attr("r", function(d) {
					if (d.name.includes("(") && d.name.includes(")")) {
						var count = scenarioCircleSize
						links.forEach(function(link) {
							if (link.target.name == d.name) {
								count++
							}
						});
						return count;
					} else {
						return scenarioCircleSize
					}
				})
				.style("fill", function(d) {
					return color(d.name)
				})
				.classed("activeNode", true)
				.on(
						"mouseover",
						function(d) {
							if (d3.select(this).classed("activeNode")
									&& !d3.select(this).classed("baseNode")) {
								force.stop();
								var isMethod = d.name.includes("(") && d.name.includes(")") ? true : false
								tooltip.transition().duration(200).style("opacity", 0.9);
								if (isMethod) {
									tooltip.html("<p><b>Method Signature:</b></p><p>" + d.name + "</p>").style("left", left).style("top", (nodeHighlighted ? top : top));
								} else {
									tooltip.html("<p><b>Scenario:</b></p><p>" + d.name + "</p>").style("left", left).style("top", (nodeHighlighted ? top : top));
								}
							}
						}).on("mouseout", function(d) {
					if (!isDrag && !nodeHighlighted) {
						force.resume();
					}
					tooltip.transition().duration(500).style("opacity", 0);
				}).call(drag);

		// Start the force layout.
		force.nodes(nodes).links(links).linkDistance(40)
		//      .linkStrength(0.08)
		.on("tick", function() {
			tick();
		}).start();

		graph.on("mouseleave", function() {
			force.stop();
		}).on("mouseenter", function() {
			force.resume();
		});

		/* Configure highlighting of connected nodes */
		var toggle = 0;

		//Create an array logging what is connected to what
		var linkedByIndex = {};
		for (i = 0; i < nodes.length; i++) {
			linkedByIndex[i + "," + i] = 1;
		}
		;
		links.forEach(function(d) {
			linkedByIndex[d.source.index + "," + d.target.index] = 1;
		});
		
		//This function looks up whether a pair are neighbours
		function neighboring(a, b) {
			return linkedByIndex[a.index + "," + b.index];
		}

		// Change opacity to highlight connected nodes
		function connectedNodes(clickedOn, firstClick, nodeClicked) {
			var isMethod = clickedOn.name.includes("(") && clickedOn.name.includes(")") ? true : false
			nodeHighlighted = true;
			d3.selectAll("g.cell").classed("active", false); // Clear faculty/entry filters
			if (d3.select(nodeClicked).classed("baseNode")) { // Base node was clicked, show all
				showAllNodes();
				return;
			}
			force.stop(); // Stop moving
			tooltip.style("opacity", 0); // Clear unpinned tooltip (because it is the same as the pinned)
			pinnedTooltip.transition().duration(200).style("opacity", 0.9);
			if (isMethod) {
				pinnedTooltip.html("<p><b>Method Signature:</b></p><p>" + clickedOn.name + "</p>") // Pin tooltip with name of clicked on node
				.style("left", left).style("top", top);
			} else {
				$.each($.parseJSON($("#urlsMap").val()), function(k, v) {
					if (clickedOn.name == k) {
						pinnedTooltip.html("<p><b>Scenario:</b></p><p>" + clickedOn.name + " (<a href='" + v + "' style='pointer-events: all;'>view call graph</a>)</p>") // Pin tooltip with name of clicked on node
						.style("left", left).style("top", top);	
					}
				});
			}
			node.each(function(d) { // Allow for clicking back on previous baseNodes
				d3.select(this).classed("baseNode", false);
			});
			d3.select(nodeClicked).classed("baseNode", true);
			node.classed("activeNode", function(o) {
				return neighboring(clickedOn, o) | neighboring(o, clickedOn) ? true
						: false;
			})
			node.style("stroke-opacity", function(o) {
				return (neighboring(clickedOn, o) | neighboring(o, clickedOn)) ? 1
						: 0.1;
			});
			node.style("fill-opacity", function(o) {
				return (neighboring(clickedOn, o) | neighboring(o, clickedOn)) ? 1
						: 0.1;
			});
			link.style("stroke-opacity", function(o) {
				return clickedOn.index == o.source.index
						| clickedOn.index == o.target.index ? 0.6 : 0.1;
			});
			d3.select("activeNode").moveToFront(); // Brings activeNode nodes to front
			
			if (isMethod) {
				pinnedTooltip.html(pinnedTooltip.html() + "<p><b>Affected Scenarios:</b></p>");
			} else {
				pinnedTooltip.html(pinnedTooltip.html() + "<p><b>Responsible Methods:</b></p>");
			}
			$.each(links, function(i, l) {
				if (isMethod) {
					if (clickedOn.name == l.target.name) {
						$.each($.parseJSON($("#urlsMap").val()), function(k, v) {
							if (l.source.name == k) {
								pinnedTooltip.html(pinnedTooltip.html() + "<p>" + l.source.name + " (<a href='" + v + "' style='pointer-events: all;'>view call graph</a>)</p>")
							}
						});
					}
				} else {
					if (clickedOn.name == l.source.name) {
						pinnedTooltip.html(pinnedTooltip.html() + "<p>" + l.target.name + "</p>")
					}
				}
			});
			
			allShowing = false;
			facultySelected = false;
		}

		// Show all nodes on click in empty space
		function showAllNodes() {
			if (d3.event.stopPropagation) {
				d3.event.stopPropagation();
			}
			force.resume();
			//Put them back to opacity=1
			node.style("stroke-opacity", 1).style("fill-opacity", 1).classed(
					"activeNode", true).classed("clickedNode", false).classed(
					"baseNode", false);
			link.style("stroke-opacity", 0.6);
			d3.selectAll("g.cell").classed("active", false); // Clear faculty/entry filters
			allShowing = true;
			facultySelected = false;
			nodeHighlighted = false;
			pinnedTooltip.style("opacity", 0);
		}

		// Update positions of nodes and links
		function tick() {
			link.attr(
					"x1",
					function(d) {
						return translation[0] + scaleFactor * d.source.x - widthDiscount ;
//						+ (minWidthPoly1 + minWidthPoly2) / 4;
					}).attr("y1", function(d) {
				return translation[1] + scaleFactor * d.source.y;
			}).attr(
					"x2",
					function(d) {
						return translation[0] + scaleFactor * d.target.x - widthDiscount;
								//+ (minWidthPoly1 + minWidthPoly2) / 4;
					}).attr("y2", function(d) {
				return translation[1] + scaleFactor * d.target.y;
			});

			node.attr(
					"cx",
					function(d) {
						return translation[0] + scaleFactor * d.x - widthDiscount;
								//+ (minWidthPoly1 + minWidthPoly2) / 4;
					}).attr("cy", function(d) {
				return translation[1] + scaleFactor * d.y;
			});

		}

		resize();
		d3.select(window).on("resize", resize);

		function resize() {
			//width = window.innerWidth
			//       + minWidthPoly2
			//, height = 400;//(window.innerHeight < 500 ? 500 : window.innerHeight);
			d3.select("svg").attr("width", $("#svgResponsibleMethods")["0"].clientWidth).attr("height", height);
			force.size([ $("#svgResponsibleMethods")["0"].clientWidth, height ]).resume();

			rect.attr("x", minWidthPoly1);
			tick();
		}
		
	}
});

$(window).load(function() {
	$("[data-toggle='offcanvas']").click()
	$("#legendBoxHeader").on('click', function(e) {
		if ($("#legendBoxChildDiv").hasClass("collapsed-box")) {
			$("#legendBox").css("width", "180px")
		} else {
			$("#legendBox").css("width", "60px")
		}
	});
});