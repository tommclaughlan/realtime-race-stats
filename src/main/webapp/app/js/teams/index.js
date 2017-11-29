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
            return !(c.carid === car && c.teamid === team);
        });

        this.cars.push({
            name : name,
            colour : this.colours[team],
            team : this.teams[team].name,
            teamid : team,
            carid : car,
            pos : { x : 0, y : 0 },
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
            return car.carid === carid && car.teamid === teamid;
        });
    };

    CarsModel.getCars = function() {
        return CarsModel.cars;
    };

    CarsModel.updateCarPosition = function(car, team, position) {
        var c = CarsModel.getCar(car, team);
        var pos = TrackModel.getPositionAtLength(position);
        c.pos = pos;

        if (this.teams[team] && this.teams[team].cars[car]) {
            this.teams[team].cars[car].position = position;
        }
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