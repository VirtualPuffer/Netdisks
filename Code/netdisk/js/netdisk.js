let signout_btn = document.getElementById("signout_btn");
let filepath = document.querySelector(".filepath");
let filesDisplay = document.querySelector(".files_display");
let upload_btn = document.getElementById("upload_btn");
let check_all_btn = document.getElementById("check_all");
let mkdir_btn = document.getElementById("mkdir_btn");
let del_btn = document.getElementById("del_btn");
let download_btn = document.getElementById("download_btn");
let inputSearch = document.getElementById("inputSearch");
let searchIm = document.getElementById("search_btn");
let search_title = document.getElementById("search_title");
let title_select = document.getElementById("title_select");
let file_select = document.getElementById("file_select");
let filePath_select = document.getElementById("filePath_select");

var pathnow = "";
let getDirFd;
let nlisten = true;
let slisten = true;

// 新建文件夹
let mkbox = document.getElementById("mkdirbox")
let mkconfirm = document.getElementById("mkdir_submit")
let mkcalcel = document.getElementById("mkdir_cancel")
let mkinput = mkbox.querySelector("input")
function mkconfirmFun() {
    mkinput.value = mkinput.value.trim()
    if (!mkinput.value) {
        alert("文件名不能为空！")
        return;
    }

    let mkdirFd = new FormData();
    mkdirFd.append("source", pathnow + "/" + mkinput.value);
    axios.post("buildDir", mkdirFd).then((response) => {
        console.log(response.data.msg);
        if (response.data.code === 200) {
            mkbox.style.display = "none"
            mkinput.value = ""
            presentPathDisplay(pathnow);
        } else {
            alert("同名文件夹已存在！！")
        }
    }).catch((response) => {
        console.log(response.data.msg);
    });
}
mkinput.onkeyup = e => {
    if (e.key === "Enter" || e.keyCode === 13) mkconfirmFun()
}
mkconfirm.addEventListener("click", mkconfirmFun)


mkcalcel.onclick = () => {
    mkbox.style.display = "none"
    mkinput.value = ""
}
mkdir_btn.addEventListener("click", () => {
    mkbox.style.display = ""
})

// 全局设置axios允许cookie
// axios.defaults.withCredentials = true

axios.defaults.baseURL = "http://47.96.253.99/Netdisk/";


// 进行免登录检查
axios.get('freeLogin')
    .then(function (response) {
        if (response.data.msg)
            console.log(`axios结果: ${response.data.msg}`);
        if (response.data.code === 200) {
            document.getElementById("photo").innerHTML = "用户名：" + response.data.LogUsers.name;
            // 打印毛泽东
            console.log("%c                               \n%c最高指示：\n不要忘记阶级斗争！",
            `line-height:300px;
            background:url('http://47.96.253.99/Netdisk/img/cat.jpg') no-repeat 0 0;background-size: 200px`,
            "color:yellow;background-color:red;font-size:25px");
            //网页加载时初始化
            presentPathDisplay("");
        } else {
            window.location.href = "./register_login.html";
        }

    })
    .catch(function (error) {
        console.error(error);
    });

//清除cookie,重定向
signout_btn.addEventListener("click", () => {
    myConfirm("确认退出登录？", () => {
        // document.cookie = "verCode=; expires=Thu, 01 Jan 1970 00:00:00 GMT";
        alert('加载中，请稍后。。。')
        axios.get("/userLogout").then(response => {
            window.location.href = "./register_login.html";
        }).catch(res => {
            alert('登出失败！请稍后重试')
        })

    })

});

// 判断图标类型函数，返回值为字符串
function iconExtension(exten) {
    if (exten === "png" || exten === "jpg" || exten === "jpeg" || exten === "gif") {
        //图片
        return "./img/pic_icon.png";
    } else if (exten === "pdf") {
        // pdf文档
        return "./img/pdf_icon.png";
    } else if (exten === "zip" || exten === "rar") {
        // 压缩文件
        return "./img/zip_icon.png";
    } else if (exten === "doc" || exten === "docx") {
        // word文档
        return "./img/word_icon.png";
    } else if (exten === "xls" || exten === "xlsx") {
        // excel文档
        return "./img/excel_icon.png";
    } else if (exten === "ppt" || exten === "pptx") {
        // ppt文档
        return "./img/ppt_icon.png";
    } else if (exten === "css" || exten === "js" || exten === "html" || exten === "java" ||
        exten === "c" || exten === "cpp" || exten === "php" || exten === "py" || exten === "bat" || exten === "md") {
        // 代码文档
        return "./img/code_icon.png";
    } else {
        // 其他文件
        return "./img/file_icon.png";
    }
}


//当前路径下的导航和路径渲染
function presentPathDisplay(path, mk, urlCtrl) {
    //按钮显示设置
    search_title.style.display = "none";
    mkdir_btn.style.display = "inline-block";
    upload_btn.style.display = "inline-block";
    download_btn.style.display = "none";
    del_btn.style.display = "none";
    check_all_btn.style.display = "inline-block";
    pathnow = path;
    getDirFd = new FormData();

    getDirFd.delete("source")
    getDirFd.set("source", path)
    let url = "/getDir";
    if (urlCtrl) {
        url = urlCtrl;
    }

    // console.log(axios.defaults.headers.post['Content-Type'])
    // axios.post(url, getDirFd).then(res => {
    //     console.log("axios结果：\n" + res.data.msg)
    // }).catch(res => {
    //     console.error(res)
    // })

    //ajaxPOST(url, getDirFd).then((res) => {
    //console.log(res)
    axios.post(url, getDirFd).then((response) => {
        console.log("axios结果 " + response.data.msg);
        let res = response.data;
        //路径渲染
        pathnow = path;
        let pathArray = pathnow.split("/");
        pathArray[0] = "我的网盘";
        filepath.innerHTML = "";
        for (let i = 0; i < pathArray.length; i++) {
            let dir_link = document.createElement("a");
            let dir_divide = document.createElement("a");
            dir_link.href = "javascript:;";
            if (i != pathArray.length - 1) {
                dir_link.classList.add("dir_link");
                dir_link.innerHTML = pathArray[i];
                dir_link.style.color = "#1d9de3";
                dir_divide.innerHTML = "  &gt;  ";
            } else {
                dir_link.innerHTML = pathArray[i];
            }
            // 上一级路径
            let last_path = "";
            if (i == 0) {
                dir_link.addEventListener("click", () => {
                    if (pathnow) {
                        presentPathDisplay("");
                    }
                })
            } else {
                let link_path = "";
                for (let j = 1; j < i + 1; j++) {
                    last_path = link_path;
                    link_path += "/" + pathArray[j];
                }
                //跳转
                dir_link.addEventListener("click", () => {
                    if (link_path != pathnow) presentPathDisplay(link_path);
                })
            }
            filepath.appendChild(dir_link);
            filepath.appendChild(dir_divide);
            window.onkeyup = (e) => {
                let confirmNow = confirmBox.style.display !== "" && confirmBox.style.display !== "none"
                if ((!confirmNow) && path !== "" && (e.key === "Escape" || e.keyCode === "27"))
                    presentPathDisplay(last_path)
            }
        }

        filesDisplay.innerHTML = "";
        //清除后把mkbox重新加入
        filesDisplay.appendChild(mkbox);

        if (res.dir.length === 0 && res.file.length === 0) {
            filesDisplay.innerHTML = "此文件夹为空"
            files_json_array.length = 0
            folders_json_array.length = 0
        } else {
            // 重新渲染文件夹和文件
            resultFilesAndFolder(res)
        }
    }).catch((res) => {
        if (res.code === 404) {
            filepath.innerHTML = "";
            filesDisplay.innerHTML = "文件夹为空！";
        } else {
            console.log(path);
            console.error(res);
        }
    });
}


// 文件上传
let uploadBtn = document.getElementById("upload_btn");
uploadBtn.style.display = "";
let uploadInput = document.getElementById("upload_input")
uploadBtn.onclick = () => {
    uploadInput.click();
}
uploadInput.onchange = () => {
    let uploadFd = new FormData();
    uploadFd.append("source", pathnow);
    uploadFd.append("getFile", uploadInput.files[0]);
    /*for(let i=0;i<uploadInput.file.length;i++){
        uploadFd.append("getFile", uploadInput.files[i]);
    }*/
    ajaxPOST("uploadFile", uploadFd).then((res) => {
        console.log("成功上传");
        presentPathDisplay(pathnow);//延时重新加载&刷新
    }).catch((res) => {
        console.log("上传失败");
    });
}

// 搜索函数
function toSearch(path) {
    //按钮显示设置
    search_title.style.display = "";
    mkdir_btn.style.display = "none";
    upload_btn.style.display = "none";
    download_btn.style.display = "none"
    del_btn.style.display = "none"
    check_all_btn.style.display = "inline-block"
    title_select.style.display = "inline-block"
    file_select.style.display = "inline-block"
    filePath_select.style.display = "inline-block"
    title_select.innerText = "";
    getDirFd.delete("source");
    getDirFd.append("source", path);

    //获取搜索结果
    // ajaxPOST("searchDir", getDirFd).then((res) => {
    axios.post("searchDir", getDirFd).then(response => {
        let res = response.data
        console.log("axios结果:" + res.msg)

        if (res.dir.length === 0 && res.file.length === 0) {
            filesDisplay.innerHTML = "搜索结果为空!请输入正确的关键字"
            files_json_array.length = 0
            folders_json_array.length = 0
        } else {
            filesDisplay.innerHTML = ""
            resultFilesAndFolder(res)
        }

    }).catch(res => {
        if (res.data.code === 404) {
            filepath.innerHTML = "";
            filesDisplay.innerHTML = "错误：404 文件夹不存在！";
            console.error(res.data.msg)
        } else {
            console.error(res);
        }

    });
}

let files_json_array = []
let folders_json_array = []
//根据请求结果把对应的文件夹和文件渲染
function resultFilesAndFolder(res) {
    let selectcheck = 0
    files_json_array.length = 0
    folders_json_array.length = 0
    // 文件夹
    for (let i = 0; i < res.dir.length; i++) {
        let fileDiv = document.createElement("div");
        fileDiv.classList.add("fileList");

        let check = document.createElement("input");
        check.classList.add("fileList");
        check.type = "checkbox";
        //check.id = "checkDir" + i;
        fileDiv.appendChild(check);
        folders_json_array.push({
            check,
            objName: res.dir[i]
        })
        check.addEventListener("click", () => {
            if (check.checked) {
                selectcheck++;
                download_btn.style.display = "inline-block";
                del_btn.style.display = "inline-block";
            } else {
                selectcheck--;
                if (selectcheck === 0) {
                    download_btn.style.display = "none";
                    del_btn.style.display = "none";
                }
            }
        });
        // 文件夹图标
        let icon = document.createElement("img");
        icon.src = "./img/folder_icon.png";
        fileDiv.appendChild(icon);

        // 点击文件夹跳转
        let file_folder = document.createElement("a");
        file_folder.classList.add("file_select");
        file_folder.innerHTML = res.dir[i];
        file_folder.href = "javascript:;";
        fileDiv.appendChild(file_folder);
        filesDisplay.appendChild(fileDiv);
        file_folder.addEventListener("click", () => {
            presentPathDisplay(pathnow + "/" + file_folder.innerHTML);
        });
    }
    //文件
    for (let i = 0; i < res.file.length; i++) {
        let filename = res.file[i];
        let fileDiv = document.createElement("div");
        fileDiv.classList.add("fileList");

        //checkbox生成
        let check = document.createElement("input");
        check.classList.add("fileList");
        check.type = "checkbox";
        //check.id = "checkbox" + i;
        fileDiv.appendChild(check);
        files_json_array.push({
            check,
            objName: res.file[i]
        })
        check.addEventListener("click", () => {
            if (check.checked) {
                selectcheck++;
                download_btn.style.display = "inline-block";
                del_btn.style.display = "inline-block";
            } else {
                selectcheck--;
                if (selectcheck === 0) {
                    download_btn.style.display = "none";
                    del_btn.style.display = "none";
                }
            }
        });
        // 文件图标
        let icon = document.createElement("img");
        // 文件扩展名
        let exten = filename.substring(filename.lastIndexOf(".") + 1);
        let file_icon_url = iconExtension(exten);
        icon.src = file_icon_url ? file_icon_url : "./img/file_icon.png";
        fileDiv.appendChild(icon);


        let file_select = document.createElement("a");
        fileDiv.appendChild(file_select);
        file_select.classList.add("file_select");
        file_select.innerHTML = res.file[i];
        file_select.href = "javascript:;";
        file_select.addEventListener("click", () => {
            myConfirm("确定要下载" + res.file[i] + "吗", () => {
                ajaxDownload(pathnow + "/" + res.file[i]);
            })
        });
        filesDisplay.appendChild(fileDiv);
    }
    if (nlisten) {
        //全选函数
        check_all_btn.onclick = function () {
            if (check_all_btn.checked) {
                selectcheck = res.file.length;
                download_btn.style.display = "inline-block";
                del_btn.style.display = "inline-block";
            } else {
                selectcheck = 0;
                download_btn.style.display = "none";
                del_btn.style.display = "none";
            }
            for (let i = 0; i < files_json_array.length; i++) {
                files_json_array[i].check.checked = check_all_btn.checked;
            }
            for (let i = 0; i < folders_json_array.length; i++) {
                folders_json_array[i].check.checked = check_all_btn.checked;
            }
        }



        //多选下载轮询
        download_btn.addEventListener("click", () => {
            myConfirm("确定要下载所选文件吗", () => {
                for (let i = 0; i < files_json_array.length; i++) {//轮询文件
                    if (!files_json_array[i]) continue
                    let checkb = files_json_array[i].check;
                    if (checkb.checked) {
                        checkb.checked = false;
                        selectcheck--;
                        //ajaxDownload(pathnow + "/" + files_json_array[i].objName);
                        window.open(`http://47.96.253.99/Netdisk/downLoadFile?source=${pathnow}/${files_json_array[i].objName}`)
                    }
                }
                for (let i = 0; i < folders_json_array.length; i++) {//轮询目录
                    if (!folders_json_array[i]) continue
                    let checkd = folders_json_array[i].check;
                    if (checkd.checked) {
                        checkd.checked = false;
                        selectcheck--;
                        ajaxZip(pathnow + "/" + folders_json_array[i].objName);
                    }
                }
                check_all_btn.checked = false;//全选
            });

        })
        //多选删除轮询
        del_btn.addEventListener("click", () => {
            myConfirm("确定要删除所选文件吗", () => {
                console.log("delete!!!")
                let finishCount = 0;
                for (let i = 0; i < files_json_array.length; i++) {//轮询文件
                    if (!files_json_array[i]) continue
                    let checkb = files_json_array[i].check
                    if (checkb.checked) {
                        let deleteFd = new FormData();
                        checkb.checked = false;
                        selectcheck--;
                        deleteFd.append("source", pathnow + "/" + files_json_array[i].objName);
                        axios.post("deleteDir", deleteFd).then((response) => {
                            console.log("axois结果 " + response.data.msg)
                            filesDisplay.removeChild(checkb.parentElement)
                            // finishCount++
                            delete files_json_array[i]
                        }).catch(() => {
                            console.log("报错信息：" + response.data.msg)
                        });
                    }
                }
                for (let i = 0; i < folders_json_array.length; i++) {//轮询文件夹
                    if (!folders_json_array[i]) continue
                    let checkd = folders_json_array[i].check
                    if (checkd.checked) {
                        let deleteFd = new FormData();
                        checkd.checked = false;
                        selectcheck--;
                        deleteFd.append("source", pathnow + "/" + folders_json_array[i].objName);
                        axios.post("deleteDir", deleteFd).then((response) => {
                            console.log("axois结果 " + response.data.msg)
                            console.log("删除文件夹成功")
                            // finishCount++
                            // console.log("finishcount=" +finishCount)
                            filesDisplay.removeChild(checkd.parentElement)
                            delete folders_json_array[i]
                        }).catch((response) => {
                            console.error(response)
                        });
                    }
                }
            })
            check_all_btn.checked = false;//全选
        })

    }
}

//搜索功能
searchIm.onclick = () => {
    inputSearch.value = inputSearch.value.trim()
    if (inputSearch.value) {
        toSearch(inputSearch.value);
    } else {
        if (upload_btn.style.display === "inline-block" || upload_btn.style.display === "") {
            alert("请输入要搜索的关键字！")
            return
        }
        presentPathDisplay("");//空值跳回
    }
}
inputSearch.onkeydown = function (e) {
    if (e.key === 'Enter' || e.keyCode == 13) {
        inputSearch.value = inputSearch.value.trim()
        if (inputSearch.value) {
            toSearch(inputSearch.value);
        } else {
            if (upload_btn.style.display === "inline-block" || upload_btn.style.display === "") {
                alert("请输入要搜索的关键字！")
                return
            }
            presentPathDisplay("");//空值跳回
        }
    }
}