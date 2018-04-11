

(function(context) {
    context.global = context;

    var logger = context.logger || Interop.import('logger');

    if(!context.console){
        context.console = {
            log: function(){
                for(var i = 0; i < arguments.length; i ++){
                    logger.info(arguments[i])
                }
            },
            info: function(){
                for(var i = 0; i < arguments.length; i ++){
                    logger.info(arguments[i])
                }
            },
            debug: function(){
                for(var i = 0; i < arguments.length; i ++){
                    logger.debug(arguments[i])
                }
            },
            trace: function(){
                for(var i = 0; i < arguments.length; i ++){
                    logger.debug(arguments[i])
                }
            },
            warn: function(){
                for(var i = 0; i < arguments.length; i ++){
                    logger.warning(arguments[i])
                }
            },
            error: function(){
                for(var i = 0; i < arguments.length; i ++){
                    logger.error(arguments[i])
                }
            }
        };
    }

})(this);