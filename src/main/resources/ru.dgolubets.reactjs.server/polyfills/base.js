(function (context) {
    context.global = context;

    var logger = context.logger;

    context.console = context.console || {};

    context.console.debug = function () {
        for (var i = 0; i < arguments.length; i++) {
            logger.debug(arguments[i])
        }
    };

    context.console.info = function () {
        for (var i = 0; i < arguments.length; i++) {
            logger.info(arguments[i])
        }
    };

    context.console.warn = function () {
        for (var i = 0; i < arguments.length; i++) {
            logger.warning(arguments[i])
        }
    };

    context.console.error = function () {
        for (var i = 0; i < arguments.length; i++) {
            logger.error(arguments[i])
        }
    };

    context.console.log = context.console.info;

    context.console.trace = context.console.debug;

})(this);