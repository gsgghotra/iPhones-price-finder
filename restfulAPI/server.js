// Import the required modules
var express = require('express');
var url = require("url");

// Status codes defined in an external file
require('./http_status.js');

// The express module is a function. When executed, it returns an app object
var app = express();

// Import the mysql module
var mysql = require('mysql');

// Create a connection object with the user details
var connectionPool = mysql.createPool({
    connectionLimit: 1,
    host: "localhost",
    user: "root",
    password: "root",
    database: "web_scraper",
    debug: false 
});

// Enable Cross-Origin Resource Sharing (CORS) for all routes
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});

// Set up the application to handle GET requests sent to specific paths
app.get('/phone', handleGetRequest);
app.get('/phone/*', handleGetRequest); // Subfolders
app.get('/search/*', handleGetRequest); // Subfolders

// Start the app listening on port 8081
app.listen(8081);

/* Handles GET requests sent to the web service.
   Processes the path and query string and calls appropriate functions to return the data. */
    function handleGetRequest(request, response) {
    // Parse the URL
    var urlObj = url.parse(request.url, true);

    // Extract object containing queries from the URL object.
    var queries = urlObj.query;

    // Get the pagination properties if they have been set. Will be undefined if not set.
    var numItems = queries['num_items'];
    var offset = queries['offset'];
    var search = queries['search'];

    // Split the path of the request into its components
    var pathArray = urlObj.pathname.split("/");

    // Get the last part of the path
    var pathEnd = pathArray[pathArray.length - 1];

    if (pathEnd === 'phone') {
        // If the path ends with 'phone', return all phones or apply pagination
        if (numItems !== undefined && offset !== undefined) {
            // Paginated response
            getAllPhone(response, numItems, offset);
        } else {
            // All phones response
            getTotalPhoneCount(response);
        }
    } else if (pathEnd === '' && pathArray[pathArray.length - 2] === 'search') {
        // If the path ends with 'search/', return search results
        if (search) {
            getSearchProduct(response, search, numItems, offset);
        } else {
            // Handle the case where 'search/' is accessed without a search query
            response.status(HTTP_STATUS.BAD_REQUEST);
            response.json({ error: 'Search query is missing', url: request.url });
        }
    } else if (pathEnd === '' && pathArray[pathArray.length - 2] === 'phone') {
        // If the path ends with 'phone/', return all phones or apply pagination
        if (numItems !== undefined && offset !== undefined) {
            // Paginated response
            getAllPhone(response, numItems, offset);
        } else {
            // All phones response
            getTotalPhoneCount(response);
        }
    } else {
        // If the last part of the path is a valid id, return data about that phone
        var regEx = new RegExp('^[0-9]+$'); // RegEx returns true if the string is all digits.
        if (regEx.test(pathEnd)) {
            getPhone(response, pathEnd);
        } else {
            // The path is not recognized. Return an error message
            response.status(HTTP_STATUS.NOT_FOUND);
            response.json({ error: 'Path not recognized', url: request.url });
        }
    }
}


/** Returns all phones, possibly with a limit on the total number of items returned and the offset (to enable pagination) */
function getAllPhone(response, totNumItems, numItems, offset) {
    // Select the phone data using JOIN to convert foreign keys into useful data.
    var sql = "SELECT phone.id, MAX(image) AS image, MAX(colour) AS colour, MAX(brand) AS brand, MAX(model) AS model, MAX(website) AS website, phone_id, MIN(price) AS minimumPrice, MAX(websiteUrl) AS websiteUrl, MAX(storage) AS storage FROM phone INNER JOIN price ON phone.id = price.phone_id GROUP BY phone.id, phone_id ";
    
    // Limit the number of results returned if specified in the query string
    if (numItems !== undefined && offset !== undefined) {
        sql += " ORDER BY phone.id LIMIT " + numItems + " OFFSET " + offset;
    }


    // Execute the query
    connectionPool.query(sql, function (err, result) {
        if (err) {
            // If there's an error, provide a clear error response
            const errorMessage = "An error occurred while fetching data from the database.";

            // Log the error for debugging
            console.error("Database error:", err);

            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({ error: true, message: errorMessage });

            return;
        }

        // Create a JavaScript object that combines the total number of items with data
        var returnObj = { totNumItems: totNumItems };
        returnObj.data = result; // Array of data from the database

        // Return results in JSON format
        response.json(returnObj);
    });
}

function getSearchProduct(response, search) {
    let mySearch = '%' + search + '%'; // Use parameterized query with placeholders
    console.log(mySearch);

    var sql = "SELECT phone.id, MAX(price.id) AS price_id, MAX(brand) AS brand, MAX(image) AS image, MAX(colour) AS colour, MAX(model) AS model, MAX(website) AS website, phone_id AS phone_id, MIN(price) AS minimumPrice, MAX(websiteUrl) AS websiteUrl, MAX(storage) AS storage FROM phone INNER JOIN price ON phone.id = price.phone_id WHERE model LIKE ? OR colour LIKE ? OR storage LIKE ? GROUP BY phone.id, phone_id LIMIT 15";

    // Execute the query with placeholders
    connectionPool.query(sql, [mySearch, mySearch, mySearch], function (err, result) {
        // Check for errors
        if (err) {
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({ error: true, message: err });
            return;
        }
        response.json(result);
    });
}


/** When retrieving all phones, we start by retrieving the total number of phones.
 The database callback function will then call the function to get the phone data with pagination. */
function getTotalPhoneCount(response, numItems, offset) {
    var sql = "SELECT COUNT(*) FROM phone";

    // Execute the query and call the anonymous callback function.
    connectionPool.query(sql, function (err, result) {
        // Check for errors
        if (err) {
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({ error: true, message: err });
            return;
        }

        // Get the total number of items from the result
        var totNumItems = result[0]['COUNT(*)'];

        // Call the function that retrieves all phones
        getAllPhone(response, totNumItems, numItems, offset);
    });
    //console.log(response); // Commented out for clarity
}


/** Returns the phone with the specified ID */
function getPhone(response, id) {
    // Build an SQL query to select a phone with the specified ID.
    var sql = "SELECT brand, model, website, price, colour, websiteUrl, storage " +
                "FROM phone " +
                "INNER JOIN price ON phone.id = price.phone_id " +
                "WHERE price.phone_id = ?";
        
    // Use placeholders for the parameterized query to prevent SQL injection
    connectionPool.query(sql, [id], function (err, result) {
        // Check for errors
        if (err) {
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({ error: true, message: err.message }); // Use err.message to get the error message
            return;
        }
        // Check if a phone with the specified ID exists
        if (result.length === 0) {
            response.status(HTTP_STATUS.NOT_FOUND);
            response.json({ error: true, message: "Phone not found" });
        } else {
            // Output results in JSON format
            response.json(result[0]); // Assuming you only expect one phone with the given ID
        }
    });
}
