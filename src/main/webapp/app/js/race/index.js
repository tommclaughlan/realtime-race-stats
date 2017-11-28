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

    Diffusion.session();

    $interval(function() {
        var cars = CarsModel.teams.reduce(function(res, team) {
            team.cars.forEach(function(car) {
                res.push({ name : car.name, team : team.name });
            });
            return res;
        }, []);

        CarsModel.updateCars(cars);
    }, 1000);
}]);