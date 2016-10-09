<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<!-- IMPORTAÇÕES ESPECÍFICAS DESTA PÁGINA -->
		<!-- INÍCIO JAVASCRIPT -->
		<!--<asset:javascript src="drawUMLDiagram.js" />-->
		<asset:javascript src="events.js"/>
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
			   padding-left: 9px;
			   width: 7%;
			   cursor: pointer;
			}
			.releaseTitle {
				width: 50%;
			    float: left;
			    text-align: center;
			}
			
			.generalInfoSpan {
				font-weight: bold;
			}
			
			.paper-scroller {
				padding: 20px !important;
			}
			
			.info-box-icon {
				height: 50px !important;
    			width: 50px !important;
    			font-size: 35px !important;
    			line-height: 55px !important;
			}
			
			.info-box-content {
				margin-left: 50px !important;
				padding: 2px 10px !important;
			}
			
			.info-box {
				min-height: 50px !important;
			}
			
			.col-md-6, .col-md-12 {
				padding-left: 5px;
				padding-right: 5px;
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
			--%><div style="width: 28%; float: left; margin: 10px 10px 0 10px;">
				<h3>Description</h3>
				<div class="col-md-6">
					<div class="info-box">
						<div class="info-box-content" style="margin-left: 0px !important">
							<span class="info-box-text">System</span>
							<span class="info-box-number" style="font-size: 14px !important;">${info.system}</span>
						</div>
						<!-- /.info-box-content -->
					</div>
					<!-- /.info-box -->
				</div>
				<div class="col-md-6">
					<div class="info-box">
						<div class="info-box-content" style="margin-left: 0px !important">
							<span class="info-box-text">Versions</span>
							<span class="info-box-number" style="font-size: 14px !important;">${info.versionFrom} to ${info.versionTo}</span>
						</div>
						<!-- /.info-box-content -->
					</div>
					<!-- /.info-box -->
				</div>
				<div class="col-md-12">
					<div class="info-box">
						<div class="info-box-content" style="margin-left: 0px !important">
							<span class="info-box-text">Scenario</span>
							<span class="info-box-number" style="font-size: 14px !important;">${info.scenarioName}</span>
						</div>
						<!-- /.info-box-content -->
					</div>
					<!-- /.info-box -->
				</div>
				<p>Broad Time:
					<span class="generalInfoSpan">${info.broadScenarioTime} ms</span>
				</p>
				<h3>Nodes</h3>
				<div class="col-md-6">
					<div class="info-box">
						<span class="info-box-icon bg-aqua">
							<i class="fa fa-bars"></i>
						</span>
						<div class="info-box-content">
							<span class="info-box-text">Total</span>
							<span class="info-box-number">${info.totalNodes}</span>
						</div>
						<!-- /.info-box-content -->
					</div>
					<!-- /.info-box -->
				</div>
				<div class="col-md-6">
					<div class="info-box">
						<span class="info-box-icon bg-aqua">
							<i class="fa fa-files-o"></i>
						</span>
						<div class="info-box-content">
							<span class="info-box-text">Deviation</span>
							<span class="info-box-number">${info.deviationNodes}</span>
						</div>
						<!-- /.info-box-content -->
					</div>
					<!-- /.info-box -->
				</div>
				<div class="col-md-6">
					<div class="info-box">
						<span class="info-box-icon bg-aqua">
							<i class="fa fa-plus"></i>
						</span>
						<div class="info-box-content">
							<span class="info-box-text">Added</span>
							<span class="info-box-number">${info.addedNodes}</span>
						</div>
						<!-- /.info-box-content -->
					</div>
					<!-- /.info-box -->
				</div>
				<div class="col-md-6">
					<div class="info-box">
						<span class="info-box-icon bg-aqua">
							<i class="fa fa-minus"></i>
						</span>
						<div class="info-box-content">
							<span class="info-box-text">Removed</span>
							<span class="info-box-number">${info.removedNodes}</span>
						</div>
						<!-- /.info-box-content -->
					</div>
					<!-- /.info-box -->
				</div>
				<div class="col-md-6">
					<div class="info-box">
						<span class="info-box-icon bg-aqua">
							<i class="fa fa-eye"></i>
						</span>
						<div class="info-box-content">
							<span class="info-box-text">Viewing</span>
							<span class="info-box-number">${info.showingNodes}</span>
						</div>
						<!-- /.info-box-content -->
					</div>
					<!-- /.info-box -->
				</div>
			</div>
			<div class="mailbox-controls text-center" style="height: 40px; width: 70%; float: right;">
				<!-- Check all button -->
				<div class="btn-group">
					<button id="zoomOutButton" class="btn btn-default btn-sm" title="Zoom Out">
						<i class="fa fa-search-minus"></i>
					</button>
					<button id="zoomToFitButton" class="btn btn-default btn-sm" title="Zoom to Fit">
						<i class="fa fa-search"></i>
					</button>
					<button id="zoomInButton" class="btn btn-default btn-sm" title="Zoom In">
						<i class="fa fa-search-plus"></i>
					</button>
				</div>
			</div>
			<div style="width: 70%; height: 530px; overflow: scroll; border: 1px solid black; float: right;">
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
