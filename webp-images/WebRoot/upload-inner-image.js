document.domain = "wn518.com";
var upload_image_site_root = "http://imgs-server.corp.wn518.com/";

function UploadImage(app) {
	this.app = app;
}

UploadImage.prototype.openUploadImage = function() {
	window.open(upload_image_site_root + "show_upload_inner_image.wn?app=" + this.app);
};
