<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
		<asset:stylesheet href="select2.css"/>
		<asset:javascript src="select2.full.js"/>
	</head>
	<body>
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>Criar Projeto</h1>
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
			<g:hasErrors bean="${projectInstance}">
				<div class="alert alert-danger alert-dismissable">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
					<h4><i class="icon fa fa-ban"></i> Erro!</h4>
					Existe(m) erro(s) no cadastro. Verifique os campos destacados em vermelho antes de salvar novamente.                
				</div>
			</g:hasErrors>
			<div class="box box-default">
                <div class="box-header with-border">
                  <h3 class="box-title">Dados Básicos</h3>
                </div><!-- /.box-header -->
                <!-- form start -->
                <g:form url="[resource:projectInstance, action:'save']" class="form-horizontal">
                  <g:render template="form"/>
                  <div class="box-footer">
                    <button type="submit" class="btn btn-info pull-right">Salvar</button>
                  </div><!-- /.box-footer -->
                </g:form>
              </div>
		</section>
		<!-- /.content -->
	</body>
</html>
