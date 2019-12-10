function onBeforeUnload(e)
{
  e.preventDefault();
  e.returnValue = '';
}

var uploadedImgs = [];

function uploadFiles(files)
{
  if (files.length == 0) {
    if (uploadedImgs.length) {
      window.removeEventListener('beforeunload', onBeforeUnload);
      document.location.href = "/i/" + uploadedImgs.join(",");
    }
    return;
  }

  var fileKey = files[0][0];
  var file = files[0][1];
  var formData = new FormData();
  formData.append("fileData", file, file.name);
  formData.append("captchaRes", captchaRes);
  formData.append("ct", "51cb89eb53efb7ce");

  $.ajax({
           type: "POST", url: "upload", data: formData, contentType: false, processData: false, xhr: function () {
      var xhr = $.ajaxSettings.xhr();
      if (xhr.upload) {
        xhr.upload.onprogress = function (e) {
          $("#fileQueue [data-fileKey='" + fileKey + "'] .fileLoad").css("width", Math.round(e.loaded / e.total * 98) + "%");
        };
      }
      return xhr;
    }, success: function (data) {
      if (data.success) {
        uploadedImgs.push(data.fileId);
        $("#fileQueue [data-fileKey='" + fileKey + "'] .fileLoad").css("width", "100%");
      } else {
        toastr.error("A problem occurred during the upload process" + "<div style='margin-top:5px;font-size:0.8em'>" + data + "</div>");
      }
    }, complete: function () {
      uploadFiles(files.slice(1));
    }
         });
}

var uploadStarted = false;

function uploadbutton()
{
  if (uploadStarted) return;

  if (fileQueue.length > 0) {
    uploadStarted = true;
    $("#uploadButton").css("display", "none");
    $("#uploadingButton").css("display", "block");
    $("#fileSelector").prop("disabled", true);
    $("#fileQueue").addClass("uploading");

    if (captchaRes) {
      upload();
    } else {

      UTILS.sendRequest("/captcha/check", {batchSize: fileQueue.length}, function (data) {
        var isCaptchaNeeded = JSON.parse(data);
        if (isCaptchaNeeded) {
          showCaptcha();
        } else {
          upload();
        }
      });

    }
  } else {
    toastr.info('Please add an image to upload.');
  }
}

function upload()
{
  var files = $("#fileQueue").children("div.fileRow").map(function (_, fileRow) {
    var fileKey = $(fileRow).attr("data-fileKey");
    return [[fileKey, fileQueue[fileKey]]]; // Double-wrapping in arrays is necessary due to jQuery flattening top level array
  }).get();
  uploadFiles(files);
}

// Java String's hashCode clone
function hashCode(s)
{
  var hash = 0;
  if (s.length == 0) {
    return hash;
  }
  for (var i = 0; i < s.length; i++) {
    var char = s.charCodeAt(i);
    hash = ((hash << 5) - hash) + char;
    hash = hash & hash; // Convert to 32bit integer
  }
  return hash;
}

function getFileKey(file, dataUrl)
{
  return hashCode(file.name).toString(36) + "|" + hashCode(dataUrl).toString(36) + "|" + file.size.toString(36);
}

function removeFile(fileKey)
{
  if (fileQueue.hasOwnProperty(fileKey)) {
    delete fileQueue[fileKey];
    fileQueue.length--;
    $("#fileQueue").children("[data-fileKey='" + fileKey + "']").remove();
  }
}

var dotLoaderCount = 0;
var fileQueue = {length: 0};

function processFile(file)
{
  dotLoaderCount++;
  $(".dotLoader").css("display", "block");

  var fileReader = new FileReader();
  fileReader.onloadend = function (e) {
    var dataUrl = e.target.result;
    var fileKey = getFileKey(file, dataUrl);

    if (!fileQueue.hasOwnProperty(fileKey) && fileQueue.length < 10) {
      fileQueue[fileKey] = file;
      fileQueue.length++;

      var removeFileBtn = $("<img src='/img/outline-clear-24px.svg' width='18' class='removeFileBtn' />")
        .click(function () {
          removeFile(fileKey)
        });

      var isVideo = dataUrl.substring(0, 20).indexOf('video') > 0;
      var imgPreview = (isVideo ? $("<video muted autoplay loop playsinline poster='/img/round-ondemand_video-24px.svg'>") : $("<img>"))
        .addClass('imgPreview')
        .attr("src", dataUrl);

      var sizeStr = file.size >= 1024 * 1024 ? Math.round(file.size / 1024 / 1024 * 100) / 100 + " MB" : Math.round(file.size / 1024) + " KB";
      var typeStr = !file.type ? "" : " (" + file.type.split("/")[1].toUpperCase() + ")";
      var fileDesc = $("<div class='fileDesc' />")
        .append("<span>" + file.name + "</span>")
        .append("<div class='fileSubText'>" + sizeStr + typeStr + "</div>");

      var fileRow = $("<div class='fileRow' />")
        .attr("data-fileKey", fileKey)
        .append(removeFileBtn)
        .append(imgPreview)
        .append(fileDesc)
        .append("<div class='fileLoad' />");
      $("#fileQueue").append(fileRow);
    }

    if (--dotLoaderCount === 0) {
      $(".dotLoader").css("display", "none");
    }
  };
  fileReader.readAsDataURL(file);
}

function fileSelected(event)
{
  addFiles(event.target.files);
  event.target.value = null;
}

var maxImgSize = 8388608;
var maxVidSize = 20971520;
var imgExts = ["gif", "png", "bmp", "jpg", "jpeg", "webp"];
var vidExts = ["mp4", "wmv", "mov", "avi", "m4v", "mkv", "webm", "mpeg", "flv"];

function addFiles(fileList)
{
  $("#dropCta").css("display", "none");
  var files = Array.prototype.slice.call(fileList); // Copy FileList into an array
  if (fileQueue.length + files.length > 10) {
    toastr.info('You can upload up to 10 images at a time.');
    files = files.slice(0, 10 - fileQueue.length);
  }

  for (var i = 0; i < files.length; i++) {
    var file = files[i];

    var maxFileSize;
    var lowerFileExt = file.name.split('.').pop().toLowerCase();
    if (imgExts.indexOf(lowerFileExt) >= 0) {
      maxFileSize = maxImgSize;
    } else if (vidExts.indexOf(lowerFileExt) >= 0) {
      maxFileSize = maxVidSize;
    } else {
      toastr.info('File type not allowed.');
      continue;
    }

    if (file.size > maxFileSize) {
      toastr.info('Maximum file size allowed is {}MB.'.replace('{}', maxFileSize / 1024 / 1024));
      continue;
    }

    window.setTimeout(processFile, i * 100, file);
  }
  window.addEventListener('beforeunload', onBeforeUnload);
}

var captchaRes = '';

function captchaCallback(res)
{
  $("#captchaContainer").css("display", "none");
  captchaRes = res;
  upload();
}

function captchaErrorCallback()
{
  $("#captchaContainer").css("display", "none");
  toastr.error("CAPTCHA Error. Please try again.");
}

function showCaptcha()
{
  $("#captchaContainer").css("display", "block");
  grecaptcha.render('captcha', {
    'sitekey': '6LedoJsUAAAAAJ_zMlS4mjo1H-PMBPC0kMcedMG_',
    'callback': 'captchaCallback',
    'expired-callback': 'captchaErrorCallback',
    'error-callback': 'captchaErrorCallback'
  });
}

$(function () {
  var $body = $('body');

  // Handle file drag and drop
  if (!('ondragover' in document.body) || !('ondrop' in document.body)) {
    $("#dropCta").css("display", "none");
  }

  var indicatorExpiry = 0;
  $body.bind('dragover', function (e) {
    e.preventDefault();
    e.stopPropagation();
    var dragTypes = e.originalEvent && e.originalEvent.dataTransfer && e.originalEvent.dataTransfer.types;
    if (dragTypes.includes && dragTypes.includes("Files") || dragTypes.contains && dragTypes.contains("Files")) {
      indicatorExpiry = new Date().getTime() + 400;
      $("#dropIndicator").css("display", "block");
      window.setTimeout(function () {
        if (indicatorExpiry < new Date().getTime()) {
          $("#dropIndicator").css("display", "none");
        }
      }, 450);
    }
  });

  $body.bind('drop', function (e) {
    e.preventDefault();
    e.stopPropagation();
    var files = e.originalEvent && e.originalEvent.dataTransfer && e.originalEvent.dataTransfer.files;
    if (files && files.length) {
      indicatorExpiry = 0;
      $("#dropIndicator").css("display", "none");
      addFiles(files);
    }
  });

  // Handle file paste
  $body.bind('paste', function (e) {
    var files = e.originalEvent && e.originalEvent.clipboardData && e.originalEvent.clipboardData.files;
    if (files && files.length) {
      e.preventDefault();
      e.stopPropagation();
      addFiles(files);
    }
  });
});
