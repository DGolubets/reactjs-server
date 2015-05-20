define(['react'], function(React){
    return React.createClass({
        render: function() {
            return (
                React.createElement("div", {className: "commentBox"}, 
                    "Hello, world! I am a CommentBox."
                )
            );
        }
    });
});
