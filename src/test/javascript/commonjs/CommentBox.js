var React = require('React');

module.exports = React.createClass({displayName: "exports",
    render: function() {
        return (
            React.createElement("div", {className: "commentBox", "data-url": this.props.url },
                "Hello, world! I am a CommentBox."
            )
        );
    }
});
