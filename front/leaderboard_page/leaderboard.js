class Leaderboard {
  constructor() {
    this.topLeadersList = document.getElementById("topLeadersList");
    this.currentPlayerList = document.getElementById("currentPlayerList");
    this.currentPlayerSection = document.getElementById("currentPlayerSection");
    this.noPlayerData = document.getElementById("noPlayerData");
    this.loadingElement = document.getElementById("loading");
    this.contentElement = document.getElementById("leaderboardContent");
    this.errorElement = document.getElementById("errorMessage");

    this.currentPlayer = this.getCurrentPlayer();
    this.init();
  }

  getCurrentPlayer() {
    // Get player nickname from sessionStorage or URL parameters
    const quizData = sessionStorage.getItem("quizData");
    if (quizData) {
      try {
        const data = JSON.parse(quizData);
        return data.nickname;
      } catch (error) {
        console.error("Error parsing quiz data:", error);
      }
    }

    // Alternative: get from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("player") || null;
  }

  async init() {
    await this.loadLeaderboard();
  }

  async loadLeaderboard() {
    try {
      // Load top 3 leaders
      const topLeaders = await this.fetchTopLeaders(3);

      // Load current player's position if available
      let currentPlayerData = null;
      if (this.currentPlayer) {
        currentPlayerData = await this.fetchPlayerPosition(this.currentPlayer);
      }

      this.displayLeaderboard(topLeaders, currentPlayerData);
    } catch (error) {
      console.error("Error loading leaderboard:", error);
      this.showError();
    }
  }

  async fetchTopLeaders(leadersNum) {
    const response = await fetch(`/api/leaderboard?leadersNum=${leadersNum}`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  }

  async fetchPlayerPosition(playerName) {
    const response = await fetch(
      `/api/leaderboard/${encodeURIComponent(playerName)}`
    );

    if (!response.ok) {
      // If player not found, return null instead of throwing error
      if (response.status === 404) {
        return null;
      }
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  }

  displayLeaderboard(topLeaders, currentPlayerData) {
    this.loadingElement.classList.add("hidden");
    this.contentElement.classList.remove("hidden");

    // Display top 3 leaders
    this.renderTopLeaders(topLeaders);

    // Display current player's position if available
    if (currentPlayerData) {
      this.renderCurrentPlayer(currentPlayerData, topLeaders);
    } else if (this.currentPlayer) {
      this.showNoPlayerData();
    }
  }

  renderTopLeaders(leaders) {
    if (!leaders || leaders.length === 0) {
      this.topLeadersList.innerHTML = `
              <div class="no-data-message">
                  <p>No leaderboard data available yet.</p>
                  <p>Be the first to play!</p>
              </div>
          `;
      return;
    }

    this.topLeadersList.innerHTML = leaders
      .map((player) => this.createLeaderboardItem(player, false))
      .join("");
  }

  renderCurrentPlayer(currentPlayerData, topLeaders) {
    // Check if current player is already in top leaders
    const isInTopLeaders = topLeaders.some(
      (leader) => leader.playerName === currentPlayerData.playerName
    );

    if (!isInTopLeaders) {
      this.currentPlayerSection.classList.remove("hidden");
      this.currentPlayerList.innerHTML = this.createLeaderboardItem(
        currentPlayerData,
        true
      );
    } else {
      // If player is in top leaders, highlight them in the top list
      this.highlightCurrentPlayerInTopList(currentPlayerData.playerName);
      this.currentPlayerSection.classList.add("hidden");
    }
  }

  highlightCurrentPlayerInTopList(playerName) {
    const leaderItems =
      this.topLeadersList.querySelectorAll(".leaderboard-item");
    leaderItems.forEach((item) => {
      const nameElement = item.querySelector(".player-name");
      if (nameElement.textContent === playerName) {
        item.classList.add("current-player-highlight");
      }
    });
  }

  createLeaderboardItem(player, isCurrentPlayer) {
    const medalEmoji = this.getMedalEmoji(player.leaderBoardPlace);
    const playerClass = isCurrentPlayer ? "current-player-highlight" : "";

    return `
          <div class="leaderboard-item ${playerClass}" data-player="${
      player.playerName
    }">
              <div class="player-rank">
                  ${medalEmoji} ${player.leaderBoardPlace}
              </div>
              <div class="player-info">
                  <div class="player-name">${this.escapeHtml(
                    player.playerName
                  )}</div>
                  <div class="player-score">Total score: ${
                    player.totalScore
                  }</div>
              </div>
              ${
                isCurrentPlayer
                  ? '<div class="current-player-badge">You</div>'
                  : ""
              }
          </div>
      `;
  }

  getMedalEmoji(place) {
    switch (place) {
      case 1:
        return "🥇";
      case 2:
        return "🥈";
      case 3:
        return "🥉";
      default:
        return "🏅";
    }
  }

  showNoPlayerData() {
    this.noPlayerData.classList.remove("hidden");
  }

  showError() {
    this.loadingElement.classList.add("hidden");
    this.errorElement.classList.remove("hidden");
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

// Add leaderboard specific styles
const style = document.createElement("style");
style.textContent = `
  .leaderboard-container {
      padding: 30px;
  }

  .top-players-section,
  .current-player-section {
      margin-bottom: 30px;
  }

  .top-players-section h2,
  .current-player-section h2 {
      text-align: center;
      color: #333;
      margin-bottom: 20px;
      font-size: 1.5rem;
  }

  .leaderboard-list {
      margin-bottom: 20px;
  }

  .leaderboard-item {
      display: flex;
      align-items: center;
      padding: 15px 20px;
      background: #f8f9fa;
      border-radius: 10px;
      margin-bottom: 10px;
      border-left: 4px solid #667eea;
      transition: all 0.3s ease;
  }

  .leaderboard-item.current-player-highlight {
      background: rgba(102, 126, 234, 0.1);
      border-left: 4px solid #ff6b6b;
      transform: scale(1.02);
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  }

  .player-rank {
      font-size: 1.2rem;
      font-weight: bold;
      color: #667eea;
      margin-right: 20px;
      min-width: 60px;
      text-align: center;
  }

  .current-player-highlight .player-rank {
      color: #ff6b6b;
  }

  .player-info {
      flex: 1;
  }

  .player-name {
      font-weight: 600;
      color: #333;
      font-size: 1.1rem;
  }

  .current-player-highlight .player-name {
      color: #ff6b6b;
  }

  .player-score {
      color: #666;
      margin-top: 5px;
  }

  .current-player-badge {
      background: #ff6b6b;
      color: white;
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 600;
      margin-left: 10px;
  }

  .no-data-message,
  .no-player-data {
      text-align: center;
      padding: 40px 20px;
      color: #666;
      background: #f8f9fa;
      border-radius: 10px;
      border: 2px dashed #ddd;
  }

  .no-data-message p,
  .no-player-data p {
      margin-bottom: 10px;
      font-size: 1.1rem;
  }

  .error-message {
      text-align: center;
      padding: 40px 20px;
      color: #e74c3c;
      background: rgba(231, 76, 60, 0.1);
      border-radius: 10px;
      border: 1px solid #e74c3c;
  }

  .error-message p {
      margin-bottom: 20px;
      font-size: 1.1rem;
  }

  .action-buttons {
      display: flex;
      gap: 15px;
      justify-content: center;
      flex-wrap: wrap;
      margin-top: 30px;
  }
`;
document.head.appendChild(style);

// Initialize leaderboard when loaded
document.addEventListener("DOMContentLoaded", () => {
  new Leaderboard();
});
