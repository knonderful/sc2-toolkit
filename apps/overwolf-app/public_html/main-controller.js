const BUILDORDER_WINDOW = "BuildOrder";

var MainContoller = new Class({
  showBuildOrderWindow: function () {
    console.log("WE ARE HERE");
    overwolf.windows.obtainDeclaredWindow(BUILDORDER_WINDOW, function (result) {
      if (result.status === "success") {
        overwolf.windows.restore(result.window.id);
      }
    });
  },
  bla: function () {
    var timestamp;
    responsiveVoice.speak("Build an engineering bay", "US English Female",
            {
              onstart: function () {
                timestamp = Date.now();
              },
              onend: function () {
                var diff = Date.now() - timestamp;
                console.log("Took " + diff + " ms.");
              },
              rate: 1.1
            });
  }
});

var mainController = new MainContoller();
