<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<asset:javascript src="bootstrap3-typeahead.js"/>
	</head>
	<body>
		<div id="page-body" role="main">
			<input type="text" class="typeahead">
			<input type="hidden" id="scenarios" value="${scenarios}">
		</div>
		<script type="text/javascript">
			var $input = $('.typeahead');
			var scenarios = $("#scenarios").val();
			$input.typeahead({
					source: [
						{id: 1, name: "ABC"},
						{id: 2, name: "América"},
						{id: 3, name: "Alecrim"}
						]
				}); 
			$input.change(function() {
			    var current = $input.typeahead("getActive");
			    console.log(current);
			    if (current) {
			        // Some item from your model is active!
			        if (current.name == $input.val()) {
			            // This means the exact match is found. Use toLowerCase() if you want case insensitive match.
			            console.log(current);
			            console.log($input.val());
			        } else {
			            // This means it is only a partial match, you can either add a new item 
			            // or take the active if you don't want new items
			        }
			    } else {
			        // Nothing is active so it is a new value (or maybe empty value)
			    }
			});
		</script>
	</body>
</html>
