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
} from "reactstrap";


export default class UploadModal extends Component {

    constructor(props) {
        super(props);
        this.state = {
            importFile: null
        };
    }

    handleChange = e => {
        const dumpFile = e.target.files[0];
        this.state.importFile = dumpFile;
    };


    render() {
        const { uploadToggle, upload } = this.props;
        return (
          <Modal isOpen={true} toggle={uploadToggle}>
            <ModalHeader toggle={uploadToggle}><h3>데이터베이스 복원 (백업 덤프 파일 업로드)</h3></ModalHeader>
            <ModalBody>
              <Form action="" method="post" enctype="multipart/form-data">
                <FormGroup>
                  <Input 	type="file"
                            value={this.state.importFile}
                            onChange={this.handleChange}
                            placeholder=""
                  />
                </FormGroup>
              </Form>
            </ModalBody>
            <ModalFooter>
                <Button color="success" onClick={() => upload(this.state.importFile)}>
                    전송
                </Button>
                <Button color="success" onClick={() => uploadToggle()}>
                    닫기
                </Button>
            </ModalFooter>
          </Modal>
        );
    }
}
