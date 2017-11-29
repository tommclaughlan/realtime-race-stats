var app = require('angular').module('racing');

app.controller('LeaderboardController', ['$scope', 'CarsModel', function($scope, CarsModel) {
    $scope.getOrderedCars = function() {
        return CarsModel.getCars();
    };

    $scope.selectCar = function(car, team) {
        CarsModel.selectCar(car, team);
    };

    $scope.selectedCar = function(car, team) {
        var selected = CarsModel.getSelectedCar();
        if (!selected) return false;
        return selected.carid === car && selected.teamid === team;
    };

    $scope.getTeamName = CarsModel.getTeamName;
}]);
