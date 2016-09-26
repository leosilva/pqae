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
			   top: 2px;
			   right: 9px;
			}
			
			.infoSpan {
			   position: absolute;
			   top: 30px;
			   right: 9px;
			}
			.releaseTitle {
				width: 50%;
			    float: left;
			    text-align: center;
			}
		</style>
	</head>
	<body>
		<div id="page-body" role="main">
			<input type="hidden" name="mapScenariosPreviousVersion" id="mapScenariosPreviousVersion" value="${mapPreviousVersion}"/>
			<input type="hidden" name="mapScenariosNextVersion" id="mapScenariosNextVersion" value="${mapNextVersion}"/>
			<div style="height: 30px;">
				<div class="releaseTitle">
					<h5><span style="font-weight: bold;">Version</span>: 9.3.0.M0 ::::: <span style="font-weight: bold;">Scenario</span>: ${scenarioPV.name}</h5>
				</div>
				<div class="releaseTitle">
					<h5><span style="font-weight: bold;">Version</span>: 9.3.0.M1 ::::: <span style="font-weight: bold;">Scenario</span>: ${scenarioNV.name}</h5>
				</div>
			</div>
			<div style="width: 49.5%; height: 500px; overflow: scroll; border: 1px solid black; margin-right: 10px; float: left;">
				<div id="paperPreviousVersion"></div>
			</div>
			<div style="width: 49.5%; height: 500px; overflow: scroll; border: 1px solid black;">
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
