class QuizGame {
  constructor() {
    this.form = document.getElementById("quizForm");
    this.nicknameInput = document.getElementById("nickname");
    this.questionCountInput = document.getElementById("questionCount");
    this.difficultySelect = document.getElementById("difficulty");
    this.submitBtn = document.getElementById("submitBtn");
    this.loadingElement = document.getElementById("loading");

    this.init();
  }

  init() {
    this.setupEventListeners();
    this.setupRealTimeValidation();
  }

  setupEventListeners() {
    this.form.addEventListener("submit", (e) => this.handleSubmit(e));
  }

  setupRealTimeValidation() {
    this.nicknameInput.addEventListener("blur", () => {
      this.validateNickname();
    });

    this.questionCountInput.addEventListener("blur", () => {
      this.validateQuestionCount();
    });

    this.difficultySelect.addEventListener("change", () => {
      this.validateDifficulty();
    });
  }

  validateNickname() {
    const nickname = this.nicknameInput.value.trim();
    const errorElement = document.getElementById("nicknameError");

    if (!nickname) {
      this.showError(errorElement, "Nickname is required");
      return false;
    }

    if (nickname.length < 2) {
      this.showError(
        errorElement,
        "Nickname must be at least 2 characters long"
      );
      return false;
    }

    if (nickname.length > 20) {
      this.showError(errorElement, "Nickname must be less than 20 characters");
      return false;
    }

    this.hideError(errorElement);
    return true;
  }

  validateQuestionCount() {
    const questionCount = parseInt(this.questionCountInput.value);
    const errorElement = document.getElementById("questionCountError");

    if (isNaN(questionCount)) {
      this.showError(errorElement, "Please enter a valid number");
      return false;
    }

    if (questionCount < 1 || questionCount > 100) {
      this.showError(
        errorElement,
        "Number of questions must be between 1 and 100"
      );
      return false;
    }

    this.hideError(errorElement);
    return true;
  }

  validateDifficulty() {
    const difficulty = this.difficultySelect.value;
    const errorElement = document.getElementById("difficultyError");

    if (!difficulty) {
      this.showError(errorElement, "Please select a difficulty level");
      return false;
    }

    this.hideError(errorElement);
    return true;
  }

  showError(errorElement, message) {
    errorElement.textContent = message;
    errorElement.style.display = "block";
  }

  hideError(errorElement) {
    errorElement.textContent = "";
    errorElement.style.display = "none";
  }

  async handleSubmit(e) {
    e.preventDefault();

    const isNicknameValid = this.validateNickname();
    const isQuestionCountValid = this.validateQuestionCount();
    const isDifficultyValid = this.validateDifficulty();

    if (isNicknameValid && isQuestionCountValid && isDifficultyValid) {
      await this.submitForm();
    } else {
      this.showNotification("Please fix the errors before submitting", "error");
    }
  }

  async submitForm() {
    const formData = {
      nickname: this.nicknameInput.value.trim(),
      questionCount: parseInt(this.questionCountInput.value),
      difficulty: this.difficultySelect.value,
    };

    // Show loading state
    this.showLoading(true);
    this.submitBtn.disabled = true;

    try {
      // Fetch questions from backend
      const questions = await this.fetchQuestions(
        formData.difficulty,
        formData.questionCount
      );

      // Store data in sessionStorage for the quiz page
      sessionStorage.setItem(
        "quizData",
        JSON.stringify({
          nickname: formData.nickname,
          questionCount: formData.questionCount,
          difficulty: formData.difficulty,
          questions: questions,
        })
      );

      // Redirect to quiz page
      window.location.href = "../quiz_page/quiz.html";
    } catch (error) {
      console.error("Error fetching questions:", error);
      this.showNotification(
        "Failed to load questions. Please try again.",
        "error"
      );
    } finally {
      this.showLoading(false);
      this.submitBtn.disabled = false;
    }
  }

  async fetchQuestions(difficulty, questionCount) {
    const url = `/api/quiz?questionsLevel=${difficulty}&questionsNum=${questionCount}`;

    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const questions = await response.json();
    return questions;
  }

  showLoading(show) {
    if (show) {
      this.form.classList.add("hidden");
      this.loadingElement.classList.remove("hidden");
    } else {
      this.form.classList.remove("hidden");
      this.loadingElement.classList.add("hidden");
    }
  }

  showNotification(message, type) {
    const notification = document.createElement("div");
    notification.className = `notification ${type}`;
    notification.textContent = message;
    notification.style.cssText = `
          position: fixed;
          top: 20px;
          right: 20px;
          padding: 15px 20px;
          border-radius: 8px;
          color: white;
          font-weight: 600;
          z-index: 1000;
          transform: translateX(100%);
          transition: transform 0.3s ease;
          ${type === "error" ? "background: #e74c3c;" : "background: #27ae60;"}
      `;

    document.body.appendChild(notification);

    setTimeout(() => {
      notification.style.transform = "translateX(0)";
    }, 100);

    setTimeout(() => {
      notification.style.transform = "translateX(100%)";
      setTimeout(() => {
        if (document.body.contains(notification)) {
          document.body.removeChild(notification);
        }
      }, 300);
    }, 3000);
  }
}

// Initialize the quiz game when the page loads
document.addEventListener("DOMContentLoaded", () => {
  new QuizGame();
});
