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
        templateUrl : 'views/leaderboard.html',
        controller : 'LeaderboardController'
    };
});

app.controller('RaceController', ['$scope', '$interval', 'TrackModel', 'Diffusion', 'CarsModel', function($scope, $interval, TrackModel, Diffusion, CarsModel) {
    $scope.getTrack = function() {
        return TrackModel.getPath();
    };

    $scope.getDrawables = function() {
        return {
            path : TrackModel.getPath(),
            cars : CarsModel.getCars(),
            startingLine : TrackModel.getStartingLine()
        };
    };

    Diffusion.session().stream('race/updates/fast').asType(Diffusion.datatypes.json())
        .on('value', function(topic, spec, value) {
            $scope.$apply(function() {
                var val = value.value.get();

                val.forEach(function(team, i) {
                    team.forEach(function(car, j) {
                        CarsModel.updateCarLocation(j, i, car.loc);
                    });
                });

            });
        });
    Diffusion.session().subscribe('race/updates/fast');
}]);