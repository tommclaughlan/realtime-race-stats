var app = require('angular').module('racing');

app.controller('LeaderboardController', ['$scope', 'CarsModel', function($scope, CarsModel) {
    $scope.getOrderedCars = function() {
        return CarsModel.getCars();
    };

    $scope.selectCar = function(car, team) {
        console.log(car, team);
        CarsModel.selectCar(car, team);
    };

    $scope.getTeamName = CarsModel.getTeamName;
}]);
