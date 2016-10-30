$(document).ready(function () {
	var fileInput = $("#uploaded-file");
	var cancelBtn = $("#file-cancel-btn");
	var filenameArea = $("#filename-area");
	var uploadBtn = $("#file-upload-btn");
	var defaultTextWidth = 20;
	
	filenameArea.css("resize", "none");
	filenameArea.attr("rows", 1);
	$("#uploaded-file").change(function () {
		
		/* Show the filename in the field */
		if (fileInput[0].files[0] !== null) {
			var fileNames = "";
			var maxLength = 0;
			var fileCount = fileInput[0].files.length;
			
			for (var file of fileInput[0].files) {
				fileNames += file.name + "\n";
				
				if (file.name.length > maxLength) {
					maxLength = file.name.length;
				}
			}
			
			filenameArea.attr("rows", fileCount)
			filenameArea.attr("cols", (maxLength + 2 * fileCount));
			filenameArea.val(fileNames);
			cancelBtn.removeClass("hidden");
			uploadBtn.attr("disabled", false);
			
		} else {
			cancelBtn.addClass("hidden");
			uploadBtn.attr("disabled", true);
		}
	});
	
	$("#file-cancel-btn").click(function () {
		fileInput.val("");
		uploadBtn.attr("disabled", true);
		filenameArea.val("");
		filenameArea.attr("rows", 1);
		console.log(defaultTextWidth);
		filenameArea.attr("cols", defaultTextWidth);
	});
});
