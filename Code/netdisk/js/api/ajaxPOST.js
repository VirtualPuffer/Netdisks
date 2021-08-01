function ajaxPOST(url, formdata) {

    let xhr = new XMLHttpRequest();
    xhr.open("POST", `http://47.96.253.99/Netdisk/${url}`);
    // xhr.open("POST", `http://127.0.0.1:10001/${url}`);
    //设置请求头
    // xhr.setRequestHeader("Content-Type", "multipart/form-data")
    // xhr.setRequestHeader("Content-Type", 'application/json')
    // xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
    if (url === "uploadFile") {
        progressOperator(document.querySelector('.task'), xhr, formdata.get("getFile").name);
    }
    return new Promise(
        (resolve, reject) => {
            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    let res = JSON.parse(xhr.responseText);
                    console.log("原生ajax结果：" + res.msg);
                    if (res.code === 200) {
                        resolve(res);
                    } else {
                        reject(res);
                    }
                } else if (xhr.status != 200) {
                    reject(xhr.status);
                }
            }
            xhr.send(formdata);
        }
    );
}