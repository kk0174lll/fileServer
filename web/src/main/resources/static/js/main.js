$(document).ready(function () {

  client.start();
});

function allowDrop(ev)
{
  ev.preventDefault();
}

function drop(ev)
{
  ev.preventDefault();
  //client.socket.send(ev.dataTransfer.files[0]);
  var file = ev.dataTransfer.files[0], reader = new FileReader();
  var name = file.name;
  var type = file.type.split("/")[0];
  if (type != "image") {
    alert("only image");
  } else {
    var expansion = file.type.split("/")[1];
    reader.onload = function (evt) {
      client.socket.send(JSON.stringify({
                                          data: evt.target.result, filename: name, type: type, expansion: expansion
                                        }));
    };
    reader.readAsDataURL(file);
  }
}

var client = {
  socket: null, start: function () {
    client.setStatus('opening');
    //var url = 'ws://134.209.94.251:8765/';
    var url = 'ws://localhost:8765/';
    var connected = false;

    function poll()
    {
      if (connected) {
        return
      }
      ;var ws = new WebSocket(url);
      //ws.binaryType = "arraybuffer";
      ws.onopen = ws.onclose = ws.onerror = function (event) {
        var code = ws.readyState;
        var codes = {
          0: "opening", 1: "open", 2: "closing", 3: "closed"
        };

        client.setStatus(codes[code]);
        connected = (code == 0 || code == 1);
      };
      ws.onmessage = function (event) {
        var message = JSON.parse(event.data)
        switch (message.type) {
          case 'image':
            var div = document.createElement("h2");
            div.setAttribute("class", "imgcontainer");
            //var img = document.createElement("img");
            //img.setAttribute("src", message.data);
            div.textContent = message.name + " send: " + message.hex;
            var br = document.createElement("br");
            //div.appendChild(br);
            //div.appendChild(img);
            //document.getElementById("result").appendChild(div);
            result.insertBefore(div, result.children[0]);
            break;
          case'name':
            client.setName(message.data);
            break;
          default:
            console.log(message.msg);
        }
      };
      connected = true;
      client.socket = ws;
    }

    setInterval(poll, 100);
  }, setStatus: function (status) {
    document.getElementById("status").textContent = status;
  }, setName: function (name) {
    document.getElementById("name").textContent = "name: " + name;
  }
};