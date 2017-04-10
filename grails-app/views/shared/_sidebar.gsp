<!-- sidebar: style can be found in sidebar.less -->
<section class="sidebar">

	<!-- Sidebar Menu -->
	<ul class="sidebar-menu">
		<g:if env="development">
			<li class="header">
				${message(code: "sidebar.menu.navigation").toUpperCase()}
			</li>
		</g:if>
		<!-- Optionally, you can add icons to the links -->
		<g:if test="${backPage}">
			<li>
				<g:link url="${backPage}">
					<i class="fa fa-arrow-left" aria-hidden="true"></i> <span><g:message code="sidebar.menu.navigation.back" /></span>
				</g:link>
			</li>
		</g:if>
		<g:if env="development">
			<li>
				<g:link controller="index" action="index" >
					<i class="fa fa-tasks" aria-hidden="true"></i> <span><g:message code="sidebar.menu.navigation.systems"/></span>
				</g:link>
			</li>
			<li>
				<g:link controller="analysis" action="startAnalysis" params="[targetUri : createLink(uri: '/')]">
					<i class="fa fa-plus" aria-hidden="true"></i> <span><g:message code="sidebar.menu.navigation.newAnalysis" /></span>
				</g:link>
			</li>
			<li>
				<g:link controller="config" action="preBackupUpload" params="[targetUri : createLink(uri: '/')]">
					<i class="fa fa-upload" aria-hidden="true"></i> <span><g:message code="sidebar.menu.navigation.config.backupUpload"/></span>
				</g:link>
			</li>
		</g:if>
	</ul>
	<!-- /.sidebar-menu -->
</section>
<!-- /.sidebar -->