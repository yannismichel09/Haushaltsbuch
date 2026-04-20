document.addEventListener('DOMContentLoaded', () => {
    const welcomeDiv = document.getElementById('welcome');
    const loginDiv = document.getElementById('login');
    const registerDiv = document.getElementById('register');

    const btnShowLogin = document.getElementById('loginButton');
    const btnShowRegister = document.getElementById('registerButton');
    const btnCancelLogin = document.getElementById('loginCancel');
    const btnCancelRegister = document.getElementById('registerCancel');

    const btnSubmitLogin = document.getElementById('loginSubmit');
    const btnSubmitRegister = document.getElementById('registerSubmit');

    const inputLoginUsername = document.getElementById('loginUsername');
    const inputLoginPassword = document.getElementById('loginPassword');

    const inputRegisterUsername = document.getElementById('registerUsername');
    const inputRegisterEmail = document.getElementById('registerEmail');
    const inputRegisterPassword = document.getElementById('registerPassword');

    function showSection(sectionToShow) {
        welcomeDiv.style.display = 'none';
        loginDiv.style.display = 'none';
        registerDiv.style.display = 'none';
        sectionToShow.style.display = 'block';
    }

    btnShowLogin.addEventListener('click', () => showSection(loginDiv));
    btnShowRegister.addEventListener('click', () => showSection(registerDiv));
    
    btnCancelLogin.addEventListener('click', () => {
        inputLoginUsername.value = '';
        inputLoginPassword.value = '';
        showSection(welcomeDiv);
    });
    
    btnCancelRegister.addEventListener('click', () => {
        inputRegisterUsername.value = '';
        inputRegisterEmail.value = '';
        inputRegisterPassword.value = '';
        showSection(welcomeDiv);
    });

    btnSubmitLogin.addEventListener('click', async () => {
        const username = inputLoginUsername.value.trim();
        const password = inputLoginPassword.value.trim();

        if (!username || !password) {
            alert("Please fill in all fields.");
            return;
        }

        btnSubmitLogin.disabled = true;
        btnSubmitLogin.textContent = "Loading...";

        const result = await loginUser({ username, password });

        if (result && result.token) {
            globalToken = result.token; 
            window.location.href = "homePage.html"; 
        } else {
            alert("Login failed. Username or password is incorrect.");
            btnSubmitLogin.disabled = false;
            btnSubmitLogin.textContent = "Login";
        }
    });

    btnSubmitRegister.addEventListener('click', async () => {
        const username = inputRegisterUsername.value.trim();
        const email = inputRegisterEmail.value.trim();
        const password = inputRegisterPassword.value.trim();

        if (!username || !email || !password) {
            alert("Please fill in all fields.");
            return;
        }

        btnSubmitRegister.disabled = true;
        btnSubmitRegister.textContent = "Loading...";

        const result = await registerUser({ username, email, password });

        if (result && result.token) {
            globalToken = result.token; 
            window.location.href = "homePage.html";
        } else {
            alert("Registration failed.");
            btnSubmitRegister.disabled = false;
            btnSubmitRegister.textContent = "Register";
        }
    });
});