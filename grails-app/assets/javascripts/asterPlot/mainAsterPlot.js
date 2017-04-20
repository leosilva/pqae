$(document).ready(function() {
	var minRadius = 5
	var width = ($(document).height()) - (($(document).height() * 27) / 100), // igual a altura para formar um quadrado
	height = ($(document).height()) - (($(document).height() * 27) / 100),
	radius = Math.min(width, height) / 2,
	innerRadius = 0.3 * radius;

	var pie = d3.layout.pie()
	.sort(null)
	.value(function(d) { return d.width; });
	
	var maiorScore = 0.0
	
	var jsonScenarios = $.parseJSON($("#jsonScenarios").val())
	
	// ordenando alfabeticamente para apresentar o aster plot sempre do mesmo jeito
	jsonScenarios.sort(function(a, b){
		if(a.label < b.label) return -1;
	    if(a.label > b.label) return 1;
	    return 0;
	})

	$.each(jsonScenarios, function (key, val) {
	    if (maiorScore < parseFloat(val.score)) {
	    	maiorScore = val.score
	    }
	    if (val.isDegraded == "true") {
	    	val.color = findProperty('.degradation-color', 'background-color');
	    } else {
	    	val.color = findProperty('.optimization-color', 'background-color');
	    }
	});
	
	var tip = d3.tip()
	  .attr('class', 'd3-tip')
	  .offset([0, 0])
	  .html(function(d) {
		  	var returnArray = defineNumberAndExtension(d.data.previousTime)
		  	var scenarioHTML = "<p>Scenario: <span style='font-weight:bold;'>" + d.data.label + "</span></p>"
		  	scenarioHTML += "<table style='width: 100%; text-align: center; margin-bottom: 10px;'>"
	  		scenarioHTML += "<tr>"
	  		scenarioHTML += "<td><i class='fa fa-clock-o'></i> previous: <span style='font-weight:bold;'>" + returnArray[0] + " " + returnArray[1] + "</span></td>"	
	  		returnArray = defineNumberAndExtension(d.data.score)
	  		scenarioHTML += "<td><i class='fa fa-clock-o'></i> current: <span style='font-weight:bold;'>" + returnArray[0] + " " + returnArray[1] + "</span></td>"
	  		scenarioHTML += "<td><i class='material-icons' style='vertical-align: middle; padding-bottom: 3px;'>change_history</i>: <span style='font-weight:bold;'>" + Math.round(d.data.width * 100) / 100 + " %</span></td>"
	  		scenarioHTML += "</table>"
		  	//scenarioHTML += "<p style='margin-bottom: 7px;'>Previous Execution Time: <span style='font-weight:bold;'>" + returnArray[0] + " " + returnArray[1] + "</span></p>"
		  	//scenarioHTML += "<p style='margin-bottom: 7px;'>Current Execution Time: <span style='font-weight:bold;'>" + returnArray[0] + " " + returnArray[1] + "</span></p>"
		  	//scenarioHTML += "<p style='margin-bottom: 7px;'>Deviation: <span style='font-weight:bold;'>" + Math.round(d.data.width * 100) / 100 + " %</span></p>"
		  	scenarioHTML += "<p style='color: blue; font-weight: bold; margin-bottom: 0px;'>Click for more details.</p>"
		  	return scenarioHTML;
	  })
	  
	var arc = d3.svg.arc()
	  .innerRadius(innerRadius)
	  .outerRadius(function (d) {
		  return ((radius - innerRadius) * (d.data.score / Math.ceil(maiorScore)) + innerRadius) + minRadius; 
	  });
	
	var outlineArc = d3.svg.arc()
	        .innerRadius(innerRadius)
	        .outerRadius(radius + minRadius);
	
	var svg = d3.select("#svgAsterPlot").append("svg")
	    .attr("width", width + (minRadius * 2.2))
	    .attr("height", height + (minRadius * 2.2))
	    .append("g")
	    .attr("transform", "translate(" + (width + (minRadius * 2.2)) / 2 + "," + (height + (minRadius * 2.2)) / 2 + ")");
	
	svg.call(tip);

	// for (var i = 0; i < data.score; i++) { console.log(data[i].id) }
	  var path = svg.selectAll(".solidArc")
	      .data(pie(jsonScenarios))
	    .enter().append("path")
	      .attr("fill", function(d) { return d.data.color; })
	      .attr("class", "solidArc")
	      .attr("stroke", "gray")
	      .attr("d", arc)
	      .on('mouseover', tip.show)
	      .on('mouseout', tip.hide)
	      .on('click', function(d) {
	    	  window.location = d.data.url;
	      });
	
	  var outerPath = svg.selectAll(".outlineArc")
	      .data(pie(jsonScenarios))
	      .enter().append("path")
	      .attr("fill", "none")
	      .attr("stroke", "gray")
	      .attr("class", "outlineArc")
	      .attr("d", outlineArc);  
	
	
	  // calculate the weighted mean score
//	  var score = 
//		  jsonScenarios.reduce(function(a, b) {
//	      //console.log('a:' + a + ', b.score: ' + b.score + ', b.weight: ' + b.weight);
//	      return a + (b.score * b.weight); 
//	    }, 0) / 
//	    jsonScenarios.reduce(function(a, b) { 
//	      return a + b.weight; 
//	    }, 0);
	
//	  svg.append("svg:text")
//	    .attr("class", "aster-score")
//	    .attr("dy", ".35em")
//	    .attr("text-anchor", "middle") // text-align: right
//	    .text(Math.round(score));
	  
	  $("#legend").css("height", height)
	
});

$(window).load(function() {
	$("[data-toggle='offcanvas']").click()
});