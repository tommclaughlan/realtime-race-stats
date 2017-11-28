var app = require('angular').module('racing');

app.controller('ConnectingController', ['$scope', '$state', '$timeout', 'Diffusion', 'TrackModel', 'CarsModel', function($scope, $state, $timeout, Diffusion, TrackModel, CarsModel) {
    Diffusion.connect('localhost:8080', function() {
        Diffusion.session().stream('race').asType(Diffusion.datatypes.string())
            .on('value', function(topic, spec, value) {
                TrackModel.init(value);
                $state.go('race');
            });

        Diffusion.session().stream('race/teams').asType(Diffusion.datatypes.int64())
            .on('value', function(topic, spec, value) {
                for (var i = 0; i < value; ++i) {
                    Diffusion.session().stream('race/teams/' + i).asType(Diffusion.datatypes.string())
                        .on('value', function(topic, spec, value) {
                            var parts = topic.split('/');
                            var team = parseInt(parts[2], 10);
                            CarsModel.addTeam(team, value);
                        });

                    Diffusion.session().stream('race/teams/' + i + '/cars').asType(Diffusion.datatypes.int64())
                        .on('value', function(topic, spec, value) {
                            var parts = topic.split('/');
                            var team = parseInt(parts[2], 10);
                            var nCars = value;

                            for(var j = 0; j < value; ++j) {
                                Diffusion.session().stream('race/teams/' + team + '/cars/' + j).asType(Diffusion.datatypes.string())
                                    .on('value', function(topic, spec, value) {
                                        var parts = topic.split('/');
                                        var car = parseInt(parts[4], 10);
                                        CarsModel.addCar(car, value, team);
                                    });
                            }
                        });
                }
            });

        Diffusion.session().subscribe('race');
        Diffusion.session().subscribe('race/teams');
        Diffusion.session().subscribe('?race/teams/.*//');
    });
}]);