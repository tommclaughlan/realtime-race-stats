var app = require('angular').module('racing');

app.controller('StatsController', ['$scope', 'CarsModel', function($scope, CarsModel) {

    $scope.stats = false;

    $scope.carSelected = function() {
        return !!CarsModel.getSelectedCar();
    };

    $scope.getCar = function() {
        return CarsModel.getSelectedCar();
    };

    $scope.toggleStats = function() {
        $scope.stats = !$scope.stats;
    };
}]);