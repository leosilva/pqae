<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<!-- IMPORTAÇÕES ESPECÍFICAS DESTA PÁGINA -->
		<!-- INÍCIO JAVASCRIPT -->
		<!--<asset:javascript src="drawUMLDiagram.js" />-->
		<asset:javascript src="methodCallGraph/drawHTMLElement.js"/>
		<asset:javascript src="methodCallGraph/mainCallGraph.js"/>
		<!-- FIM JAVASCRIPT -->
		<style type="text/css">
			.html-element {
			   position: absolute;
			}
			.html-element span {
			   color: black;
			   font-size: 10px;
			}
			
			.timeSpan {
			   position: absolute;
			   top: 3px;
			   right: 9px;
			}
			
			.infoSpan {
			   position: absolute;
			   top: 3px;
			   left: 9px;
			}
			.releaseTitle {
				width: 50%;
			    float: left;
			    text-align: center;
			}
			
			.generalInfoSpan {
				padding-left: 10px;
				font-weight: bold;
				width: 350px;
				display: inline-block;
			}
			
			.paper-scroller {
				padding: 20px !important;
			}
		</style>
	</head>
	<body>
		<div id="page-body" role="main">
			<%--<input type="hidden" name="mapPreviousVersionNodes" id="mapPreviousVersionNodes" value="${previousNodes}"/>
			--%><input type="hidden" name="mapAffectedNodes" id="mapAffectedNodes" value="${affectedNodes}"/>
			<%--<div style="height: 30px;">
				<div class="releaseTitle">
					<h5><span style="font-weight: bold;">Version</span>: 9.3.0.M0 ::::: <span style="font-weight: bold;">Scenario</span>: ${scenarioPV.name}</h5>
				</div>
				<div class="releaseTitle">
					<h5><span style="font-weight: bold;">Version</span>: 9.3.0.M1 ::::: <span style="font-weight: bold;">Scenario</span>: ${scenarioNV.name}</h5>
				</div>
			</div>
			--%><div style="width: 25%; float: left; margin: 10px 10px 0 10px;">
				<p>System: <br>
					<span class="generalInfoSpan">${info.system}</span>
				</p>
				<p>Versions: <br>
					<span class="generalInfoSpan">From: ${info.versionFrom}</span> <br>
					<span class="generalInfoSpan">To: ${info.versionTo}</span>
				</p>
				<p>Scenario: <br>
					<span class="generalInfoSpan">${info.scenarioName}</span>
				</p>
				<p>Broad Time: <br>
					<span class="generalInfoSpan">${info.broadScenarioTime} ms</span>
				</p>
				<p>Total nodes: <br>
					<span class="generalInfoSpan">${info.totalNodes}</span>
				</p>
				<p>Deviation nodes: <br>
					<span class="generalInfoSpan">${info.deviationNodes}</span>
				</p>
			</div>
			<div style="width: 70%; height: 500px; overflow: scroll; border: 1px solid black; float: right;">
				<div id="paperNextVersion"></div>
			</div>
		</div>
		<script>
			$(document).ready(function() {
				$("[data-toggle='offcanvas']").click()
			});
		</script>
	</body>
</html>
