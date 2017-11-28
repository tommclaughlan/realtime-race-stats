var app = require('angular').module('racing');

app.controller('LeaderboardController', ['$scope', 'CarsModel', function($scope, CarsModel) {
    $scope.getOrderedCars = function() {
        return CarsModel.getCars();
    };

    $scope.getTeamName = CarsModel.getTeamName;
}]);
