function drawHistoryChart() {
	var labelsSet = new Set()
	var dataSet = new Set()
	var value = $.parseJSON($("#history").val())
	$.each(value, function(i, e) {
		defineGitHubCommitAddress(e)
		$.each(e.analyzedScenarios, function(ind, elm) {
			if (elm.name == $("#scenarioName").html().trim()) {
				//if (i == 0) {
					var time = convertNanoToMilis(elm.previousTime)
					labelsSet.add(e.previousVersion)
					dataSet.add(time)
					//dataSet.add(elm.previousTime)
				//}
				var time = convertNanoToMilis(elm.nextTime)
				labelsSet.add(e.nextVersion)
				dataSet.add(time)
				//dataSet.add(elm.nextTime)
			}
		})
	})
	
	var labelsArray = [...labelsSet]
	var dataArray = [...dataSet]
	
	var ctx = $("#chartDiv");
	var data = {
		    labels: labelsArray,
		    datasets: [
		        {
		            label: yAxisTitle,
		            fill: false,
		            lineTension: 0.1,
		            backgroundColor: "rgba(75,192,192,0.4)",
		            borderColor: "rgba(75,192,192,1)",
		            borderCapStyle: 'butt',
		            borderDash: [],
		            borderDashOffset: 0.0,
		            borderJoinStyle: 'miter',
		            pointBorderColor: "rgba(75,192,192,1)",
		            pointBackgroundColor: "#fff",
		            pointBorderWidth: 1,
		            pointHoverRadius: 8,
		            pointHoverBackgroundColor: "rgba(75,192,192,1)",
		            pointHoverBorderColor: "rgba(220,220,220,1)",
		            pointHoverBorderWidth: 2,
		            pointRadius: 4,
		            pointHitRadius: 10,
		            data: dataArray,
		            spanGaps: false
		        }
		    ]
		};
	
	var options = {
		responsible: true,
		maintainAspectRatio: false,
		title: {
			display: true,
			text: graphTitle
		},
		legend: {
			display: false
		},
		scales: {
		    yAxes: [{
		      scaleLabel: {
		        display: true,
		        labelString: yAxisTitle
		      }
		    }],
		    xAxes: [{
		      scaleLabel: {
		        display: true,
		        labelString: xAxisTitle
		      }
		    }],
		},
		layout: {
			padding: 0
		}
	}
	
	var myLineChart = new Chart(ctx, {
	    type: 'line',
	    data: data,
	    options: options
	});
}

function defineGitHubCommitAddress(e) {
	if (e.systemName.includes("Jetty")) {
		githubCommitsAddress = githubCommitAddressJettyServlet	
	} else if (e.systemName.includes("VRaptor")) {
		githubCommitsAddress = githubCommitAddressVRaptor 
	} else if (e.systemName.includes("wicket")) {
        githubCommitsAddress = githubCommitAddressWicket
	} else if (e.systemName.includes("netty")) {
        githubCommitsAddress = githubCommitAddressNetty
    }
}