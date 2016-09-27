document.domain = "wn518.com";
var upload_image_site_root = "http://imgs-server.wn518.com:8080/image/";

function UploadImage(app) {
	this.app = app;
}

UploadImage.prototype.openUploadImage = function(callback) {
	window.open(upload_image_site_root + "show_upload_image.wn?app=" + this.app + "&callback=" + callback);
};
