var app = require('angular').module('racing');

app.controller('ConnectingController', ['$scope', '$state', '$timeout', 'Diffusion', function($scope, $state, $timeout, Diffusion) {



    $state.go('race');
}]);