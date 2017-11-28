var app = require('angular').module('racing');

app.factory('CarsModel', ['Diffusion', function(Diffusion) {
    var CarsModel = {
        teams : [],
        cars : []
    };

    CarsModel.addCar = function(car, name, team) {
        this.teams[team].cars[car] = { name : name };
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
        return this.cars;
    };

    CarsModel.updateCars = function(cars) {
        this.cars = cars;
    };

    return CarsModel;
}]);