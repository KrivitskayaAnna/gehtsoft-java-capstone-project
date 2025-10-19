class QuizPage {
  constructor() {
    this.quizForm = document.getElementById("quizAnswersForm");
    this.questionsContainer = document.getElementById("questionsContainer");
    this.submitBtn = document.getElementById("submitQuizBtn");
    this.loadingElement = document.getElementById("loading");
    this.quizData = null;
    this.results = null;

    this.init();
  }

  init() {
    this.loadQuizData();
    this.setupEventListeners();
  }

  loadQuizData() {
    const storedData = sessionStorage.getItem("quizData");

    if (!storedData) {
      this.showNotification("No quiz data found. Please start over.", "error");
      setTimeout(() => {
        window.location.href = "../form_page/form.html";
      }, 2000);
      return;
    }

    try {
      this.quizData = JSON.parse(storedData);
      this.displayQuizInfo();
      this.renderQuestions();
      this.showQuizContent();
    } catch (error) {
      console.error("Error parsing quiz data:", error);
      this.showNotification(
        "Error loading quiz data. Please start over.",
        "error"
      );
      setTimeout(() => {
        window.location.href = "../form_page/form.html";
      }, 2000);
    }
  }

  displayQuizInfo() {
    document.getElementById("playerNickname").textContent =
      this.quizData.nickname;
    document.getElementById("totalQuestions").textContent =
      this.quizData.questionCount;
    document.getElementById("quizDifficulty").textContent =
      this.formatDifficulty(this.quizData.difficulty);
  }

  renderQuestions() {
    this.questionsContainer.innerHTML = "";

    this.quizData.questions.forEach((question, index) => {
      const questionElement = this.createQuestionElement(question, index);
      this.questionsContainer.appendChild(questionElement);
    });
  }

  createQuestionElement(question, index) {
    const questionDiv = document.createElement("div");
    questionDiv.className = "question-block";
    questionDiv.setAttribute("data-question-id", question.questionId);

    questionDiv.innerHTML = `
          <div class="question-header">
              <div class="question-text">${index + 1}. ${this.decodeHtml(
      question.question
    )}</div>
              <div class="question-result hidden" id="result-${
                question.questionId
              }">
                  <!-- Result icon will be added here -->
              </div>
          </div>
          <div class="answers-container">
              ${question.answers
                .map(
                  (answer, answerIndex) => `
                  <label class="answer-option">
                      <input type="radio" name="question_${
                        question.questionId
                      }" value="${answerIndex}" required>
                      <span class="answer-label">${this.decodeHtml(
                        answer
                      )}</span>
                  </label>
              `
                )
                .join("")}
          </div>
      `;

    // Add click event to entire answer option
    const answerOptions = questionDiv.querySelectorAll(".answer-option");
    answerOptions.forEach((option) => {
      option.addEventListener("click", (e) => {
        const radio = option.querySelector('input[type="radio"]');
        radio.checked = true;

        // Update visual selection
        answerOptions.forEach((opt) => opt.classList.remove("selected"));
        option.classList.add("selected");
      });
    });

    return questionDiv;
  }

  decodeHtml(html) {
    const txt = document.createElement("textarea");
    txt.innerHTML = html;
    return txt.value;
  }

  setupEventListeners() {
    this.quizForm.addEventListener("submit", (e) => this.handleQuizSubmit(e));
  }

  async handleQuizSubmit(e) {
    e.preventDefault();

    if (this.validateAllQuestionsAnswered()) {
      await this.submitAnswers();
    } else {
      this.showNotification(
        "Please answer all questions before submitting.",
        "error"
      );
    }
  }

  validateAllQuestionsAnswered() {
    const allQuestions = this.quizData.questions;

    for (const question of allQuestions) {
      const radioName = `question_${question.questionId}`;
      const selectedAnswer = document.querySelector(
        `input[name="${radioName}"]:checked`
      );

      if (!selectedAnswer) {
        // Scroll to the first unanswered question
        const questionElement = document
          .querySelector(`input[name="${radioName}"]`)
          .closest(".question-block");
        questionElement.scrollIntoView({ behavior: "smooth", block: "center" });

        // Highlight the unanswered question
        questionElement.style.animation = "pulse 0.5s ease-in-out";
        setTimeout(() => {
          questionElement.style.animation = "";
        }, 500);

        return false;
      }
    }

    return true;
  }

  collectAnswers() {
    const answers = [];

    this.quizData.questions.forEach((question) => {
      const radioName = `question_${question.questionId}`;
      const selectedAnswer = document.querySelector(
        `input[name="${radioName}"]:checked`
      );

      if (selectedAnswer) {
        answers.push({
          questionId: question.questionId,
          answerIdx: parseInt(selectedAnswer.value),
        });
      }
    });

    return answers;
  }

  async submitAnswers() {
    const answers = this.collectAnswers();
    const submissionData = {
      playerName: this.quizData.nickname,
      answers: answers,
    };

    console.log("Submitting answers:", submissionData);

    // Show loading state
    this.submitBtn.disabled = true;
    this.submitBtn.textContent = "Checking Answers...";

    try {
      const response = await this.sendAnswersToBackend(submissionData);
      this.results = response;
      this.displayResults();
    } catch (error) {
      console.error("Error submitting answers:", error);
      this.showNotification(
        "Failed to submit answers. Please try again.",
        "error"
      );
    } finally {
      this.submitBtn.disabled = false;
      this.submitBtn.textContent = "Submit Answers";
    }
  }

  getCsrfTokenFromCookie(csrf_resp) {
    const match = csrf_resp.match(/CSRF Token: ([^,]+)/);
    return match ? match[1].trim() : null;
  }

  async sendAnswersToBackend(submissionData) {
    //Делаем предварительный запрос для установки CSRF токена в куках
    const csrf_response = await fetch("/api/quiz/csrf", {
      method: "GET",
      headers: {
        accept: "application/json",
        "Content-Type": "application/json",
      },
      credentials: "include",
    });
    if (!csrf_response.ok) {
      throw new Error(
        `HTTP error while getting csrf token! status: ${csrf_response.status}`
      );
    }
    const csrfToken = await csrf_response.text();

    const response = await fetch("/api/quiz/check", {
      method: "POST",
      headers: {
        accept: "application/json",
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": csrfToken,
      },
      credentials: "include",
      body: JSON.stringify(submissionData),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  }

  displayResults() {
    this.disableAllAnswers();

    // Mark each question with correct/incorrect
    this.quizData.questions.forEach((question) => {
      const isCorrect = this.results.correctQuestionIds.includes(
        question.questionId
      );
      this.markQuestionResult(question.questionId, isCorrect);
    });

    // Change submit button to show results and leaderboard button
    this.showResultsActions();
  }

  disableAllAnswers() {
    // Add a class to the form that disables only the answer sections
    this.quizForm.classList.add("answers-disabled");

    // Disable radio buttons for good measure
    const allInputs = document.querySelectorAll('input[type="radio"]');
    allInputs.forEach((input) => {
      input.disabled = true;
    });
  }

  markQuestionResult(questionId, isCorrect) {
    const questionElement = document.querySelector(
      `[data-question-id="${questionId}"]`
    );
    const resultElement = questionElement.querySelector(".question-result");

    resultElement.classList.remove("hidden");
    resultElement.innerHTML = isCorrect
      ? '<span class="result-correct">Correct</span>'
      : '<span class="result-incorrect">Incorrect</span>';

    // Also mark the selected answer
    const selectedInput = questionElement.querySelector(
      'input[type="radio"]:checked'
    );
    if (selectedInput) {
      const selectedOption = selectedInput.closest(".answer-option");
      if (isCorrect) {
        selectedOption.classList.add("correct-answer");
      } else {
        selectedOption.classList.add("incorrect-answer");
      }
    }
  }

  showResultsActions() {
    const quizActions = document.querySelector(".quiz-actions");
    quizActions.innerHTML = `
          <div class="results-summary">
              <h3><b>Your total score is ${this.results.totalScore} out of ${this.results.maxScore}!</b></h3>
          </div>
          <div class="action-buttons">
              <button type="button" class="submit-btn" onclick="window.location.href='../leaderboard_page/leaderboard.html'">
                  go to the leaderboard
              </button>
          </div>
      `;
  }

  showQuizContent() {
    this.loadingElement.classList.add("hidden");
    this.quizForm.classList.remove("hidden");
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

  formatDifficulty(difficulty) {
    const difficultyMap = {
      easy: "Easy",
      medium: "Medium",
      hard: "Hard",
    };
    return difficultyMap[difficulty] || difficulty;
  }

  escapeHtml(unsafe) {
    return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }
}

// Add CSS for results styling
const style = document.createElement("style");
style.textContent = `
  @keyframes pulse {
      0% { box-shadow: 0 0 0 0 rgba(231, 76, 60, 0.7); }
      70% { box-shadow: 0 0 0 10px rgba(231, 76, 60, 0); }
      100% { box-shadow: 0 0 0 0 rgba(231, 76, 60, 0); }
  }

  .question-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;
  }

  .question-result {
      font-weight: 600;
      border-radius: 5px;
  }

  .result-correct {
      color: #27ae60;
      background: rgba(39, 174, 96, 0.1);
      border-radius: 5px;
      border: 1px solid #27ae60;
  }

  .result-incorrect {
      color: #e74c3c;
      background: rgba(231, 76, 60, 0.1);
      border-radius: 5px;
      border: 1px solid #e74c3c;
  }

  .correct-answer {
      border-color: #27ae60 !important;
      background: rgba(39, 174, 96, 0.1) !important;
  }

  .incorrect-answer {
      border-color: #e74c3c !important;
      background: rgba(231, 76, 60, 0.1) !important;
  }

  .results-summary {
      text-align: center;
      margin-bottom: 30px;
      padding: 20px;
      background: #f8f9fa;
      border-radius: 10px;
      border-left: 4px solid #667eea;
  }

  .results-summary h3 {
      color: #333;
      margin-bottom: 10px;
      font-size: 1.5rem;
      font-weight: 1000;
  }

  .results-summary p {
      color: #666;
      font-size: 1.1rem;
  }

  .action-buttons {
      display: flex;
      gap: 15px;
      justify-content: center;
      flex-wrap: wrap;
  }

  .secondary-btn {
      background: #6c757d;
      color: white;
      border: none;
      padding: 12px 25px;
      border-radius: 8px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
  }

  .secondary-btn:hover {
      background: #5a6268;
      transform: translateY(-2px);
  }

  input:disabled {
      cursor: not-allowed;
  }

  .answer-option input:disabled ~ .answer-label {
      cursor: not-allowed;
  }
`;
document.head.appendChild(style);

// Initialize the quiz page when loaded
document.addEventListener("DOMContentLoaded", () => {
  new QuizPage();
});
