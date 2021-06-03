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

export default class TaskModal extends Component {

    constructor(props) {
        super(props);
        this.state = {
            activeItem: this.props.activeItem
        };
    }

    handleChange = e => {
        let { name, value } = e.target;
        if (e.target.type === "checkbox") {
            value = e.target.checked;
        }
        const activeItem = { ...this.state.activeItem, [name]: value };
        this.setState({ activeItem });
    };

    render() {
        const { toggle, onSave } = this.props;
        return (
          <Modal isOpen={true} toggle={toggle}>
            <ModalHeader toggle={toggle}><h3>작업 생성/수정</h3></ModalHeader>
            <ModalBody>
              <Form>
                <FormGroup>
                  <Label for="title">작업명</Label>
                  <Input 	type="text"
                    		name="title"
                    	 	value={this.state.activeItem.title}
                            onChange={this.handleChange}
                            placeholder="작업명을 입력하세요!"
                  />
                </FormGroup>
                <FormGroup>
                  <Label for="priors">상위 작업</Label>
                  <Input    type="text"
                            name="priors"
                            value={this.state.activeItem.priors}
                            onChange={this.handleChange}
                            placeholder="상위 작업 아이디를 입력하세요! - 예: 10,11,12 (쉼표 구분자)"
                  />
                </FormGroup>
                <FormGroup check>
                  <Label for="completed">
                    완료 여부 &nbsp;&nbsp;
                    <Input  type="checkbox"
                            name="completed"
                            checked={this.state.activeItem.completed}
                            onChange={this.handleChange}
                    />
                  </Label>
                </FormGroup>
              </Form>
            </ModalBody>
            <ModalFooter>
                <Button color="success" onClick={() => onSave(this.state.activeItem)}>저장</Button>
                <Button color="success" onClick={() => toggle()}>닫기</Button>
            </ModalFooter>
          </Modal>
        );
    }
}
