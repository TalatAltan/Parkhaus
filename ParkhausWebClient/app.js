var baseurl = "http://localhost:8080"; //web_war_exploded/

var carColors = ['images/car1_blue.png', 'images/car1_green.png', 'images/car1_orange.png', 'images/car1_purple.png', 'images/car1_red.png', 'images/car1_yellow.png',];
var waitingCarUuid, carColor;

var isBusy = false;
function printMousePos(event) {

  if (isBusy)
    return;

  var x = event.pageX - $('#parkhaus').offset().left;
  var y = event.pageY - $('#parkhaus').offset().top;

  if (x < 136 || x > 591 || y < 8 || y > 463) {
    return;
  }

  isBusy = true;

  $.ajax({
    type: "GET",
    dataType: "json",
    url: baseurl + "/PositionServlet?action=byPosition&posX=" + x + "&posY=" + y,
    success: function (parkingSpot) {
      $.ajax({
        type: "GET",
        dataType: 'json',
        url: baseurl + "/ParkingServlet?action=isAviable&id=" + parkingSpot.id,
        statusCode: {
          200: function (customer) {
            $.ajax({
              type: "POST",
              data: JSON.stringify({
                uuid: customer.uuid,
                car: customer.car,
                parkingSpotId: parkingSpot.id,
                enterTimestamp: customer.enterTimestamp,
                leaveTimestamp: new Date(),
              }),
              contentType: 'application/json',
              mimeType: 'application/json',
              url: baseurl + "/ParkingServlet",
              statusCode: {
                200: function (table) {
                  $('#carTable').html(table.responseText);
                  $('#' + customer.uuid).animate({ "opacity": 0 }, 150);
                  $('#' + customer.uuid).promise().done(function () {
                    $('#' + customer.uuid).css({ "transform": "rotate(-90deg)", "top": 450, "left": 62, "opacity": 1 }).animate({ "opacity": 0, "top": 30 }, 800);
                    $('#' + customer.uuid).promise().done(function () {
                      $('#' + customer.uuid).remove();
                      isBusy = false;
                    });
                  });
                }
              }
            });
          },
          204: function () {
            $.ajax({
              type: "POST",
              dataType: 'json',
              data: JSON.stringify({
                uuid: waitingCarUuid,
                car: carColor,
                parkingSpotId: parkingSpot.id,
                enterTimestamp: new Date()
              }),
              contentType: 'application/json',
              mimeType: 'application/json',
              url: baseurl + "/ParkingServlet",
              statusCode: {
                200: function (table) {
                  $('#carTable').html(table.responseText);
                  $('#' + waitingCarUuid).animate({ "opacity": 0, "top": 450 }, 800);
                  $('#' + waitingCarUuid).promise().done(function () {
                    $('#' + waitingCarUuid).css({ "transform": "rotate(-180deg)", "top": parkingSpot.xyTop.y, "left": parkingSpot.xyTop.x }).animate({ "opacity": 1 }, 150);
                    createCar();
                    isBusy = false;
                  });
                }
              }
            });
          }
        }
      });
    },
    error: function () {
      isBusy = false;
    }
  });
}

function uuidv4() {
  return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
  )
}

function createCar() {
  carColor = carColors[Math.floor(Math.random() * carColors.length)];
  waitingCarUuid = uuidv4();

  var img = document.createElement("img");
  img.src = carColor;
  img.draggable = false;
  img.id = waitingCarUuid;
  img.className = "car";
  $('#parkhaus').append(img);
}

document.addEventListener("click", printMousePos);

window.onload = function () {
  $.ajax({
    type: "GET",
    dataType: 'json',
    url: baseurl + "/ParkingServlet?action=clearSession"
  });

  createCar();
}

function avgParking() {
  isBusy = true;
  $.ajax({
    type: "GET",
    url: baseurl + "/StatisticServlet?action=avg",
    success: function (avg) {
      $('#avgOut').html(avg);
      isBusy = false;
    },
    error: function () {
      isBusy = false;
    }
  });
}

function maxParking() {
  isBusy = true;
  $.ajax({
    type: "GET",
    url: baseurl + "/StatisticServlet?action=max",
    success: function (max) {
      $('#maxOut').html(max);
      isBusy = false;
    },
    error: function () {
      isBusy = false;
    }
  });
}

function topParkingSpot() {
  isBusy = true;
  $.ajax({
    type: "GET",
    dataType: "json",
    url: baseurl + "/StatisticServlet?action=topParkingSpot",
    success: function (parkingSpotId) {
      $.ajax({
        type: "GET",
        dataType: "json",
        url: baseurl + "/PositionServlet?action=byId&id=" + parkingSpotId,
        success: function (parkingSpot) {
          var div = document.createElement("div");
          div.id = "topParkingSpot";
          $('#parkhaus').append(div);
          $('#topParkingSpot').css({
            "position": "absolute", "top": parkingSpot.xyTop.y, "left": parkingSpot.xyTop.x,
            "background": "yellow", "width": 74, "height": 42, "opacity": 0.7
          }).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500);
          $('#topParkingSpot').promise().done(function () {
            $('#topParkingSpot').remove();
            isBusy = false;
          });
        },
        error: function () {
          isBusy = false;
        }
      });
    },
    error: function () {
      isBusy = false;
    }
  });
}

var myChart = null;

function colorChart() {
  isBusy = true;
  $.ajax({
    type: "GET",
    dataType: "json",
    url: baseurl + "/StatisticServlet?action=colorChart",
    success: function (dataContent) {
      if(myChart != null){
        myChart.destroy();
      }

      var ctx = $('#myChart');
       myChart = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: ['Blue', 'Green', 'Orange', 'Purple', 'Red', 'Yellow'],
          datasets: [
            {
              label: 'Car color distribution',
              data: [dataContent.blue, dataContent.green, dataContent.orange, dataContent.purple, dataContent.red, dataContent.yellow],
              backgroundColor: ['Blue', 'Green', 'Orange', 'Purple', 'Red', 'Yellow']
            }
          ]
        }
      });
    }
  });
  isBusy = false;
}