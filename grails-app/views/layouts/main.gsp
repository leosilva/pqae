<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!-->
<html lang="en" class="no-js">
	<!--<![endif]-->
	<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<!-- Tell the browser to be responsive to screen width -->
	<meta
		content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
		name="viewport">
	<title><g:layoutTitle default="Architecture Visualization" /></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<!-- IMPORTAÇÕES GERAIS PARA A APLICAÇÃO -->
	<!-- INÍCIO CSS -->
	<asset:stylesheet href="joint.css" />
	<!-- INICIO BOOTSTRAP 3.3.5-->
	<asset:stylesheet href="bootstrap.min.css" />
	<asset:stylesheet href="font-awesome.min.css" />
	<asset:stylesheet href="ionicons.min.css" />
	<asset:stylesheet href="material_icons.css" />
	<!-- FIM BOOTSTRAP -->
	<!-- FIM CSS -->
	<!-- INÍCIO JAVACSRIPT -->
	<asset:javascript src="generalInformation.js" />
	<asset:javascript src="utils.js" />
	<asset:javascript src="jquery.min.js" />
	<asset:javascript src="bootstrap.min.js" />
	<asset:javascript src="app.js" />
	<asset:javascript src="lodash.min.js" />
	<asset:javascript src="backbone-min.js" />
	<asset:javascript src="joint.js" />
	<asset:javascript src="joint.shapes.uml.js" />
	<asset:javascript src="graphlib.js" />
	<asset:javascript src="dagre.js" />
	<asset:javascript src="joint.layout.DirectedGraph.js" />
	<asset:javascript src="layout.js" />
	<asset:javascript src="application.js" />
	<!-- FIM JAVASCRIPT -->
	<g:layoutHead />
	<!-- OS PROXIMOS ARQUIVOS CSS ESTÃO NESTE LUGAR POIS SOBRESCREVEM ALGUMAS CLASSES IMPORTADAS ANTERIORMENTE -->
	<asset:stylesheet href="AdminLTE.css" />
	<asset:stylesheet href="skin-black-light.min.css" />
	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
		        <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
		        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
		    <![endif]-->
	</head>
	<body class="hold-transition skin-black-light sidebar-mini">
		<div class="wrapper">
			<!-- Main Header -->
			<header class="main-header">
				<g:render template="/shared/header" />
			</header>
			<!-- Left side column. contains the logo and sidebar -->
			<aside class="main-sidebar">
				<g:render template="/shared/sidebar" />				
			</aside>
	
			<!-- Content Wrapper. Contains page content -->
			<div class="content-wrapper">
				<g:layoutBody />
			</div>
			<!-- /.content-wrapper -->
	
			<!-- Main Footer -->
			<footer class="main-footer">
				<g:render template="/shared/footer" />
			</footer>
		</div>
		<!-- ./wrapper -->
	</body>
</html>
