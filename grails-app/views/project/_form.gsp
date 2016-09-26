<div class="box-body">
	<div class="form-group ${hasErrors(bean: projectInstance, field: 'name', 'has-error')}">
		<label for="name" class="col-sm-2 control-label">
			<g:message code="project.name.label" default="Name" />
		</label>
		<div class="col-sm-10">
			<g:textField name="name" required="required" value="${projectInstance?.name}" class="form-control"/>
		</div>
	</div>
	<div class="form-group ${hasErrors(bean: projectInstance, field: 'repositoryUrl', 'has-error')}">
		<label for="repositoryUrl" class="col-sm-2 control-label">
			<g:message code="project.repositoryUrl.label" default="Repository Url" />
		</label>
		<div class="col-sm-10">
			<g:textField name="repositoryUrl" required="required" value="${projectInstance?.repositoryUrl}" class="form-control"/>
		</div>
	</div>
	<div class="form-group ${hasErrors(bean: projectInstance, field: 'repositoryType', 'has-error')}">
		<label for="repositoryType" class="col-sm-2 control-label">
			<g:message code="project.repositoryType.label" default="Repository Type" />
		</label>
		<div class="col-sm-10">
			<g:select name="repositoryType"
				from="${architecturevisualization.RepositoryType?.values()}"
				keys="${architecturevisualization.RepositoryType.values()*.name()}"
				required="required" value="${projectInstance?.repositoryType?.name()}"
				class="form-control select2" />
		</div>
	</div>
</div>
<script>
	$(".select2").select2({
		  minimumResultsForSearch: Infinity
	});
</script>
<!-- /.box-body -->