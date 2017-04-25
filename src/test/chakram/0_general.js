var chakram = require('chakram'),
    expect = chakram.expect;

var hostUrl = process.env.tylrurl;

// var hostUrl = "https://api.teiler.io/";
// var hostUrl = "http://localhost:4567/";
var version = "v1/";
var baseUrl = hostUrl + version;

describe("Root URL", function() {
    it("should provide a simple json with the doc url", function () {
        var response = chakram.get(hostUrl);
        expect(response).to.have.status(200);
	      expect(response).to.have.header("content-type", "application/json");
        expect(response).to.have.json("documentation", function (url) {
            expect(url).to.equal("doc.teiler.io");
        });
        return chakram.wait();
    });
});

describe("Create a group", function () {
  it("should create group", function () {
    var group = {
      name: "Hello World"
    };
    var response = chakram.post(baseUrl + "/groups", group);
    expect(response).to.have.status(200);
    expect(response).to.have.header("content-type", "application/json");
    expect(response).to.have.json("id", function (id) {
      expect(id).to.be.a("string");
    });
    expect(response).to.have.json("name", function (url) {
      expect(url).to.equal("Hello World");
    });
    expect(response).to.have.json("update-time", function (updateTime) {
      var check = Date.parse(updateTime);
      expect(check).not.to.be.NaN;
    });
    expect(response).to.have.json("create-time", function (createTime) {
      var check = Date.parse(createTime);
      expect(check).not.to.be.NaN;
    });
    return chakram.wait();
  })
});

describe("Get a group", function () {
  var groupId;
  before("create group", function () {
    var group = {
      name: "Hello World"
    };
    return chakram.post(baseUrl + "/groups", group)
    .then(function (response) {
      groupId = response.body.id;
    })
  });

  it("should get group", function () {
    var response = chakram.get(baseUrl + "/groups/" + groupId);
    expect(response).to.have.status(200);
    expect(response).to.have.header("content-type", "application/json");
    expect(response).to.have.json("id", function (id) {
      expect(id).to.be.a("string");
    });
    expect(response).to.have.json("name", function (url) {
      expect(url).to.equal("Hello World");
    });
    expect(response).to.have.json("currency", function (currency) {
      expect(currency).to.equal("chf");
    });
    expect(response).to.have.json("update-time", function (updateTime) {
      var check = Date.parse(updateTime);
      expect(check).not.to.be.NaN;
    });
    expect(response).to.have.json("create-time", function (createTime) {
      var check = Date.parse(createTime);
      expect(check).not.to.be.NaN;
    });
    return chakram.wait();
  })

  it("should return Not authorized to group", function () {
      var response = chakram.get(baseUrl + "/groups/ABC");
      expect(response).to.have.status(401);
      expect(response).to.have.json("error", function (error) {
        expect(error[0]).to.equal("NOT_AUTHORIZED_TO_GROUP");
      });
      return chakram.wait();
  })
});

describe("Edit a group", function () {
  var groupId;
  before("create group", function () {
    var group = {
      name: "Hello World"
    };
    return chakram.post(baseUrl + "/groups", group)
    .then(function (response) {
      groupId = response.body.id;
    })
  });

  it("should edit group", function () {
    var editedGroup = new Object();
    editedGroup.name = "Hallo Welt";
    editedGroup.currency = "eur";

    var response = chakram.put(baseUrl + "/groups/" + groupId, editedGroup);
    expect(response).to.have.status(200);
    expect(response).to.have.header("content-type", "application/json");
    expect(response).to.have.json("id", function (id) {
      expect(id).to.be.a("string");
    });
    expect(response).to.have.json("name", function (url) {
      expect(url).to.equal("Hallo Welt");
    });
    expect(response).to.have.json("currency", function (currency) {
      expect(currency).to.equal("eur");
    });
    expect(response).to.have.json("update-time", function (updateTime) {
      var check = Date.parse(updateTime);
      expect(check).not.to.be.NaN;
    });
    expect(response).to.have.json("create-time", function (createTime) {
      var check = Date.parse(createTime);
      expect(check).not.to.be.NaN;
    });
    return chakram.wait();
  })

  it("should return Not authorized to group", function () {
    var response = chakram.put(baseUrl + "/groups/ABC");
    expect(response).to.have.status(401);
    expect(response).to.have.json("error", function (error) {
      expect(error[0]).to.equal("NOT_AUTHORIZED_TO_GROUP");
    });
    return chakram.wait();
  })

  /* Commenting out till pull/38
  it("should return Currency not valid", function () {
    var editedGroup = new Object();
    editedGroup.name = "Hallo Welt";
    editedGroup.currency = "abc";

    var response = chakram.put(baseUrl + "/groups/" + groupId, editedGroup);
    expect(response).to.have.status(401);
    expect(response).to.have.json("error", function (error) {
      expect(error[0]).to.equal("CURRENCY_NOT_VALID");
    });
    return chakram.wait();
  })
  */
});

describe("Delete group", function () {
  var groupId;
  before("create group", function () {
    var group = {
      name: "Hello World"
    };
    return chakram.post(baseUrl + "/groups", group)
    .then(function (response) {
      groupId = response.body.id;
    })
  });

  it("should delete group", function () {
    var response = chakram.delete(baseUrl + "/groups/" + groupId);
    expect(response).to.have.status(200);
    expect(response).to.have.header("content-type", "application/json");
    return chakram.wait();
  })

  it("should return Not authorized to group", function () {
    var response = chakram.delete(baseUrl + "/groups/ABC");
    expect(response).to.have.status(401);
    expect(response).to.have.json("error", function (error) {
      expect(error[0]).to.equal("NOT_AUTHORIZED_TO_GROUP");
    });
    return chakram.wait();
  })
});
