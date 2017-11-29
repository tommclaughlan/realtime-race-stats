var app = require('angular').module('racing');

app.factory('ClockModel', function() {
    var ClockModel = {
        paused : false,
        live : true
    };

    ClockModel.togglePause = function() {
        ClockModel.paused = !ClockModel.paused;
    };

    ClockModel.setPaused = function(paused) {
        ClockModel.paused = paused;
    };

    ClockModel.setLive = function(live) {
        ClockModel.live = live;
    };

    ClockModel.isPaused = function() {
        return ClockModel.paused;
    };

    ClockModel.isLive = function() {
        return ClockModel.live;
    };

    ClockModel.setStartTime = function(time) {
        ClockModel.start = time;
    };

    ClockModel.setViewTime = function(time) {
        ClockModel.view = time;
    };

    ClockModel.setLiveTime = function(time) {
        ClockModel.latest = time;
    };

    ClockModel.getStartTime = function() {
        return ClockModel.start;
    };

    ClockModel.getViewTime = function() {
        return ClockModel.view;
    };

    ClockModel.getLiveTime = function() {
        return ClockModel.latest;
    };

    return ClockModel;
});

app.controller('ClockController', ['$scope', 'ClockModel', 'Diffusion', 'CarsModel', function($scope, ClockModel, Diffusion, CarsModel) {
    $scope.togglePause = ClockModel.togglePause;
    $scope.isPaused = ClockModel.isPaused;

    $scope.backToLive = function() {
        ClockModel.setLive(true);
        ClockModel.setPaused(false);
    };

    $scope.slider = {
        value : 1,
        options :{
            floor : 0,
            ceil : 1,
            onStart : function() {
                ClockModel.setLive(false);
                ClockModel.setPaused(true);
            },
            step : 1000
        }
    };

    var getHistoricalData = function() {
        Diffusion.session().timeseries.rangeQuery()
            .from(new Date(ClockModel.getViewTime()))
            .next(1)
            .as(Diffusion.datatypes.json())
            .selectFrom('race/updates').then(function(result) {
                var val = result.events[0].value.get();
                val.forEach(function(car) {
                    CarsModel.updateCarPosition(car.id, car.team, car.loc);
                });
            }, function(err) {
                console.log(err);
            });
    };

    if (Diffusion.session()) {
        Diffusion.session().timeseries.rangeQuery()
            .fromStart()
            .next(1)
            .as(Diffusion.datatypes.json())
            .selectFrom('race/updates').then(function(result) {
                ClockModel.setStartTime(result.events[0].timestamp);
            }, function(err) {
                console.log(err);
            });
    }

    $scope.$watch(function() {
        return ClockModel.getStartTime() + ' ' + ClockModel.getLiveTime();
    }, function() {
        $scope.slider.options.floor = ClockModel.getStartTime(),
        $scope.slider.options.ceil = ClockModel.getLiveTime();

        if (!ClockModel.isLive() && $scope.slider.value < ClockModel.getStartTime()) {
            $scope.slider.value = ClockModel.getStartTime();
        } else if (ClockModel.isLive() && !ClockModel.isPaused()) {
            $scope.slider.value = ClockModel.getLiveTime();
        }
    });

    $scope.$watch(function() {
        return $scope.slider.value;
    }, function() {
        ClockModel.setViewTime($scope.slider.value);

        if (!ClockModel.isLive()) {
            getHistoricalData();
        }
    });


}]);