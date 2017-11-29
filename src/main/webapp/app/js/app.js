'use strict';

var angular = require('angular');

require('@uirouter/angularjs');
require('angularjs-slider');
require('angularjs-gauge');

var app = angular.module('racing', ['ui.router', 'rzModule', 'angularjs-gauge']);

require('./diffusion');
require('./connecting');
require('./track');
require('./race');
require('./teams');
require('./leaderboard');
require('./clock');
require('./stats');

app.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/connecting');

    $stateProvider.state('connecting', {
        url : '/connecting', templateUrl : 'views/connecting.html',
        controller : 'ConnectingController'
    });

    $stateProvider.state('race', {
        url : '/race', templateUrl : 'views/race.html',
        controller : 'RaceController'
    });
}]);




