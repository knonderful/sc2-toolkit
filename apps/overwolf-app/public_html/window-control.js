// Should already be in the context. This is here to make the IDE happy.
var overwolf;

var WindowControl = new Class({
  resize: function (edge) {
    overwolf.windows.getCurrentWindow(function (result) {
      if (result.status === "success") {
        overwolf.windows.dragResize(result.window.id, edge);
      }
    });
  },
  move: function () {
    overwolf.windows.getCurrentWindow(function (result) {
      if (result.status === "success") {
        overwolf.windows.dragMove(result.window.id);
      }
    });
  },
  close: function () {
    overwolf.windows.getCurrentWindow(function (result) {
      if (result.status === "success") {
        overwolf.windows.close(result.window.id);
      }
    });
  }
});

var windowControl = new WindowControl();