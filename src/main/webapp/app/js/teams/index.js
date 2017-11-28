var app = require('angular').module('racing');

app.factory('CarsModel', ['Diffusion', function(Diffusion) {
    var CarsModel = {
        teams : [],
        cars : [],
        colours : ['#cc0066', '#00aeef', '#00b956', '#ffc600', '#f64d3c']
    };

    CarsModel.addCar = function(car, name, team) {
        this.teams[team].cars[car] = { name : name, colour : this.colours[team] };
    };

    CarsModel.addTeam = function(team, name) {
        this.teams[team] = {
            name : name,
            cars : []
        };
    };

    CarsModel.getCar = function(car, team) {
        return CarsModel.cars.find(function(car) {
            return car.id === car && car.teamid === team;
        });
    };

    CarsModel.getTeamName = function(team) {
        return CarsModel.teams[team];
    };

    CarsModel.getCars = function() {
        return CarsModel.cars;
    };

    CarsModel.updateCars = function(cars) {
        CarsModel.cars = cars;
    };

    CarsModel.updateCarPosition = function(car, team, position) {
        if (this.teams[team]) {
            this.teams[team].cars[car].position = position;
        }
    };

    return CarsModel;
}]);