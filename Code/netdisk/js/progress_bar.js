function progressOperator(progress_box, xhr, filename) {
    if (progress_box === null || xhr === null) return;
    // 每个进度的盒子
    let progress_item_box = document.createElement("div");

    // 进度条
    let progress_bar = document.createElement("div");

    // 进度标题
    let progress_title = document.createElement("h3");

    // 文件名
    progress_title.innerHTML = filename;

    let progress_info = document.createElement("div");
    progress_bar.classList.add("progress_bar");
    progress_item_box.classList.add("progress_item_box");


    //移除按钮初始化
    let remove_link = document.createElement("a");
    remove_link.classList.add("process_btn");
    remove_link.style.display="none";
    remove_link.href = "javascript:;";
    remove_link.innerHTML = "移除";
    remove_link.style.float = "right";
    remove_link.addEventListener("click", () => {
        progress_box.removeChild(progress_item_box);
    });

    // 停止按钮
    let stop_link = document.createElement("a");
    stop_link.classList.add("process_btn");
    stop_link.href = "javascript:;";
    stop_link.innerHTML = "停止";
    stop_link.style.float = "right";
    stop_link.addEventListener("click", () => {
        xhr.abort();
        progress_bar.style.backgroundColor = "red";
        progress_info.innerHTML="下载中止"
        stop_link.style.display="none";
        remove_link.style.display="block";
        progress_item_box.removeChild(stop_link);
    });


    // 依次添加到盒子
    progress_item_box.appendChild(remove_link);
    progress_item_box.appendChild(stop_link);
    progress_item_box.appendChild(progress_title);
    progress_item_box.appendChild(progress_info);
    progress_item_box.appendChild(progress_bar);

    // 进度盒子添加到大盒子
    progress_box.appendChild(progress_item_box);

    let time = new Date().getTime();
    let loadedSize = 0;
    function progressFun(e) {
        let time2 = new Date().getTime();
        let speed = (e.loaded - loadedSize) / ((time2 - time) / 1000);
        loadedSize = e.loaded;
        let speedStr;
        if (speed > 1024 * 1024) {
            speedStr = (speed / (1024 * 1024)).toFixed(2) + "MB/S";
        } else if (speed > 1024) {
            speedStr = (speed / 1024).toFixed(2) + "KB/S";
        } else {
            speedStr = speed.toFixed(2) + "B/S";
        }

        if (e.lengthComputable) {
            let percentComplete = e.loaded / e.total;
            progress_bar.style.width = percentComplete * 200 + "px";

            if (e.total / 1024 < 1024) {
                // kb级别
                progress_info.innerHTML = (e.loaded / 1024).toFixed(2) + "kb / " + (e.total / 1024).toFixed(2) + " kb";

            } else {
                //mb级别
                if (e.loaded / 1024 < 1024) {
                    // kb级别
                    progress_info.innerHTML = (e.loaded / 1024).toFixed(2) + "kb / " + (e.total / 1048576).toFixed(2) + " Mb";
                } else {
                    progress_info.innerHTML = (e.loaded / 1048576).toFixed(2) + "Mb / " + (e.total / 1048576).toFixed(2) + " Mb";
                }
            }
        }

        progress_info.innerHTML += "网速： " + speedStr;
        if (e.loaded === e.total) {
            progress_info.innerHTML = "已完成";
            stop_link.style.display="none";
            remove_link.style.display="block";
        }
    }
    if (document.getElementById("upload_input").files.length!=0) {
        xhr.upload.addEventListener("progress", progressFun, false);
    } else {
        xhr.addEventListener("progress", progressFun, false);
    }

}