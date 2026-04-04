  // Login Page Script
    const loginForm = document.getElementById('loginForm');
    const signupForm = document.getElementById('signupForm');
    const authTitle = document.getElementById('authTitle');
    const authSubtitle = document.getElementById('authSubtitle');

    document.getElementById('toggleToSignup').addEventListener('click', e => {
      e.preventDefault();
      loginForm.hidden = true;
      signupForm.hidden = false;
      authTitle.textContent = 'CREATE ACCOUNT';
      authSubtitle.textContent = 'Create your account to start measuring.';
    });

    document.getElementById('toggleToLogin').addEventListener('click', e => {
      e.preventDefault();
      signupForm.hidden = true;
      loginForm.hidden = false;
      authTitle.textContent = 'WELCOME BACK';
      authSubtitle.textContent = 'Welcome back! Please enter your details.';
    });

    loginForm.addEventListener('submit', e => {
      e.preventDefault();
      const popup = document.getElementById('successPopup');
      popup.classList.remove('hidden');
      setTimeout(() => { window.location.href = 'dashboard.html'; }, 2000);
    });

    signupForm.addEventListener('submit', e => {
      e.preventDefault();
      const popup = document.getElementById('successPopup');
      popup.classList.remove('hidden');
      setTimeout(() => { window.location.href = 'dashboard.html'; }, 2000);
    });
