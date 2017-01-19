<div class="box box-default">
	<div class="box-header with-border" data-widget="collapse">
		<h3 class="box-title">
			<a role="button" data-widget="collapse"><g:message code="callGraphVisualization.callGraph"/></a>
		</h3>
		<!-- /.box-tools -->
	</div>
	<!-- /.box-header -->
	<div class="box-body bg-gray-light">
		<div class="mailbox-controls text-center" style="width: 100%; margin-top: -10px;">
			<!-- Check all button -->
			<div class="btn-group">
				<button id="zoomOutButton" class="btn btn-default btn-sm"
					title="${message(code: 'callGraphVisualization.callGraph.zoom.out')}">
					<i class="fa fa-search-minus"></i>
				</button>
				<button id="zoomToFitButton" class="btn btn-default btn-sm"
					title="${message(code: 'callGraphVisualization.callGraph.zoom.fit')}">
					<i class="fa fa-search"></i>
				</button>
				<button id="zoomInButton" class="btn btn-default btn-sm"
					title="${message(code: 'callGraphVisualization.callGraph.zoom.in')}">
					<i class="fa fa-search-plus"></i>
				</button>
			</div>
			<div class="btn-group">
				<button id="hightlightButton" class="btn btn-default btn-sm" title="${message(code: 'callGraphVisualization.callGraph.highlight.on')}">
					<i class="material-icons" style="top: 2px; position: relative; font-size: 12px !important;">highlight</i>
				</button>
				<button id="hightlightOffButton" class="btn btn-default btn-sm" title="${message(code: 'callGraphVisualization.callGraph.highlight.off')}">
					<i class="material-icons" style="top: 2px; position: relative; font-size: 12px !important;">highlight_off</i>
				</button>
			</div>
			<div class="btn-group" style="float: right;">
				<button type="button" id="helpButton" class="btn btn-box-tool" title="Help">
					<i class="fa fa-question"></i>
				</button>
			</div>
		</div>
		<div id="evolvePaper" class="evolve-paper">
			<div id="paperNextVersion" class="evolve-paper-graph"></div>
		</div>
		<div id="evolveLegend" class="text-center evolve-legend">
			<div class="legend-block">
				<div class="legend-square legend-no-deviation"></div>
				<span class="legend-text">
					<g:message code="callGraphVisualization.legend.noDeviation"/>
				</span>
			</div>
			<div class="legend-block">
				<div class="legend-square legend-optimization"></div>
				<span class="legend-text">
					<g:message code="callGraphVisualization.legend.optimization"/>
				</span>
			</div>
			<div class="legend-block">
				<div class="legend-square legend-degradation"></div>
				<span class="legend-text">
					<g:message code="callGraphVisualization.legend.degradation"/>
				</span>
			</div>
			<div class="legend-block">
				<div class="legend-square legend-added"></div>
				<span class="legend-text">
					<g:message code="callGraphVisualization.legend.added"/>
				</span>
			</div>
			<div class="legend-block">
				<div class="legend-square legend-removed"></div>
				<span class="legend-text">
					<g:message code="callGraphVisualization.legend.removed"/>
				</span>
			</div>
		</div>
	</div>
</div>