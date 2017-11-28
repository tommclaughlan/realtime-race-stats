var app = require('angular').module('racing');

app.directive('raceMap', function() {
    return {
        restrict : 'E',
        templateUrl : 'views/track.html'
    };
});

app.directive('leaderboard', function() {
    return {
        restrict : 'E',
        templateUrl : 'views/leaderboard.html'
    };
});

app.controller('RaceController', ['$scope', 'TrackModel', function($scope, TrackModel) {

    TrackModel.init('tracks/track1.svg.track');

    $scope.getTrack = function() {
        return TrackModel.getPath();
    };

}]);