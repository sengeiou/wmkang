import React, { Component } from "react";
import {
    Button,
    Modal,
    ModalHeader,
    ModalBody,
    ModalFooter,
    Form,
    FormGroup,
    Input,
    Label
} from "reactstrap";


export default class LoginModal extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loginInfo: {
                id:     "wmkang@wmkang.com",
                passwd: "abc123!@#"
            }
        };
    }

    handleChange = e => {
        let { name, value } = e.target;
        const loginInfo = { ...this.state.loginInfo, [name]: value };
        this.setState({ loginInfo });
    };

    render() {
        const { loginToggle, login } = this.props;
        return (
          <Modal isOpen={true} toggle={loginToggle}>
            <ModalHeader toggle={loginToggle}><h3>로그인</h3></ModalHeader>
            <ModalBody>
              <Form>
                <FormGroup>
                  <Label    for="id">아이디</Label>
                  <Input    type="text"
                            name="id"
                            value={this.state.loginInfo.id}
                            onChange={this.handleChange}
                            placeholder="아이디를 입력하세요"
                  />
                </FormGroup>
                <FormGroup>
                  <Label    for="passwd">패스워드</Label>
                  <Input    type="text"
                            name="passwd"
                            value={this.state.loginInfo.passwd}
                            onChange={this.handleChange}
                            placeholder="패스워드를 입력하세요"
                  />
                </FormGroup>
              </Form>
            </ModalBody>
            <ModalFooter>
                <Button color="success" onClick={() => login(this.state.loginInfo)}>
                    로그인
                </Button>
            </ModalFooter>
          </Modal>
        );
    }
}
