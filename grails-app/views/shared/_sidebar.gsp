<!-- sidebar: style can be found in sidebar.less -->
<section class="sidebar">

	<!-- Sidebar Menu -->
	<ul class="sidebar-menu">
		<li class="header">
			${message(code: "sidebar.menu.navigation").toUpperCase()}
		</li>
		<!-- Optionally, you can add icons to the links -->
		<!-- 
		<li class="active">
			<g:link controller="project" action="create">
				<i class="fa fa-plus"></i> <span>Novo Projeto</span>
			</g:link>
		</li>
		<li>
			<g:link controller="project" action="index">
				<i class="fa fa-bars"></i> <span>Listar Projetos</span>
			</g:link>
		</li>
		-->
		<g:if test="${backPage}">
			<li>
				<g:link url="${backPage}">
					<i class="fa fa-arrow-left" aria-hidden="true"></i> <span>Back</span>
				</g:link>
			</li>
		</g:if>
		<li>
			<g:link controller="index" action="index" >
				<i class="fa fa-tasks" aria-hidden="true"></i> <span><g:message code="sidebar.menu.navigation.systems"/></span>
			</g:link>
		</li>
		
	</ul>
	<!-- /.sidebar-menu -->
</section>
<!-- /.sidebar -->