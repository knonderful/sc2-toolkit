var LinkedNode = new Class({
  initialize: function (data) {
    this.previous = null;
    this.next = null;
    this.data = data;
  },
  setPrevious: function (node) {
    this.previous = node;
  },
  setNext: function (node) {
    this.next = node;
  },
  getPrevious: function () {
    return this.previous;
  },
  getNext: function () {
    return this.next;
  },
  getData: function () {
    return this.data;
  }
});

var BuildOrderEntry = new Class({
  initialize: function (time, displayTime, command) {
    this.time = time;
    this.displayTime = displayTime;
    this.command = command;
  },
  getTime: function () {
    return this.time;
  },
  getDisplayTime: function () {
    return this.displayTime;
  },
  getCommand: function () {
    return this.command;
  }
});

var BuildOrder = new Class({
  initialize: function () {
    this.head = null;
    this.tail = null;
  },
  addEntry: function (buildOrderEntry) {
    var lastTail = this.tail;
    this.tail = new LinkedNode(buildOrderEntry);
    this.tail.setPrevious(lastTail);

    if (lastTail === null) {
      this.head = this.tail;
    } else {
      lastTail.setNext(this.tail);
    }
  },
  getHead: function () {
    return this.head;
  }
});

var BuildOrderMap = new Class({
  initialize: function (t, z, p, r) {
    this.terran = t;
    this.zerg = z;
    this.protoss = p;
    this.random = r;
  },
  getTerran: function () {
    return this.terran;
  },
  getZerg: function () {
    return this.zerg;
  },
  getProtoss: function () {
    return this.protoss;
  },
  getRandom: function () {
    return this.random;
  }
});

const CLIENT_API_URL = "http://localhost:6119/";
const UI_URL = CLIENT_API_URL + "ui";
const GAME_URL = CLIENT_API_URL + "game";

var Sc2GameInfo = new Class({
  initialize: function (uiData, gameData) {
    if (uiData === null || gameData === null) {
      this.inGame = false;
      return;
    }

    if (uiData.activeScreens.length !== 0) {
      this.inGame = false;
      return;
    }

    this.inGame = !gameData.isReplay;
    if (!this.inGame) {
      return;
    }

    this.displayTime = gameData.displayTime;
    this.players = gameData.players;
  },
  isInGame: function () {
    return this.inGame;
  },
  getDisplayTime: function () {
    return this.displayTime;
  },
  getPlayers: function () {
    return this.players;
  }
});

// In ms.
const REQUEST_INTERVAL = 5000;

var Sc2GameDetector = new Class({
  initialize: function () {
  },
  start: function() {
    this.pollState();
  },
  pollState: function () {
    var jsonRequest = new Request.JSON({url: UI_URL,
      onSuccess: function (uiData) {
        console.log("UIDATA " + uiData);
      },
      onFailure: function (xhr) {
        console.log("FAIL " + xhr);
      }
    }).get();
  }
});

var BuildOrderController = new Class({
  onLoad: function () {
    this.resetGui();
    this.gameDetector = new Sc2GameDetector();
    this.gameDetector.start();
  },
  resetGui: function () {
    $('notInGame').show();
//    $('inGame').hide();
  },
  startPolling: function () {

  },
  test: function () {
    var c = $('inGame');
    var elts = [];
    for (var i = 0; i < 5; i++) {
      elts[i] = new Element("div", {id: "elt" + i});
      elts[i].inject(c);
    }

    elts[2].dispose();
  }
});

var buildOrderController = new BuildOrderController();

var sampleData = {
  terran: {
    steps: [
      {time: 0, displayTime: "00:00", command: "Build an SCV"},
      {time: 12000, displayTime: "00:12", command: "Build an SCV"},
      {time: 17000, displayTime: "00:17", command: "Build a supply depot"},
      {time: 24000, displayTime: "00:24", command: "Build an SCV"},
      {time: 38000, displayTime: "00:38", command: "Build an SCV"},
      {time: 39000, displayTime: "00:39", command: "Build a barracks"}
    ]
  }
  // ... zerg, protoss...
};

function speak(head) {
  if (head !== null) {
    var nextHead = head.getNext();
    console.log("NOW = " + Date.now());
    var nextTime;
    if (nextHead !== null) {
      nextTime = Date.now() + (nextHead.getData().getTime() - head.getData().getTime());
    } else {
      nextTime = null;
    }
    responsiveVoice.speak(head.getData().getCommand(), "US English Female",
            {
              onstart: function () {
                // Target time for the next command
              },
              onend: function () {
                if (nextTime === null) {
                  // This means we have no more steps
                  return;
                }

                var interval = nextTime - Date.now();
                setTimeout(
                        function () {
                          speak(nextHead);
                        },
                        interval);
              },
              rate: 1
            });
  }
}

function createSomething() {
  var buildOrder = new BuildOrder();
  var boElt = $("buildOrder");
  Array.each(sampleData.terran.steps, function (step) {
    var boe = new BuildOrderEntry(step.time, step.displayTime, step.command);
    buildOrder.addEntry(boe);

    var boeElt = new Element("div.buildOrderStep");
    boeElt.inject(boElt);
    var dtime = new Element("span.displayTime");
    dtime.set("text", boe.getDisplayTime());
    dtime.inject(boeElt);
    var cmd = new Element("span.command");
    cmd.set("text", boe.getCommand());
    cmd.inject(boeElt);
  });

  console.log("Set up build order.");

  var head = buildOrder.getHead();
  speak(head);
}