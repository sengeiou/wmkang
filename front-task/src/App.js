import React, { Component } from "react";
import "./App.css";
import TodoList from "./components/TodoList";


class App extends Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return (
            <div className="App">
                <TodoList data={this.state.todos} />
            </div>
        );
    }
}

export default App;

