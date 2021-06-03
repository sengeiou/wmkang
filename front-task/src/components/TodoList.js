import React from "react";
import axios from "axios";
import 'react-notifications/lib/notifications.css';
import {NotificationContainer, NotificationManager} from 'react-notifications';
import Pagination from "react-js-pagination";
import TaskModal from "./TodoModal";
import UploadModal from "./UploadModal";
import LoginModal from "./LoginModal";
import "./TodoList.css"


const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8000';


axios.defaults.baseURL = API_URL;
axios.defaults.withCredentials = true;


class TodoList extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            loginModal: false,
            loginInfo: {
                id:     "",
                passwd: ""
            },
            userInfo: {
                name:   "",
                email:  "",
                role:   "",
                shard:  ""
            },
            activeItem: {
                title:  "",
                priors: "",
                completed: false
            },
            todoList: [],
            searching : {
                title:  "",
                fromDate: "",
                completed: false,
                activated: false
            },
            paging : {
                page: 1,
                size: 10,
                sort: "createdDate,desc",
                total: 0
            },
            session: ""
        };
    }

    componentDidMount() {
        axios.defaults.withCredentials = true;
        this.createNotification('server.info');
        this.loginToggle();
    }

    createNotification = (type, msg) => {
        switch (type) {

            case 'server.info':
                NotificationManager.info(`${API_URL}`, '서버 접속 주소', 2000);
                break;

            case 'login.success':
                NotificationManager.success(this.state.userInfo.name + '님 환영합니다!', '로그인 성공', 2000);
                break;
            case 'login.fail':
                if(msg) {
                    NotificationManager.error(msg, '로그인 실패', 3000);
                } else {
                    NotificationManager.error('로그인 실패하였습니다.', '로그인 실패', 3000);
                }
                break;

            case 'create.success':
                NotificationManager.success('작업이 성공적으로 생성되었습니다', '작업 생성 성공', 2000);
                break;
            case 'create.fail':
                if(msg) {
                    NotificationManager.error(msg, '작업 생성 실패', 3000);
                } else {
                    NotificationManager.error('작업 생성에 실패하였습니다.', '작업 생성 실패', 3000);
                }
                break;

            case 'modify.success':
                NotificationManager.success('작업이 성공적으로 수정되었습니다.', '작업 수정 성공', 2000);
                break;
            case 'modify.fail':
                if(msg) {
                    NotificationManager.error(msg, '작업 수정 실패', 3000);
                } else {
                    NotificationManager.error('작업 수정에 실패하였습니다.', '작업 수정 실패', 3000);
                }
                break;

            case 'delete.success':
                NotificationManager.success('작업이 성공적으로 삭제되었습니다.', '작업 삭제 성공', 2000);
                break;
            case 'delete.fail':
                if(msg) {
                    NotificationManager.error(msg, '작업 삭제 실패', 3000);
                } else {
                    NotificationManager.error('작업 삭제에 실패하였습니다.', '작업 삭제 실패', 3000);
                }
                break;

            case 'load.fail':
                if(msg) {
                    NotificationManager.error(msg, '데이터 조회 오류', 3000);
                } else {
                    NotificationManager.error(`서버(${API_URL}) 접속 장애`, '데이터 조회 오류', 3000);
                }
                break;

            case 'search.fail':
                if(msg) {
                    NotificationManager.error(msg, '검색 오류', 3000);
                } else {
                    NotificationManager.error(`검색 수행 중 오류가 발생하였습니다.`, '검색 오류', 3000);
                }
                break;

            case 'inform':
                NotificationManager.info(msg, '알림', 3000);
                break;

            case 'import.success':
                NotificationManager.success('성공적으로 복원되었습니다.', 'DB 복원 성공', 2000);
                break;
            case 'import.fail':
                if(msg) {
                    NotificationManager.error(msg, 'DB 복원 실패', 3000);
                } else {
                    NotificationManager.error('DB 복원에 실패하였습니다.', 'DB 복원 실패', 3000);
                }
                break;

            case 'debug':
                NotificationManager.info(msg, '디버그', 3000);
                break;

            case 'error':
                NotificationManager.error(msg, '에러', 3000);
                break;

            default:
                NotificationManager.warning('디폴트 메시지', type, 3000);
        }
    };

    refreshTasks = direct => {
        if(this.state.searching.activated === true) {
            this.handleSearch();
        } else {
            axios.get( `${API_URL}/task/task/page`,
                { withCredentials: true,
                  params: { "page": this.state.paging.page,
                            "size": this.state.paging.size,
                            "sort": this.state.paging.sort
            }}).then( resp => {
                this.setState({ todoList: resp.data.data });
                this.setState({ paging: { page:  this.state.paging.page,
                                          size:  this.state.paging.size,
                                          sort:  this.state.paging.sort,
                                          total: resp.data.page.total } });
                if(direct) {
                    this.createNotification('inform', `전체 작업은 총 ${resp.data.page.total}건 입니다!`);
                }
            }).catch(error => {
                this.handleError(error, 'load');
            });
        }
    };

    handleSearch = direct => {
        this.setState({ searching: { title: this.state.searching.title,
                                     fromDate: this.state.searching.fromDate,
                                     completed: this.state.searching.completed,
                                     activated: true } });

        axios.get( `${API_URL}/task/task/search/`, {withCredentials: true, params: {
            "title": this.state.searching.title,
            "fromDate": this.state.searching.fromDate,
            "completed": this.state.searching.completed,
            "page": this.state.paging.page - 1,
            "size": this.state.paging.size,
            "sort": this.state.paging.sort
        }}).then(res => {
            this.setState({ todoList: res.data.data });
            this.setState({ paging: { page: this.state.paging.page,
                                      size: this.state.paging.size,
                                      sort: this.state.paging.sort,
                                      total: res.data.totalElements } });
            if(direct) {
                this.createNotification('inform', `총 ${res.data.page.total}건이 검색되었습니다!`);
            }
        }).catch(error => {
            this.handleError(error, 'search');
        });
    };

    renderItems = () => {
        const newItems = this.state.todoList.filter(
            item => item.id != null
        );
        /*const newItems = this.state.todoList;*/
        return newItems.map(item => (
            <li key={item.id} className="list-group-item d-flex justify-content-between align-items-center">
                <ul className='row-1'>
                    <span className={`todo-title mr-2 ${item.completed ? "completed-todo" : ""}`} title={item.title}>
                       <font color='gray'>{item.id} )</font> &nbsp;&nbsp;    <b>{item.title}</b>
                    </span>
                    <span className='todo-action mr-2'>
                        <font color='gray'>{item.completed? "완료" : "미완료"} / {item.modifiedDate} / {item.createdDate}</font>
                    </span>
                </ul>
                <ul className='row-2'>
                    <span>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color='green'>선행작업 : {item.priors}</font>
                    </span>
                    <span className='todo-action mr-2'>
                        <button onClick={() => this.editItem(item)} className="btn btn-default mr-2">
                            수정{""}
                        </button>
                        <button onClick={() => this.handleDelete(item)} className="btn btn-danger">
                            삭제{" "}
                        </button>
                    </span>
                </ul>
            </li>
          ) );
    };

    loginToggle = () => {
        this.setState({ loginModal: !this.state.logindModal });
    }

    toggle = () => {
        this.setState({ modal: !this.state.modal });
    };

    uploadToggle = () => {
        this.setState({ uploadModal: !this.state.uploadModal });
    };

    // 로그인
    handleLogin = input => {

        var username = input.id;
        var passwd   = input.passwd;

        const login_url = `${API_URL}/task/open/user/login`;
        const token = `${username}:${passwd}`;
        const encodedToken = Buffer.from(token).toString('base64');

        var config = {
            method: 'get',
            url: login_url,
            withCredentials: true,
            headers: { 'Authorization': 'Basic '+ encodedToken }
        };

        axios(config)
        .then( resp => {
            var data = resp.data.data;
            this.setState({ userInfo: { name:   data.name,
                                        email:  data.email,
                                        role:   data.role,
                                        shard:  data.shard  }});
            this.setState({loginModal : false});
            this.setState({session : false});
            this.createNotification('login.success');
            this.refreshTasks();
        }).catch( error => {
            this.handleError(error, 'login');
        });
    };

    handleSubmit = item => {
        this.toggle();
        /*this.setState({ searching : {activated: false }});*/
        if (item.id) {
            // 작업 수정
            axios({ method: 'PUT',
                    url: `${API_URL}/task/task/${item.id}`,
                    withCredentials: true,
                    data: { "title": item.title,
                            "priors": item.priors,
                            "completed": item.completed}
            }).then(resp => {
                this.createNotification('modify.success');
                this.refreshTasks();
            }).catch(error => {
                this.handleError(error, 'modify');
            });
            return;
        } else {
            // 작업 추가
            axios({ method: 'POST',
                    url: `${API_URL}/task/task`,
                    withCredentials: true,
                    data: { "title": item.title,
                            "priors": item.priors,
                            "completed": item.completed}
            }).then(resp => {
                this.createNotification('create.success');
                this.refreshTasks();
            }).catch(error => {
                this.handleError(error, 'create');
            });
        }
    };

    // 작업 삭제
    handleDelete = item => {
        axios.delete(`${API_URL}/task/task/${item.id}/`, { withCredentials: true})
        .then(resp => {
            this.refreshTasks();
            this.createNotification('delete.success');
        }).catch(error => {
            this.handleError(error, 'delete');
        });
    };

    // DB Export(백업)
    handlDatabaseeExport = () => {
        window.location.href = `${API_URL}/task/database/export`;
    }

    // DB Import(복원)
    handlDatabaseeImport = file => {
        this.uploadToggle();
        var formData = new FormData();
        formData.append("file", file);
        axios.post(`${API_URL}/task/database/import/`,
                    formData,
                   {withCredentials: true, headers: {'Content-Type': 'multipart/form-data'}}
        ).then(resp => {
            this.createNotification('import.success');
            this.refreshTasks();
        }).catch(error => {
            this.handleError(error, 'import');
        });
    }

    createItem = () => {
        const item = { title: "", priors: "", completed: false };
        this.setState({ activeItem: item, modal: !this.state.modal });
    };

    editItem = item => {
        this.setState({ activeItem: item, modal: !this.state.modal });
    };

    openUpload = () => {
        this.setState({ uploadModal: !this.state.uploadModal });
    };

    handleChangeForSearch = e => {
        let { name, value } = e.target;
        if (e.target.type === "checkbox") {
            value = e.target.checked;
        }
        var searching = { ...this.state.searching, [name]: value };
        this.setState({ searching: searching });
    };

    handleFindAll = () => {
        this.state.searching = { title: "",
                                 fromDate: "",
                                 completed: false,
                                 activated:  false };
        this.state.paging = { page:  1,
                              size:  this.state.paging.size,
                              sort:  this.state.paging.sort,
                              total: 0 };
        this.refreshTasks(true);
    }

    handlePageChange = pageNo => {
        this.createNotification('inform', `${pageNo}번 페이지로 이동합니다!`);
        this.state.paging.page = pageNo;
        if(this.state.searching.activated === true) {
            this.handleSearch();
        } else {
            this.refreshTasks();
        }
    }

    handleError = (error, msgPrefix) => {
        console.log(JSON.stringify(error.response));
        if((error.status)&&(error.status === 401)){
            this.loginToggle();
        } else {
            if( !error.response || !error.response.data ) {
                this.createNotification(msgPrefix+'.fail');
            } else {
                console.log(JSON.stringify(error));
                var response = error.response.data;
                if((response.data) && (response.header) && (response.header.message)) {
                    this.createNotification(msgPrefix+'.fail', response.header.message + ' - ' + response.data );
                } else if((response.header) && (response.header.message)) {
                    this.createNotification(msgPrefix+'.fail', response.header.message);
                } else if(response.data) {
                    this.createNotification(msgPrefix+'.fail', response.data);
                } else {
                    console.log(JSON.stringify(response));
                    this.createNotification(msgPrefix+'.fail', response);
                }
            }
        }
    }

    renderPaging() {
        return (
            <div>
                <Pagination
                    activePage={this.state.paging.page}
                    itemsCountPerPage={this.state.paging.size}
                    totalItemsCount={this.state.paging.total}
                    pageRangeDisplayed={10}
                    onChange={this.handlePageChange.bind(this)}
                />
            </div>
        );
    }


render() {

    return (
      <main className="content">
        <h1 className="text-white text-uppercase text-center my-4">작업 관리</h1>
        <div className="row ">
            <NotificationContainer/>
          <div className="col-lg-7 col-md-8 col-sm-10">
            <div className="card p-3">
              <div className="" align="center">
                <button onClick={() => this.handleFindAll()} className="btn btn-primary">
                  전체 보기
                </button>  &nbsp;&nbsp;
                <button onClick={this.createItem} className="btn btn-primary">
                  작업 추가
                </button>  &nbsp;&nbsp;
                <button onClick={() => this.handlDatabaseeExport()} className="btn btn-primary">
                  DB 백업
                </button>&nbsp;&nbsp;
                <button onClick={() => this.openUpload()}  className="btn btn-primary">
                  DB 복원
                </button>
              </div>
             <div align="center">
                <form>
                    작업명 &nbsp;&nbsp;
                    <input  type="text"
                            name="title"
                            size="20"
                            placeholder="작업명 키워드 입력"
                            value={this.state.searching.title}
                            onChange={this.handleChangeForSearch} /> &nbsp;&nbsp;
                    생성일자 &nbsp;&nbsp;
                    <input  type="text"
                            name="fromDate"
                            size="23"
                            placeholder="검색 시작일자 (yyyyMMdd)"
                            value={this.state.searching.fromDate}
                            onChange={this.handleChangeForSearch} />  &nbsp;&nbsp;
                    완료여부 &nbsp;&nbsp;
                    <input  type="checkbox"
                            name="completed"
                            value={this.state.searching.completed}
                            onChange={this.handleChangeForSearch} />  &nbsp;&nbsp;&nbsp;&nbsp;
                    <input type="button" value="검색" onClick={() => this.handleSearch(true)} />
                </form>
                <font color="Purple">(검색 3가지 입력 항목 모두 필수이며, AND 조건)</font>
              </div>
              <ul className="list-group list-group-flush">
                {this.renderItems()}
                <li align="center">
                    {this.renderPaging()}
                </li>
              </ul>
            </div>
          </div>
        </div>
        {this.state.loginModal ? (
          <LoginModal
            loginToggle={this.loginToggle}
            login={this.handleLogin}
          />
        ) : null}
        {this.state.modal ? (
          <TaskModal
            activeItem={this.state.activeItem}
            toggle={this.toggle}
            onSave={this.handleSubmit}
          />
        ) : null}
        {this.state.uploadModal ? (
          <UploadModal
            uploadToggle={this.uploadToggle}
            upload={this.handlDatabaseeImport}
          />
        ) : null}
      </main>
    );
  }
}
export default TodoList;
