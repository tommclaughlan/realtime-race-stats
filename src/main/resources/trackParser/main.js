var path = require('svg-path-properties');
var extract = require('extract-svg-path');

var fs = require('fs');

var fileToRead = process.argv[2];

if (!fileToRead) {
    console.log('No file specified to read');
    process.exit(0);
}

var pathToParse = extract(fileToRead);

var properties = path.svgPathProperties(pathToParse);


var parts = properties.getParts();

var length = 0;

var parsedPath = {
    svg_url : fileToRead,
    parts : parts.map(function(part) {
        var type = part.type.indexOf('Bezier') !== -1 ? 'c' : 's';
        return {
            length : part.length,
            type : type
        }
    })
};

fs.writeFile(fileToRead + '.track', JSON.stringify(parsedPath), (err) => {
    if (err) throw err;
    console.log('Saved track as ', fileToRead + '.track');
});