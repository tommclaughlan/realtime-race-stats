var app = require('angular').module('racing');

app.factory('StatsModel', function() {
    var StatsModel = {
        options : {
                    "chart": {
                      "type": "lineChart",
                      "height": 300,
                      "margin": {
                        "top": 20,
                        "right": 20,
                        "bottom": 40,
                        "left": 55
                      },
                      "useInteractiveGuideline": true,
                      "dispatch": {},
                      "xAxis": {
                        "axisLabel": "Lap"
                      },
                      "yAxis": {
                        "axisLabel": "Lap Time",
                        "axisLabelDistance": -10
                      }
                    },
                    "legend" : {enable:false},
                    "title": {
                      "enable": false
                    },
                    "subtitle": {
                      "enable": false
                    },
                    "caption": {
                      "enable": false
                    }
                  },
        data : [
            {
                values : [],
                key : 'Lap Times',
                color : '#f15500',
                area : false
            }
        ]
    };

    StatsModel.init = function(data) {
        StatsModel.clear();
        data.forEach(function(time, lap) {
            StatsModel.data[0].values.push({ x : lap, y : time });
        });
    };

    StatsModel.getLaps = function() {
        return StatsModel.data;
    };

    StatsModel.clear = function() {
        StatsModel.data[0].values = [];
    };

    StatsModel.dummy = function() {
        var promise = function(success, failure) {
            success([
                34.123,
                33.109,
                34.893,
                31.513,
                31.301,
                29.985,
                29.994,
                28.964,
                29.543,
                29.859
            ]);
        };

        return { then : promise };
    };

    return StatsModel;
});

app.controller('StatsController', ['$scope', 'CarsModel', 'StatsModel', function($scope, CarsModel, StatsModel) {

    $scope.stats = false;

    $scope.data = StatsModel.getLaps();

    $scope.carSelected = function() {
        return !!CarsModel.getSelectedCar();
    };

    $scope.getCar = function() {
        return CarsModel.getSelectedCar();
    };

    $scope.options = StatsModel.options;

    $scope.toggleStats = function() {
        $scope.stats = !$scope.stats;
        updateStats();
    };

    var updateStats = function() {
        if ($scope.stats) {
            StatsModel.dummy().then(function(data) {
                StatsModel.init(data);
                $scope.data = StatsModel.getLaps();
            }, function(err) {
                console.log(err);
            });
        }
    };
}]);