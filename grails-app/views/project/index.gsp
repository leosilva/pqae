<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
	</head>
	<body>
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>Listar Projetos</h1>
		</section>
		
		<!-- Main content -->
		<section class="content">
			<g:if test="${flash.message}">
				<div class="alert alert-info alert-dismissable">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
					<h4><i class="icon fa fa-check"></i> Informação!</h4>
					${flash.message}
				</div>
			</g:if>
			<div class="box box-default">
				<div class="box-body table-responsive no-padding">
                  <table class="table table-hover">
                  	<thead>
						<tr>
							<g:sortableColumn property="name" title="${message(code: 'project.name.label', default: 'Name')}" />
							<g:sortableColumn property="repositoryUrl" title="${message(code: 'project.repositoryUrl.label', default: 'Repository Url')}" />
							<g:sortableColumn property="repositoryType" title="${message(code: 'project.repositoryType.label', default: 'Repository Type')}" />
							<th>Ações</th>
						</tr>
					</thead>
					<tbody>
						<g:each in="${projectInstanceList}" status="i" var="projectInstance">
							<tr>
								<td>${fieldValue(bean: projectInstance, field: "name")}</td>
								<td>${fieldValue(bean: projectInstance, field: "repositoryUrl")}</td>
								<td>${fieldValue(bean: projectInstance, field: "repositoryType")}</td>
								<td>
									<g:link controller="project" action="show" class="btn btn-xs btn-default btn-flat col-xs-2 btn-action" id="${projectInstance?.id}" title="Detalhes"><i class="fa fa-info"></i></g:link>
									<g:link name="linkClone" controller="project" action="clone" class="btn btn-xs btn-default btn-flat col-xs-2 btn-action ${!projectInstance?.isClonned ?: 'disabled'}" params="[id : projectInstance?.id]" title="Clone/Checkout">
										<i class="fa fa-clone"></i>
									</g:link>
									<g:link controller="project" action="findClassInfo" class="btn btn-xs btn-default btn-flat col-xs-2 btn-action" title="Analisar"><i class="fa fa-dashboard"></i></g:link>
								</td>
							</tr>
						</g:each>
					</tbody>
                  </table>
                </div>
                <div class="box-footer clearfix text-center">
	                <ul class="pagination pagination-sm no-margin">
	                	<boots:paginate total="${projectInstanceCount ?: 0}" /> 
					</ul>
                </div>
                <div class="overlay hidden">
		        	<i class="fa fa-refresh fa-spin"></i>
		        </div>
			</div>
		</section>
		<!-- /.content -->
		<script>
			$(document).ready(function() {
				$("[name=linkClone]").on('click', function(e) {
					$('.overlay').removeClass('hidden');
				});
			});
		</script>
	</body>
</html>
