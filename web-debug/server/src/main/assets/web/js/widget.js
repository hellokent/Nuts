var a, b;
var isMeasuring = false;
var isMeasureVertical;

var dpScale;

function showBoarder(model, width, height, scale, index) {

    if (model.left < 0 || model.right > width || model.top < 0 || model.bottom > height) {
        console.log("l:" + model.left + ";r:" + model.right + ":" + width + ":" + height);
        return;
    }
    var newDiv = $("<div></div>");
    newDiv.css({
        "border": "0px solid blue",
        "z-index": index,
        "position": "absolute",
        "left": model.left / scale + 15, //外层有15px的padding
        "top": model.top / scale,
        "width": model.width / scale + "px",
        "height": model.height / scale + "px"
    });
    newDiv.attr("data-tooltip-text", model.viewName + "-" + toDp(model.width) + "*" + toDp(model.height) +
        "  (" + toDp(model.left) + "," + toDp(model.top) + "), (" + toDp(model.right) + "," + toDp(model.bottom) + ")");
    newDiv.doomTooltip({
        position: "top",
        topMargin: -5
    });

    var trArray = [];

    addTableContent("Id", model.idName, trArray);
    addTableContent("节点名称", model.viewName, trArray);
    addTableContent("Margin", "l:" + toDp(model.marginLeft) + ",t:" + toDp(model.marginTop) + ",r" + toDp(model.marginRight) + ",b" + toDp(model.marginBottom), trArray);
    addTableContent("Padding", "l:" + toDp(model.paddingLeft) + ",t:" + toDp(model.paddingTop) + ",r:" + toDp(model.paddingRight) + ",b:" + toDp(model.paddingBottom), trArray);


    if (model.viewName == "TextView") {
        addTableContent("文本", model.text, trArray);
        addTableContent("字体颜色", model.textColor, trArray);
        addTableContent("字体大小", toDp(model.textSize), trArray);

        if (model.hintText != null) {
            addTableContent("提示文本", model.hintText, trArray);
            addTableContent("提示文本颜色", model.hintTextColor, trArray);
        }

    }

    newDiv.mouseenter(function () {
        newDiv.css({
            "border": "1px solid blue"
        });
        newDiv.setOffset();
        newDiv.showTooltip();

        if (!isMeasuring) {
            $("#widget-table").append(trArray);
        }
    });
    newDiv.click(function () {
        if (isMeasuring) {

            trArray = [];
            if (a == null) {
                a = model;
                addTableContent("A-类型", a.viewName, trArray);
                addTableContent("A-Id", a.idName, trArray);
            } else if (b == null) {
                b = model;
                addTableContent("B-类型", b.viewName, trArray);
                addTableContent("B-Id", b.idName, trArray);
            }

            if (a != null && b != null) {
                if (isMeasureVertical) {

                    var aNoPaddingTop = a.top + a.paddingTop;
                    var aNoPaddingBottom = a.bottom - a.paddingBottom;
                    var bNoPaddingTop = b.top + b.paddingBottom;
                    var bNoPaddingBottom = b.bottom - b.paddingBottom;

                    addTableContent("A.top - B.top", toDp(a.top - b.top), trArray);
                    addTableContent("A.top - B.bottom", toDp(a.top - b.bottom), trArray);
                    addTableContent("A.bottom - B.top", toDp(a.bottom - b.top), trArray);
                    addTableContent("A.bottom - B.bottom", toDp(a.bottom - b.bottom), trArray);

                    addTableContent("No Padding", "", trArray);

                    addTableContent("A.top - B.top", toDp(aNoPaddingTop - bNoPaddingTop), trArray);
                    addTableContent("A.top - B.bottom", toDp(aNoPaddingTop - bNoPaddingBottom), trArray);
                    addTableContent("A.bottom - B.top", toDp(aNoPaddingBottom - bNoPaddingTop), trArray);
                    addTableContent("A.bottom - B.bottom", toDp(aNoPaddingBottom - bNoPaddingBottom), trArray);
                } else {
                    var aNoPaddingLeft = a.left + a.paddingLeft;
                    var aNoPaddingRight = a.right - a.paddingRight;
                    var bNoPaddingLeft = b.left + b.paddingLeft;
                    var bNoPaddingRight = b.right - b.paddingRight;

                    addTableContent("A.left - B.left", toDp(a.left - b.left), trArray);
                    addTableContent("A.left - B.right", toDp(a.left - b.right), trArray);
                    addTableContent("A.right - B.left", toDp(a.right - b.left), trArray);
                    addTableContent("A.right - B.right", toDp(a.right - b.right), trArray);

                    addTableContent("No Padding", "", trArray);

                    addTableContent("A.left - B.left", toDp(aNoPaddingLeft - bNoPaddingLeft), trArray);
                    addTableContent("A.left - B.right", toDp(aNoPaddingLeft - bNoPaddingRight), trArray);
                    addTableContent("A.right - B.left", toDp(aNoPaddingRight - bNoPaddingLeft), trArray);
                    addTableContent("A.right - B.right", toDp(aNoPaddingRight - bNoPaddingRight), trArray);
                }
                a = null;
                b = null;
            }
            $("#widget-table").append(trArray);
        }

    });
    newDiv.mouseleave(function () {
        newDiv.hideTooltip();
        newDiv.css({
            "border": "0px solid blue"
        });
        if (isMeasuring) {
        } else {
            $("tr#widget-content").remove();
        }
    });
    $("#screen-container").append(newDiv);
    for (var i = 0; i < model.childrenCount; ++i) {
        showBoarder(model.children[i], width, height, scale, index + 1);
    }
}

function addTableContent(key, value, array) {
    if (value == null) {
        return;
    }
    var tr = $("<tr id='widget-content'>" +
        "<td class='col-md-4'>" + key + "</td>" +
        "<td style='text-align: left'>" + value + "</td>" +
        "</tr>");
    array.push(tr);
}

function toDp(a) {
    return Math.round(a / dpScale);
}

function update(host) {
    var screen = $("#screen");
    var w = screen.width();


    $.getJSON(host + "/api/widget/phone", null, function (result) {
        console.log(result);
        dpScale = result.scale;
        $('#phone').text(result.name + " - " + result.os);

        $.getJSON(host + "/api/widget/current", null, function (result) {
            console.log(result);

            var scale = result.width / w;

            console.log("scale:" + scale);

            showBoarder(result, result.width, result.height, scale, 1)
        });
    });


    var screenContainer = $("#screen-container");
    screenContainer.mouseleave(function () {
        isMeasuring = false;
    });

    screenContainer.mouseenter(function () {
        $("tr#widget-content").remove();
    });
}

function measureVertical() {
    isMeasuring = true;
    isMeasureVertical = true;
}

function measureHorizontal() {
    isMeasuring = true;
    isMeasureVertical = false;
}
