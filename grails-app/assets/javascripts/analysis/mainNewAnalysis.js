$(document).ready(function() {
	$("#systemName").on("change", getBackupsBySystemName);
	getBackupsBySystemName();
});

function showWaitMessage() {
	setTimeout(function(){
    	$(".wait-message").css("display", "inherit");
    }, 5000);
}

function redirectToHomePage() {
	setTimeout(function(){
    	window.location = $("#rootURL").val()
    }, 20000);
}

function getBackupsBySystemName() {
	var systemName = $("#systemName").val()
	if (systemName != null || systemName != "") {
		$.ajax({
			method : "POST",
			url : $("#ajaxURL").val(),
			data : {"systemName" : systemName},
			dataType : "json",
			success : function(data) {
				$.each(data, function(v) {
					$('#backupFilePreviousVersion').append($('<option>', {
						value: v,
						text: v
					}));
					$('#backupFilePreviousVersion')[0].disabled = false
					$('#backupFileNextVersion').append($('<option>', {
						value: v,
						text: v
					}));
					$('#backupFileNextVersion')[0].disabled = false
				})
			}
		})
	}
	
}

function resetForm() {
	$("#backupFilePreviousVersion").empty()
	$('#backupFilePreviousVersion').append($('<option>', {
		value: "",
		text: ""
	}));
	$("#backupFilePreviousVersion")[0].disabled = true

	$("#backupFileNextVersion").empty()
	$('#backupFileNextVersion').append($('<option>', {
		value: "",
		text: ""
	}));
	$("#backupFileNextVersion")[0].disabled = true
	
	$('form')[0].reset();
}

function validateForm() {
	var isValid = true
	isValid = validateRequiredFields()
	if (isValid == true) {
		isValid = validateBackupSelects()
	}
	
	if (isValid == true) {
		$('.overlay').css('display', 'inherit');
		showWaitMessage()
		redirectToHomePage();
		return true
	} else {
		return false
	}
	
}

function validateRequiredFields() {
	var isValid = true
	var inputSystemName = $("#systemName")
	var selectPreviousBackup = $("#backupFilePreviousVersion")
	var selectNextBackup = $("#backupFileNextVersion")
	var perfMinerFileDegraded = $("#resultFileDegradedScenarios")
	var perfMinerFileOptimized = $("#resultFileOptimizedScenarios")
	var previousVersionInput = $("#previousVersion")
	var nextVersionInput = $("#nextVersion")
	
	if (inputSystemName.val().trim() == "" || inputSystemName.val().trim() == null) {
		inputSystemName.parent().addClass("has-error")
		isValid = false
	} else {
		inputSystemName.parent().removeClass("has-error")
	}
	
	if (selectPreviousBackup.val() == "" || selectPreviousBackup.val() == null) {
		selectPreviousBackup.parent().addClass("has-error")
		isValid = false
	} else {
		selectPreviousBackup.parent().removeClass("has-error")
	}
	
	if (selectNextBackup.val() == "" || selectNextBackup.val() == null) {
		selectNextBackup.parent().addClass("has-error")
		isValid = false
	} else {
		selectNextBackup.parent().removeClass("has-error")
	}
	
	if (perfMinerFileDegraded.val() == "" || perfMinerFileDegraded.val() == null) {
		perfMinerFileDegraded.parent().addClass("has-error")
		isValid = false
	} else {
		perfMinerFileDegraded.parent().removeClass("has-error")
	}
	
	if (perfMinerFileOptimized.val() == "" || perfMinerFileOptimized.val() == null) {
		perfMinerFileOptimized.parent().addClass("has-error")
		isValid = false
	} else {
		perfMinerFileOptimized.parent().removeClass("has-error")
	}
	
	if (previousVersionInput.val().trim() == "" || previousVersionInput.val() == null) {
		previousVersionInput.parent().addClass("has-error")
		isValid = false
	} else {
		previousVersionInput.parent().removeClass("has-error")
	}
	
	if (nextVersionInput.val().trim() == "" || nextVersionInput.val() == null) {
		nextVersionInput.parent().addClass("has-error")
		isValid = false
	} else {
		nextVersionInput.parent().removeClass("has-error")
	}
	
	return isValid
}

function validateBackupSelects() {
	var isValid = true

	var select1 = $("#backupFilePreviousVersion")
	var select2 = $("#backupFileNextVersion")
	if (select1.val().trim() == select2.val().trim()) {
		select1.parent().addClass("has-error")
		select2.parent().addClass("has-error")
		isValid = false
	} else {
		select1.parent().removeClass("has-error")
		select2.parent().removeClass("has-error")
		isValid = true
	}
	return isValid
}