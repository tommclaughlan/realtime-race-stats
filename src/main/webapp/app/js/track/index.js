
var app = require('angular').module('racing');

app.directive('track', function() {
    return {
        restrict : 'A',
        scope : {
            path : '@path'
        },
        link : function(scope, elem, attrs) {
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

            var draw = function(path) {
                var ctx = elem[0].getContext('2d');

                var p = new Path2D(path);
                ctx.stroke(p);

                setBoundingBox(ctx);

                ctx.strokeStyle = '#aaa';
                ctx.lineWidth = 40;
                ctx.stroke(p);
            };

            attrs.$observe('path', draw);
        }
    }
});

app.factory('TrackModel', ['$http', function($http) {
    var svg = require('svg-path-properties');
    var TrackModel = {
        path : 'm 0,0 0,0'
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
        return TrackModel.properties.getPointAtLength(length);
    };

    return TrackModel;
}]);