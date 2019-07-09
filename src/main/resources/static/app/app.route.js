(function () {
    "use strict";

    var app = angular.module('App');

    app.constant('STATES', {
        LOGIN: 'login',
        HOME: 'home'
    });
    
    app.config(function ($stateProvider, $urlRouterProvider, STATES) {
        $urlRouterProvider.otherwise(STATES.HOME);
        $stateProvider
                .state(STATES.HOME, {url: '/home', component: 'home.component', canActivate: 'AuthGuard'})
                .state(STATES.LOGIN, {url: '/login', component: 'login.component'});
    });

    app.run(function ($transitions) {
        $transitions.onStart({}, function (transition) {
            if (transition.to().canActivate) {
                var guard = transition.injector().get(transition.to().canActivate);
                return guard.canActivate(transition.to());
            }
            return true;
        });
    });
})();
