let registerUsername = document.getElementById("registerUsername");
let registerPassword = document.getElementById("registerPassword");
let registerPassword2 = document.getElementById("registerPassword2");
let registerName = document.getElementById("registerName");
let pwd_strength = document.getElementById("pwd_strength");

let freeLogin_checkbox = document.getElementById("freeLogin");

//是否能够注册的状态
let registerDisabled = true;
let username = document.getElementById("username");
let password = document.getElementById("password");
let loginFB = document.getElementById("loginFB");

//获取所有的input标签，统一设置
let inputs = document.getElementsByTagName("input");


axios.defaults.baseURL = 'http://47.96.253.99/Netdisk/'

// ajaxPOST("freeLogin", null).then((res) => {
axios.get('/freeLogin').then(response => {
  /*console.log("免登录成功");*/
  if (response.data.code === 200)
    window.location.href = './index.html'
}).catch((res) => {
  //免登录失败
  console.log(res)
});

for (let i = 0; i < inputs.length; i++) {
  // 调用inputcheck.js中的函数
  inputstyle(inputs[i]);
}

//用户名的正则只可包含字母，数字，下划线和短连接号-
function usernameCheck() {
  let usernameFB = registerUsername.parentNode.querySelector(".fbInfo");
  let uPattern = /[^A-za-z0-9_-]/;
  let letter1st = /^[a-z]/i;
  warningRed(registerUsername);
  registerDisabled = true;
  if (!registerUsername.value) {
    usernameFB.innerText = "用户名不能为空！";
  } else if (uPattern.test(registerUsername.value)) {
    usernameFB.innerText =
      "请输入正确的用户名\n只可包含字母，数字，下划线和短连接号";
  } else if (!letter1st.test(registerUsername.value)) {
    usernameFB.innerText = "请输入正确的用户名\n必须以字母开头";
  } else if (
    registerUsername.value.length > 16 ||
    registerUsername.value.length < 4
  ) {
    usernameFB.innerText = "用户名长度应为4-16位！";
  } else {
    usernameFB.innerText = "";
    clearWarning(registerUsername);
    registerDisabled = false;
  }
}
// 绑定用户名正则
registerUsername.onkeyup = usernameCheck;

//密码的正则，包含大小写英文、数字、特殊符号
function passwordCheck() {
  let passwordFB = registerPassword.parentNode.querySelector(".fbInfo");
  //基础正则
  let pPattern = /[^A-za-z0-9~!@#$%^&*]/;

  //大写字母
  let majuscule = /[A-Z]/;

  //小写字母
  let lowercase = /[a-z]/;

  //英文字母
  let hasNumber = /[0-9]/;

  //特殊符号
  let specials = /[~!@#$%^&*]/;
  passwordFB.style.color = "";
  warningRed(registerPassword);
  registerDisabled = true;
  if (!registerPassword.value) {
    passwordFB.innerText = "密码不能为空！";
    pwd_strength.style.width = "0";
  } else if (pPattern.test(registerPassword.value)) {
    passwordFB.innerText =
      "请输入正确的密码\n只能包含大小写英文、数字和特殊符号";
    pwd_strength.style.width = "0";
  } else if (
    registerPassword.value.length > 16 ||
    registerPassword.value.length < 6
  ) {
    passwordFB.innerText = "密码长度应为6-16位！";
    pwd_strength.style.width = "0";
  } else {
    passwordFB.innerText = "";
    clearWarning(registerPassword);
    registerDisabled = false;
    //判断密码强度
    let strength = 0;

    //累次判断正则，条件累次添加密码强度
    if (majuscule.test(registerPassword.value)) strength++;
    if (lowercase.test(registerPassword.value)) strength++;
    if (hasNumber.test(registerPassword.value)) strength++;
    if (specials.test(registerPassword.value)) strength++;

    //根据密码强度设置“密码强度”小边框的动画
    switch (strength) {
      case 1:
        {
          passwordFB.innerText = "密码强度：低";
          pwd_strength.style.width = "60px";
          pwd_strength.style.backgroundColor = "#ff0000";
        }
        break;
      case 2:
        {
          passwordFB.style.color = "#FFA500";
          passwordFB.innerText = "密码强度：中";
          pwd_strength.style.width = "120px";
          pwd_strength.style.backgroundColor = "#FF7F50";
        }
        break;
      case 3:
        {
          passwordFB.style.color = "#FFA500";
          passwordFB.innerText = "密码强度：高";
          pwd_strength.style.width = "180px";
          pwd_strength.style.backgroundColor = "#ffff00";
        }
        break;
      case 4:
        {
          passwordFB.style.color = "#008000";
          passwordFB.innerText = "密码强度：极高";
          pwd_strength.style.width = "240px";
          pwd_strength.style.backgroundColor = "#008000";
        }
        break;
    }
  }
}
// 绑定密码正则
registerPassword.onkeyup = passwordCheck;

//再次输入密码验证两次输入是否一致
function password2Check() {
  let password2FB = registerPassword2.parentNode.querySelector(".fbInfo");
  warningRed(registerPassword2);
  registerDisabled = true;
  if (!registerPassword2.value) {
    password2FB.innerText = "请在次输入密码以确认！";
  } else if (registerPassword.value != registerPassword2.value) {
    password2FB.innerText = "两次输入的密码不一致！";
  } else {
    password2FB.innerText = "";
    registerDisabled = false;
    clearWarning(registerPassword2);
  }
}
// 绑定再次输入密码函数
registerPassword2.addEventListener("blur", password2Check);

//判断姓名的正则表达式
function nameCheck() {
  let nameFB = registerName.parentNode.querySelector(".fbInfo");
  // 只能输入纯中文或纯英文，包含少数民族和外文名连接号“·”
  let nPattern = /^[A-z ]+$|^[\u4E00-\u9FA5·]+$/;
  warningRed(registerName);
  registerDisabled = true;
  if (nPattern.test(registerName.value)) {
    registerDisabled = false;
    nameFB.innerText = "";
    clearWarning(registerName);
  } else {
    registerDisabled = true;
    nameFB.innerText = "只能输入纯中文或纯英文";
  }
}
// 姓名正则函数绑定
registerName.addEventListener("keyup", nameCheck);

// 注册按钮
let registerBtn = document.getElementById("signup");
let registerFB = document.getElementById("signupFB");
registerBtn.addEventListener("click", function () {
  // 注册前再一次全部检查所有表单信息
  usernameCheck();
  passwordCheck();
  password2Check();
  nameCheck();
  registerFB.innerHTML = registerDisabled
    ? "有不合法的表单信息，请检查是否填写正确!"
    : "";
  if (!registerDisabled) {
    toRegister();
  }
});

// 登录按钮
let loginBtn = document.getElementById("login");
loginBtn.addEventListener("click", function () {
  toLogin();
});

// 注册函数
function toRegister() {
  let fd = new FormData();
  fd.append("username", registerUsername.value);
  fd.append("password", registerPassword.value);
  fd.append("name", registerName.value);

  ajaxPOST("userRegister", fd).then((res) => {
    console.log(res.msg);
    alert("注册成功！欢迎你，" + registerName.value);
    username.value = registerUsername.value;
    document.getElementById("jumpLogin").click();
    username.focus();
    password.focus();

  }).catch((res) => {
    if (res === 300) {
      alert("账号名已存在！");
      registerUsername.value = "";
      usernameCheck();
    } else if (res === 500) {
      console.error(res.msg);
    } else {
      console.error("错误：");
    }
  });
}

// 登录函数
function toLogin() {
  let fd = new FormData();
  fd.append("username", username.value);
  fd.append("password", password.value);
  fd.append("freeLogin", freeLogin_checkbox.checked)
  axios.post("userLogin", fd).then(response => {
    //ajaxPOST("userLogin", fd).then((res) => {
    /*alert("登录成功");*/
    if (response.data.code === 200)
      window.location.href = "./index.html";
    else if (response.data.code === 300) {
      //账号或密码错误
      console.log(response.data.msg);
      alert("账号或密码错误");
    }
  }).catch((status) => {
    console.error("错误：" + status);
  });
}

// 回车触发登录
username.addEventListener("keydown", (e) => {
  if (e.key === 'Enter' || e.keyCode == 13) {
    username.value.replace(/[\r\n]/g, "");
    toLogin();
  }
})

// 回车触发登录
password.addEventListener("keydown", (e) => {
  if (e.key === 'Enter' || e.keyCode == 13) {
    password.value.replace(/[\r\n]/g, "");
    toLogin();
  }
})
