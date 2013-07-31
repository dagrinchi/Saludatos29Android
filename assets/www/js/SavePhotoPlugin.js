(function(cordova) {

	function SavePhotoPlugin() {}

	
	SavePhotoPlugin.prototype.saveImageDataToLibrary = function(successCallback, failureCallback, canvasId) {
		if (typeof successCallback != "function") {
			console.log("SavePhotoPlugin Error: successCallback is not a function");
			return;
		}
		if (typeof failureCallback != "function") {
			console.log("SavePhotoPlugin Error: failureCallback is not a function");
			return;
		}
		var canvas = document.getElementById(canvasId);
		var imageData = canvas.toDataURL().replace(/data:image\/png;base64,/,'');
		return cordova.exec(successCallback, failureCallback, "SavePhotoPlugin","saveImageDataToLibrary",[imageData]);
	};

	cordova.addConstructor(function() {
		window.plugins = window.plugins || {};
		window.plugins.SavePhotoPlugin = new SavePhotoPlugin();
	});
	
})(window.cordova || window.Cordova);