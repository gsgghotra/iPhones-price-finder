const request = require("supertest")("http://localhost:8081");
const expect = require("chai").expect;

describe("Get All phones", function () {
    it("returns all phones", async function () {
        const response = await request.get("/phone");
        expect(response.status).to.eql(200);
        //expect(response.body.data.length).to.eql(229);
        //expected 200 phones and value can go up or below by 100
        expect(response.body.data.length).to.approximately(200, 100);
    });
});

describe("Get phones limit 10 and 20 offside", function () {
    it("returns phones limited to 10 and 20 offside", async function () {
        const response = await request.get("/phone/?num_items=10&offset=20");

        expect(response.status).to.eql(200);
        expect(response.body.data.length).to.eql(10);
    });
}) ;

describe("get the phone by ID", function () {
    it("returns the phone with the specific ID if it exists", async function () {
        const response = await request.get("/phone/15937");

        expect(response.status).to.eql(200);
        expect(response.body.length).to.eql(1);
    });
}) ;


describe("Total number of phones in the Database", function () {
    it("Check if the response body contains the total number of phones", async function () {
        const response = await request.get("/phone");

        expect(response.status).to.eql(200);
        expect(response.body).to.haveOwnProperty('totNumItems');
      //  exp.body.should.have.property('totNumItems');

    });
}) ;

describe("Search for phone", function () {
    it("Returns maximum of 15 phones if it matches with the keywords", async function () {
        const response = await request.get("/search/?search=iPhone_13_pro_max");
        expect(response.status).to.eql(200);
        expect(response.body.length).to.greaterThan(0).lessThan(16);
    });
}) ;


//https://dev-tester.com/dead-simple-api-tests-with-supertest-mocha-and-chai/