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

    var updateLeaderBoard = function() {
        var cars = CarsModel.teams.reduce(function(res, team, i) {
            team.cars.forEach(function(car, j) {
                var pos = TrackModel.getPositionAtLength(car.position);
                res.push({ name : car.name, team : team.name, pos : { x : pos.x, y : pos.y }, colour : car.colour, teamid : i, carid : j, selected : car.selected });
            });
            return res;
        }, []);

        CarsModel.updateCars(cars);
    };

    Diffusion.session().stream('race/updates/fast').asType(Diffusion.datatypes.json())
        .on('value', function(topic, spec, value) {
            $scope.$apply(function() {
                var val = value.value.get();

                val.forEach(function(team, i) {
                    team.forEach(function(car, j) {
                        CarsModel.updateCarPosition(j, i, car.pos);
                    });
                });

                updateLeaderBoard();
            });
        });
    Diffusion.session().subscribe('race/updates/fast');
}]);