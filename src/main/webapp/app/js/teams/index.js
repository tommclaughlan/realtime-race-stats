var app = require('angular').module('racing');

app.factory('CarsModel', ['Diffusion', 'TrackModel', function(Diffusion, TrackModel) {
    var CarsModel = {
        teams : [],
        cars : [],
        toDraw : [],
        colours : ['#cc0066', '#00aeef', '#00b956', '#ffc600', '#f64d3c']
    };

    CarsModel.addCar = function(car, name, team) {
        this.teams[team].cars[car] = { name : name, colour : this.colours[team] };

        this.cars = this.cars.filter(function(c) {
            return !(c.id === car && c.teamid === team);
        });

        this.cars.push({
            name : name,
            colour : this.colours[team],
            team : this.teams[team].name,
            teamid : team,
            id : car,
            loc : { x : 0, y : 0 },
            pos : this.cars.length,
            speed : 120,
            selected : false
        });
    };

    CarsModel.addTeam = function(team, name) {
        this.teams[team] = {
            name : name,
            cars : []
        };
    };

    CarsModel.getCar = function(carid, teamid) {
        return CarsModel.cars.find(function(car) {
            return car.id === carid && car.teamid === teamid;
        });
    };

    CarsModel.getCars = function() {
        return CarsModel.cars;
    };

    CarsModel.updateCarPosition = function(car) {
        var id = car.id;
        var team = car.team;
        var location = car.loc;
        var position = car.pos;
        var laps = car.lap;
        var speed = car.speed;

        var c = CarsModel.getCar(id, team);
        var loc = TrackModel.getPositionAtLength(location);
        c.loc = loc;
        c.pos = position;
        c.laps = laps;
        c.speed = speed;
    };

    CarsModel.getSelectedCar = function() {
        return CarsModel.cars.find(function(car) {
            return car.selected;
        });
    };

    CarsModel.selectCar = function(i, j) {
        if (CarsModel.getSelectedCar()) {
            CarsModel.getSelectedCar().selected = false;
        }
        CarsModel.getCar(i, j).selected = true;
    };

    return CarsModel;
}]);