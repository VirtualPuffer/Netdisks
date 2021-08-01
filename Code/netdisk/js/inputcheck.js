function inputstyle(input) {
  //   获取input标签对应的label
  let label = input.parentNode.querySelector("label");
  let fbbox = input.parentNode.querySelector(".fbInfo");
  //   用户焦点事件时设置样式
  input.addEventListener("focus", function () {
    //console.log("焦点在\“" + label.innerHTML + "\”input框");

    clearWarning(input);
    label.classList.add("labelFocus");
    label.classList.add("labelFocusColor");
  });
  //   失去焦点时设置样式
  input.addEventListener("blur", function () {
    //console.log("失去焦点\“" + label.innerHTML + "\”input框");
    label.classList.remove("labelFocusColor");
    //fbbox.innerHTML = "";
    switch (input.id) {

      case "registerUsername":
        usernameCheck();
        break;
      case "registerPassword":
        passwordCheck();
        break;
      case "registerPassword2":
        password2Check();
        break;
      case "registerName":
        nameCheck();
        break;
      case 'registerPhone':
        phoneCheck();
        break;

    }
    // 若为空则设置边框和字为红色
    if (!input.value) {
      label.classList.remove("labelFocus");
      warningRed(input);

      fbbox.innerHTML = `请输入${label.innerHTML}！`;
      function afterEmptyInput() {
        fbbox.innerHTML = "";
        input.removeEventListener("focus", afterEmptyInput);
      }
      input.addEventListener("focus", afterEmptyInput);
    }
  });
}

function clearWarning(input) {
  let label = input.parentNode.querySelector("label");
  label.classList.remove("labelEmpty");
  input.classList.remove("inputEmpty");
}

function warningRed(input) {
  let label = input.parentNode.querySelector("label");
  label.classList.add("labelEmpty");
  input.classList.add("inputEmpty");
}
