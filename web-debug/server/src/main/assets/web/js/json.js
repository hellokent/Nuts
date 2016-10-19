function init(host) {

    var webSocket = new WebSocket('ws://' + host);

    webSocket.onopen = function (e) {
        console.log('onopen', e);
        $('#socket-status').html('已连接');
        // setInterval(send, 1000);
    };

    webSocket.onclose = function (e) {
        console.log('onclose', e);
        $('#socket-status').html('关闭');
    };

    webSocket.onmessage = function (e) {
        console.log('onmessage', e);
        render(e);
    };

    webSocket.onerror = function (e) {
        console.log('onerror', e);
        $('#socket-status').html('连接错误');
    };
}

function render(e) {

    var data = eval('(' + e.data + ')'), $tr;

    if (data.response) {
        console.log(data);
        $tr = $('.id_' + data.id);

        $tr.find('.status').html(data.statusCode);

        $tr.find('.json').html(json(data.response));

        // 闭合 json
        setTimeout(function () {
            $tr.find('td i:first').trigger('click');
        }, 10);

    } else {
        console.log(data);
        /***


         var tr = $('<tr></tr>');
         tr.attr('class', 'id_' + data.id);
         tr.append($('<td>' +data.url + '</td>'));
         tr.append($('<td>' +data.method + '</td>'));
         tr.append($('<td>' + params(data.param) + '</td>'));
         tr.append($('<td class="status">' +data.status + '</td>'));
         tr.append($('<td class="json"></td>'));



         */
        var item = '<tr class="id_' + data.id + '">' +
            '<td>' + data.url + '</td>' +
            '<td>' + data.method + '</td>' +
            '<td>' + params(data.param) + '</td>' +
            '<td class="status"></td>' +
            '<td class="json"></td></tr>';
        $('#table').append(item);
    }
}

function params(params) {
    var html = '';
    if (!params.length) {
        return html;
    }
    $.each(params, function (i, param) {
        html += param.key + '=' + param.value + '<br>'
    });
    return html;

}

function json(data) {
    return '<span style="color: #f1592a;font-weight:bold;">' + HTMLEncode(data) + '</span>';
}

function HTMLEncode(html) {
    var temp = document.createElement("div");
    (temp.textContent != null) ? (temp.textContent = html) : (temp.innerText = html);
    var output = temp.innerHTML;
    temp = null;
    return output;
}
