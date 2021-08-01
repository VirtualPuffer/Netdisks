let btnRegister = document.getElementById("jumpRegister");
let btnLogin = document.getElementById("jumpLogin");
let signupPage = document.getElementById("signupPage");
let loginPage = document.getElementById("loginPage");

//设置点击按钮，用隐藏实现切换登录/注册页面
btnRegister.onclick = function () {
  //css表中为flex布局
  signupPage.style.display = "flex";
  loginPage.style.display = "none";
};

btnLogin.onclick = function () {
  //css表中为flex布局
  loginPage.style.display = "flex";
  signupPage.style.display = "none";
};

if (sessionStorage.getItem("tologin") != null) {
  if (sessionStorage.getItem("tologin") == 0) {
    btnRegister.click();
  } else {
    btnLogin.click();
  }
} else {
  btnLogin.click();
}