function validateForm() {
	var isValid = true
	isValid = validateRequiredFields()
	
	if (isValid == true) {
		$('.overlay').css('display', 'inherit');
		return true
	} else {
		return false
	}
	
}

function validateRequiredFields() {
	var isValid = true
	var inputSystemName = $("#systemName")
	var backupFile = $("#backupFile")
	
	if (inputSystemName.val().trim() == "" || inputSystemName.val().trim() == null) {
		inputSystemName.parent().addClass("has-error")
		isValid = false
	} else {
		inputSystemName.parent().removeClass("has-error")
	}
	
	if (backupFile.val() == "" || backupFile.val() == null) {
		backupFile.parent().addClass("has-error")
		isValid = false
	} else {
		backupFile.parent().removeClass("has-error")
	}
	
	return isValid
}