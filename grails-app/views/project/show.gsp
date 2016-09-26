<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
	</head>
	<body>
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>Detalhes do Projeto</h1>
		</section>
		
		<!-- Main content -->
		<section class="content">
			<g:if test="${flash.message}">
				<div class="alert alert-success alert-dismissable">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
					<h4><i class="icon fa fa-check"></i> Sucesso!</h4>
					${flash.message}
				</div>
			</g:if>
			<div class="box box-default">
                <div class="box-header with-border">
					<h3 class="box-title">Dados Básicos</h3>
                </div><!-- /.box-header -->
                
                <dl class="dl-horizontal">
                    <dt><g:message code="project.name.label" default="Name" /></dt>
                    <dd><span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${projectInstance}" field="name"/></span></dd>
                    <dt><g:message code="project.repositoryUrl.label" default="Repository Url" /></dt>
                    <dd><g:fieldValue bean="${projectInstance}" field="repositoryUrl"/></dd>
                    <dt><g:message code="project.repositoryType.label" default="Repository Type" /></dt>
                    <dd><g:fieldValue bean="${projectInstance}" field="repositoryType"/></dd>
				</dl>
                
                <div class="box-footer">
       	            <g:form url="[resource:projectInstance, action:'delete']" method="DELETE">
						<g:actionSubmit class="btn btn-danger btn-flat pull-right" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
						<g:link class="btn btn-default btn-flat pull-right btn-action" action="edit" resource="${projectInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					</g:form>
				</div><!-- /.box-footer -->
                
              </div>
		</section>
	</body>
</html>
