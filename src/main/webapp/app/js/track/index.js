
var app = require('angular').module('racing');

app.directive('track', function() {
    return {
        restrict : 'A',
        scope : {
            drawables : '@drawables'
        },
        link : function(scope, elem, attrs) {
            var times = 0;
            var setBoundingBox = function(ctx,alphaThreshold){
                if (alphaThreshold===undefined) alphaThreshold = 15;
                var minX=Infinity,minY=Infinity,maxX=-Infinity,maxY=-Infinity;
                var w=ctx.canvas.width,h=ctx.canvas.height;
                var data = ctx.getImageData(0,0,w,h).data;
                for (var x=0;x<w;++x){
                    for (var y=0;y<h;++y){
                        var a = data[(w*y+x)*4+3];
                        if (a>alphaThreshold){
                            if (x>maxX) maxX=x;
                            if (x<minX) minX=x;
                            if (y>maxY) maxY=y;
                            if (y<minY) minY=y;
                        }
                    }
                }

                var width = maxX - minX;
                var height = maxY - minY;
                if (width > 0 && height > 0) {
                    ctx.canvas.width = width * 1.2;
                    ctx.canvas.height = height * 1.2;
                }
            };

            var draw = function(drawables) {
                drawables = JSON.parse(drawables);
                var path = drawables.path;
                var ctx = elem[0].getContext('2d');

                ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

                var p = new Path2D(path);
                ctx.stroke(p);

                if (times < 5) {
                    setBoundingBox(ctx);
                    times++;
                }

                ctx.strokeStyle = '#aaa';
                ctx.lineWidth = 40;
                ctx.stroke(p);

                var line = drawables.startingLine;

                ctx.beginPath();
                ctx.strokeStyle = '#ffffff';
                ctx.moveTo(line.x0, line.y0);
                ctx.lineTo(line.x1, line.y1);
                ctx.stroke();

                var cars = drawables.cars;
                cars.forEach(function(car) {
                    ctx.beginPath();
                    ctx.fillStyle = car.colour;
                    ctx.arc(car.loc.x, car.loc.y, 10, 0, 2*Math.PI);
                    ctx.fill();
                    if (car.selected) {
                        ctx.strokeStyle = '#f1f1f1';
                        ctx.lineWidth = 4;
                    } else {
                        ctx.strokeStyle = '#444444';
                        ctx.lineWidth = 2;
                    }
                    ctx.stroke();
                });
            };

            attrs.$observe('drawables', draw);
        }
    }
});

app.factory('TrackModel', ['$http', function($http) {
    var svg = require('svg-path-properties');
    var TrackModel = {
        path : 'm 0,0 0,0',
        properties : null
    };

    TrackModel.init = function(trackName) {
        $http.get(trackName).then(function(data) {
            TrackModel.path = data.data.rawPath;
            TrackModel.properties = svg.svgPathProperties(TrackModel.path);
        });
    };

    TrackModel.getPath = function() {
        return TrackModel.path;
    };

    TrackModel.getPositionAtLength = function(length) {
        var relativeLength = length * TrackModel.properties.getTotalLength();
        return TrackModel.properties.getPointAtLength(relativeLength);
    };

    TrackModel.getStartingLine = function() {
        if (TrackModel.properties) {
            var tangent = TrackModel.properties.getTangentAtLength(0);
            var pos = TrackModel.properties.getPointAtLength(0);

            return {
                x0 : pos.x - tangent.x,
                y0 : pos.y - tangent.y,
                x1 : pos.x + tangent.x,
                y1 : pos.y + tangent.y
            };
        } else {
            return { x0 : 0, y0 : 0, x1 : 0, y1 : 0 };
        }
    };

    return TrackModel;
}]);