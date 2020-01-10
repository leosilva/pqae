<div class="col-md-12 pl0 pr0">
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
					<button id="highlightButton" class="btn btn-default btn-sm" title="${message(code: 'callGraphVisualization.callGraph.highlight')}" 
					data-toggle="button" aria-pressed="false" autocomplete="off">
						<i class="material-icons" style="top: 2px; position: relative; font-size: 12px !important;">highlight</i>
					</button>
				</div>
				<div class="btn-group">
					<button id="packageButton" class="btn btn-default btn-sm" title="${message(code: 'callGraphVisualization.callGraph.method')}" 
					data-toggle="button" aria-pressed="false" autocomplete="off">
						<i class="material-icons" style="top: 2px; position: relative; font-size: 12px !important;">account_balance_wallet</i>
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
				<div id="legendBox" class="col-md-2" style="width: 180px;">
					<div id="legendBoxChildDiv" class="box box-default box-solid">
						<div id="legendBoxHeader" class="box-header with-border text-center" data-widget="collapse" style="padding: 5px;">
							<h3 class="box-title" style="font-size: 14px;"><g:message code="callGraphVisualization.caption.title"/></h3>
						</div>
						<!-- /.box-header -->
						<div class="box-body">
							<div id="evolveLegend" class="evolve-legend">
								<div class="legend-block">
									<div class="legend-square legend-no-deviation"></div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.noDeviation"/>
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-square legend-optimization"></div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.optimization"/>
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-square legend-degradation"></div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.degradation"/>
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-square legend-added"></div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.added"/>
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-square legend-removed"></div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.removed"/>
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-square legend-many-times"></div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.manyTimes" />
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-square text-center" style="border: none;">
										<i class='ionicons ion-arrow-up-b optimization arrow'></i>
									</div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.perfOpt"/> <span id="perfOptHelp" class="btn btn-box-tool"><i class="fa fa-question"></i></span>
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-square text-center" style="border: none;">
										<i class='ionicons ion-arrow-down-b degradation arrow'></i>
									</div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.perfDeg"/> <span id="perfDegHelp" class="btn btn-box-tool"><i class="fa fa-question"></i></span>
									</span>
								</div>
							</div>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
			</div>
		</div>
	</div>
</div>