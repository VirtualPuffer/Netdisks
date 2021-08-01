function ajaxZip(url) {

    let urlFd = new FormData();
    urlFd.append("source", url);
    let xhr = new XMLHttpRequest();
    //文件名
    let filename = url.substr(url.lastIndexOf("/") + 1) + ".zip";

    // 不用请求头
    // xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.open("POST", `http://47.96.253.99/Netdisk/downLoadZipFile`);
    //xhr.open("POST", `http://127.0.0.1:10001/downLoadZipFile`);

    //进度条
    let progress_box = document.querySelector(".task");
    //若浏览器页面存在进度条盒子则绑定进度事件
    if (progress_box) {
        progressOperator(progress_box, xhr, filename);
    }

    xhr.responseType = "blob";
    xhr.onreadystatechange = () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("文件夹下载成功，  xhr.status=" + xhr.status);
            let reader = new FileReader();
            reader.readAsDataURL(xhr.response);  // 转换为base64，可以直接放入a标签href
            reader.onload = function (e) {
                // 转换完成，创建一个a标签用于下载
                let a = document.createElement('a');
                // 设置下载后文件名
                a.download = filename;
                // 设置触发浏览器下载
                a.href = e.target.result;
                document.body.appendChild(a);
                // 触发
                a.click();
                // 清理a
                document.body.removeChild(a);
            }
        } else if (xhr.status != 200) {
            console.error("ajax下载失败： xhr.status=" + xhr.status);
        }
    }
    // xhr.addEventListener("load", () => {
    //     console.log("ajax下载成功");
    //     let reader = new FileReader();
    //     reader.readAsDataURL(xhr.response);  // 转换为base64，可以直接放入a标签href
    //     reader.onload = function (e) {
    //         // 转换完成，创建一个a标签用于下载
    //         let a = document.createElement('a');
    //         a.download = 'bg2.png';
    //         a.href = e.target.result;
    //         document.querySelector("body").appendChild(a);
    //         a.click();
    //         document.querySelector("body").removeChild(a);
    //     }
    // })

    xhr.addEventListener("abort", () => { console.log("终止"); });
    xhr.send(urlFd);

}