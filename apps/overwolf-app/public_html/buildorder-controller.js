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
  initialize: function (element, time, displayTime, command) {
    this.element = element;
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
  },
  cleanUp: function () {
    this.element.dispose();
  }
});

var BuildOrder = new Class({
  head: null,
  tail: null,
  current: null,
  addEntry: function (buildOrderEntry) {
    var lastTail = this.tail;
    this.tail = new LinkedNode(buildOrderEntry);
    this.tail.setPrevious(lastTail);

    if (lastTail === null) {
      this.head = this.tail;
      this.current = this.tail;
    } else {
      lastTail.setNext(this.tail);
    }
  },
  getHead: function () {
    return this.head;
  },
  cleanUp: function () {
    var curHead = this.head;
    while (curHead !== null) {
      curHead.getData().cleanUp();
      curHead = curHead.getNext();
    }
  },
  getCurrent: function () {
    return this.current;
  },
  setCurrent: function (node) {
    this.current = node;
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

const WEB_SERVER_PORT = 8989;
const PATH_START_GAME = "/startGame";
const PATH_UPDATE_GAME_TIME = "/updateGameTime";
const PATH_END_GAME = "/endGame";

var BuildOrderController = new Class({
  buildOrder: null,
  onLoad: function () {
    this.startServer();
  },
  startServer: function () {
    overwolf.web.createServer(WEB_SERVER_PORT, serverInfo => {
      if (serverInfo.status === "error") {
        console.log("Failed to create server: ");
        return;
      } else {
        _server = serverInfo.server;
        _server.onRequest.addListener(info => this.onRequest(info));

        _server.listen(info => {
          if (serverInfo.status === "error") {
            console.log("Failed to open server socket.");
            return;
          }

          console.log("Server listening status at " + info.url + " : " + info);
        });
      }
    });
  },
  onRequest: function (info) {
    var content = JSON.parse(info.content);
    if (info.url.endsWith(PATH_START_GAME)) {
      this.handleStartGame(content);
    } else if (info.url.endsWith(PATH_UPDATE_GAME_TIME)) {
      this.handleUpdateGameTime(content);
    } else if (info.url.endsWith(PATH_END_GAME)) {
      this.handleEndGame(content);
    } else {
      console.log("Unexpected URL: " + info.url);
    }
  },
  handleStartGame: function (message) {
    if (this.buildOrder !== null) {
      this.buildOrder.cleanUp();
    }

    var order = new BuildOrder();
    var boElt = $("buildOrder");
    Array.each(sampleData.terran.steps, function (step) {
      var boeElt = new Element("div.buildOrderStep");
      var boe = new BuildOrderEntry(boeElt, step.time, step.displayTime, step.command);
      order.addEntry(boe);

      boeElt.inject(boElt);
      var dtime = new Element("span.displayTime");
      dtime.set("text", boe.getDisplayTime());
      dtime.inject(boeElt);
      var cmd = new Element("span.command");
      cmd.set("text", boe.getCommand());
      cmd.inject(boeElt);
    });

    this.buildOrder = order;

    console.log("Set up build order: " + this.buildOrder);
  },
  handleUpdateGameTime: function (message) {
    var gameTimeMs = Math.round(parseFloat(message.displayTime) * 1000);
    // TODO: Convert to mm:ss
    $("gameTime").set("text", gameTimeMs / 1000);

    if (this.buildOrder === null) {
      return;
    }

    var currentNode = this.buildOrder.getCurrent();
    if (currentNode === null) {
      return;
    }

    var currentEntry = currentNode.getData();
    // Start talking 1 second ahead of time
    if (currentEntry.getTime() - 1000 <= gameTimeMs) {
      responsiveVoice.speak(currentEntry.getCommand(), "US English Female");
    }

    if (currentEntry.getTime() <= gameTimeMs) {
      // Remove this entry...
      currentEntry.cleanUp();
      // ... and move on to the next.
      this.buildOrder.setCurrent(currentNode.getNext());
    }
  },
  handleEndGame: function (message) {
    if (this.buildOrder !== null) {
      this.buildOrder.cleanUp();
    }
  }
});

var buildOrderController = new BuildOrderController();

var sampleData = {
  terran: {
    steps: [
      //{time: 0, displayTime: "00:00", command: "SCV"},
      {time: 12000, displayTime: "00:12", command: "SCV"},
      {time: 17000, displayTime: "00:17", command: "Supply depot"},
      {time: 24000, displayTime: "00:24", command: "SCV"},
      {time: 38000, displayTime: "00:38", command: "SCV"},
      {time: 39000, displayTime: "00:39", command: "Barracks"}
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
                head.getData().cleanUp();

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
    var boeElt = new Element("div.buildOrderStep");
    var boe = new BuildOrderEntry(boeElt, step.time, step.displayTime, step.command);
    buildOrder.addEntry(boe);

    boeElt.inject(boElt);
    var dtime = new Element("span.displayTime");
    dtime.set("text", boe.getDisplayTime());
    dtime.inject(boeElt);
    var cmd = new Element("span.command");
    cmd.set("text", boe.getCommand());
    cmd.inject(boeElt);
  });

  console.log("Set up build order. =)");

  var head = buildOrder.getHead();
  speak(head);
}