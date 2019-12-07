'use strict';
$(function(){
  $("[data-hide]").on("click", function(){
    $(this).closest("." + $(this).attr("data-hide")).hide();
  });
});

var UTILS = {
  waiter: {
    count: 0, waitBlock: null, spinnerBlock: null
  }
};

UTILS.sendRequest = function (url, params, callBack) {
  var success = function (data) {
    if ($.trim(data) === "") {
      console.error("empty answer. request: " + JSON.stringify(params));
      return;
    }
    try {
      if (callBack) {
        callBack(data);
      }
    } catch (e) {
      console.error(e);
    }
  };
  var error = function (jqXHR, textStatus, errorThrown) {
    console.error("ajax error: " + textStatus + ". request: " + JSON.stringify(params));
  };

  return $.ajax({
                  url: url,
                  type: "POST",
                  data: params,
                  dataType: 'json',
                  contentType: "application/json; charset=utf-8",
                  success: success,
                  error: error
                });
};
UTILS.hideWaitBlock = function () {
  UTILS.waiterInit();
  if (UTILS.waiter.count) {
    UTILS.waiter.count--;
  }
  if (!UTILS.waiter.count) {
    UTILS.waiter.waitBlock.hide();
    UTILS.waiter.spinnerBlock.hide();
  }
};
UTILS.showWaitBlock = function () {
  UTILS.waiterInit();
  if (!UTILS.waiter.count) {
    UTILS.waiter.count = 1;
    UTILS.waiter.waitBlock.show();
    UTILS.waiter.spinnerBlock.show();
  } else {
    UTILS.waiter.count++;
  }
};
UTILS.waiterInit = function () {
  if (!UTILS.waiter.waitBlock) {
    UTILS.waiter.waitBlock = $(".wait-block");
  }
  if (!UTILS.waiter.spinnerBlock) {
    UTILS.waiter.spinnerBlock = $(".spinner-center");
  }
};
UTILS.createTD = function (data) {
  return $("<td>").html($.trim(data));
};

UTILS.getJson = function (data) {
  try {
    if (typeof data == "string") {
      return $.parseJSON($.trim(data));
    }
    if (typeof data == "object") {
      return data;
    }
  } catch (ignored) {
  }
  return undefined;
};

UTILS.getErrorText = function (jsonData) {
  if (!jsonData || typeof jsonData == "string" || !jsonData.errorMessage) {
    return null;
  }
  if (jsonData.exceptionMessage) {
    return jsonData.errorMessage + ": " + jsonData.exceptionMessage;
  }
  return jsonData.errorMessage;
};

UTILS.isError = function (jsonData) {
  var errorMessage = UTILS.getErrorText(jsonData);
  if (errorMessage) {
    UTILS.error(errorMessage);
    return true;
  }
  return false;
};

UTILS.error = function (text) {
  console.error(text);
  $("#alertDangerText").html(text);
  $("#alertDanger").show();
};
UTILS.success = function (text) {
  console.log(text);
  $("#alertSuccessText").html(text);
  $("#alertSuccess").show();
};

UTILS.isNull = function (value) {
  return value === null || value === undefined;
};

UTILS.isNotNull = function (value) {
  return !UTILS.isNull(value);
};

UTILS.isEmpty = function (value) {
  if (UTILS.isNull(value)) {
    return true;
  }

  if (typeof value === 'string') {
    return $.trim(value) === '';
  }

  if (value instanceof Array) {
    return value.length === 0;
  }

  return false;
};

UTILS.isNotEmpty = function (value) {
  return !UTILS.isEmpty(value);
};