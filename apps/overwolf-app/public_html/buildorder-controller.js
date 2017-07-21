var Announcer = new Class({
  say: function (message) {
    responsiveVoice.speak(message, "US English Female");
  }
});

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
  announced: false,
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
  },
  highlight: function () {
    this.element.addClass("highlighted");
  },
  unhighlight: function () {
    this.element.removeClass("highlighted");
  },
  isAnnounced: function () {
    return this.announced;
  },
  setAnnounced: function (flag) {
    this.announced = flag;
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
  }
});

// Web server port to which the SC2 Tool Kit application can connect
const WEB_SERVER_PORT = 8989;
// Path that identifies a start-of-game message
const PATH_START_GAME = "/startGame";
// Path that identifies an update-game-time message
const PATH_UPDATE_GAME_TIME = "/updateGameTime";
// Path that identifies a end-of-game message
const PATH_END_GAME = "/endGame";
// Time in ms to announce the next build order element
const ANNOUNCE_TIME_MS = 2000;
// Time in ms to retain a build order element
const ENTRY_RETAIN_TIME_MS = 10000;
// Maximum number of build order elements to retain (overrides retain time)
const ENTRY_RETAIN_MAX_ITEMS = 5;

var BuildOrderController = new Class({
  buildOrder: null,
  onLoad: function () {
    this.startServer();
    this.announcer = new Announcer();
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
    console.log("GOT " + info.url);
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

    this.announcer.say("Good luck and have fun");

    console.log("glhf <3");
  },
  handleUpdateGameTime: function (message) {
    var gameTimeMs = Math.round(parseFloat(message.displayTime) * 1000);
    // TODO: Convert to mm:ss
    $("gameTime").set("text", gameTimeMs / 1000);

    if (this.buildOrder === null) {
      return;
    }

    var currentNode = this.buildOrder.getHead();

    // Remove nodes that are beyond the retain time
    var retainCutOff = gameTimeMs - ENTRY_RETAIN_TIME_MS;
    while (currentNode !== null && currentNode.getData().getTime() < retainCutOff) {
      currentNode.getData().cleanUp();
      currentNode = currentNode.getNext();
      if (currentNode !== null) {
        currentNode.setPrevious(null);
      }
    }

    // Unhighlight elements that have passed in time
    while (currentNode !== null && currentNode.getData().getTime() < gameTimeMs) {
      currentNode.getData().unhighlight();
      currentNode = currentNode.getNext();
    }

    if (currentNode === null) {
      // We have reached the end of the build order
      return;
    }

    // Highlight the first entry that is in the future
    currentNode.getData().highlight();

    // Announce all upcoming entries
    var announceCutOff = gameTimeMs + ANNOUNCE_TIME_MS;
    var commands = [];
    while (currentNode !== null && currentNode.getData().getTime() <= announceCutOff) {
      if (!currentNode.getData().isAnnounced()) {
        currentNode.getData().setAnnounced(true);
        commands[commands.length] = currentNode.getData().getCommand();
      }
      currentNode = currentNode.getNext();
    }

    if (commands.length > 0) {
      this.announcer.say(commands.toString());
    }
  },
  handleEndGame: function (message) {
    if (this.buildOrder !== null) {
      this.buildOrder.cleanUp();
    }

    this.buildOrder = null;
  }
});

var buildOrderController = new BuildOrderController();

var sampleData = {
  terran: {
    steps: [
      //{time: 0, displayTime: "00:00", command: "SCV"},
      {time: 12000, displayTime: "00:12", command: "SCV"},
      {time: 17000, displayTime: "00:17", command: "Supply Depot"},
      {time: 24000, displayTime: "00:24", command: "SCV"},
      {time: 38000, displayTime: "00:38", command: "SCV"},
      {time: 39000, displayTime: "00:39", command: "Barracks"},
      {time: 44000, displayTime: "00:44", command: "Refinery"},
      {time: 50000, displayTime: "00:50", command: "SCV"},
      {time: 62000, displayTime: "01:02", command: "SCV"},
      {time: 74000, displayTime: "01:14", command: "SCV"},
      {time: 86000, displayTime: "01:26", command: "Orbital Command"},
      {time: 86100, displayTime: "01:26", command: "Reaper"},
      {time: 99000, displayTime: "01:39", command: "Command Center"},
      {time: 110000, displayTime: "01:50", command: "Barracks"},
      {time: 113000, displayTime: "01:53", command: "SCV"},
      {time: 119000, displayTime: "01:59", command: "Barracks Reactor"},
      {time: 123000, displayTime: "02:03", command: "Supply Depot"},
      {time: 126000, displayTime: "02:06", command: "SCV"},
      {time: 132000, displayTime: "02:12", command: "Refinery"},
      {time: 138000, displayTime: "02:18", command: "SCV"},
      {time: 144000, displayTime: "02:24", command: "Factory"},
      {time: 150000, displayTime: "02:30", command: "SCV"},
      {time: 155000, displayTime: "02:35", command: "2 Marines"},
      {time: 157000, displayTime: "02:37", command: "Barracks Tech Lab"},
      {time: 162000, displayTime: "02:42", command: "SCV"},
      {time: 171000, displayTime: "02:51", command: "Orbital Command"},
      {time: 175000, displayTime: "02:55", command: "SCV"},
      {time: 178000, displayTime: "02:58", command: "Stimpack"}
    ]
  }
  // ... zerg, protoss...
};
