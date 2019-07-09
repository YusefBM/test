(function () {
    "use strict";

    var app = angular.module('App');

    app.component('home.component', {
        templateUrl: '/app/components/home/home.component.html',
        controllerAs: 'ctrl',
        controller: function (AuthService, $state, STATES) {
            var ctrl = this;
            ctrl.state = 'waiting';
            ctrl.user = {};

            ctrl.$onInit = function () {
                ctrl.user = AuthService.getUser();
                ctrl.state = 'ready';
            };

            ctrl.logout = function () {
                AuthService.logout();
                $state.go(STATES.LOGIN);
            };
        }});
})();
