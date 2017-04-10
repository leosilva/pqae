<%@page import="architecturevisualization.AnalyzedSystemStatus"%>
<g:each in="${ans}" var="an" status="i" >
	<div class="box">
		<div class="box-header">
			<h3 class="box-title">
				${an.getKey()}
			</h3>
		</div>
		<!-- /.box-header -->
		<div class="box-body no-padding">
			<table class="table table-striped table-responsive">
				<tbody>
					<tr>
						<th><g:message code="analyzedSystems.versionFrom" /></th>
						<th><g:message code="analyzedSystems.versionTo" /></th>
						<th><g:message code="analyzedSystems.status" /></th>
						<th><g:message code="analyzedSystems.actions" /></th>
					</tr>
					<g:each in="${an.getValue()}" var="v">
						<tr>
							<td>
								${v.previousVersion}
							</td>
							<td>
								${v.nextVersion}
							</td>
							<td>
								<g:if test="${v.analyzedSystemStatus == AnalyzedSystemStatus.PENDING}">
									<span class="label label-warning" id="status${v.id}">${v.analyzedSystemStatus}&nbsp;&nbsp;<i class="fa fa-refresh fa-spin"></i></span>
								</g:if>
								<g:elseif test="${v.analyzedSystemStatus == AnalyzedSystemStatus.ERROR}">
									<span class="label label-danger">${v.analyzedSystemStatus}</span>
								</g:elseif>
								<g:else>
									<span class="label label-primary">${v.analyzedSystemStatus}</span>
								</g:else>
							</td>
							<td id="actions${v.id}">
								<g:if test="${v.analyzedSystemStatus == AnalyzedSystemStatus.COMPLETED}">
									<g:link controller="deviationScenarios" action="index"
										params="['systemName' : an.getKey(), 'previousVersion' : v.previousVersion, 'nextVersion' : v.nextVersion, 'targetUri': createLink(controller: controllerName, action:'index', params:params, absolute:true)]"
										title="${message(code: 'analyzedSystems.actions.showDeviationScenarios.title')}">
										<i class="material-icons">donut_small</i>
									</g:link>
									<!-- &nbsp;
									<g:link controller="responsibleMethods" action="showResponsibleMethods" params="['systemName' : an.getKey(), 'previousVersion' : v.previousVersion, 'nextVersion' : v.nextVersion, 'targetUri': createLink(controller: controllerName, action:'index', params:params, absolute:true)]" title="${message(code: 'application.pageTitle.networkGraph')}">
										<i class="ionicons ion-network" style="font-size: 18px; position: relative; top: -2px;"></i>
									</g:link> -->
									<g:if env="development">
										&nbsp;
										<g:link controller="analysis" action="deleteAnalysis" params="['systemName' : an.getKey(), 'previousVersion' : v.previousVersion, 'nextVersion' : v.nextVersion]" onclick="return confirm('Are you sure?');" title="${message(code: 'default.button.delete.label')}">
											<i class="material-icons">delete</i>
										</g:link>
									</g:if>
								</g:if>
								<g:elseif test="${v.analyzedSystemStatus == AnalyzedSystemStatus.ERROR}">
									<g:link controller="analysis" action="deleteAnalysis" params="['systemName' : an.getKey(), 'previousVersion' : v.previousVersion, 'nextVersion' : v.nextVersion]" onclick="return confirm('Are you sure?');" title="${message(code: 'default.button.delete.label')}">
										<i class="material-icons">delete</i>
									</g:link>
								</g:elseif>
							</td>
						</tr>
					</g:each>
				</tbody>
			</table>
		</div>
		<!-- /.box-body -->
	</div>
</g:each>